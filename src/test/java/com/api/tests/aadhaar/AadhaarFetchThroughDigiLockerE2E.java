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
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.api.models.request.aadhaar.GenerateClientTokenRequest;
import com.api.models.request.aadhaar.GenerateDigilockerLinkRequest;
import com.api.models.request.aadhaar.RetrieveAadhaarDataRequest;
import com.api.pages.aadhaar.AadhaarConsentPage;
import com.api.pages.aadhaar.AadhaarNumberPage;
import com.api.pages.aadhaar.AadhaarOtpPage;
import com.api.pages.aadhaar.AadhaarSecurityPinPage;
import com.api.utility.AadhaarEvidenceCapture;
import com.api.utility.LoggerUtility;
import com.api.utility.JSONUtility;
import com.api.pojos.BaseURL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public final class AadhaarFetchThroughDigiLockerE2E {

	private enum ConsentTransition { CONSENT_VISIBLE, CALLBACK }

	private static final Set<String> APPROVED_DIGILOCKER_HOSTS = Set.of(
			"digilocker.meon.co.in", "accounts.digitallocker.gov.in");
	private static final String AADHAAR_API_BASE_URL = "https://digilocker.meon.co.in";
	private static final String OTP_ENDPOINT = "https://api.extora.store/webhook/otp";
	private static final long OTP_WAIT_MILLIS = Duration.ofMinutes(2).toMillis();
	private static final long OTP_POLL_INTERVAL_MILLIS = 5_000L;
	private static final List<String> REQUIRED_AADHAAR_FIELDS = Arrays.asList(
			"aadhar_address", "aadhar_filename", "aadhar_img_filename", "aadhar_no", "aadhar_xml", "adharimg",
			"country", "date_time", "dist", "dob", "fathername", "gender", "house", "locality", "name",
			"pincode", "state", "transaction_id", "vtc");

	private final Logger logger = LoggerUtility.getLogger(getClass());

	@Test(description = "Complete DigiLocker Aadhaar consent and validate exported data", groups = { "aadhaar-live", "e2e" }, timeOut = 1_200_000L)
	public void completeDigiLockerAadhaarFetch() throws InterruptedException {
		BaseURL aadhaarConfig = JSONUtility.getAadhaar();
		if (!aadhaarConfig.isLive_test_enabled()) {
			throw new SkipException("Enable live_test_enabled in config/config.json to run the live DigiLocker flow.");
		}
		String aadhaarNumber = requiredConfigValue(aadhaarConfig.getAadhaar_number(), "aadhaar_number").replaceAll("\\D", "");
		String securityPin = requiredConfigValue(aadhaarConfig.getSecurity_pin(), "security_pin");
		String companyName = requiredConfigValue(aadhaarConfig.getCompany_name(), "company_name");
		String secretToken = requiredConfigValue(aadhaarConfig.getSecret_token(), "secret_token");
		String redirectUrl = requiredConfigValue(aadhaarConfig.getRedirect_url(), "redirect_url");
		String otpReadToken = requiredConfigValue(aadhaarConfig.getOtp_read_token(), "otp_read_token");
		String otpEndpoint = requiredConfigValue(aadhaarConfig.getOtp_endpoint(), "otp_endpoint");
		assertTrue(aadhaarNumber.matches("[0-9]{12}"), "AADHAAR_NUMBER must contain exactly 12 digits.");
		assertTrue(securityPin.matches("[0-9]{6}"), "DIGILOCKER_SECURITY_PIN must contain exactly 6 digits.");
		int maxResends = configuredMaxResends(aadhaarConfig);

		logger.info("[DigiLocker] Step 1/5: requesting client token; endpointHost={}, companyConfigured={}",
				URI.create(AADHAAR_API_BASE_URL).getHost(), !companyName.isBlank());
		long tokenStarted = System.nanoTime();
		Response tokenResponse = RestAssured.given().baseUri(AADHAAR_API_BASE_URL).relaxedHTTPSValidation()
				.contentType(ContentType.JSON).body(new GenerateClientTokenRequest(companyName, secretToken))
				.post("/get_access_token");
		logger.info("[DigiLocker] Client-token response: status={}, elapsedMs={}, contentType={}, body={}",
				tokenResponse.statusCode(), Duration.ofNanos(System.nanoTime()-tokenStarted).toMillis(),
				tokenResponse.contentType(), redactedBody(tokenResponse.asString()));
		assertEquals(tokenResponse.getStatusCode(), 200, "Client-token API should return HTTP 200; status="
				+ tokenResponse.statusCode() + ", body=" + redactedBody(tokenResponse.asString()));
		assertTrue(Boolean.TRUE.equals(tokenResponse.jsonPath().getBoolean("status")), "Client-token API should succeed.");
		String clientToken = tokenResponse.jsonPath().getString("client_token");
		String state = tokenResponse.jsonPath().getString("state");
		assertNotNull(clientToken, "Client-token API did not return a client token.");
		assertNotNull(state, "Client-token API did not return state.");
		assertTrue(!clientToken.trim().isEmpty() && !state.trim().isEmpty(), "Client-token API returned empty credentials.");

		logger.info("[DigiLocker] Client-token API passed; state and token received (values redacted)");
		logger.info("[DigiLocker] Step 2/5: requesting consent URL");
		GenerateDigilockerLinkRequest linkRequest = new GenerateDigilockerLinkRequest(clientToken,
				redirectUrl, companyName, new String[] { "aadhaar" });
		Response linkResponse = RestAssured.given().baseUri(AADHAAR_API_BASE_URL).relaxedHTTPSValidation()
				.contentType(ContentType.JSON).body(linkRequest).post("/digi_url");
		assertEquals(linkResponse.getStatusCode(), 200, "DigiLocker-link API should return HTTP 200.");
		assertTrue(Boolean.TRUE.equals(linkResponse.jsonPath().getBoolean("success")), "DigiLocker-link API should succeed.");
		String digilockerUrl = linkResponse.jsonPath().getString("url");
		assertNotNull(digilockerUrl, "DigiLocker-link API did not return a URL.");
		assertTrue(!digilockerUrl.trim().isEmpty(), "DigiLocker-link API returned an empty URL.");
		assertApprovedDigiLockerUrl(digilockerUrl);
		logger.info("[DigiLocker] Consent URL API passed; approved host={}", URI.create(digilockerUrl).getHost());

		WebDriver driver = null;
		String uiPhase = "starting Firefox";
		try {
			FirefoxOptions browserOptions = new FirefoxOptions();
			boolean headless = "headless".equalsIgnoreCase(System.getProperty("digilocker.browser.mode", "headless"));
			if (headless) browserOptions.addArguments("-headless");
			logger.info("[DigiLocker] Browser mode: {}", headless ? "headless" : "headed");
			driver = new FirefoxDriver(browserOptions);
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
			driver.manage().window().maximize();

			uiPhase = "opening the generated DigiLocker link";
			logger.info("[DigiLocker] Step 3/5: opening identity journey; host={}", URI.create(digilockerUrl).getHost());
			AadhaarNumberPage aadhaarPage = new AadhaarNumberPage(driver);
			aadhaarPage.open(digilockerUrl);
			logBrowserCheckpoint(driver, "aadhaar-entry-page");
			long initialOtpRequestAt = System.currentTimeMillis();
			uiPhase = "submitting Aadhaar and waiting for OTP page";
			aadhaarPage.enterNumberAndContinue(aadhaarNumber);

			AadhaarOtpPage otpPage = new AadhaarOtpPage(driver);
			otpPage.waitUntilLoaded();
			logger.info("[DigiLocker] Aadhaar submitted; OTP page loaded; visibleOtpFields={}", otpPage.visibleOtpFieldCount());
			logBrowserCheckpoint(driver, "otp-entry-page");
			uiPhase = "waiting for a fresh OTP";
			logger.info("[DigiLocker] Step 4/5: polling OTP relay; maxResends={}", maxResends);
			String otp = waitForOtpAndResendIfNeeded(otpPage, otpEndpoint, otpReadToken, initialOtpRequestAt, maxResends);
			uiPhase = "submitting OTP";
			logger.info("[DigiLocker] OTP read successfully; entering into {} visible field(s); value redacted", otpPage.visibleOtpFieldCount());
			otpPage.enterAndVerify(otp);
			logger.info("[DigiLocker] OTP entered and verify control clicked");

			uiPhase = "waiting for security PIN page";
			AadhaarSecurityPinPage pinPage = new AadhaarSecurityPinPage(driver);
			pinPage.waitUntilLoaded();
			logger.info("[DigiLocker] Security PIN page loaded");
			logBrowserCheckpoint(driver, "security-pin-page");
			uiPhase = "submitting security PIN";
			pinPage.enterAndContinue(securityPin);

			uiPhase = "waiting for consent or Meon callback";
			logger.info("[DigiLocker] OTP accepted and PIN submitted; awaiting consent/callback");
			ConsentTransition consentTransition = waitForConsentOrCallback(driver, redirectUrl);
			logger.info("[DigiLocker] Post-PIN transition detected: {}", consentTransition);
			logBrowserCheckpoint(driver, consentTransition == ConsentTransition.CONSENT_VISIBLE ? "consent-page" : "callback-page");
			if (consentTransition == ConsentTransition.CONSENT_VISIBLE) {
				logger.info("[DigiLocker] Consent screen detected; selecting Aadhaar only");
				uiPhase = "selecting Aadhaar consent";
				new AadhaarConsentPage(driver).selectAadhaarCardAndAllow();
				logger.info("[DigiLocker] Aadhaar consent selected and Allow clicked");
			} else {
				logger.info("DigiLocker returned to the configured Meon host before a consent control was detected; validating exported data next.");
			}
			uiPhase = "waiting for Meon callback";
			assertTrue(waitForMeonRedirect(driver, redirectUrl),
					"DigiLocker did not return to the configured Meon callback.");
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			Path evidence = AadhaarEvidenceCapture.capture(driver, uiPhase);
			logger.warn("DigiLocker UI flow interrupted at {}; redacted evidence={}", uiPhase, evidence);
			throw new IllegalStateException("DigiLocker browser flow was interrupted at " + uiPhase
					+ "; redacted evidence=" + evidence + "; sensitive page details were suppressed.");
		} catch (RuntimeException exception) {
			Path evidence = AadhaarEvidenceCapture.capture(driver, uiPhase);
			String exceptionType = exception.getClass().getSimpleName();
			logger.error("DigiLocker UI flow failed at {}; exceptionType={}; message={}; redacted evidence={}",
					uiPhase, exceptionType, safeError(exception.getMessage()), evidence, exception);
			throw new IllegalStateException("DigiLocker browser flow failed at " + uiPhase
					+ "; exceptionType=" + exceptionType + "; redacted evidence=" + evidence
					+ "; sensitive page details were suppressed.");
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}

		Response dataResponse = RestAssured.given().baseUri(AADHAAR_API_BASE_URL).relaxedHTTPSValidation()
				.contentType(ContentType.JSON)
				.body(new RetrieveAadhaarDataRequest(clientToken, state, true))
				.post("/v2/send_entire_data");
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

	private String waitForOtpAndResendIfNeeded(AadhaarOtpPage otpPage, String endpoint, String readToken,
			long initialRequestedAtMillis, int maxResends)
			throws InterruptedException {
		for (int resendCount = 0; resendCount <= maxResends; resendCount++) {
			logger.info("[DigiLocker] OTP wait cycle {}/{}", resendCount + 1, maxResends + 1);
			long requestedAt = resendCount == 0 ? initialRequestedAtMillis : System.currentTimeMillis();
			if (resendCount > 0) {
				logger.warn("[DigiLocker] OTP timeout; clicking resend attempt {} of {}", resendCount, maxResends);
				otpPage.resendOtp();
				logger.info("[DigiLocker] Resend clicked; polling for OTP newer than request timestamp");
			}
			long deadline = System.nanoTime() + Duration.ofMillis(OTP_WAIT_MILLIS).toNanos();
			while (System.nanoTime() < deadline) {
				String otp = readFreshOtp(endpoint, readToken, requestedAt);
				if (otp != null) {
					return otp;
				}
				Thread.sleep(OTP_POLL_INTERVAL_MILLIS);
			}
		}
		throw new IllegalStateException("No fresh OTP arrived after the configured resend attempts. Verify the phone webhook was accepted by POST /webhook/otp (HTTP 202) and uses the configured write credential.");
	}

	private String readFreshOtp(String endpoint, String readToken, long requestedAtMillis) {
		HttpURLConnection connection = null;
		try {
			String separator = endpoint.contains("?") ? "&" : "?";
			connection = (HttpURLConnection) new URL(endpoint + separator + "after=" + requestedAtMillis).openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer " + readToken);
			connection.setConnectTimeout(3_000);
			connection.setReadTimeout(3_000);
			if (connection.getResponseCode() != 200) {
				logger.warn("[DigiLocker] OTP relay returned HTTP {}; retrying poll", connection.getResponseCode());
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
			boolean fresh = receivedAt >= requestedAtMillis && otp.matches("[0-9]{6}");
			if (fresh) logger.info("[DigiLocker] Fresh six-digit OTP received (value redacted); receivedAt={}, requestedAt={}", receivedAt, requestedAtMillis);
			else logger.debug("[DigiLocker] OTP response not usable; success={}, hasOtp={}, fresh={}, sixDigits={}",
					json.has("success") && json.get("success").getAsBoolean(), json.has("otp"), receivedAt >= requestedAtMillis, otp.matches("[0-9]{6}"));
			return fresh ? otp : null;
		} catch (IOException | RuntimeException exception) {
			logger.warn("[DigiLocker] OTP poll failed: type={}, message={}", exception.getClass().getSimpleName(), safeError(exception.getMessage()));
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private void logBrowserCheckpoint(WebDriver driver, String phase) {
		String host = "unavailable";
		try { host = URI.create(driver.getCurrentUrl()).getHost(); } catch (RuntimeException ignored) { }
		Path evidence = AadhaarEvidenceCapture.capture(driver, phase);
		logger.info("[DigiLocker] Browser checkpoint: phase={}, host={}, redactedEvidence={}", phase, host, evidence);
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

	/** Wait for the consent option, or recognize an earlier callback as a terminal UI transition. */
	private ConsentTransition waitForConsentOrCallback(WebDriver driver, String configuredRedirectUrl) {
		String expectedHost = URI.create(configuredRedirectUrl).getHost();
		return new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(90)).until(d -> {
			try {
				if (expectedHost.equalsIgnoreCase(URI.create(d.getCurrentUrl()).getHost())) return ConsentTransition.CALLBACK;
			} catch (RuntimeException ignored) {
				// Keep polling while the browser changes URL.
			}
			if (new AadhaarConsentPage(d).isAadhaarCardOptionVisible()) return ConsentTransition.CONSENT_VISIBLE;
			return null;
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

	private String redactedBody(String body) {
		if (body == null) return "[empty]";
		String redacted = body.replaceAll("(?i)(\"(?:client_token|secret_token|token|state|otp|aadhaar_number|aadhaar|url)\"\\s*:\\s*\")[^\"]*", "$1[REDACTED]");
		return redacted.substring(0, Math.min(redacted.length(), 1200));
	}

	private String safeError(String message) {
		if (message == null) return "[no message]";
		return message.replaceAll("\\b[0-9]{12}\\b", "[AADHAAR_REDACTED]")
				.replaceAll("(?i)(otp|token|pin)[=: ]+\\S+", "$1=[REDACTED]")
				.substring(0, Math.min(message.length(), 500));
	}

	private String requiredConfigValue(String value, String key) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalStateException("Missing required Aadhaar setting in config/config.json: " + key);
		}
		return value.trim();
	}

	private int configuredMaxResends(BaseURL aadhaarConfig) {
		String value = aadhaarConfig.getOtp_max_resends() == null ? "3" : aadhaarConfig.getOtp_max_resends().toString();
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
