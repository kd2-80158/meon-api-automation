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
import com.api.models.response.reversepennydrop.InitiateRequestRPDResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.mail.Session;

@Listeners({ com.api.listeners.TestListener.class })
public class GenerateExportToken_RPD extends BaseTest {

	Logger logger;
	Response response;
	RequestSpecification rs;
	AuthService authService;
	String transaction_id;
	Gson gson;
	String authRPD;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("rpd");
		gson = new Gson();
		rs = RestAssured.given();
		logger = LoggerUtility.getLogger(this.getClass());
		transaction_id = SessionUtility.get("transactionId");
	}

	public String getTransactionID() {
		if (authRPD == null) {
			String client_id = JSONUtility.getReversePennyDrop().getClient_id();
			String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
			GenerateTokenRPDRequest request = new GenerateTokenRPDRequest(client_id, client_secret);
			response = authService.generateTokenRPD(request);
			String responseBody = response.asString();
			logger.info("Response body: " + responseBody);
			GenerateTokenRPDResponse res = gson.fromJson(responseBody, GenerateTokenRPDResponse.class);
			String token = res.getData().getToken();
			SessionUtility.put("authRPD", token);
			this.authRPD = (String) SessionUtility.get("authRPD");
		}
		if (transaction_id == null) {
			String redirect_url = JSONUtility.getReversePennyDrop().getRedirect_url();
			String version = JSONUtility.getReversePennyDrop().getVersion();
			InitiateRequestRPDRequest request = new InitiateRequestRPDRequest(redirect_url, version);
			logger.info("token is: " + authRPD);
			response = authService.initiateRequest(request, authRPD);

			String responseBody = response.asString();
			logger.info("Response body: " + responseBody);
			InitiateRequestRPDResponse res = gson.fromJson(responseBody, InitiateRequestRPDResponse.class);
			String transactionId = res.getData().getTransaction_id();
			SessionUtility.put("transactionId", transactionId);
			this.transaction_id = transactionId;
		}
		return transaction_id;
	}

	@Test(description = "tc_01 - Verify token generated successfully with valid client_id, client_secret and transaction_id", priority = 1, alwaysRun = true,groups= {"e2e","smoke","regression"})
	public void verifyResponseWithValidCredentials_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String client_id = JSONUtility.getReversePennyDrop().getClient_id();
		String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
		GenerateExportTokenRPDRequest request = new GenerateExportTokenRPDRequest(client_id, client_secret,
				transaction_id);
		response = authService.generateExportTokenRPD(request);
		String responseBody = response.asPrettyString();
		GenerateExportTokenRPDResponse res = gson.fromJson(responseBody, GenerateExportTokenRPDResponse.class);
		String token = res.getData().getToken();
		SessionUtility.put("token", token);
		softAssert.assertEquals(res.getCode(), 200, Severity.CRITICAL, "");
		softAssert.assertTrue(res.isSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify error when client_id is missing (transaction_id present).", priority = 2,groups= {"smoke","regression"})
	public void verifyResponseWithMissingCLientId_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
		GenerateExportTokenRPDRequest request = new GenerateExportTokenRPDRequest(client_secret, transaction_id);
		response = authService.generateExportTokenRPD(request);
		String responseBody = response.asPrettyString();
		GenerateExportTokenRPDResponse res = gson.fromJson(responseBody, GenerateExportTokenRPDResponse.class);
		String msg = res.getMsg();
		softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(msg, "Invalid Credentials");
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify error when client_secret is missing (transaction_id present).", priority = 3,groups= {"sanity","regression"})
	public void verifyResponseWithMissingCLientSecret_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String client_id = JSONUtility.getReversePennyDrop().getClient_id();
		GenerateExportTokenRPDRequest request = new GenerateExportTokenRPDRequest(client_id, transaction_id);
		response = authService.generateExportTokenRPD(request);
		String responseBody = response.asPrettyString();
		GenerateExportTokenRPDResponse res = gson.fromJson(responseBody, GenerateExportTokenRPDResponse.class);
		String msg = res.getMsg();
		softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(msg, "Invalid Credentials");
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify authentication fails with invalid client_secret.", priority = 4,groups= {"sanity","regression"})
	public void verifyResponseWithInvalidCLientId_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String client_id = "InvalidClientId";
		GenerateExportTokenRPDRequest request = new GenerateExportTokenRPDRequest(client_id, transaction_id);
		response = authService.generateExportTokenRPD(request);
		String responseBody = response.asPrettyString();
		GenerateExportTokenRPDResponse res = gson.fromJson(responseBody, GenerateExportTokenRPDResponse.class);
		String msg = res.getMsg();
		softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(msg, "Invalid Credentials");
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify authentication fails with invalid client_secret.", priority = 5,groups= {"regression"})
	public void verifyResponseWithInvalidCLientSecret_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String client_id = "InvalidClientId";
		GenerateExportTokenRPDRequest request = new GenerateExportTokenRPDRequest(client_id, transaction_id);
		response = authService.generateExportTokenRPD(request);
		String responseBody = response.asPrettyString();
		GenerateExportTokenRPDResponse res = gson.fromJson(responseBody, GenerateExportTokenRPDResponse.class);
		String msg = res.getMsg();
		softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(msg, "Invalid Credentials");
		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify invalid JSON returns parse error.", priority = 6,groups= {"regression"})
	public void verifyResponseWithInvalidJSON_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String client_id = JSONUtility.getReversePennyDrop().getClient_id();
		String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
		response = rs.baseUri(JSONUtility.getReversePennyDrop().getUrl()).contentType("application/json")
				.body("{\\n\" +\n" + "        \"    \"client_id\": \\\"" + client_id + "\",\\n\" +\n"
						+ "        \"    \"client_secret\": \"" + client_secret + "\",\\n\" +\n"
						+ "        \"    \"transaction_id\": \"" + transaction_id + "\"\\n\" +\n")
				.when().post(AuthService.BASE_PATH_REVERSEPENNYDROP_EXPORT_TOKEN);
		GenerateExportTokenRPDResponse res = new GenerateExportTokenRPDResponse();
		String detail=res.getDetail();
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertEquals(detail, "JSON parse error - Expecting ',' delimiter: line 5 column 1 (char 158)");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_08 - Verify content-type enforcement (non-JSON rejected).", priority = 7,groups= {"regression"})
	public void verifyResponseWithNonJSONContentType_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String detail=null;
		String client_id = JSONUtility.getReversePennyDrop().getClient_id();
		String client_secret = JSONUtility.getReversePennyDrop().getClient_secret();
		response = rs.baseUri(JSONUtility.getReversePennyDrop().getUrl()).contentType("text/plain")
				.body("{\\n\" +\n" + "        \"    \"client_id\": \\\"" + client_id + "\",\\n\" +\n"
						+ "        \"    \"client_secret\": \"" + client_secret + "\",\\n\" +\n"
						+ "        \"    \"transaction_id\": \"" + transaction_id + "\"\\n\" +\n"+"\\}")
				.when().post(AuthService.BASE_PATH_REVERSEPENNYDROP_EXPORT_TOKEN);
		GenerateExportTokenRPDResponse res = new GenerateExportTokenRPDResponse(detail);
		detail=res.getDetail();
		softAssert.assertEquals(response.getStatusCode(), 415, Severity.CRITICAL, "");
		softAssert.assertEquals(detail, "Unsupported media type \"text/plain\" in request.");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_10 - Verify invalid characters in client_id/client_secret rejected.", priority = 8,groups= {"regression"})
	public void verifyResponseWithInvalidCharacters_RPD() {
		if (transaction_id == null)
			getTransactionID();
		String client_id = "@#$%^&*";
		String client_secret = "%^&*";
		GenerateExportTokenRPDRequest request = new GenerateExportTokenRPDRequest(client_id,client_secret, transaction_id);
		response = authService.generateExportTokenRPD(request);
		String responseBody = response.asPrettyString();
		GenerateExportTokenRPDResponse res = gson.fromJson(responseBody, GenerateExportTokenRPDResponse.class);
		String msg = res.getMsg();
		softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(msg, "Invalid Credentials");
		softAssert.assertAll();
	}
}
