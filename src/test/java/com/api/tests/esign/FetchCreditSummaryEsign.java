package com.api.tests.esign;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.esign.FetchCreditSummaryEsignRequest;
import com.api.models.response.esign.FetchDocumentEsignResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class FetchCreditSummaryEsign extends BaseTest {

	AuthService authService;
	Logger logger;
	String signature;
	String token;
	String esign_url;
	Response response;
	RequestSpecification rs;
	FetchDocumentEsignResponse res;
	String mobileNumber = "8810619472";

	@BeforeMethod
	public void setup() {
		authService = new AuthService("eSign");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}

	public void getSessionVariables() {
		signature = SessionUtility.get("signature");
		token = SessionUtility.get("token");
		esign_url = SessionUtility.get("esign_url");
//		logger.info("Signature in 3rd api:" + signature);
//		logger.info("Token in 3rd api:" + token);
	}

	@Test(description = "tc_01 - Verify API returns eSign summary successfully for valid company_id and date range", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "regression" })
	public void verifyResponseWithValidCredentials_Esign() {
		getSessionVariables();
		FetchCreditSummaryEsignRequest request = new FetchCreditSummaryEsignRequest();
		request.setCompany_id(JSONUtility.getEsign().getCompany_id());
		request.setFrom_date(JSONUtility.getEsign().getFrom_date());
		request.setTo_date(JSONUtility.getEsign().getTo_date());

		response = authService.fetchCreditSummaryEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertTrue(response.jsonPath().getBoolean("status"));
		softAssert.assertNotNull(response.jsonPath().getString("company_name"));
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify API handles non-existing company_id", priority = 2, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithNonExistingCompanyId_Esign() {
		getSessionVariables();
		String nonExistingCompanyId = "1";
		FetchCreditSummaryEsignRequest request = new FetchCreditSummaryEsignRequest();
		request.setCompany_id(nonExistingCompanyId);
		request.setFrom_date(JSONUtility.getEsign().getFrom_date());
		request.setTo_date(JSONUtility.getEsign().getTo_date());

		response = authService.fetchCreditSummaryEsign(request, token);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		// System.out.println("response is: " + responseBody);
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!DOCTYPE"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			Assert.fail("HTML response " + responseBody);
		}
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify API handles missing company_id", priority = 3, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithMissingCompanyId_Esign() {
		getSessionVariables();
//		String nonExistingCompanyId = "1";
		FetchCreditSummaryEsignRequest request = new FetchCreditSummaryEsignRequest();
		request.setFrom_date(JSONUtility.getEsign().getFrom_date());
		request.setTo_date(JSONUtility.getEsign().getTo_date());

		response = authService.fetchCreditSummaryEsign(request, token);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
//System.out.println("response is: " + responseBody);
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!DOCTYPE"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			Assert.fail("HTML response " + responseBody);
		}
		softAssert.assertEquals(response.getStatusCode(), 400);
		//softAssert.assertFalse(response.jsonPath().getBoolean("status"));
		softAssert.assertAll();
	}
}
