package com.api.tests.reversepennydrop;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.reversepennydrop.GenerateExportTokenRPDRequest;
import com.api.models.request.reversepennydrop.GenerateTokenRPDRequest;
import com.api.models.request.reversepennydrop.InitiateRequestRPDRequest;
import com.api.models.response.reversepennydrop.GenerateExportTokenRPDResponse;
import com.api.models.response.reversepennydrop.GenerateTokenRPDResponse;
import com.api.models.response.reversepennydrop.GetExportDataRPDResponse;
import com.api.models.response.reversepennydrop.InitiateRequestRPDResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class GetExportData_RPD extends BaseTest {

	String authRPD;
	Response response;
	AuthService authService;
	Logger logger;
	Gson gson;
	String transaction_id;
	String token;
	RequestSpecification rs;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("rpd");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
		rs = RestAssured.given();
		this.authRPD = SessionUtility.get("authRPD");
		this.transaction_id = SessionUtility.get("transactionId");
		this.token = SessionUtility.get("token");
	}

	public String getToken() {
		if (this.authRPD == null) {
			String clientId = JSONUtility.getReversePennyDrop().getClient_id();
			String clientSecret = JSONUtility.getReversePennyDrop().getClient_secret();
			GenerateTokenRPDRequest req = new GenerateTokenRPDRequest(clientId, clientSecret);
			response = authService.generateTokenRPD(req);
			String body = response.asString();
			logger.info("Response body: " + body);
			GenerateTokenRPDResponse genRes = gson.fromJson(body, GenerateTokenRPDResponse.class);
			this.authRPD = genRes.getData().getToken();
			SessionUtility.put("authRPD", this.authRPD);
		}
		if (this.transaction_id == null) {
			String redirectUrl = JSONUtility.getReversePennyDrop().getRedirect_url();
			String version = JSONUtility.getReversePennyDrop().getVersion();
			InitiateRequestRPDRequest initReq = new InitiateRequestRPDRequest(redirectUrl, version);
			logger.info("token is: " + this.authRPD);
			response = authService.initiateRequest(initReq, this.authRPD);

			String body = response.asString();
			logger.info("Response body: " + body);
			InitiateRequestRPDResponse initRes = gson.fromJson(body, InitiateRequestRPDResponse.class);
			this.transaction_id = initRes.getData().getTransaction_id();
			SessionUtility.put("transactionId", this.transaction_id);
		}
		if (this.token == null) {
			String clientId = JSONUtility.getReversePennyDrop().getClient_id();
			String clientSecret = JSONUtility.getReversePennyDrop().getClient_secret();
			GenerateExportTokenRPDRequest expReq = new GenerateExportTokenRPDRequest(clientId, clientSecret,
					this.transaction_id);
			response = authService.generateExportTokenRPD(expReq);

			String body = response.asPrettyString();
			logger.info("Response body: " + body);
			GenerateExportTokenRPDResponse expRes = gson.fromJson(body, GenerateExportTokenRPDResponse.class);
			this.token = expRes.getData().getToken();
			SessionUtility.put("token", this.token);
		}

		return this.token;
	}

	@Test(description = "tc_01 - Verify successful data export with valid token and default params.", priority = 1, alwaysRun = true,groups={"smoke","regression"})
	public void verifyResponseWithValidToken_RPD() throws InterruptedException {

		if (this.token == null) {
			getToken();
			logger.info("Inside test class - Token is: " + this.token);
		}

		try {
			Thread.sleep(10000);
			response = rs.baseUri(JSONUtility.getReversePennyDrop().getUrl()).header("token", this.token).when()
					.get(AuthService.BASE_PATH_REVERSEPENNYDROP_GET_DATA);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String msg = response.jsonPath().get("msg");
		String responseBody = response.asString();
        waitAndGetResponseHeaders(responseBody);
		
		logger.info("response body: " + responseBody);
		logger.info("Message: "+msg);
		softAssert.assertEquals(msg, "Penny drop was successful.");
		if(response.getStatusCode()>=400)
			return;
		Thread.sleep(10000);
	}

	private void waitAndGetResponseHeaders(String responseBody) {
		GetExportDataRPDResponse res = gson.fromJson(responseBody, GetExportDataRPDResponse.class);

		softAssert.assertEquals(res.getMsg(), "Penny drop was successful.", "Message mismatch");
		softAssert.assertEquals(res.getCode(), 200, "Incorrect code");
		softAssert.assertTrue(res.isSuccess(), "Success flag should be true");

		softAssert.assertEquals(res.getData().getAccount_details().getBank_name(), "STATE BANK OF INDIA",
				"Bank name mismatch");

		softAssert.assertEquals(res.getData().getAccount_details().getIfsc_code(), "SBIN0014141", "IFSC mismatch");

		softAssert.assertEquals(res.getData().getAccount_details().getMicr_code(), "263002051", "MICR mismatch");

		softAssert.assertEquals(res.getData().getAccount_details().getBranch_name(), "KALADHUNGI", "Branch mismatch");

		softAssert.assertEquals(res.getData().getAccount_details().getBank_address(),
				"MAIN MARKET KALADHUNGI,PO- KALADHUNGI,DISTT NAINITAL (UTTRAKHAND) -263140", "Bank address mismatch");

		softAssert.assertEquals(res.getData().getAccount_details().getAccount_number(), "00000038056684427",
				"Account number mismatch");
		softAssert.assertNotNull(res.getData().getAdditional_info().getTransaction_id(),
				"Transaction ID should not be null");

		softAssert.assertEquals(res.getData().getAdditional_info().getTransaction_status(), "completed",
				"Transaction status mismatch");
		softAssert.assertEquals(res.getData().getCustomer_details().getUtr(), "reverse_penny_drop_yrJNKfKwdlhdzymwruIz",
				"UTR mismatch");

		softAssert.assertEquals(res.getData().getCustomer_details().getUpi_id(), "9990785954@ptyes", "UPI ID mismatch");

		softAssert.assertEquals(res.getData().getCustomer_details().getRegistered_name(), "SAURABH  CHHIMWAL",
				"Registered name mismatch");
		softAssert.assertAll();
		
	}

}
