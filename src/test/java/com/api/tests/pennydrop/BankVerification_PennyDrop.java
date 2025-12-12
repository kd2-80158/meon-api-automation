package com.api.tests.pennydrop;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.pennydrop.BankVerificationPennyDropRequest;
import com.api.models.request.pennydrop.GenerateTokenPennyDropRequest;
import com.api.models.request.pennydrop.GenerateTokenPennyDropRequest.Live;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.api.utility.Severity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public final class BankVerification_PennyDrop extends BaseTest {

	Logger logger;
	AuthService authService;
	RequestSpecification rs;
	Response response;
	String token;
	String username;
	String password;
	String bearerToken;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("pennydrop");
		rs = RestAssured.given();
		logger = LoggerUtility.getLogger(this.getClass());
		token = SessionUtility.get("Authorization");
		System.out.println("Method setup's token: " + token);
		this.username = JSONUtility.getPennyDrop().getLive().getUsername();
		this.password = JSONUtility.getPennyDrop().getLive().getPassword();
	}

	public String getBearerToken() {
		if (token == null) {
			Live live = new Live();
			live.setUsername(username);
			live.setPassword(password);
			GenerateTokenPennyDropRequest request = new GenerateTokenPennyDropRequest(live);
			response = authService.generateTokenPennyDrop(request);
			bearerToken = response.jsonPath().getString("token");
			System.out.println("token from method: " + bearerToken);
			this.token = bearerToken;
		}
		return token;
	}

	@Test(description = "tc_01 - Verify successful pennydrop creation with valid data and valid token.", priority = 1, alwaysRun = true)
	public void verifyResponseWithValidCredentialsAndToken_PD() {
		getBearerToken();
		String name = JSONUtility.getPennyDrop().getName();
		String mobile = JSONUtility.getPennyDrop().getMobile();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, mobile, ifsc,
				accountnumber, accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		String msg = response.jsonPath().getString("msg");
		Object data = response.jsonPath().get("data");
		String bankAddress = response.jsonPath().getString("data.account_details.bank_address");
		String branchName = response.jsonPath().getString("data.account_details.branch_name");
		String ifscCode = response.jsonPath().getString("data.account_details.ifsc_code");
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertEquals(bankAddress, "MAIN MARKET KALADHUNGI,PO KALADHUNGI,DISTT NAINITAL,UTTRAKHAND 263140");
		softAssert.assertEquals(branchName, "KALADHUNGI");
		softAssert.assertEquals(ifscCode, "SBIN0014141");
		softAssert.assertEquals(msg, "Penny drop was successful.", Severity.HIGH, "Something went wrong!");
		softAssert.assertNotNull(data);
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify API rejects request with missing Authorization header.", priority = 2)
	public void verifyResponseWithMissingAuthHeader_PD() {
		getBearerToken();
		String name = JSONUtility.getPennyDrop().getName();
		String mobile = JSONUtility.getPennyDrop().getMobile();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, mobile, ifsc,
				accountnumber, accounttype);
		response = authService.bankVerification(request);
		String msg = response.jsonPath().getString("msg");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertEquals(msg, "Invalid Token", Severity.HIGH, "Something went wrong!");
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify API rejects invalid/expired token.", priority = 3)
	public void verifyResponseWithInvalidToken_PD() {
		String name = JSONUtility.getPennyDrop().getName();
		String mobile = JSONUtility.getPennyDrop().getMobile();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		String token = "invalidToken123";
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, mobile, ifsc,
				accountnumber, accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		String msg = response.jsonPath().getString("msg");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertEquals(msg, "Invalid Token", Severity.HIGH, "Something went wrong!");
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify validation fails when required field 'name' is missing.", priority = 4)
	public void verifyResponseWithMissingNameField_PD() {
		getBearerToken();
		String mobile = JSONUtility.getPennyDrop().getMobile();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(mobile, ifsc, accountnumber,
				accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		String msg = response.jsonPath().getString("msg");
		int code = response.jsonPath().get("code");
//		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(Severity.CRITICAL, code, 400);
		softAssert.assertEquals(msg, "Missing field: name", Severity.HIGH, "Something went wrong!");
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify validation fails when 'mobile' is missing.", priority = 5)
	public void verifyResponseWithMissingMobileField_PD() {
		getBearerToken();
		String name = JSONUtility.getPennyDrop().getName();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, ifsc, accountnumber,
				accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		String msg = response.jsonPath().getString("msg");
		int code = response.jsonPath().get("code");
//		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(Severity.CRITICAL, code, 400);
		softAssert.assertEquals(msg, "Missing field: mobile", Severity.HIGH, "Something went wrong!");
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify mobile number format validation.", priority = 6)
	public void verifyResponseWithInvalidMobileField_PD() {
		getBearerToken();
		String mobile = "12345";
		String name = JSONUtility.getPennyDrop().getName();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, mobile, ifsc,
				accountnumber, accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		String msg = response.jsonPath().getString("msg");
		int code = response.jsonPath().get("code");
//		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(Severity.CRITICAL, code, 400);
		softAssert.assertEquals(msg, "Mobile number must be a valid 10-digit Indian number", Severity.HIGH,
				"Something went wrong!");
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify IFSC format validation.", priority = 7)
	public void verifyResponseWithInvalidIFSCField_PD() {
		getBearerToken();
		String ifsc = "invalidifsc";
		String name = JSONUtility.getPennyDrop().getName();
		String mobile = JSONUtility.getPennyDrop().getMobile();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, mobile, ifsc,
				accountnumber, accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		String msg = response.jsonPath().getString("msg");
		int code = response.jsonPath().get("code");
//		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(Severity.CRITICAL, code, 400);
		softAssert.assertEquals(msg, "IFSC should be like 'SBIN0001234", Severity.HIGH, "Something went wrong!");
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}

	@Test(description = "tc_10 - Verify API rejects malformed JSON.", priority=8)
	public void verifyResponseWithMalformedJSON_PD()
	{
		String name = JSONUtility.getPennyDrop().getName();
		String mobile = JSONUtility.getPennyDrop().getMobile();
		response = rs.baseUri(JSONUtility.getPennyDrop().getUrl()).contentType("application/json")
		.header("Authorization",token).body(" {\"name\":\""+name+"\",\"mobile\":\""+mobile+"\"")
		.when().post(AuthService.BASE_PATH_PENNYDROP_BANK_VERIFICATION);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getContentType();
		boolean isHtml = responseBody !=null && responseBody.trim().startsWith("<!doctype") || contentType.contains("html");
		if(isHtml)
		{
			softAssert.assertFail(Severity.CRITICAL, "Response body is returning HTML body with invalid JSON format. Response body is: "+responseBody);
		}
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}

	@Test(description = "tc_13 - Verify SQL injection or malicious input is sanitized.", priority = 9)
	public void verifyResponseWithSqlInjection_PD() {
		getBearerToken();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String name = "Robert'); DROP TABLE authuser;--";
		String mobile = JSONUtility.getPennyDrop().getMobile();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, mobile, ifsc,
				accountnumber, accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		String msg = response.jsonPath().getString("msg");
		int code = response.jsonPath().get("code");
//		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(Severity.CRITICAL, code, 400);
		softAssert.assertEquals(msg, "Name contains invalid characters", Severity.HIGH, "Something went wrong!");
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}
	
	@Test(description = "tc_17 - Verify response time within SLA for normal request.", priority = 10)
	public void verifyResponseWithResponseTime_PD() {
		getBearerToken();
		String name = JSONUtility.getPennyDrop().getName();
		String mobile = JSONUtility.getPennyDrop().getMobile();
		String ifsc = JSONUtility.getPennyDrop().getIfsc();
		String accountnumber = JSONUtility.getPennyDrop().getAccountnumber();
		String accounttype = JSONUtility.getPennyDrop().getAccounttype();
		BankVerificationPennyDropRequest request = new BankVerificationPennyDropRequest(name, mobile, ifsc,
				accountnumber, accounttype);
		response = authService.bankVerificationWithAuth(request, token);
		Long responseTime = response.getTime();
		logger.info("Response time: "+responseTime);
        softAssert.assertTrue(responseTime<2000);
		softAssert.assertAll();
	}
}
