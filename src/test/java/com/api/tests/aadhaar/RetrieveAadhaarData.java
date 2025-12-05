package com.api.tests.aadhaar;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.aadhaar.RetrieveAadhaarDataRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({com.api.listeners.TestListener.class})
public class RetrieveAadhaarData extends BaseTest {

	Response response;
	AuthService authService;
	Logger logger;
	RequestSpecification rs;
	GenerateClientToken generateClientToken;

	@BeforeTest
	public void setup(ITestContext context)
	{
		authService = new AuthService("aadhaar");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
		generateClientToken = new GenerateClientToken();
		generateClientToken.clientToken = (String) context.getAttribute("clientToken");
		generateClientToken.state = (String) context.getAttribute("state");
	}

	@Test(description="tc_01 - Valid client_token/state returns success.", priority=1)
	public void verifyResponseWithValidCredentialsRetrieveAadhaarData()
	{
		RetrieveAadhaarDataRequest request = new RetrieveAadhaarDataRequest(generateClientToken.clientToken, generateClientToken.state);
		response = authService.retrieveData(request);

		softAssert.assertEquals(response.jsonPath().getString("msg"), "Data Exported Successfully", "Message mismatch");
		softAssert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");
		softAssert.assertAll();
	}

	@Test(description="tc_04 - Missing state should return error.", priority=2)
	public void verifyResponseWithMissingStateRetrieveAadhaarData()
	{
		RetrieveAadhaarDataRequest request = new RetrieveAadhaarDataRequest(generateClientToken.clientToken);
		response = authService.retrieveData(request);

		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid Client Token", "Message mismatch");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400, "Status code mismatch");
		softAssert.assertAll();
	}

	@Test(description="tc_06 - Invalid client_token should return error.", priority=3)
	public void verifyResponseWithInvalidClientTokenRetrieveAadhaarData()
	{
		String invalidClientToken = "invalidToken";
		RetrieveAadhaarDataRequest request = new RetrieveAadhaarDataRequest(invalidClientToken, generateClientToken.state);
		response = authService.retrieveData(request);

		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid Client Token", "Message mismatch");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description="tc_07 - Invalid state should return error.", priority=4)
	public void verifyResponseWithInvalidStateRetrieveAadhaarData()
	{
		String invalidState = "invalidState";
		RetrieveAadhaarDataRequest request = new RetrieveAadhaarDataRequest(generateClientToken.clientToken, invalidState);
		response = authService.retrieveData(request);

		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid Client Token");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Malformed JSON should not return HTML.", priority = 5)
	public void verifyResponseWhenRequestBodyJSONMalformedRetrieveAadhaarData()
	{
		String state = JSONUtility.getAadhaar().getState();

		Response response = rs.baseUri(JSONUtility.getAadhaar().getUrl())
				.contentType("application/json")
				.body("{\n"
						+ "    \"client_token\": \"" + generateClientToken.clientToken + "\",\n"
						+ "    \"state\": \"" + state + "\",\n"
						+ "    \"status\": true\n")
				.when().post(AuthService.BASE_PATH_RETRIEVE_DATA);

		String body = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (body != null && body.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML instead of JSON:\n" + body);

		softAssert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Wrong Content-Type should return error.", priority = 6)
	public void verifyResponseWithWrongContentHeaderRetrieveAadhaarData()
	{
		String state = JSONUtility.getAadhaar().getState();

		Response response = rs.baseUri(JSONUtility.getAadhaar().getUrl())
				.contentType("text/plain")
				.body("{\n"
						+ "    \"client_token\": \"" + generateClientToken.clientToken + "\",\n"
						+ "    \"state\": \"" + state + "\",\n"
						+ "    \"status\": true\n"
						+ "}")
				.when().post(AuthService.BASE_PATH_RETRIEVE_DATA);

		String body = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (body != null && body.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml)
			softAssert.fail("Server returned HTML instead of JSON:\n" + body);

		softAssert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");
		softAssert.assertAll();
	}

	@Test(description = "tc_10 - Numeric values should fail with proper error.", priority = 7)
	public void verifyResponseWithNumericValuesOfFieldsRetrieveAadhaarData()
	{
		Map<String, Object> body = new HashMap<>();
		body.put("client_token", 12345);
		body.put("state", 45678);

		String rawJson = new Gson().toJson(body);

		response = authService.retrieveDataAadhaarWithRawJson(rawJson);

		String msg = response.jsonPath().getString("msg");
		Boolean status = response.jsonPath().getBoolean("status");

		softAssert.assertFalse(status != null && status, "Expected success=false");
		softAssert.assertNotNull(msg, "Error message should be present");
		softAssert.assertEquals(msg, "Invalid Client Token");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description="tc_15 - SQL injection attempt should fail.", priority=8)
	public void verifyResponseWithSqlInjectionRetrieveAadhaarData()
	{
		String clientToken = "14E5454; DROP TABLE users;";
		RetrieveAadhaarDataRequest request = new RetrieveAadhaarDataRequest(clientToken, generateClientToken.state);
		response = authService.retrieveData(request);

		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid Client Token");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description="tc_16 - XSS payload should fail safely.", priority=9)
	public void verifyResponseWithXSSPayloadRetrieveAadhaarData()
	{
		String clientToken = "<script>alert(1)</script>";
		RetrieveAadhaarDataRequest request = new RetrieveAadhaarDataRequest(clientToken, generateClientToken.state);
		response = authService.retrieveData(request);

		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid Client Token");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}
}
