package com.api.tests.esign;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.esign.FetchDocumentEsignRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({com.api.listeners.TestListener.class})
public final class FetchDocumentEsign extends BaseTest {
	
	AuthService authService;
	Logger logger;
	String signature;
	String token;
	String esign_url;
	Response response;
	RequestSpecification rs;
	String mobileNumber = "8810619472";
	
	@BeforeMethod
	public void setup()
	{
		authService = new AuthService("eSign");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}
	
	public void getSessionVariables()
	{
		signature = SessionUtility.get("signature");
	    token = SessionUtility.get("token");
	    esign_url = SessionUtility.get("esign_url");
		logger.info("Signature in 3rd api:"+signature);
		logger.info("Token in 3rd api:"+token);
	}
	
	@Test(description="tc_01 - Verify that the API returns PDF URL when valid token and mobile are provided.",priority=1)
	public void verifyResponseWithValidCredentialsEsign()
	{
		getSessionVariables();
		authService.getEsign(esign_url);
		FetchDocumentEsignRequest fetchDocumentEsignRequest = new FetchDocumentEsignRequest(token, mobileNumber);
		response = authService.fetchDocumentWithAuth(fetchDocumentEsignRequest,signature);
		logger.info("Response body: "+response.asPrettyString());
		String fullPath = response.jsonPath().getString("full_path");
		boolean isSuccess = response.jsonPath().getBoolean("Success");
		softAssert.assertTrue(isSuccess,"Some issue occured while attempting to fetch the document");
		softAssert.assertNotNull(fullPath, "Missing full_path field from the response");
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertAll();
	}
	
	@Test(description="tc_02 - Verify API returns error when token is missing.",priority=2)
	public void verifyResponseWithMissingTokenField_FetchDocumentEsign()
	{
		getSessionVariables();
		FetchDocumentEsignRequest fetchDocumentEsignRequest = new FetchDocumentEsignRequest(mobileNumber);
		response = authService.fetchDocumentWithAuth(fetchDocumentEsignRequest,signature);
		logger.info("Response body: "+response.asPrettyString());
		String message = response.jsonPath().getString("msg");
		boolean isSuccess = response.jsonPath().getBoolean("Success");
		softAssert.assertFalse(isSuccess,"Some issue occured while attempting to fetch the document");
		softAssert.assertEquals(message, "UUID missing in request");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}
	
	@Test(description="tc_03 - Verify API returns error when mobile is missing.",priority=3)
	public void verifyResponseWithMissingMobileNumberField_FetchDocumentEsign()
	{
		getSessionVariables();
		FetchDocumentEsignRequest fetchDocumentEsignRequest = new FetchDocumentEsignRequest(token);
		response = authService.fetchDocumentWithAuth(fetchDocumentEsignRequest,signature);
		logger.info("Response body: "+response.asPrettyString());
		String message = response.jsonPath().getString("msg");
		boolean isSuccess = response.jsonPath().getBoolean("Success");
		softAssert.assertFalse(isSuccess,"Some issue occured while attempting to fetch the document");
		softAssert.assertEquals(message, "Mobile number missing in request");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}
	
    @Test(description="tc_04 - Verify API returns unauthorized when signature header is missing.",priority=4)
	public void verifyResponseWithMissingSignatureHeader_FetchDocumentEsign()
	{
		getSessionVariables();
		FetchDocumentEsignRequest fetchDocumentEsignRequest = new FetchDocumentEsignRequest(token,mobileNumber);
		response = authService.fetchDocument(fetchDocumentEsignRequest);
		logger.info("Response body: "+response.asPrettyString());
		String message = response.jsonPath().getString("msg");
		String tokenFromResponse = response.jsonPath().getString("token");
		boolean isSuccess = response.jsonPath().getBoolean("Success");
		softAssert.assertFalse(isSuccess,"Some issue occured while attempting to fetch the document");
		softAssert.assertEquals(message, "Header Missing");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertEquals(tokenFromResponse, "","Token must be empty string");
		softAssert.assertAll();
	}
	
