package com.api.tests.aadhaar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.models.request.aadhaar.GenerateClientTokenRequest;
import com.api.models.request.aadhaar.GenerateDigilockerLinkRequest;
import com.api.models.request.aadhaar.RetrieveAadhaarDataRequest;
import com.api.pages.aadhaar.AadhaarConsentPage;
import com.api.pages.aadhaar.AadhaarNumberPage;
import com.api.pages.aadhaar.AadhaarOtpPage;
import com.api.pages.aadhaar.AadhaarSecurityPinPage;
import com.api.pojos.BaseURL;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.response.Response;

public final class AadhaarFetchThroughDigiLockerE2E {

	private static final String OTP_ENDPOINT = "http://10.112.35.159:8080/api/latest-otp";
	private static final Set<String> APPROVED_DIGILOCKER_HOSTS = Set.of(
			"digilocker.meon.co.in", "accounts.digitallocker.gov.in");
	private static final long OTP_WAIT_MILLIS = Duration.ofMinutes(2).toMillis();
	private static final long OTP_POLL_INTERVAL_MILLIS = 5_000L;
	private static final List<String> REQUIRED_AADHAAR_FIELDS = Arrays.asList(
			"aadhar_address", "aadhar_filename", "aadhar_img_filename", "aadhar_no", "aadhar_xml", "adharimg",
			"country", "date_time", "dist", "dob", "fathername", "gender", "house", "locality", "name",
			"pincode", "state", "transaction_id", "vtc");

	private final Logger logger = LoggerUtility.getLogger(getClass());

