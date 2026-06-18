package com.api.tests.credittool;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.credittool.GenerateTokenCreditToolRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class GenerateToken_CreditTool extends BaseTest {

	AuthService authService;
	Logger logger;
	Response response;
	RequestSpecification rs;
	SessionUtility sessionUtility;
	String token;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("credittool");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}

	@Test(description = "tc_01 - Verify token generation with valid credentials", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity", })
	public void verifyResponseWithValidCredentials() {
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest(
				JSONUtility.getCreditTool().getEmail(), JSONUtility.getCreditTool().getPassword());
		response = authService.generateAdminToken(request);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertTrue(response.jsonPath().getBoolean("success"));
		softAssert.assertNotNull(response.jsonPath().getString("token"));
		this.token = response.jsonPath().getString("token");
		SessionUtility.put("creditToolToken", this.token);
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify login fails with incorrect password", priority = 2, groups = { "e2e", "sanity",
			"regresssion" })
	public void verifyResponseWithIncorrectPassword() {
		String incorrectPass = "incorrect";
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest(
				JSONUtility.getCreditTool().getEmail(), incorrectPass);
		response = authService.generateAdminToken(request);
		String responseBody = response.body().asPrettyString();
		//System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Login unsuccessful");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify login fails with unregistered email", priority = 3, groups = { "e2e", "sanity",
			"regresssion" })
	public void verifyResponseWithUnregisteredEmail() {
		String unregisteredEmail = "sam@gmail.com";
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest(unregisteredEmail,
				JSONUtility.getCreditTool().getPassword());
		response = authService.generateAdminToken(request);
		String responseBody = response.body().asPrettyString();
		//System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Login unsuccessful");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify API behavior when email is missing", priority = 4, groups = { "e2e", "sanity",
			"regresssion" })
	public void verifyResponseWithMissingEmail() {
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest();
		request.setPassword(JSONUtility.getCreditTool().getPassword());
		response = authService.generateAdminToken(request);
		String responseBody = response.body().asPrettyString();
		//System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "email is required");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify API behavior when password is missing", priority = 5, groups = { "e2e",
			"sanity", "regresssion" })
	public void verifyResponseWithMissingPassword() {
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest();
		request.setEmail(JSONUtility.getCreditTool().getEmail());
		response = authService.generateAdminToken(request);
		String responseBody = response.body().asPrettyString();
		if (responseBody != null && responseBody.startsWith("<html")) {
			Assert.fail("Response is in HTML body + " + responseBody);
			return;
		} else {
			System.out.println("Response is: " + responseBody);
			softAssert.assertEquals(response.getStatusCode(), 400);
			softAssert.assertEquals(response.jsonPath().getString("msg"), "password is required");
			softAssert.assertFalse(response.jsonPath().getBoolean("success"));
			softAssert.assertAll();
		}
	}

	@Test(description = "tc_07 - Verify API behavior with empty email and password", priority = 6, groups = { "e2e",
			"sanity", "regresssion" })
	public void verifyResponseWithEmptyEmailAndPassword() {
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest();
		request.setEmail("");
		request.setPassword("");
		response = authService.generateAdminToken(request);
		String responseBody = response.body().asPrettyString();
		//System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "email is required");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Verify email format validation", priority = 7, groups = { "e2e", "sanity",
			"regresssion" })
	public void validateEmailFormat() {
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest();
		request.setEmail("invalidemail");
		request.setPassword(JSONUtility.getCreditTool().getPassword());
		response = authService.generateAdminToken(request);
		String responseBody = response.body().asPrettyString();
		//System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid email format");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_10 - Verify API rejects malformed JSON", priority = 8, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithMalformedJSON() {
		String BASE_URI = "https://central-tool.meon.co.in";
		String body = "{\"email\":\"saurabh.chhimwal123@meon.co.in\", \"password\":\"saurabh@123\"";
		response = rs.relaxedHTTPSValidation().baseUri(BASE_URI).contentType("application/json").body(body).when()
				.post("/api/get_token");

		String responseBody = response.body().asPrettyString();
		//System.out.println("Response is: " + responseBody);

		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid JSON format");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_11 - Verify API behavior with incorrect Content-Type header", priority = 9, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithUnsupportedContentType() {
		String BASE_URI = "https://central-tool.meon.co.in";
		String body = "{\"email\":\"saurabh.chhimwal123@meon.co.in\", \"password\":\"saurabh@123\"/}";
		response = rs.relaxedHTTPSValidation().baseUri(BASE_URI).contentType("text/plain").body(body).when()
				.post("/api/get_token");

		String responseBody = response.body().asPrettyString();
		//System.out.println("Response is: " + responseBody);

		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid JSON format");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}
}
