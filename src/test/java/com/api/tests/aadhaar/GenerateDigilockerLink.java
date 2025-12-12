package com.api.tests.aadhaar;

import static io.restassured.RestAssured.given;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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
import com.api.models.request.aadhaar.GenerateDigilockerLinkRequest;
import com.api.utility.ExtentReporterUtility;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
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

	@BeforeMethod
	public void setup(ITestContext context) {
		authService = new AuthService("aadhaar");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = given();
		generateClientToken = new GenerateClientToken();
		generateClientToken.clientToken = (String) context.getAttribute("clientToken");
		generateClientToken.state = (String) context.getAttribute("state");
	}

	@Test(description = "Verify API returns success response with mandatory fields only", priority = 1)
	public void verifyResponseWithMandatoryFieldsGenerateDigilockerLink(ITestContext context) {

		// --- 1. API CALL SETUP & EXECUTION ---
		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(generateClientToken.clientToken,
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

		WebDriver driver = null; // Declare driver outside try block
		try {
			if (digilockerUrl != null) {

				// Initialization
				driver = new FirefoxDriver(); // Or use a WebDriver Manager approach
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Set a small implicit wait

				// Digilocker Flow
				driver.navigate().to(digilockerUrl);

				WebElement aadhaar = driver.findElement(By.xpath("//input[@id='aadhaar_1']"));
				aadhaar.clear();
				aadhaar.sendKeys("431829337118");

				WebElement nextBtn = driver.findElement(By.xpath("//button[text()='Next']"));
				nextBtn.click();

				// Wait for the final element (Done button) to appear and be clickable
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
				// Wait until the element is clickable, then store it.
				WebElement doneBtn = wait
						.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Done']")));

				// Optionally click Done if the flow requires it to fully complete
				// doneBtn.click();
			}

			ExtentReporterUtility.getTest().info("Digilocker flow resumed by user")
					.pass("Digilocker flow completed within timeout");
			logger.info("User completed Digilocker flow — continuing execution...");

		} catch (Exception e) {
			logger.error("WebDriver flow failed: " + e.getMessage());
			softAssert.fail("Digilocker UI flow failed: " + e.getMessage());
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}

		// --- 3. FINAL ASSERTION ---
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_03 - Missing company_name should return error", priority = 2)
	public void verifyResponseWhenCompanyNameMissingGenerateDigilockerLink() {

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(generateClientToken.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), JSONUtility.getAadhaar().getDocuments());

		response = authService.generateClientToken(req);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_04 - Missing documents should return error", priority = 3)
	public void verifyResponseWhenDocumentFieldMissingGenerateDigilockerLink() {

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(generateClientToken.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), JSONUtility.getAadhaar().getCompany_name());

		response = authService.generateClientToken(req);

		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Missing required fields: documents");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_06 - Unsupported document value returns error", priority = 4)
	public void verifyResponseWhenDocumentFieldContainsUnsupportedValueGenerateDigilockerLink() {

		String[] unsupportedDocs = { "passport" };

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(generateClientToken.clientToken,
				JSONUtility.getAadhaar().getRedirect_url(), JSONUtility.getAadhaar().getCompany_name(),
				unsupportedDocs);

		response = authService.generateClientToken(req);

		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_08 - Malformed JSON should not return HTML", priority = 5)
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

	// ---------------------------------------------------------------------------
	@Test(description = "tc_09 - Wrong Content-Type should fail", priority = 6)
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

	// ---------------------------------------------------------------------------
	@Test(description = "tc_15 - Empty body should return error", priority = 7)
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

	// ---------------------------------------------------------------------------
	@Test(description = "tc_22 - Numeric types for string fields should fail", priority = 9)
	public void verifyResponseForNumericTypesGenerateDigilockerLink() {

		Map<String, Object> body = new HashMap<>();
		body.put("company_name", 12345);

		String rawJson = new Gson().toJson(body);

		response = authService.generateClientTokenAadhaarWithRawJson(rawJson);

		GenerateDigilockerLinkRequest req = new GenerateDigilockerLinkRequest(generateClientToken.clientToken,
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