	@Test(description = "Complete DigiLocker Aadhaar consent and validate exported data", groups = { "aadhaar-live", "e2e" }, timeOut = 1_200_000L)
	public void completeDigiLockerAadhaarFetch() throws InterruptedException {
		if (!Boolean.getBoolean("meon.aadhaar.live")) {
			throw new SkipException("Live DigiLocker flow is opt-in; use -Dmeon.aadhaar.live=true to run it.");
		}

		String aadhaarNumber = requiredEnvironmentValue("AADHAAR_NUMBER").replaceAll("\\D", "");
		String securityPin = requiredEnvironmentValue("DIGILOCKER_SECURITY_PIN").trim();
		assertTrue(aadhaarNumber.matches("[0-9]{12}"), "AADHAAR_NUMBER must contain exactly 12 digits.");
		assertTrue(securityPin.matches("[0-9]{6}"), "DIGILOCKER_SECURITY_PIN must contain exactly 6 digits.");

		BaseURL aadhaarConfig = JSONUtility.getAadhaar();
		AuthService authService = new AuthService("aadhaar");

		Response tokenResponse = authService.generateClientToken(new GenerateClientTokenRequest(
				aadhaarConfig.getCompany_name(), aadhaarConfig.getSecret_token()));
		assertEquals(tokenResponse.getStatusCode(), 200, "Client-token API should return HTTP 200.");
		assertTrue(Boolean.TRUE.equals(tokenResponse.jsonPath().getBoolean("status")), "Client-token API should succeed.");
		String clientToken = tokenResponse.jsonPath().getString("client_token");
		String state = tokenResponse.jsonPath().getString("state");
		assertNotNull(clientToken, "Client-token API did not return a client token.");
		assertNotNull(state, "Client-token API did not return state.");
		assertTrue(!clientToken.trim().isEmpty() && !state.trim().isEmpty(), "Client-token API returned empty credentials.");

		GenerateDigilockerLinkRequest linkRequest = new GenerateDigilockerLinkRequest(clientToken,
				aadhaarConfig.getRedirect_url(), aadhaarConfig.getCompany_name(), new String[] { "aadhaar" });
		Response linkResponse = authService.generateClientToken(linkRequest);
		assertEquals(linkResponse.getStatusCode(), 200, "DigiLocker-link API should return HTTP 200.");
		assertTrue(Boolean.TRUE.equals(linkResponse.jsonPath().getBoolean("success")), "DigiLocker-link API should succeed.");
		String digilockerUrl = linkResponse.jsonPath().getString("url");
		assertNotNull(digilockerUrl, "DigiLocker-link API did not return a URL.");
		assertTrue(!digilockerUrl.trim().isEmpty(), "DigiLocker-link API returned an empty URL.");
		assertApprovedDigiLockerUrl(digilockerUrl);

		String otpEndpoint = OTP_ENDPOINT;
		int maxResends = configuredMaxResends();
		WebDriver driver = null;
		String uiPhase = "starting Firefox";
		try {
			driver = new FirefoxDriver();
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
			driver.manage().window().maximize();

			uiPhase = "opening the generated DigiLocker link";
			AadhaarNumberPage aadhaarPage = new AadhaarNumberPage(driver);
			aadhaarPage.open(digilockerUrl);
			long initialOtpRequestAt = System.currentTimeMillis();
			uiPhase = "submitting Aadhaar and waiting for OTP page";
			aadhaarPage.enterNumberAndContinue(aadhaarNumber);

			AadhaarOtpPage otpPage = new AadhaarOtpPage(driver);
			otpPage.waitUntilLoaded();
			uiPhase = "waiting for a fresh OTP";
			String otp = waitForOtpAndResendIfNeeded(otpPage, otpEndpoint, initialOtpRequestAt, maxResends);
			uiPhase = "submitting OTP";
			otpPage.enterAndVerify(otp);

			uiPhase = "waiting for security PIN page";
			AadhaarSecurityPinPage pinPage = new AadhaarSecurityPinPage(driver);
			pinPage.waitUntilLoaded();
			uiPhase = "submitting security PIN";
			pinPage.enterAndContinue(securityPin);

			uiPhase = "selecting Aadhaar consent";
			new AadhaarConsentPage(driver).selectAadhaarCardAndAllow();
			uiPhase = "waiting for Meon callback";
			assertTrue(waitForMeonRedirect(driver, aadhaarConfig.getRedirect_url()),
					"DigiLocker did not return to the configured Meon callback.");
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("DigiLocker browser flow was interrupted at " + uiPhase
					+ "; sensitive page details were suppressed.");
		} catch (RuntimeException exception) {
			throw new IllegalStateException("DigiLocker browser flow failed at " + uiPhase
					+ "; sensitive page details were suppressed.");
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}

		Response dataResponse = authService.retrieveData(new RetrieveAadhaarDataRequest(clientToken, state, true));
		assertEquals(dataResponse.getStatusCode(), 200, "Aadhaar-data API should return HTTP 200.");
		assertEquals(dataResponse.jsonPath().getInt("code"), 200, "Aadhaar-data API code should be 200.");
		assertTrue(Boolean.TRUE.equals(dataResponse.jsonPath().getBoolean("success")), "Aadhaar-data API should succeed.");
		assertEquals(dataResponse.jsonPath().getString("status"), "success", "Aadhaar-data status should be success.");
		assertEquals(dataResponse.jsonPath().getString("msg"), "Data Exported Successfully",
				"Aadhaar-data response message mismatch.");

		Map<String, Object> data = dataResponse.jsonPath().getMap("data");
		assertNotNull(data, "Aadhaar-data response is missing the data object.");
		for (String field : REQUIRED_AADHAAR_FIELDS) {
			Object value = data.get(field);
			assertTrue(value instanceof String && !((String) value).trim().isEmpty(),
					"Aadhaar-data response is missing required field: " + field);
		}
		assertHttpsDocumentUrl(data, "aadhar_filename");
		assertHttpsDocumentUrl(data, "aadhar_img_filename");
		assertHttpsDocumentUrl(data, "aadhar_xml");
		assertHttpsDocumentUrl(data, "adharimg");

		String maskedAadhaar = String.valueOf(data.get("aadhar_no"));
		String lastFourDigits = aadhaarNumber.substring(aadhaarNumber.length() - 4);
		String visibleDigits = maskedAadhaar.replaceAll("\\D", "");
		assertTrue(visibleDigits.endsWith(lastFourDigits), "Returned Aadhaar number did not match the masked account suffix.");
		assertTrue(!visibleDigits.equals(aadhaarNumber), "Aadhaar response must not return the full Aadhaar number.");
		logger.info("DigiLocker Aadhaar flow completed; required response fields and masking checks passed.");
	}

