package com.api.tests.mca;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.mca.GenerateTokenMCARequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class GenerateToken_MCA extends BaseTest {

	AuthService authService;
	Logger logger;
	RequestSpecification rs;
	Response response;

	@BeforeSuite
	public void relaxedHTTP() {
		RestAssured.useRelaxedHTTPSValidation();
	}

	@BeforeMethod
	public void setup() {
		authService = new AuthService("mca");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}

	@Test(description = "tc_01 - Verify token generation with valid static_id", priority = 1, groups = { "e2e", "smoke",
			"sanity", "regression" })
	public void generateTokenWithValidCredentials_MCA() {
		GenerateTokenMCARequest generateTokenMCARequest = new GenerateTokenMCARequest(
				JSONUtility.getMca().getStatic_id());
		response = authService.generateTokenMCA(generateTokenMCARequest);
		System.out.println("Response is: " + response.asPrettyString());
		String access_token = response.jsonPath().getString("access_token");
		int expires_in = response.jsonPath().getInt("expires_in");
		SessionUtility.put("access_token", access_token);
		softAssert.assertEquals(expires_in, 3600);
		softAssert.assertNotNull(access_token, "Access token not found");
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify response structure of token API", priority = 2, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseStructureTokenAPI_MCA() {
		GenerateTokenMCARequest generateTokenMCARequest = new GenerateTokenMCARequest(
				JSONUtility.getMca().getStatic_id());
		response = authService.generateTokenMCA(generateTokenMCARequest);
		System.out.println("Response is: " + response.asPrettyString());
		String access_token = response.jsonPath().getString("access_token");
		int expires_in = response.jsonPath().getInt("expires_in");
		String token_type = response.jsonPath().getString("token_type");
		SessionUtility.put("access_token", access_token);
		softAssert.assertEquals(expires_in, 3600);
		softAssert.assertEquals(token_type, "Bearer");
		softAssert.assertNotNull(access_token, "Access token not found");
		softAssert.assertNotNull(token_type);
		softAssert.assertNotNull(expires_in);
		softAssert.assertAll();

	}

	@Test(description = "tc_06 - Verify API behavior when static_id is missing", priority = 3, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWhenMissingStaticID_MCA() {
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("static_id", null);
		response = rs.baseUri("https://meon.space/mca").relaxedHTTPSValidation().contentType("application/json").when()
				.body(requestBody).post("/verify/token");
		System.out.println("Response is: " + response.asPrettyString());
		String message = response.jsonPath().getString("message");
		int responseCode = response.getStatusCode();
		softAssert.assertEquals(message, "static_id is required");
		softAssert.assertEquals(responseCode, 400);
		softAssert.assertAll();
	}
	
	@Test(description = "tc_07 - Verify API behavior with empty static_id", priority = 4, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWhenEmptyStaticID_MCA() {
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("static_id", "");
		response = rs.baseUri("https://meon.space/mca").relaxedHTTPSValidation().contentType("application/json").when()
				.body(requestBody).post("/verify/token");
//		GenerateTokenMCARequest generateTokenMCARequest = new GenerateTokenMCARequest(JSONUtility.getMca().getStatic_id());
//		response = authService.generateTokenMCA(generateTokenMCARequest);
		System.out.println("Response is: " + response.asPrettyString());
		String message = response.jsonPath().getString("message");
		int responseCode = response.getStatusCode();
		softAssert.assertEquals(message, "static_id is required");
		softAssert.assertEquals(responseCode, 400);
		softAssert.assertAll();
	}
	
	@Test(description = "tc_08 - Verify API behavior with invalid static_id", priority = 5, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWhenInvalidStaticID_MCA() {
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("static_id", "invalid1234");
		response = rs.baseUri("https://meon.space/mca").relaxedHTTPSValidation().contentType("application/json").when()
				.body(requestBody).post("/verify/token");
		System.out.println("Response is: " + response.asPrettyString());
		String message = response.jsonPath().getString("message");
		int responseCode = response.getStatusCode();
		softAssert.assertEquals(message, "Unauthorized");
		softAssert.assertEquals(responseCode, 401);
		softAssert.assertAll();
	}
	
	@Test(description = "tc_09 - Verify API behavior with special characters in static_id", priority = 6, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWithSpecialCharactersStaticID_MCA() {
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("static_id", "@#$%^&*");
		response = rs.baseUri("https://meon.space/mca")
				     .relaxedHTTPSValidation()
				     .contentType("application/json")
				     .when()
				     .body(requestBody)
				     .post("/verify/token");
		System.out.println("Response is: " + response.asPrettyString());
		String message = response.jsonPath().getString("message");
		int responseCode = response.getStatusCode();
		softAssert.assertEquals(message, "static_id must be alphanumeric");
		softAssert.assertEquals(responseCode, 400);
		softAssert.assertAll();
	}
	
	@Test(description="tc_13 - Verify malformed JSON request handling", priority=7, groups= {"e2e","sanity","regression"})
	 public void verifyMalformedJSONValidation_MCA()
	 {
		response = rs.baseUri("https://meon.space/mca")
				     .relaxedHTTPSValidation()
				     .contentType("application/json")
				     .when()
				     .body("{\n"
				     		+ "  \"static_id\": \"jsdfjf4439348934ur93u349r394u9\"")
				     .post("/verify/token");
		System.out.println("Response is: " + response.asPrettyString());
		String message = response.jsonPath().getString("message");
		int responseCode = response.getStatusCode();
		softAssert.assertEquals(message, "Malformed JSON payload");
		softAssert.assertEquals(responseCode, 400);
		softAssert.assertAll();
	 }
	
	@Test(description="tc_14 - Verify content-type header validation", priority=8, groups= {"e2e","sanity","regression"})
	 public void verifyContentTypeValidation_MCA()
	 {
		response = rs.baseUri("https://meon.space/mca")
				     .relaxedHTTPSValidation()
				     .contentType("application/json")
				     .when()
				     .body("{\n"
				     		+ "  \"static_id\": \"jsdfjf4439348934ur93u349r394u9\"")
				     .post("/verify/token");
		System.out.println("Response is: " + response.asPrettyString());
		String message = response.jsonPath().getString("message");
		int responseCode = response.getStatusCode();
		softAssert.assertEquals(message, "Malformed JSON payload");
		softAssert.assertEquals(responseCode, 400);
		softAssert.assertAll();
	 }
	
	

}
