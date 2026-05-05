package com.api.tests.credittool;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.credittool.AddCreditCreditToolRequest;
import com.api.models.request.credittool.GenerateTokenCreditToolRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class AddCredit_CreditTool extends BaseTest {

	AuthService authService;
	Response response;
	RequestSpecification rs;
	Logger logger;
	String token; // SessionUtility.put("creditToolToken", this.token);

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

	@Test(description = "tc_01 - Verify credit is added successfully with valid request", alwaysRun = true, priority = 1, groups = {
			"e2e", "smoke", "regression" })
	public void verifyResponseWithValidCredentials() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id(JSONUtility.getCreditTool().getCompany_id());
		request.setCredit(JSONUtility.getCreditTool().getCredit());
		request.setProduct(JSONUtility.getCreditTool().getProduct());
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Credits Added successfully");
		softAssert.assertTrue(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify validation when credit field is missing", priority = 2, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWithMissingCreditField_CC() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id(JSONUtility.getCreditTool().getCompany_id());
		request.setProduct(JSONUtility.getCreditTool().getProduct());
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "credit is required");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify validation when product field is missing", priority = 3, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWithMissingProductField_CC() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id(JSONUtility.getCreditTool().getCompany_id());
		request.setCredit(JSONUtility.getCreditTool().getCredit());
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "product is required");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify credit value accepts only numeric input", priority = 4, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWithNonNumericCreditValue_CC() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id(JSONUtility.getCreditTool().getCompany_id());
		request.setCredit("abc");
		request.setProduct(JSONUtility.getCreditTool().getProduct());
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "credit should be numeric");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Verify negative credit value is rejected", priority = 5, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithNegativeCreditValue_CC() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id(JSONUtility.getCreditTool().getCompany_id());
		request.setCredit("-1");
		request.setProduct(JSONUtility.getCreditTool().getProduct());
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "credit should be greater than zero");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Verify zero credit value handling", priority = 6, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithZeroCreditValue_CC() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id(JSONUtility.getCreditTool().getCompany_id());
		request.setCredit("0");
		request.setProduct(JSONUtility.getCreditTool().getProduct());
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "credit should be greater than zero");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_11 - Verify invalid company_id handling", priority = 7, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithInvalidCompanyID_CC() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id("123456789");
		request.setCredit("1");
		request.setProduct(JSONUtility.getCreditTool().getProduct());
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_12 - Verify invalid product value", priority = 8, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithInvalidProduct_CC() {
		if (this.token == null) {
			getToken();
		}
		AddCreditCreditToolRequest request = new AddCreditCreditToolRequest();
		request.setCompany_id(JSONUtility.getCreditTool().getCompany_id());
		request.setCredit("1");
		request.setProduct("invalid-product");
		request.setCompany(JSONUtility.getCreditTool().getCompany());

		response = authService.addCredit(request, this.token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "product not found ");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_14 - Verify API rejects malformed JSON", priority = 9, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithMalformedJSON() {
		if (this.token == null)
			getToken();

		String BASE_URI = "https://central-tool.meon.co.in";
		String body = "{\n" + "    \"company_id\":67117,\n" + "    \"credit\":\"1\",\n"
				+ "    \"product\":\"penny-drop\",\n" + "    \"company\":\"Saurabh Chhimwal\""; // Missing closing
																								// brace '}'
		response = rs.relaxedHTTPSValidation().baseUri(BASE_URI).contentType("application/json")
				.header("Authorization", "Bearer " + this.token).body(body).when().post("/api/add_credit");
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);

		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Invalid JSON format");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();

	}
}