	private String waitForOtpAndResendIfNeeded(AadhaarOtpPage otpPage, String endpoint, long initialRequestedAtMillis,
			int maxResends)
			throws InterruptedException {
		for (int resendCount = 0; resendCount <= maxResends; resendCount++) {
			long requestedAt = resendCount == 0 ? initialRequestedAtMillis : System.currentTimeMillis();
			if (resendCount > 0) {
				otpPage.resendOtp();
			}
			long deadline = System.nanoTime() + Duration.ofMillis(OTP_WAIT_MILLIS).toNanos();
			while (System.nanoTime() < deadline) {
				String otp = readFreshOtp(endpoint, requestedAt);
				if (otp != null) {
					return otp;
				}
				Thread.sleep(OTP_POLL_INTERVAL_MILLIS);
			}
		}
		throw new IllegalStateException("No fresh OTP arrived after the configured resend attempts.");
	}

	private String readFreshOtp(String endpoint, long requestedAtMillis) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(endpoint).openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3_000);
			connection.setReadTimeout(3_000);
			if (connection.getResponseCode() != 200) {
				return null;
			}
			String body;
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
				body = reader.lines().collect(Collectors.joining());
			}
			JsonObject json = JsonParser.parseString(body).getAsJsonObject();
			if (!json.has("success") || !json.get("success").getAsBoolean()
					|| !json.has("otp") || !json.has("timestamp")) {
				return null;
			}
			String otp = json.get("otp").getAsString();
			long receivedAt = json.get("timestamp").getAsLong();
			return receivedAt >= requestedAtMillis && otp.matches("[0-9]{6}") ? otp : null;
		} catch (IOException | RuntimeException ignored) {
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private boolean waitForMeonRedirect(WebDriver driver, String configuredRedirectUrl) {
		String expectedHost = URI.create(configuredRedirectUrl).getHost();
		if (!"live.meon.co.in".equalsIgnoreCase(expectedHost)) {
			throw new IllegalArgumentException("The configured DigiLocker callback is not the approved Meon live host.");
		}
		return new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(90)).until(d -> {
			try {
				return expectedHost.equalsIgnoreCase(URI.create(d.getCurrentUrl()).getHost());
			} catch (RuntimeException ignored) {
				return false;
			}
		});
	}

	private void assertApprovedDigiLockerUrl(String value) {
		URI uri = URI.create(value);
		assertTrue("https".equalsIgnoreCase(uri.getScheme()), "DigiLocker URL must use HTTPS.");
	assertTrue(APPROVED_DIGILOCKER_HOSTS.contains(uri.getHost().toLowerCase(java.util.Locale.ROOT)),
				"Unexpected DigiLocker URL host.");
	}

	private void assertHttpsDocumentUrl(Map<String, Object> data, String field) {
		String value = String.valueOf(data.get(field));
		assertTrue(value.startsWith("https://"), "Document field must contain an HTTPS URL: " + field);
	}

	private String requiredEnvironmentValue(String key) {
		String value = System.getenv(key);
		if (value == null || value.trim().isEmpty()) {
			throw new SkipException("Set the required environment variable " + key + " to run the live flow.");
		}
		return value.trim();
	}

	private int configuredMaxResends() {
		String value = System.getenv().getOrDefault("AADHAAR_OTP_MAX_RESENDS", "3").trim();
		try {
			int parsed = Integer.parseInt(value);
			if (parsed < 0 || parsed > 6) {
				throw new IllegalArgumentException("AADHAAR_OTP_MAX_RESENDS must be between 0 and 6.");
			}
			return parsed;
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("AADHAAR_OTP_MAX_RESENDS must be an integer.");
		}
	}
}
