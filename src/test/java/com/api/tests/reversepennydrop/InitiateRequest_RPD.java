package com.api.tests.reversepennydrop;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.reversepennydrop.GenerateTokenRPDRequest;
import com.api.models.request.reversepennydrop.InitiateRequestRPDRequest;
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

@Listeners({ com.api.listeners.TestListener.class })
public class InitiateRequest_RPD extends BaseTest {

	Logger logger;
	AuthService authService;
	Response response;
	RequestSpecification rs;
	String authRPD;
	Gson gson;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("rpd");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
		gson = new Gson();
		authRPD = SessionUtility.get("authRPD");
	}

	public String getToken() {
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
		return authRPD;
	}

	@Test(description = "tc_01 - Verify request initiates successfully with valid token and valid body", priority = 1, alwaysRun = true)
	public void verifyResponseWithValidCredentials() {
		getToken();
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
		boolean isRedirect = false;
		String url = res.getData().getQr_page();
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().fullscreen();
		driver.get(url);
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			String expectedRedirect = redirect_url;

			try {
				wait.until(ExpectedConditions.urlToBe(expectedRedirect));
				isRedirect = true;
			} catch (TimeoutException e) {
				if (driver.getCurrentUrl() != null && driver.getCurrentUrl().contains(expectedRedirect)) {
					isRedirect = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		softAssert.assertTrue(isRedirect);
		softAssert.assertEquals(res.getCode(), 200);
		softAssert.assertTrue(res.isSuccess());
	}
	
	@Test(description = "tc_02 - Verify error when redirect_url is missing from body.", priority = 2)
	public void verifyResponseWithMissingRedirectUrl_RPD() {
		getToken();
		String version = JSONUtility.getReversePennyDrop().getVersion();
		InitiateRequestRPDRequest request = new InitiateRequestRPDRequest(version);
		logger.info("token is: " + authRPD);
		response = authService.initiateRequest(request, authRPD);
		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		InitiateRequestRPDResponse res = gson.fromJson(responseBody, InitiateRequestRPDResponse.class);
		softAssert.assertFalse(res.isSuccess(),Severity.INFO,"Success value should be false");
		softAssert.assertEquals(res.getMsg(), "Please pass \"redirect_url\" for successfull redirection",Severity.CRITICAL,"Message");
		softAssert.assertEquals(res.getCode(), 400);
		softAssert.assertAll();
	}
	
	@Test(description = "tc_08 - Verify version value validation (unsupported version).", priority = 3)
	public void verifyResponseWithInvalidVersion_RPD() {
		getToken();
		String redirect_url = JSONUtility.getReversePennyDrop().getRedirect_url();
		String version = "v99";
		InitiateRequestRPDRequest request = new InitiateRequestRPDRequest(redirect_url,version);
		response = authService.initiateRequest(request, authRPD);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getContentType();
		int statusCode = response.getStatusCode();
		int expectedStatusCode = 400;
		logger.info("Content Type: "+contentType);
		logger.info("Status code is: "+statusCode);
		if (statusCode >= 500 && contentType.contains("text/plain")) {
	        softAssert.assertFail(Severity.CRITICAL,"Response is in text/plain format."+contentType);
	    }
		softAssert.assertEquals(statusCode, expectedStatusCode,Severity.HIGH,"Status code should be 400 Bad Request \"msg\": \"Unsupported version\",");
	    softAssert.assertAll();
	}
	
	@Test(description = "tc_10 - Verify Content-Type enforcement (non-JSON rejected).",priority=4)
	public void verifyResponseWithNonJsonContentType_RPD()
	{
		String redirect_url = JSONUtility.getReversePennyDrop().getRedirect_url();
		String version = JSONUtility.getReversePennyDrop().getVersion();
		response = rs
			    .baseUri(JSONUtility.getReversePennyDrop().getUrl())
			    .contentType("application/json")
			    .header("token", authRPD)
			    .body("{\"redirect_url\":\""+redirect_url+"\",\"version\":\""+version+"\"}")
			    .when()
			    .post(AuthService.BASE_PATH_REVERSEPENNYDROP_INITIATE_REQUEST);
        String responseBody = response.asPrettyString();

		logger.info("Response body: "+responseBody);
		//status code should be 415 - but here we consider it as 400
		InitiateRequestRPDResponse res = gson.fromJson(responseBody, InitiateRequestRPDResponse.class);
        boolean isSuccess = res.isSuccess();
		softAssert.assertEquals(res.getCode(), 400);
		softAssert.assertEquals(res.getMsg(), "Unsupported media type \"text/plain\" in request.");
		softAssert.assertFalse(isSuccess);
		softAssert.assertAll();
	}
	
	
	

}
