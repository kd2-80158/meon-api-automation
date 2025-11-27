package com.api.tests.aadhaar;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.models.request.GenerateClientTokenRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.response.Response;

@Listeners({ com.api.listeners.TestListener.class })
public class GenerateClientToken {

	AuthService authService;
	Response response;
	Logger logger;

	@BeforeMethod
	public void setup() {
		logger = LoggerUtility.getLogger(this.getClass());
		authService = new AuthService();
	}

	@Test(description = "tc_01 - Verify that the API returns the appropriate response when valid credentials are provided.", priority = 1)
	public void getTokenWithValidCredentials() {

		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				JSONUtility.getAadhaar().getSecret_token());
		response = authService.generateClientToken(request);
		logger.info(response.asPrettyString());
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(response.jsonPath().get("status"), true);
	}

	@Test(description = "tc_02 - Verify that the API returns the appropriate response when the secret_token field is missing.", priority = 2)
	public void verifyResponseWithMissingSecretToken() {
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name());
		response = authService.generateClientToken(request);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Missing company_name or secret_token");
		Assert.assertEquals(httpStatus, 400);
//	    }
	}

	@Test(description = "tc_03 - Verify that the API returns the appropriate response when the company_name field is missing.", priority = 3)
	public void verifyResponseWithMissingCompanyName() {
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getSecret_token());
		response = authService.generateClientToken(request);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Missing company_name or secret_token");
		Assert.assertEquals(httpStatus, 400);
	}

	@Test(description = "tc_04 - Verify that the API returns the appropriate response when both fields are empty strings.", priority = 4)
	public void verifyResponseWithMissingBothCompanyNameAndSecretToken() {
		GenerateClientTokenRequest request = new GenerateClientTokenRequest();
		response = authService.generateClientToken(request);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Missing company_name or secret_token");
		Assert.assertEquals(httpStatus, 400);
	}

	@Test(description = "tc_05 - Verify that the API returns the appropriate response when an invalid secret_token is provided.", priority = 5)
	public void verifyResponseWithInvalidSecretToken() {
		String invalidToken = "invalid123";
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				invalidToken);
		response = authService.generateClientToken(request);
		logger.info(response.asPrettyString());
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Invalid Secret Token OR Company Not registered with us.");
		Assert.assertEquals(httpStatus, 401);
	}

	@Test(description = "tc_06 - Verify that the API returns the appropriate response when company_name does not match secret_token.", priority = 6)
	public void verifyResponseWithInvalidCompany() {
		String invalidCompany = "otherCompany";
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(invalidCompany,
				JSONUtility.getAadhaar().getSecret_token());
		response = authService.generateClientToken(request);
		logger.info(response.asPrettyString());
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Invalid Secret Token OR Company Not registered with us.");
		Assert.assertEquals(httpStatus, 401);
	}

	@Test(description = "tc_07 - Verify that the API returns the appropriate response when Content-Type header is incorrect.", priority = 7)
	public void verifyIncorrectContentHeader() {
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientTokenWithPlainText(request);

		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 415, "Expected 415 Unsupported Media Type");
		String responseBody = response.getBody().asPrettyString();

		String contentType = response.getHeader("Content-Type");
		// check if it is html
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype html")
				|| contentType != null && contentType.contains("html");
		if (isHtml) {
			Assert.fail("Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
	}

	@Test(description = "tc_09 - Verify that the API returns the appropriate response when company_name contains extremely long values.", priority = 8)
	public void verifyResponseWithLongCompanyName() {
		String longCompanyName = "demogamingdemogamingdemogamingdemogamingdemogamingdemogamingdemogamingdemogaming";
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(longCompanyName,
				JSONUtility.getAadhaar().getSecret_token());
		response = authService.generateClientToken(request);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Invalid Secret Token OR Company Not registered with us.");
		Assert.assertEquals(httpStatus, 400);
	}

	@Test(description = "tc_10 - Verify that the API returns the appropriate response when secret_token contains extremely long values.", priority = 9)
	public void verifyResponseWithLongSecretToken() {
		String longSecretToken = "longsecrettokenlongsecrettokenlongsecrettokenlongsecrettokenlongsecrettokenlongsecrettokenlongsecrettokenlongsecrettoken";
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				longSecretToken);
		response = authService.generateClientToken(request);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Invalid Secret Token OR Company Not registered with us.");
		Assert.assertEquals(httpStatus, 401);
	}

	@Test(description = "tc_11 - Verify that the API returns the appropriate response when SQL injection patterns are used.", priority = 10)
	public void verifyResponseWithSQLInjection() {
		String companyName = "demo gaming; DROP TABLE users;";
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(companyName,
				JSONUtility.getAadhaar().getSecret_token());
		response = authService.generateClientToken(request);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Invalid Secret Token OR Company Not registered with us.");
		Assert.assertEquals(httpStatus, 401);
	}

	@Test(description = "tc_15 - Verify that the API returns the appropriate response based on response time SLA.", priority = 11)
	public void verifyResponseTimeSLA() {

		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				JSONUtility.getAadhaar().getSecret_token());
		response = authService.generateClientToken(request);
		logger.info(response.asPrettyString());
		Assert.assertEquals(response.statusCode(), 200);
		Assert.assertEquals(response.jsonPath().get("status"), true);
		logger.info("Response time: " + response.getTime());
		Assert.assertTrue(response.getTime() < 2000,
				"Response time is greater than the threshhold limit(i..e,500 ms to 2 sec)");
	}

	@Test(description = "tc_17 - Verify that the API returns the appropriate response when request body is missing.", priority = 12)
	public void verifyResponeWithNoRequestBody() {
		response = authService.generateClientTokenWithNoBody();

		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 400, "Expected 400 Bad Request");
		String responseBody = response.getBody().asPrettyString();
		// check if it is html
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype html");
		if (isHtml) {
			Assert.fail("Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
	}

	@Test(description = "tc_18 - Verify that the API returns the appropriate response when numeric values are sent instead of strings.", priority = 13)
	public void verifyResponseWithNumericValuesOfFields() {
		Map<String, Object> body = new HashMap<>();
		body.put("company_name", 123);
		body.put("secret_token", 456);
		String rawJson = new Gson().toJson(body);
		response = authService.generateClientTokenWithRawJson(rawJson);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		Assert.assertFalse(status != null && status, "Expected status false");
		Assert.assertNotNull(msg, "Error message should be present");
		Assert.assertEquals(msg, "Invalid Secret Token OR Company Not registered with us.");
		Assert.assertEquals(httpStatus, 400, "Status should be 400 Bad Request");
	}

//	@Test(description = "tc_20 - Verify that the API returns the appropriate response when HTTP is used instead of HTTPS.", priority = 14)
//	public void verifyResponseWhenHTTPInsteadOfHTTPS() {
//		Map<String, Object> payload = new HashMap<>();
//		payload.put("company_name", "demogaming");
//		payload.put("secret_token", "Ypw9dZR8EdJXNXGDOj6TcOrxhKmMZf3Y");
//		Response response = given().baseUri("https://digilocker.meon.co.in")
//				.contentType("application/json").body(payload).when().get("/get_access_token");
//		int httpStatus = response.getStatusCode();
//
//			String responseBody = response.getBody().asPrettyString();
//			Assert.assertEquals(httpStatus, 405,
//					"Expected 405 Method Not Allowed (server currently returns " + httpStatus + ")");
//			boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype html");
//			if (isHtml) {
//				Assert.fail(
//						"Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
//			}
//		}

	@Test(description = "tc_23 - Verify API returns correct error when HTTP method GET is used instead of POST", priority = 15)
	public void verifyResponseWhenGETMethodInsteadOfPOST() {
		GenerateClientTokenRequest request = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				JSONUtility.getAadhaar().getSecret_token());

		response = authService.generateClientTokenWithGetMethod(request);

		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 405, "Expected 405 Method Not Allowed");
		String responseBody = response.getBody().asPrettyString();

		String contentType = response.getHeader("Content-Type");
		// check if it is html
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype html")
				|| contentType != null && contentType.contains("html");
		if (isHtml) {
			Assert.fail("Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
	}
}
