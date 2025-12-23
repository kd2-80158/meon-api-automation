package com.api.tests.facefinder;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.facefinder.GenerateTokenFaceFinderRequest;
import com.api.models.response.facefinder.GenerateTokenFaceFinderResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class GenerateToken_FaceFinder extends BaseTest {

	AuthService authService;
	Response response;
	Logger logger;
	Gson gson;
	RequestSpecification rs;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("facefinder");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
		rs = RestAssured.given();
	}

	@Test(description = "tc_01 - Verify successful token generation with valid client_id and client_secret.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyResponseWithValidCredentials() {
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_id, client_secret);
		response = authService.generateTokenFaceFinder(request);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		String token_ff = res.getData().getToken();
		SessionUtility.put("token_ff", token_ff);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 200);
		softAssert.assertTrue(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "token generated Successfully");
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify error when client_id is missing.", priority = 2, groups = { "smoke",
			"regression" })
	public void verifyResponseWithMissingClientIdField_FaceFinder() {
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_secret);
		response = authService.generateTokenFaceFinder(request);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Please pass client id and client secret in request");
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify error when client_secret is missing.", priority = 3, groups = { "sanity",
			"regression" })
	public void verifyResponseWithMissingClientSecretField_FaceFinder() {
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_id);
		response = authService.generateTokenFaceFinder(request);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Please pass client id and client secret in request");
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify error when both fields are missing.", priority = 4, groups = { "sanity",
			"regression" })
	public void verifyResponseWithBothMissingFields_FaceFinder() {
		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest();
		response = authService.generateTokenFaceFinder(request);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Please pass client id and client secret in request");
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify error when client_id is invalid.", priority = 5, groups = { "sanity",
			"regression" })
	public void verifyResponseWithInvalidClientIdField_FaceFinder() {
		String client_id = "invalidClientId";
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_id, client_secret);
		response = authService.generateTokenFaceFinder(request);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Invalid credentials");
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify error when client_secret is invalid.", priority = 6, groups = { "regression" })
	public void verifyResponseWithInvalidClientSecretField_FaceFinder() {
		String client_secret = "invalidClientSecret";
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_id, client_secret);
		response = authService.generateTokenFaceFinder(request);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Invalid credentials");
		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify error when Content-Type header is missing.", priority = 7, groups = {
			"regression" })
	public void verifyResponseWithMissingContentType_FaceFinder() {
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		response = rs
				.baseUri(JSONUtility.getFaceFinder().getUrl()).body("{\n" + "    \"client_id\": \"" + client_id
						+ "\",\n" + "    \"client_secret\": \"" + client_secret + "\"\n" + "}")
				.when().post(AuthService.BASE_PATH_FACE_FINDER_GENERATE_TOKEN);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(res.getDetail(),
				"Unsupported media type \"text/plain; charset=ISO-8859-1\" in request.", Severity.CRITICAL, "");
		softAssert.assertEquals(response.getStatusCode(), 415);
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Verify error when JSON body is malformed.", priority = 8, groups = {
			"regression" })
	public void verifyResponseWithMalformedJSON_FaceFinder() {
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		response = rs
				.baseUri(JSONUtility.getFaceFinder().getUrl()).contentType("application/json").body("{\n"
						+ "    \"client_id\": \""+client_id+"\",\n"
						+ "    \"client_secret\": \""+client_secret+"\")")
				.when().post(AuthService.BASE_PATH_FACE_FINDER_GENERATE_TOKEN);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(res.getDetail(),
				"JSON parse error - Expecting ',' delimiter: line 3 column 68 (char 104)", Severity.CRITICAL, "");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}
	
	@Test(description = "tc_10 - Verify error when data types are incorrect (numbers instead of strings).", priority = 9, groups = { "regression" })
	public void verifyResponseWithIncorrectDataType_FaceFinder() {
		Map<String,Object> map = new HashMap<>();
		map.put("client_id", 123);
		map.put("client_secret", 456);
		
		String rawJson = new Gson().toJson(map);
		response = authService.generateTokenFaceFinderWithRawJson(rawJson);
		String responseBody = response.asString();
		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Invalid credentials");
		softAssert.assertAll();
	}
}
