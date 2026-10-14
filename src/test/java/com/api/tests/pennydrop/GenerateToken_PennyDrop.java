package com.api.tests.pennydrop;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
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
public class GenerateToken_PennyDrop extends BaseTest {

	AuthService authService;
	Logger logger;
	Response response;
	RequestSpecification rs;
	String username;
	String password;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("pennydrop");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
		this.username = JSONUtility.getPennyDrop().getLive().getUsername();
		this.password = JSONUtility.getPennyDrop().getLive().getPassword();
	}

	@Test(description = "tc_01 - Verify token is generated successfully with valid credentials.", priority = 1,alwaysRun = true,groups= {"e2e","smoke","regression"})
	public void verifyResponseWithValidCredentials_PD() {

		Live live = new Live();
		live.setUsername(username);
		live.setPassword(password);
		GenerateTokenPennyDropRequest request = new GenerateTokenPennyDropRequest(live);

		response = authService.generateTokenPennyDrop(request);
		logger.info("Response body: " + response.asPrettyString());
		String token = response.jsonPath().getString("token");
		SessionUtility.put("Authorization", token);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Token Generated Successfully");
		softAssert.assertEquals(response.jsonPath().getString("status"), "Success");
		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify request fails with invalid JSON format.",priority=2,groups= {"smoke","sanity","regression"})
	public void verifyResponseWithMalformedJSON_PD()
	{
		response = rs.baseUri(JSONUtility.getPennyDrop().getUrl())
	            .contentType("application/json")
	            .body("{\"live\":{\"username\":\""+username+"\",\"password\":\""+password+"}")
	            .when()
	            .post(AuthService.BASE_PATH_PENNYDROP_TOKEN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getContentType();
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html") || contentType.contains("html");
        if(isHtml)
        {
        	softAssert.assertFail(Severity.CRITICAL, "Response body is returning HTML body with invalid JSON format"+responseBody);
        }
        else
        {
        	softAssert.assertEquals(response.getStatusCode(), 400);
        }
        softAssert.assertAll();
	}
	
	@Test(description = "tc_08 - Verify API rejects non-JSON content type..",priority=3,groups= {"sanity","regression"})
	public void verifyResponseWithNonJSONContentType_PD()
	{
		response = rs.baseUri(JSONUtility.getPennyDrop().getUrl())
	            .contentType("application/json")
	            .body("{\"live\":{\"username\":\""+username+"\",\"password\":\""+password+"}}")
	            .when()
	            .post(AuthService.BASE_PATH_PENNYDROP_TOKEN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getContentType();
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html") || contentType.contains("html");
        if(isHtml)
        {
        	softAssert.assertFail(Severity.CRITICAL, "Response body is returning HTML body with invalid JSON format"+responseBody);
        }
        else
        {
        	softAssert.assertEquals(response.getStatusCode(), 400);
        }
        softAssert.assertAll();
	}

	@Test(description = "tc_12 - Verify GET method not allowed for this API.",priority=4,groups= {"sanity","regression"})
	public void verifyResponseWithNonSupportedHTTPMethod_PD()
	{
		response = rs.baseUri(JSONUtility.getPennyDrop().getUrl())
	            .contentType("application/json")
	            .body("{\"live\":{\"username\":\""+username+"\",\"password\":\""+password+"}}")
	            .when()
	            .get(AuthService.BASE_PATH_PENNYDROP_TOKEN);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getContentType();
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html") || contentType.contains("html");
        if(isHtml)
        {
        	softAssert.assertFail(Severity.CRITICAL, "Response body is returning HTML body with invalid JSON format"+responseBody);
        }
        else
        {
        	softAssert.assertEquals(response.getStatusCode(), 405);
        }
        softAssert.assertAll();
	}
	
	
}
