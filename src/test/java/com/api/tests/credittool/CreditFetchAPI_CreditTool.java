package com.api.tests.credittool;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import com.api.base.AuthService;
import com.api.models.request.credittool.GenerateTokenCreditToolRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class CreditFetchAPI_CreditTool {

	AuthService authService;
	Response response;
	RequestSpecification rs;
	Logger logger;
	String token; // SessionUtility.put("creditToolToken", this.token);
	String BASE_URI = "https://central-tool.meon.co.in";

	@BeforeMethod
	public void setup() {
		authService = new AuthService("credittool");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
		token = SessionUtility.get("creditToolToken");

	}

	public void getToken() {
		GenerateTokenCreditToolRequest request = new GenerateTokenCreditToolRequest(
				JSONUtility.getCreditTool().getEmail(), JSONUtility.getCreditTool().getPassword());
		response = authService.generateAdminToken(request);
		this.token = response.jsonPath().getString("token");
		SessionUtility.put("creditToolToken", this.token);
	}
}
