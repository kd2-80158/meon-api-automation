package com.api.tests.aadhaar;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.aadhaar.GenerateClientTokenRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public final class GenerateClientToken extends BaseTest {

	AuthService authService;
	Response response;
	Logger logger;
	RequestSpecification rs;
	String clientToken;
	String state;

	@BeforeMethod
	public void setup() {
		logger = LoggerUtility.getLogger(this.getClass());
		authService = new AuthService("aadhaar");
		rs = given();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_01 - Valid credentials return success", priority = 1)
	public void getTokenWithValidCredentialsAadhaar(ITestContext context) {

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
						JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientToken(request);
		logger.info(response.asPrettyString());

		clientToken = response.jsonPath().getString("client_token");
		state = response.jsonPath().getString("state");

		context.setAttribute("clientToken", clientToken);
		context.setAttribute("state", state);

		softAssert.assertTrue(response.jsonPath().getBoolean("status"), "Expected status=true");
		softAssert.assertEquals(response.getStatusCode(), 200, "HTTP 200 expected");
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_02 - Missing secret_token returns error", priority = 2)
	public void verifyResponseWithMissingSecretTokenAadhaar() {

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name());

		response = authService.generateClientToken(request);

		String msg = response.jsonPath().getString("msg");
		boolean status = response.jsonPath().getBoolean("status");

		softAssert.assertFalse(status, "Expected status=false");
		softAssert.assertEquals(msg, "Missing company_name or secret_token");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_03 - Missing company_name returns error", priority = 3)
	public void verifyResponseWithMissingCompanyNameAadhaar() {

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientToken(request);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Missing company_name or secret_token");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_04 - Missing both fields returns error", priority = 4)
	public void verifyResponseWithMissingBothCompanyNameAndSecretTokenAadhaar() {

		GenerateClientTokenRequest request = new GenerateClientTokenRequest();

		response = authService.generateClientToken(request);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Missing company_name or secret_token");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_05 - Invalid secret_token returns unauthorized", priority = 5)
	public void verifyResponseWithInvalidSecretTokenAadhaar() {

		String invalidToken = "invalid123";

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(), invalidToken);

		response = authService.generateClientToken(request);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"),
				"Invalid Secret Token OR Company Not registered with us.");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_06 - Invalid company_name returns unauthorized", priority = 6)
	public void verifyResponseWithInvalidCompanyAadhaar() {

		String invalidCompany = "otherCompany";

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(invalidCompany, JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientToken(request);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"),
				"Invalid Secret Token OR Company Not registered with us.");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_07 - Wrong Content-Type returns 415", priority = 7)
	public void verifyIncorrectContentHeaderAadhaar() {

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
						JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientTokenWithPlainText(request);

		softAssert.assertEquals(response.getStatusCode(), 415, "Expected 415 Unsupported Media Type");

		String responseBody = response.getBody().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML error page:\n" + responseBody);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_09 - Extremely long company_name returns error", priority = 8)
	public void verifyResponseWithLongCompanyNameAadhaar() {

		String longCompanyName =
				"demogamingdemogamingdemogamingdemogamingdemogamingdemogamingdemogamingdemogaming";

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(longCompanyName, JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientToken(request);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"),
				"Invalid Secret Token OR Company Not registered with us.");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_10 - Extremely long secret_token returns unauthorized", priority = 9)
	public void verifyResponseWithLongSecretTokenAadhaar() {

		String longSecretToken =
				"longsecrettokenlongsecrettokenlongsecrettokenlongsecrettokenlongsecrettokenlongsecrettoken";

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(), longSecretToken);

		response = authService.generateClientToken(request);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"),
				"Invalid Secret Token OR Company Not registered with us.");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_11 - SQL Injection attempt should fail gracefully", priority = 10)
	public void verifyResponseWithSQLInjectionAadhaar() {

		String companyName = "demo gaming; DROP TABLE users;";

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(companyName, JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientToken(request);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"),
				"Invalid Secret Token OR Company Not registered with us.");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_15 - Validate Response Time SLA", priority = 11)
	public void verifyResponseTimeSLAAadhaar() {

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
						JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientToken(request);
		logger.info(response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertTrue(response.jsonPath().getBoolean("status"));

		long time = response.getTime();
		logger.info("Response time: " + time);

		softAssert.assertTrue(time < 2000, "Response time exceeded 2 seconds");
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_17 - Missing request body returns error", priority = 12)
	public void verifyResponeWithNoRequestBodyAadhaar() {
		
		response = rs.baseUri(AuthService.BASE_URI_AADHAAR).contentType("application/json").body("").when().post(AuthService.BASE_PATH);
		softAssert.assertEquals(response.getStatusCode(), 400);

		String responseBody = response.getBody().asPrettyString();
        logger.info("Response body: "+responseBody);
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"));
		if (isHtml)
			softAssert.fail("Server returned HTML error page:\n" + responseBody);
		
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_18 - Numeric values instead of strings return error", priority = 13)
	public void verifyResponseWithNumericValuesOfFieldsAadhaar() {

		Map<String, Object> body = new HashMap<>();
		body.put("company_name", 123);
		body.put("secret_token", 456);

		String rawJson = new Gson().toJson(body);
		response = authService.generateClientTokenAadhaarWithRawJson(rawJson);

		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"),
				"Invalid Secret Token OR Company Not registered with us.");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_23 - GET instead of POST should return 405", priority = 15)
	public void verifyResponseWhenGETMethodInsteadOfPOSTAadhaar() {

		GenerateClientTokenRequest request =
				new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
						JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientTokenWithGetMethod(request);

		softAssert.assertEquals(response.getStatusCode(), 405);

		String responseBody = response.getBody().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML:\n" + responseBody);
		softAssert.assertAll();
	}

	// ---------------------------------------------------------------------------
	@Test(description = "tc_24 - Malformed JSON should return clean error", priority = 16)
	public void verifyResponseWhenRequestBodyJSONMalformedAadhaar() {

		String company_name = JSONUtility.getAadhaar().getCompany_name();
		String secret_token = JSONUtility.getAadhaar().getSecret_token();

		Response response = rs.baseUri(JSONUtility.getAadhaar().getUrl())
				.contentType("application/json")
				.body("{\n"
						+ " \"company_name\": \"" + company_name + "\",\n"
						+ " \"secret_token\": \"" + secret_token + "\"")
				.when().post(AuthService.BASE_PATH);

		String body = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (body != null && body.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML:\n" + body);

		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}
}
