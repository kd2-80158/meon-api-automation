package com.api.tests.ocr;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.response.ocr.GenerateTokenOCRResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({com.api.listeners.TestListener.class})
public class GenerateToken_OCR extends BaseTest {
	
	AuthService authService;
	Response response;
	Logger logger;
	Gson gson;
	RequestSpecification rs;
	
	@BeforeMethod
	public void setup()
	{
		authService = new AuthService("ocr");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
		rs = RestAssured.given();
	}
	
	@Test(description = "tc_01 - Verify successful token generation with valid credentials.",priority=1,alwaysRun = true,groups= {"e2e","smoke","sanity","regression"})
	public void verifyResponseWithValidCredentials_OCR()
	{
		String company_id = JSONUtility.getOcr().getCompany_id();
		String email = JSONUtility.getOcr().getEmail();
		String password = JSONUtility.getOcr().getPassword();
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, email, password);
		
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();
		
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		String tokenOCR = res.getToken();
		SessionUtility.put("tokenOCR", tokenOCR);
		softAssert.assertEquals(response.statusCode(), 200);
		softAssert.assertNotNull(tokenOCR);
		softAssert.assertTrue(res.isSuccess());
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_02 - Verify request fails when company_id is missing.",priority=2,groups= {"smoke","sanity","regression"})
	public void verifyResponseWithMissingCompanyIdField_OCR()
	{
		String email = JSONUtility.getOcr().getEmail();
		String password = JSONUtility.getOcr().getPassword();
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(email, password);	
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();	
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		softAssert.assertEquals(response.statusCode(), 400,Severity.CRITICAL,"");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(),"Missing company_id",Severity.HIGH,"");
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_03 - Verify request fails when email is missing.",priority=3,groups= {"sanity","regression"})
	public void verifyResponseWithMissingEmailField_OCR()
	{
		String company_id = JSONUtility.getOcr().getCompany_id();
		String password = JSONUtility.getOcr().getPassword();
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, password);	
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();	
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		softAssert.assertEquals(response.statusCode(), 400,Severity.CRITICAL,"");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(),"Missing email field",Severity.HIGH,"");
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_04 - Verify request fails when password is missing.",priority=4,groups= {"sanity","regression"})
	public void verifyResponseWithMissingPasswordField_OCR()
	{
		String company_id = JSONUtility.getOcr().getCompany_id();
		String email = JSONUtility.getOcr().getEmail();
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, email);	
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();	
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		softAssert.assertEquals(response.statusCode(), 400,Severity.CRITICAL,"");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(),"Missing password field",Severity.HIGH,"");
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_05 - Verify empty string values return validation error.",priority=5,groups= {"sanity","regression"})
	public void verifyResponseWithEmptyFields_OCR()
	{
		String company_id = "";
		String email = "";
		String password = "";
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, email,password);	
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();	
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		softAssert.assertEquals(response.statusCode(), 400,Severity.CRITICAL,"");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(),"Missing id, email and password",Severity.HIGH,"");
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_06 - Verify invalid email format is rejected.",priority=6,groups= {"sanity","regression"})
	public void verifyResponseWithInvalidEmailFormat_OCR()
	{
		String company_id = JSONUtility.getOcr().getCompany_id();
		String email = "meon-at-meon.com";
		String password = JSONUtility.getOcr().getPassword();
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, email,password);	
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();	
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		softAssert.assertEquals(response.statusCode(), 400,Severity.CRITICAL,"");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(),"Invalid email format",Severity.HIGH,"");
		softAssert.assertAll();	
	}

	@Test(description = "tc_07 - Verify authentication fails for incorrect password.",priority=7,groups= {"sanity","regression"})
	public void verifyResponseWithInvalidPassword_OCR()
	{
		String company_id = JSONUtility.getOcr().getCompany_id();
		String email = JSONUtility.getOcr().getEmail();
		String password = "WrongPassword";
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, email,password);	
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();	
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		softAssert.assertEquals(response.statusCode(), 401,Severity.CRITICAL,"");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(),"Invalid credentials",Severity.HIGH,"");
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_08 - Verify invalid company_id type is rejected.",priority=8,groups= {"sanity","regression"})
	public void verifyResponseWithInvalidCompanyId_OCR()
	{
		String company_id = "InvalidCompanyId";
		String email = JSONUtility.getOcr().getEmail();
		String password = "WrongPassword";
		GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, email,password);	
		response = authService.generateTokenOCR(request);
		String responseBody = response.asString();	
		GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
		softAssert.assertEquals(response.statusCode(), 400,Severity.CRITICAL,"");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertEquals(res.getMsg(),"Invalid company_id",Severity.HIGH,"");
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_11 - Verify request without Content-Type header is rejected.",priority=9,groups= {"sanity","regression"})
	public void verifyResponseWithWrongContentType_OCR()
	{
		String company_id = JSONUtility.getOcr().getCompany_id();
		String email = JSONUtility.getOcr().getEmail();
		String password = JSONUtility.getOcr().getPassword();
		
		response = rs.baseUri(JSONUtility.getOcr().getUrl())
				     .body("{\n"
				     		+ "    \"company_id\": \""+company_id+"\",\n"
				     		+ "    \"email\": \""+email+"\",\n"
				     		+ "    \"password\": \""+password+"\"\n"
				     		+ "\n"
				     		+ "}").when().post(AuthService.BASE_PATH_OCR_GENERATE_TOKEN);
		String responseBody = response.asString();
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html");
		
		if(isHtml)
		{
			Assert.fail("Error occured for request without Content-Type header as response: "+responseBody+" "+Severity.CRITICAL);
		}
		softAssert.assertEquals(response.statusCode(), 415,Severity.CRITICAL,"");
		softAssert.assertAll();	
	}
	
	@Test(description = "tc_13 - Verify response when request payload is malformed.",priority=10,groups= {"sanity","regression"})
	public void verifyResponseWithMalformedJSON_OCR()
	{
		String company_id = JSONUtility.getOcr().getCompany_id();
		String email = JSONUtility.getOcr().getEmail();
		String password = JSONUtility.getOcr().getPassword();
		
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType("application/json")
				     .body("{\n"
				     		+ "    \"company_id\": \""+company_id+"\",\n"
				     		+ "    \"email\": \""+email+"\",\n"
				     		+ "    \"password\": \""+password+"\"").when().post(AuthService.BASE_PATH_OCR_GENERATE_TOKEN);
		String responseBody = response.asString();
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html");
		
		if(isHtml)
		{
			Assert.fail("Error occured for request when request payload is malformed as response: "+responseBody+" "+Severity.CRITICAL);
		}
		softAssert.assertEquals(response.statusCode(), 400,Severity.CRITICAL,"");
		softAssert.assertAll();	
	}
	
}
