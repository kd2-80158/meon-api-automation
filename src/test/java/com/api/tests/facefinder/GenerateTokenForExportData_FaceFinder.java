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

import io.restassured.response.Response;

@Listeners({ com.api.listeners.TestListener.class })
public class GenerateTokenForExportData_FaceFinder extends BaseTest {

	String transaction_id;
	String token;
	Logger logger;
	AuthService authService;
	Response response;
	Gson gson;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("facefinder");
		gson = new Gson();
		logger = LoggerUtility.getLogger(this.getClass());
		this.transaction_id = SessionUtility.get("transaction_id");
		this.token = SessionUtility.get("token_ff");
	}

	public String getTransactionId() {
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
		return this.transaction_id;
	}

	@Test(description = "tc_01 - Verify successful token generation with valid client_id, client_secret and transaction_id.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyResponseWithValidCredentialsForExportData_FaceFinder() {
		if (transaction_id == null)
			getTransactionId();
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		GenerateTokenForExportDataFaceFinderRequest request = new GenerateTokenForExportDataFaceFinderRequest(client_id,
				client_secret, this.transaction_id);
		response = authService.generateTokenForExportDataWithAuth(request, this.token);
		String responseBody = response.asString();
		GenerateTokenForExportDataFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenForExportDataFaceFinderResponse.class);
		String token1 = res.getData().getToken();
		SessionUtility.put("exportToken", token1);
		softAssert.assertEquals(res.getCode(), 200);
		softAssert.assertTrue(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "token generated Successfully");
		softAssert.assertNotNull(res.getData().getToken());
		softAssert.assertAll();
	}
	
	@Test(description = "tc_04 - Verify error when transaction_id is missing.", priority = 2, groups = {
			"smoke","regression" })
	public void verifyResponseWithMissingTransactionId_FaceFinder() {
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		GenerateTokenForExportDataFaceFinderRequest request = new GenerateTokenForExportDataFaceFinderRequest(client_id,
				client_secret);
		response = authService.generateTokenForExportDataWithAuth(request, this.token);
		String responseBody = response.asString();
		GenerateTokenForExportDataFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenForExportDataFaceFinderResponse.class);
		softAssert.assertEquals(res.getCode(), 400);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Missing transaction id");
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify unauthorized when token header is missing.", priority = 3, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingTokenHeader_FaceFinder() {
		if (transaction_id == null)
			getTransactionId();
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		GenerateTokenForExportDataFaceFinderRequest request = new GenerateTokenForExportDataFaceFinderRequest(client_id,
				client_secret,this.transaction_id);
		response = authService.generateTokenForExportData(request);
		String responseBody = response.asString();
		GenerateTokenForExportDataFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenForExportDataFaceFinderResponse.class);
		softAssert.assertEquals(res.getCode(), 401);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Missing token");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_06 - Verify unauthorized when token header is invalid.", priority = 4, groups = {
			"sanity", "regression" })
	public void verifyResponseWithInvalidToken_FaceFinder() {
		if (transaction_id == null)
			getTransactionId();
		String client_id = JSONUtility.getFaceFinder().getClient_id();
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		String invalidToken = "invalidToken";
		GenerateTokenForExportDataFaceFinderRequest request = new GenerateTokenForExportDataFaceFinderRequest(client_id,
				client_secret,this.transaction_id);
		response = authService.generateTokenForExportDataWithAuth(request,invalidToken);
		String responseBody = response.asString();
		GenerateTokenForExportDataFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenForExportDataFaceFinderResponse.class);
		softAssert.assertEquals(res.getCode(), 40);
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(), "Missing token");
		softAssert.assertAll();
	}
}
