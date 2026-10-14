package com.api.tests.reversepennydrop;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.reversepennydrop.GenerateTokenRPDRequest;
import com.api.models.response.reversepennydrop.GenerateTokenRPDResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class GenerateToken_RPD extends BaseTest {

	AuthService authService;
	Logger logger;
	Response response;
	RequestSpecification rs;
	Gson gson;
	String authRPD;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("rpd");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
		gson = new Gson();
	}

	@Test(description = "tc_01 - Verify token generated successfully with valid client_id and client_secret.", priority = 1,alwaysRun=true,groups= {"e2e","smoke","sanity","regression"})
	public void verifyResponseWithValidCredentials_RPD() {
		String client_id = JSONUtility.getReversePennyDrop().getClient_id();
		String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
		GenerateTokenRPDRequest request = new GenerateTokenRPDRequest(client_id, client_secret);
		response = authService.generateTokenRPD(request);
		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		GenerateTokenRPDResponse res = gson.fromJson(responseBody, GenerateTokenRPDResponse.class);
		String token = res.getData().getToken();
		SessionUtility.put("authRPD", token);
		String authRPD = (String) SessionUtility.get("authRPD");
		softAssert.assertNotNull(token);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 200);
		softAssert.assertEquals(res.getMsg(), "token generated successfully", Severity.HIGH, "Token is incorrect");
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify error when client_id is missing.", priority = 2,groups= {"smoke","regression"})
	public void verifyResponseWithMissingClientIdField_RPD() {
		String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
		GenerateTokenRPDRequest request = new GenerateTokenRPDRequest(client_secret);
		response = authService.generateTokenRPD(request);
		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		GenerateTokenRPDResponse res = gson.fromJson(responseBody, GenerateTokenRPDResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertEquals(res.getMsg(), "Invalid Credentials", Severity.HIGH, "Something went wrong");
		softAssert.assertFalse(res.getSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify error when client_secret is missing.", priority = 3,groups= {"sanity","regression"})
	public void verifyResponseWithMissingClientSecretField_RPD() {
		String client_id = JSONUtility.getReversePennyDrop().getClient_id();
		GenerateTokenRPDRequest request = new GenerateTokenRPDRequest(client_id);
		response = authService.generateTokenRPD(request);
		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		GenerateTokenRPDResponse res = gson.fromJson(responseBody, GenerateTokenRPDResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertEquals(res.getMsg(), "Invalid Credentials", Severity.HIGH, "Something went wrong");
		softAssert.assertFalse(res.getSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify authentication fails with wrong client_id.", priority = 4,groups= {"smoke","regression"})
	public void verifyResponseWithWrongClientIdField_RPD() {
		String client_id = "wrongClientId";
		String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
		GenerateTokenRPDRequest request = new GenerateTokenRPDRequest(client_id, client_secret);
		response = authService.generateTokenRPD(request);
		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		GenerateTokenRPDResponse res = gson.fromJson(responseBody, GenerateTokenRPDResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertEquals(res.getMsg(), "Invalid Credentials", Severity.HIGH, "Something went wrong");
		softAssert.assertFalse(res.getSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify authentication fails with wrong client_secret.", priority = 4,groups= {"regression"})
	public void verifyResponseWithWrongClientSecretField_RPD() {
		String client_id = JSONUtility.getReversePennyDrop().getClient_id();
		String client_secret = "wrongClientSecret";
		GenerateTokenRPDRequest request = new GenerateTokenRPDRequest(client_id, client_secret);
		response = authService.generateTokenRPD(request);
		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		GenerateTokenRPDResponse res = gson.fromJson(responseBody, GenerateTokenRPDResponse.class);
		softAssert.assertEquals(Severity.CRITICAL, res.getCode(), 400);
		softAssert.assertEquals(res.getMsg(), "Invalid Credentials", Severity.HIGH, "Something went wrong");
		softAssert.assertFalse(res.getSuccess());
		softAssert.assertAll();
	}

}