    @Test(description="tc_06 - Verify API returns unauthorized when signature header is invalid.",priority=5)
	public void verifyResponseWithInvalidSignature_FetchDocumentEsign()
	{
		getSessionVariables();
		String invalidSignature = "invalidSignature.invalid.invalid";
		FetchDocumentEsignRequest fetchDocumentEsignRequest = new FetchDocumentEsignRequest(token,mobileNumber);
		response = authService.fetchDocumentWithAuth(fetchDocumentEsignRequest,invalidSignature);
		logger.info("Response body: "+response.asPrettyString());
		String message = response.jsonPath().getString("msg");
		boolean isSuccess = response.jsonPath().getBoolean("Success");
		softAssert.assertFalse(isSuccess,"Some issue occured while attempting to fetch the document");
		softAssert.assertEquals(message, "Signature has been expired and valid for only 3 minutes");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertAll();
	}
    
	@Test(description = "tc_06 - Verify API returns error for malformed JSON body.", priority = 6)
	public void verifyResponseWhenRequestBodyJSONMalformedEsign_FetchDocumentEsign()
	{
		
		getSessionVariables();
		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).contentType("application/json").header("signature",signature).body(
				"{\n"
				+ "    \"token\": \""+token+"\",\n"
				+ "    \"mobile\": \""+mobileNumber+"\"")
				.when().post(AuthService.BASE_PATH_FETCH_DOCUMENT_ESIGN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		String message = response.jsonPath().getString("msg");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		
		if (isHtml) {
			softAssert.fail("Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertEquals(message, "Signature has been expired and valid for only 3 minutes");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_07 - Verify API returns error when token is expired.", priority = 7)
	public void verifyResponseWhenTokenExpired_FetchDocumentEsign() 
	{
		getSessionVariables();
		String expiredToken = "7bcb2b8a-7e3b-4356-93fd-58daff1def14";
		FetchDocumentEsignRequest fetchDocumentEsignRequest = new FetchDocumentEsignRequest(expiredToken,mobileNumber);
		response = authService.fetchDocumentWithAuth(fetchDocumentEsignRequest,signature);
		logger.info("Response body: "+response.asPrettyString());
		String message = response.jsonPath().getString("msg");
		boolean isSuccess = response.jsonPath().getBoolean("Success");
		softAssert.assertFalse(isSuccess,"Some issue occured while attempting to fetch the document");
		softAssert.assertEquals(message, "Token is expired");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Verify API returns error when token is invalid/unknown.", priority = 8)
	public void verifyResponseWithInvalidTokenField_FetchDocumentEsign()
	{
		getSessionVariables();
		String invalidToken = "7bcb2b8a-7e3b-4356-93fd-58daff1def14";
		Response response = rs.baseUri(JSONUtility.getEsign().getUrl())
		        .contentType("application/json")
		        .header("signature", signature)
		        .body(
		            "{\n" +
		            "  \"token\": \"" + invalidToken + "\",\n" +
		            "  \"mobile\": \"" + mobileNumber + "\"\n" +
		            "}"
		        ).when().post(AuthService.BASE_PATH_FETCH_DOCUMENT_ESIGN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		String message = response.jsonPath().getString("msg");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		
		if (isHtml) {
			softAssert.fail("Server returned HTML error page for incorrect Content-Type. Response body:\n" + responseBody);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		softAssert.assertEquals(message, "File not found");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_09 - Verify API validates mobile format and returns error for invalid mobile.", priority = 9)
	public void verifyResponseWithInvalidMobileField_FetchDocumentEsign() 
	{
		getSessionVariables();
		String invalidMobileNumber = "12ab";
		FetchDocumentEsignRequest fetchDocumentEsignRequest = new FetchDocumentEsignRequest(token,invalidMobileNumber);
		response = authService.fetchDocumentWithAuth(fetchDocumentEsignRequest,signature);
		logger.info("Response body: "+response.asPrettyString());
		String message = response.jsonPath().getString("msg");
		boolean isSuccess = response.jsonPath().getBoolean("Success");
		softAssert.assertFalse(isSuccess,"Some issue occured while attempting to fetch the document");
		softAssert.assertEquals(message, "Please do esign for getting pdf for this signer.");
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
	}
	
}
