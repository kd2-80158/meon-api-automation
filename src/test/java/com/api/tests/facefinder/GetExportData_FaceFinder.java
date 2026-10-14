package com.api.tests.facefinder;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.facefinder.GenerateTokenFaceFinderRequest;
import com.api.models.request.facefinder.GenerateTokenForExportDataFaceFinderRequest;
import com.api.models.request.facefinder.InitiateCaptureRequestFaceFinderRequest;
import com.api.models.response.facefinder.GenerateTokenFaceFinderResponse;
import com.api.models.response.facefinder.GenerateTokenForExportDataFaceFinderResponse;
import com.api.models.response.facefinder.InitiateRequestFaceFinderResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class GetExportData_FaceFinder extends BaseTest {

	String transaction_id;
	String token;
	Logger logger;
	AuthService authService;
	Response response;
	Gson gson;
	String exportToken;
	RequestSpecification rs;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("facefinder");
		gson = new Gson();
		logger = LoggerUtility.getLogger(this.getClass());
		this.transaction_id = SessionUtility.get("transaction_id");
		this.token = SessionUtility.get("token_ff");
		this.exportToken = SessionUtility.get("exportToken");
		rs = RestAssured.given();
	}

	public String getExportToken() {
		if (this.token == null) {
			String client_id = JSONUtility.getFaceFinder().getClient_id();
			String client_secret = JSONUtility.getFaceFinder().getClient_secret();
			GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_id, client_secret);
			response = authService.generateTokenFaceFinder(request);
			String responseBody = response.asString();
			GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
			this.token = res.getData().getToken();
		}
		if (this.transaction_id == null) {
			boolean check_location = true;
			boolean match_face = true;
			InitiateCaptureRequestFaceFinderRequest request = new InitiateCaptureRequestFaceFinderRequest(
					check_location, match_face);
			response = authService.initiateRequestWithAuth(request, this.token);
			String responseBody = response.asString();
			InitiateRequestFaceFinderResponse res = gson.fromJson(responseBody,
					InitiateRequestFaceFinderResponse.class);
			this.transaction_id = res.getData().getTransaction_id();
		}
		if (this.exportToken == null) {
			String client_id = JSONUtility.getFaceFinder().getClient_id();
			String client_secret = JSONUtility.getFaceFinder().getClient_secret();
			GenerateTokenForExportDataFaceFinderRequest request = new GenerateTokenForExportDataFaceFinderRequest(
					client_id, client_secret, this.transaction_id);
			response = authService.generateTokenForExportDataWithAuth(request, this.token);
			String responseBody = response.asString();
			GenerateTokenForExportDataFaceFinderResponse res = gson.fromJson(responseBody,
					GenerateTokenForExportDataFaceFinderResponse.class);
			this.exportToken = res.getData().getToken();
		}
		return this.exportToken;
	}

	@Test(description = "tc_01 - Verify successful export download with valid token and valid query params.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyResponseWithValidCredentialsForExportData_FaceFinder() {
		if (this.exportToken == null)
			getExportToken();

		response = rs.baseUri(JSONUtility.getFaceFinder().getUrl()).contentType(ContentType.JSON)
				.header("token", this.exportToken).when().get(AuthService.BASE_PATH_FACE_FINDER_EXPORT_DATA);
		logger.info("Response body: " + response.asString());
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertTrue(response.jsonPath().getBoolean("success"));
		softAssert.assertNotNull(response.jsonPath().get("data.image"));
		softAssert.assertNotNull(response.jsonPath().get("data.location"));
		softAssert.assertNotNull(response.jsonPath().get("data.latitude"));
		softAssert.assertNotNull(response.jsonPath().get("data.longitude"));
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Data Exported Successfully");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_02 - Verify 401 Unauthorized when token header is missing..", priority = 2, groups = {
			"smoke", "sanity", "regression" })
	public void verifyResponseWithMissingTokenHeader_FaceFinder() {
		if (this.exportToken == null)
			getExportToken();

		response = rs.baseUri(JSONUtility.getFaceFinder().getUrl()).contentType(ContentType.JSON)
				.when().get(AuthService.BASE_PATH_FACE_FINDER_EXPORT_DATA);
		logger.info("Response body: " + response.asString());
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid token");
		softAssert.assertAll();
	}
}