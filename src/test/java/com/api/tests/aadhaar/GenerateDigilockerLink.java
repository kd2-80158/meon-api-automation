package com.api.tests.aadhaar;

import static io.restassured.RestAssured.given;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.aadhaar.GenerateClientTokenRequest;
import com.api.models.request.aadhaar.GenerateDigilockerLinkRequest;
import com.api.models.response.aadhaar.GenerateClientTokenResponse;
import com.api.models.response.aadhaar.GenerateDigilockerLinkResponse;
import com.api.utility.ExtentReporterUtility;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.google.gson.Gson;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public final class GenerateDigilockerLink extends BaseTest {

	AuthService authService;
	Response response;
	Logger logger;
	RequestSpecification rs;
	GenerateClientToken generateClientToken;
	GenerateDigilockerLinkResponse res;
	String clientToken;
	String state;
	WebDriverWait wait;

	@BeforeMethod
	public void setup(ITestContext context) {
		authService = new AuthService("aadhaar");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = given();
		generateClientToken = new GenerateClientToken();
		generateClientToken.clientToken = (String) context.getAttribute("clientToken");
		generateClientToken.state = (String) context.getAttribute("state");
		this.clientToken = SessionUtility.get("clientToken");
		this.state = SessionUtility.get("state");
	}

	public String getClientToken() {
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				JSONUtility.getAadhaar().getSecret_token());
		response = authService.generateClientToken(request);
		clientToken = response.jsonPath().getString("client_token");
		SessionUtility.put("clientToken", this.clientToken);
		return clientToken;
	}

	@Test(description = "tc_01 - Verify API returns success response with mandatory fields only", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "regression", "sanity" })
	public void verifyResponseWithMandatoryFieldsGenerateDigilockerLink(ITestContext context) {

		if (clientToken == null)
			getClientToken();

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(this.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), JSONUtility.getAadhaar().getCompany_name(),
				JSONUtility.getAadhaar().getDocuments());

		response = authService.generateClientToken(req);
		softAssert.assertEquals(response.jsonPath().getString("status"), "success", "Status mismatch");
		softAssert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");

		String digilockerUrl = response.jsonPath().getString("url");
		logger.info("Generated Digilocker URL: " + digilockerUrl);

		softAssert.assertNotNull(digilockerUrl, "Digilocker URL should not be null");
		if (digilockerUrl == null || digilockerUrl.trim().isEmpty()) {
			softAssert.assertAll();
			return;
		}

		WebDriver driver = null;
		try {
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			driver.navigate().to(digilockerUrl);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
			WebElement aadhaar = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='aadhaar_1']")));
			aadhaar.clear();
			aadhaar.sendKeys("431829337118");

			WebElement nextBtn = driver.findElement(By.xpath("//button[text()='Next']"));
			nextBtn.click();
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='otp_button']")));
			try {
				WebElement notFound = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[text()='Not Found']")));
				if (notFound.isDisplayed()) {
					ExtentReporterUtility.getTest().info("Digilocker flow resumed by user").pass("Completed");
					logger.info("User completed Digilocker flow.");
				}
			} catch (TimeoutException e) {
				logger.warn("Not Found header not seen, flow might have proceeded differently.");
			}

		} catch (Exception e) {
			logger.error("WebDriver flow failed: " + e.getMessage());
			softAssert.fail("Digilocker UI flow failed: " + e.getMessage());
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Missing company_name should return error", priority = 2, groups = { "smoke",
			"regression" })
	public void verifyResponseWhenCompanyNameMissingGenerateDigilockerLink() {

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(this.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), JSONUtility.getAadhaar().getDocuments());

		response = authService.generateClientToken(req);
		String responseBody = response.asString();
		if (response.getStatusCode() == 200) {
			res = gson.fromJson(responseBody, GenerateDigilockerLinkResponse.class);
			if (res.getCode() > 0) {
				logger.info("Status code present: " + res.getCode());
				softAssert.assertEquals(res.getCode(), 400);
			} else {
				softAssert.assertEquals(response.getStatusCode(), 400);
			}
		}
		softAssert.assertFalse(res.isSuccess(), "Expected status=false");
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Missing documents should return error", priority = 3, groups = { "sanity",
			"regression" })
	public void verifyResponseWhenDocumentFieldMissingGenerateDigilockerLink() {

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(this.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), JSONUtility.getAadhaar().getCompany_name());
		response = authService.generateClientToken(req);
		String responseBody = response.asString();
		if (response.getStatusCode() == 200) {
			res = gson.fromJson(responseBody, GenerateDigilockerLinkResponse.class);
			if (res.getCode() > 0) {
				logger.info("Status code present: " + res.getCode());
				softAssert.assertEquals(res.getCode(), 400);
			} else {
				softAssert.assertEquals(response.getStatusCode(), 400);
			}
		}
		softAssert.assertFalse(res.isSuccess(), "Expected success=false");
		softAssert.assertEquals(res.getMsq(), "Missing required fields: documents");
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Unsupported document value returns error", priority = 4, groups = { "sanity",
			"regression" })
	public void verifyResponseWhenDocumentFieldContainsUnsupportedValueGenerateDigilockerLink() {

		String[] unsupportedDocs = { "passport" };

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(this.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), JSONUtility.getAadhaar().getCompany_name(),
				unsupportedDocs);

		response = authService.generateClientToken(req);
		String responseBody = response.asString();
		if (response.getStatusCode() == 200) {
			res = gson.fromJson(responseBody, GenerateDigilockerLinkResponse.class);
			if (res.getCode() > 0) {
				logger.info("Status code present: " + res.getCode());
				softAssert.assertEquals(res.getCode(), 400);
			} else {
				softAssert.assertEquals(response.getStatusCode(), 400);
			}
		}
		softAssert.assertFalse(res.isSuccess(), "Expected success=false");
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Malformed JSON should not return HTML", priority = 5, groups = { "sanity",
			"regression" })
	public void verifyResponseWhenRequestBodyJSONMalformedGenerateDigilockerLink() {

		String redirectUrl = JSONUtility.getAadhaar().getRedirect_url();
		String companyName = JSONUtility.getAadhaar().getCompany_name();

		Response response = rs.baseUri(JSONUtility.getAadhaar().getUrl()).contentType("application/json")
				.body("{\n" + " \"client_token\": \"" + generateClientToken.clientToken + "\",\n"
						+ " \"redirect_url\": \"" + redirectUrl + "\",\n" + " \"company_name\": \"" + companyName
						+ "\",\n" + " \"documents\": \"aadhaar\",\n" + " \"pan_name\":\"\",\n" + " \"pan_no\":\"\"\n")
				.when().post(AuthService.BASE_PATH_DIGILOCKER_LINK);

		String body = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (body != null && body.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML instead of JSON:\n" + body);

		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Wrong Content-Type should fail", priority = 6, groups = { "regression" })
	public void verifyResponseWithWrongContentTypeHeaderGenerateDigilockerLink() {

		String redirectUrl = JSONUtility.getAadhaar().getRedirect_url();
		String companyName = JSONUtility.getAadhaar().getCompany_name();
		String documentJson = new Gson().toJson(JSONUtility.getAadhaar().getDocuments());

		Response response = rs.baseUri(JSONUtility.getAadhaar().getUrl()).contentType("text/plain")
				.body("{\n" + " \"client_token\": \"" + generateClientToken.clientToken + "\",\n"
						+ " \"redirect_url\": \"" + redirectUrl + "\",\n" + " \"company_name\": \"" + companyName
						+ "\",\n" + " \"documents\": \"" + documentJson + "\",\n" + " \"pan_name\":\"\",\n"
						+ " \"pan_no\":\"\"\n" + "}")
				.when().post(AuthService.BASE_PATH_DIGILOCKER_LINK);

		String body = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (body != null && body.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML instead of JSON:\n" + body);

		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description = "tc_15 - Empty body should return error", priority = 7, groups = { "regression" })
	public void verifyResponseWhenRequestBodyEmptyGenerateDigilockerLink() {

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest();

		Response response = authService.generateClientToken(req);

		String body = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (body != null && body.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML instead of JSON:\n" + body);

		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description = "tc_22 - Numeric types for string fields should fail", priority = 9, groups = { "regression" })
	public void verifyResponseForNumericTypesGenerateDigilockerLink() {

		Map<String, Object> body = new HashMap<>();
		body.put("company_name", 12345);

		String rawJson = new Gson().toJson(body);

		response = authService.generateClientTokenAadhaarWithRawJson(rawJson);

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(this.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), rawJson, JSONUtility.getAadhaar().getDocuments());

		Response response = authService.generateClientToken(req);

		String respBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (respBody != null && respBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML instead of JSON:\n" + respBody);

		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}
}
