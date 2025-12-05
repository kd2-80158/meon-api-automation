package com.api.tests.esign;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.esign.GenerateClientTokenEsignRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


@Listeners({com.api.listeners.TestListener.class})
public final class GenerateClientSignatureEsign extends BaseTest {
	
	Response response;
	AuthService authService;
	Logger logger;
	RequestSpecification rs;
	protected String signature;
	
	@BeforeTest
	public void setup()
	{
		authService = new AuthService("eSign");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}
	
	@Test(description = "tc_01 - Verify that the API returns the appropriate response when valid username and password are provided.",priority=1)
	public void verifyResponseWithValidCredentialsEsign()
	{
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(JSONUtility.getEsign().getUsername(), JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		signature = response.jsonPath().get("signature");
		// use boolean getter and provide message
		softAssert.assertTrue(response.jsonPath().getBoolean("status"), "Expected status=true");
		softAssert.assertEquals(response.getStatusCode(), 200, "HTTP status mismatch");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_02 - Verify that the API returns the appropriate response when username is missing.",priority=2)
	public void verifyResponseWithMissingUsernameEsign()
	{
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "Username required", "Message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_03 - Verify that the API returns the appropriate response when password is missing.",priority=3)
	public void verifyResponseWithMissingPasswordEsign()
	{
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(JSONUtility.getEsign().getUsername());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "Password required", "Message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertAll();

	}
	
	@Test(description = "tc_04 - Verify that the API returns the appropriate response when both username and password are empty strings.",priority=4)
	public void verifyResponseWithMissingBothUsernameAndPasswordEsign()
	{
		String emptyUsername = "";
		String emptyPassword = "";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(emptyUsername,emptyPassword);
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "Username required", "Message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_05 - Verify that the API returns the appropriate response when invalid credentials are provided.",priority=5)
	public void verifyResponseWithWrongBothUsernameAndPasswordEsign()
	{
		String emptyUsername = "WrongUser";
		String emptyPassword = "WrongPassword";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(emptyUsername,emptyPassword);
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "User does not exist", "Message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_06 - Verify that the API returns the appropriate response when username contains SQL injection payload.",priority=6)
	public void verifyResponseWithSqlInjectionEsign()
	{
		String username = "demo3mvP'; DROP TABLE auth_user; --";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(username,JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "User does not exist", "Message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		softAssert.assertAll();

	}
	
	@Test(description = "tc_07 - Verify that the API returns the appropriate response when username contains XSS payload.",priority=7)
	public void verifyResponseWithXSSPayloadEsign()
	{
		String username = "<script>alert(1)</script>";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(username,JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "User does not exist", "Message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_08 - Verify that the API returns a JSON error when malformed JSON is submitted.", priority = 8)
	public void verifyResponseWhenRequestBodyJSONMalformedEsign() 
	{
		String username = JSONUtility.getEsign().getUsername();
		String password = JSONUtility.getEsign().getPassword();

		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).contentType("application/json").body(
				"{\n"
				+ "    \"username\": \""+username+"\",\n"
				+ "    \"password\": \""+password+"\"")
				.when().post(AuthService.BASE_PATH_ESIGN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		
		if (isHtml) {
			softAssert.fail("Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_09 - Verify that the API rejects the request when Content-Type header is missing.", priority = 9)
	public void verifyResponseWithMissingContentTypeEsign() 
	{
		String username = JSONUtility.getEsign().getUsername();
		String password = JSONUtility.getEsign().getPassword();

		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).body(
				"{\n"
				+ "    \"username\": \""+username+"\",\n"
				+ "    \"password\": \""+password+"\"")
				.when().post(AuthService.BASE_PATH_ESIGN);
		
		String responseBody = response.body().asPrettyString();
		
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"));
		
		if (isHtml) {
			softAssert.fail("Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().getString("error"), "Invalid or missing Content-Type. Expected application/json", "Error message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertAll();
		}
	
	@Test(description = "tc_10 - Verify that the API returns the appropriate response when numeric values are provided instead of strings.", priority = 10)
	public void verifyResponseWithNumericValuesOfFieldsEsign() {
		Map<String, Object> body = new HashMap<>();
		body.put("username", 12345);
		body.put("password", 45678);
		String rawJson = new Gson().toJson(body);
		response = authService.generateClientTokenEsignWithRawJson(rawJson);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");
		softAssert.assertFalse(status != null && status, "Expected status false");
		softAssert.assertNotNull(msg, "Error message should be present");
		softAssert.assertEquals(msg, "User does not exist.");
		softAssert.assertEquals(httpStatus, 400, "Status should be 400 Bad Request");
		softAssert.assertAll();
	}
	
}
