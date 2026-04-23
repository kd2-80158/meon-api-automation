package com.api.tests.mca;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.mca.GenerateTokenMCARequest;
import com.api.models.request.mca.VerifyCompanyMCARequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.response.Response;

@Listeners({com.api.listeners.TestListener.class})
public class VerifyCompany_MCA extends BaseTest {

	AuthService authService;
	Response response;
	String token;
	Logger logger;
	
	@BeforeMethod
	public void setup()
	{
		logger = LoggerUtility.getLogger(this.getClass());
		authService = new AuthService("mca");
		this.token = SessionUtility.get("access_token");
	}
	
	public String verifyToken()
	{
		GenerateTokenMCARequest request = new GenerateTokenMCARequest(JSONUtility.getMca().getStatic_id());
		response = authService.generateTokenMCA(request);
		this.token = response.jsonPath().getString("access_token");
		System.out.println("token is: "+token);
		return token;
	}
	
	@Test(description="tc_01 - Verify successful company verification with valid CIN and token",priority=1, groups= {"e2e","smoke","sanity","regression"})
    public void verifyResponseWithValidCinAndToken()
    {
		if(token==null)
			verifyToken();
		VerifyCompanyMCARequest request = new VerifyCompanyMCARequest(JSONUtility.getMca().getCin());
		response = authService.verifyCompanyMCA(request,this.token);
		String responseBody = response.toString();
		System.out.println("Response is: "+responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(response.jsonPath().getString("company_status"));
		softAssert.assertNotNull(response.jsonPath().getJsonObject("company_details"));
		softAssert.assertAll();
    }
	
	@Test(description="tc_09 - Verify API behavior when CIN is missing",priority=2, groups= {"e2e","sanity","regresssion"})
	public void verifyResponseWithMissingCin()
	{
		if(this.token==null)
			verifyToken();
		VerifyCompanyMCARequest request = new VerifyCompanyMCARequest();
		response = authService.verifyCompanyMCA(request, token);
        softAssert.assertEquals(response.getStatusCode(), 400);
        softAssert.assertEquals(response.jsonPath().getString("error"), "cin_not_found");
		softAssert.assertAll();
	}
	
	@Test(description="tc_10 - Verify API behavior with invalid CIN format",priority=3, groups= {"e2e","sanity","regression"})
	public void verifyResponseWithIncorrectCin()
	{
		if(this.token==null)
			verifyToken();
		String incorrectCin = "abc123";
		VerifyCompanyMCARequest request = new VerifyCompanyMCARequest(incorrectCin);
		response = authService.verifyCompanyMCA(request, token);
		softAssert.assertEquals(response.getStatusCode(), 400);
        softAssert.assertEquals(response.jsonPath().getString("error"), "cin_not_found");
		softAssert.assertAll();	
	}
	
	@Test(description="tc_12 - Verify API behavior with non-existing CIN",priority=4,groups= {"e2e","sanity","regression"})
	public void verifyResponseWithNonExistingCin()
	{
		if(this.token==null)
			verifyToken();
		String nonExistingCin = "U00000UP0000PTC000000";
		VerifyCompanyMCARequest request = new VerifyCompanyMCARequest(nonExistingCin);
		response = authService.verifyCompanyMCA(request, token);
		softAssert.assertEquals(response.getStatusCode(), 404);
        softAssert.assertEquals(response.jsonPath().getString("error"), "cin_not_found");
		softAssert.assertAll();	
	}
	
	@Test(description="tc_16 - Verify 'nic_code' in response should be in integer only",priority=5,groups= {"e2e","sanity","regression"})
	public void verifyResponseWithValidNicCode()
	{
		if(this.token==null)
			verifyToken();
		VerifyCompanyMCARequest request = new VerifyCompanyMCARequest(JSONUtility.getMca().getCin());
		response = authService.verifyCompanyMCA(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: "+responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(response.jsonPath().getString("company_details.nic_code"));
		softAssert.assertAll();
	}
}
