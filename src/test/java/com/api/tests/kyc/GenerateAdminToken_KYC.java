package com.api.tests.kyc;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.kyc.GenerateAdminTokenKYCRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({com.api.listeners.TestListener.class})
public class GenerateAdminToken_KYC extends BaseTest {
	
	AuthService authService;
	Logger logger;
	Response response;
	RequestSpecification rs;
	
	@BeforeMethod
	public void setup()
	{
		authService = new AuthService("kyc");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}
	
	@Test(description="tc_01 - Verify admin token generated successfully with valid email & password.",priority=1)
	public void verifyResponseWithValidCredentials_KYC()
	{
		GenerateAdminTokenKYCRequest generateAdminTokenKYCRequest = new GenerateAdminTokenKYCRequest(JSONUtility.getKYC().getEmail(), JSONUtility.getKYC().getPassword());
	    response = authService.generateAdminToken(generateAdminTokenKYCRequest);
	    logger.info("Response body: "+response.asPrettyString());
	    boolean isSuccess = response.jsonPath().getBoolean("success");
	    String access_token = response.jsonPath().getString("access_token");
	    SessionUtility.put("access_token", access_token);
	    softAssert.assertTrue(isSuccess,"Something went wrong!!");
	    softAssert.assertEquals(response.getStatusCode(), 200);
	    softAssert.assertNotNull(access_token,"Access token should be generated");
	    softAssert.assertAll();
	}
	
	@Test(description="tc_02 - Verify authentication fails with incorrect password.",priority=2)
	public void verifyResponseWithIncorrectPasswordField_KYC()
	{
		String incorrectPassword = "wrongPassword";
		GenerateAdminTokenKYCRequest generateAdminTokenKYCRequest = new GenerateAdminTokenKYCRequest(JSONUtility.getKYC().getEmail(), incorrectPassword);
	    response = authService.generateAdminToken(generateAdminTokenKYCRequest);
	    logger.info("Response body: "+response.asPrettyString());
	    boolean isSuccess = response.jsonPath().getBoolean("success");
	    String access_token = response.jsonPath().getString("access_token");
	    SessionUtility.put("access_token", access_token);
	    softAssert.assertFalse(isSuccess,"Success should be false");
	    softAssert.assertEquals(response.getStatusCode(), 401);
	    softAssert.assertEquals(response.jsonPath().get("msg"), "UnAuthorised");
	    softAssert.assertAll();
	}
	
	@Test(description="tc_03 - Verify authentication fails with non-existent email.",priority=3)
	public void verifyResponseWithNonExistentEmailField_KYC()
	{
		String invalidEmail = "sam.meon.co.in";
		GenerateAdminTokenKYCRequest generateAdminTokenKYCRequest = new GenerateAdminTokenKYCRequest(invalidEmail, JSONUtility.getKYC().getEmail());
	    response = authService.generateAdminToken(generateAdminTokenKYCRequest);
	    logger.info("Response body: "+response.asPrettyString());
	    boolean isSuccess = response.jsonPath().getBoolean("success");
	    String access_token = response.jsonPath().getString("access_token");
	    SessionUtility.put("access_token", access_token);
	    softAssert.assertFalse(isSuccess,"Success should be false");
	    softAssert.assertEquals(response.getStatusCode(), 401);
	    softAssert.assertEquals(response.jsonPath().get("msg"), "UnAuthorised");
	    softAssert.assertAll();
	}
	
	@Test(description="tc_04 - Verify error when email field missing.",priority=4)
	public void verifyResponseWithMissingEmailField_KYC()
	{
		GenerateAdminTokenKYCRequest generateAdminTokenKYCRequest = new GenerateAdminTokenKYCRequest(JSONUtility.getKYC().getPassword());
	    response = authService.generateAdminToken(generateAdminTokenKYCRequest);
	    logger.info("Response body: "+response.asPrettyString());
	    boolean isSuccess = response.jsonPath().getBoolean("success");
	    String access_token = response.jsonPath().getString("access_token");
	    SessionUtility.put("access_token", access_token);
	    softAssert.assertFalse(isSuccess,"'email' is not available");
	    softAssert.assertEquals(response.getStatusCode(), 400);
	    softAssert.assertEquals(response.jsonPath().get("msg"), "UnAuthorised");
	    softAssert.assertAll();
	}
	
	@Test(description="tc_05 - Verify error when password field missing.",priority=5)
	public void verifyResponseWithMissingPasswordField_KYC()
	{
		GenerateAdminTokenKYCRequest generateAdminTokenKYCRequest = new GenerateAdminTokenKYCRequest(JSONUtility.getKYC().getEmail());
	    response = authService.generateAdminToken(generateAdminTokenKYCRequest);
	    logger.info("Response body: "+response.asPrettyString());
	    boolean isSuccess = response.jsonPath().getBoolean("success");
	    String access_token = response.jsonPath().getString("access_token");
	    SessionUtility.put("access_token", access_token);
	    softAssert.assertFalse(isSuccess,"'password' is not available");
	    softAssert.assertEquals(response.getStatusCode(), 400);
	    softAssert.assertEquals(response.jsonPath().get("msg"), "UnAuthorised");
	    softAssert.assertAll();
	}
	
	@Test(description = "tc_09 - Verify API returns error for malformed JSON body.", priority = 6)
	public void verifyResponseWhenRequestBodyJSONMalformed_KYC() {

		String email = JSONUtility.getKYC().getEmail();
		String password = JSONUtility.getKYC().getPassword();
		String requestBody = "{\n" + "    \"email\": \"" + email + "\",\n" + "    \"password\": \"" + password + "\"";
		Response response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_ADMIN_TOKEN);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response body: "+responseBody);
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml) {
			softAssert.fail(
					"Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
		else {
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		}
		softAssert.assertAll();
	}
	
	@Test(description = "tc_10 - Verify Content-Type validation (non-JSON).", priority = 7)
	public void verifyResponseWhenNonJsonContentType_KYC() {

		String email = JSONUtility.getKYC().getEmail();
		String password = JSONUtility.getKYC().getPassword();
		String requestBody = "{\n" + "    \"email\": \"" + email + "\",\n" + "    \"password\": \"" + password + "\"\n}";
		Response response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("text/plain")
				.body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_ADMIN_TOKEN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml) {
			softAssert.fail(
					"Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
		else {
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		}
		softAssert.assertAll();
	}
	
	@Test(description = "tc_14 - Verify GET/PUT/DELETE methods are not allowed.", priority = 8)
	public void verifyResponseWithWrongHTTPMethod_KYC() {

		String email = JSONUtility.getKYC().getEmail();
		String password = JSONUtility.getKYC().getPassword();
		String requestBody = "{\n" + "    \"email\": \"" + email + "\",\n" + "    \"password\": \"" + password + "\"\n}";
		Response response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.body(requestBody).when()
				.get(AuthService.BASE_PATH_KYC_ADMIN_TOKEN);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response body: "+responseBody);
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml) {
			softAssert.fail(
					"Server returned HTML error page for incorrect HTTP Method. Response body:\n" + responseBody);
		}
		else {
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		}
		softAssert.assertAll();
	}
}
