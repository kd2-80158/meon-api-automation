package com.api.tests.esign;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.esign.GenerateClientTokenEsignRequest;
import com.api.models.response.esign.GenerateClientTokenEsignResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public final class GenerateClientSignatureEsign extends BaseTest {

	Response response;
	AuthService authService;
	Logger logger;
	RequestSpecification rs;
	protected String signature;
	GenerateClientTokenEsignResponse res;

	@BeforeTest
	public void setup() {
		authService = new AuthService("eSign");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}

	@Test(description = "tc_01 - Verify that the API returns the appropriate response when valid username and password are provided.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyResponseWithValidCredentialsEsign() {
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(
				JSONUtility.getEsign().getUsername(), JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		signature = response.jsonPath().get("signature");

		softAssert.assertTrue(response.jsonPath().getBoolean("status"), "Expected status=true");
		softAssert.assertEquals(response.getStatusCode(), 200, "HTTP status mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify that the API returns the appropriate response when username is missing.", priority = 2, groups = {
			"smoke", "regression" })
	public void verifyResponseWithMissingUsernameEsign() {
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(
				JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateClientTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().get("error"), "Username required", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify that the API returns the appropriate response when password is missing.", priority = 3, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingPasswordEsign() {
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(
				JSONUtility.getEsign().getUsername(), "");
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
			logger.info("Error message is: " + response.jsonPath().get("error"));
			System.out.println("Error message is: " + response.jsonPath().get("error"));
		} else {
			res = gson.fromJson(responseBody, GenerateClientTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());

			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().get("error"), "Password required", "Message mismatch");
		softAssert.assertAll();

	}

	@Test(description = "tc_04 - Verify that the API returns the appropriate response when both username and password are empty strings.", priority = 4, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingBothUsernameAndPasswordEsign() {
		String emptyUsername = "";
		String emptyPassword = "";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(
				emptyUsername, emptyPassword);
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		String responseBody = response.asString();
		if (response.getStatusCode() == 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateClientTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().get("error"), "Username required", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify that the API returns the appropriate response when invalid credentials are provided.", priority = 5, groups = {
			"regression" })
	public void verifyResponseWithWrongBothUsernameAndPasswordEsign() {
		String emptyUsername = "WrongUser";
		String emptyPassword = "WrongPassword";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(
				emptyUsername, emptyPassword);
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		String responseBody = response.asString();
		if (response.getStatusCode() == 401) {
			softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateClientTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 401);
		}
		softAssert.assertFalse(response.jsonPath().get("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().get("error"), "User does not exist.", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify that the API returns the appropriate response when username contains SQL injection payload.", priority = 6, groups = {
			"regression" })
	public void verifyResponseWithSqlInjectionEsign() {
		String username = "demo3mvP'; DROP TABLE auth_user; --";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(username,
				JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		String responseBody = response.asString();
		if (response.getStatusCode() == 401) {
			softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateClientTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 401);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().get("error"), "User does not exist.", "Message mismatch");
		softAssert.assertAll();

	}

	@Test(description = "tc_07 - Verify that the API returns the appropriate response when username contains XSS payload.", priority = 7, groups = {
			"regression" })
	public void verifyResponseWithXSSPayloadEsign() {
		String username = "<script>alert(1)</script>";
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(username,
				JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		logger.info(response.asPrettyString());
		String responseBody = response.asString();
		if (response.getStatusCode() == 401) {
			softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateClientTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 401);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected status=false");
		softAssert.assertEquals(response.jsonPath().get("error"), "User does not exist.", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Verify that the API returns a JSON error when malformed JSON is submitted.", priority = 8)
	public void verifyResponseWhenRequestBodyJSONMalformedEsign() {
		String username = JSONUtility.getEsign().getUsername();
		String password = JSONUtility.getEsign().getPassword();

		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).contentType("application/json")
				.body("{\n" + "    \"username\": \"" + username + "\",\n" + "    \"password\": \"" + password + "\"")
				.when().post(AuthService.BASE_PATH_ESIGN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");

		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));

		if (isHtml) {
			softAssert.fail(
					"Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("status"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("error"), "Malformed JSON");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Verify that the API rejects the request when Content-Type header is missing.", priority = 9, groups = {
			"sanity","regression" })
	public void verifyResponseWithMissingContentTypeEsign() throws Exception {

		String username = JSONUtility.getEsign().getUsername();
		String password = JSONUtility.getEsign().getPassword();

		String payload = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(JSONUtility.getEsign().getUrl() + AuthService.BASE_PATH_ESIGN);
		post.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));

		CloseableHttpResponse response = client.execute(post);

		int statusCode = response.getStatusLine().getStatusCode();
		String responseBody = EntityUtils.toString(response.getEntity());
		logger.info("HTTP Status: " + statusCode);
		logger.info("Response Body: " + responseBody);

		JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
		softAssert.assertEquals(statusCode, 400);
		softAssert.assertFalse(json.get("status").getAsBoolean(), "Expected status=false");
		softAssert.assertEquals(json.get("error").getAsString(),
				"Invalid or missing Content-Type. Expected application/json", "Error message mismatch");
		softAssert.assertAll();
		response.close();
		client.close();
	}

	@Test(description = "tc_10 - Verify that the API returns the appropriate response when numeric values are provided instead of strings.", priority = 10, groups = {
			"sanity","regression" })
	public void verifyResponseWithNumericValuesOfFieldsEsign() {
		Map<String, Object> body = new HashMap<>();
		body.put("username", 12345);
		body.put("password", 45678);
		String rawJson = new Gson().toJson(body);
		response = authService.generateClientTokenEsignWithRawJson(rawJson);
		int httpStatus = response.getStatusCode();
		String msg = response.jsonPath().getString("error");
		Boolean status = response.jsonPath().getBoolean("status");
		softAssert.assertFalse(status != null && status, "Expected status false");
		softAssert.assertNotNull(msg, "Error message should be present");
		softAssert.assertEquals(msg, "User does not exist.");
		softAssert.assertEquals(httpStatus, 401, "Status should be 401 Unauthorized");
		softAssert.assertAll();
	}

}
