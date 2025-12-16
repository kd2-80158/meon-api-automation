package com.api.tests.kyc;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.kyc.GetSsoRouteKYCRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.Severity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners
public class GetSsoRoute_KYC extends BaseTest {

	Logger logger;
	AuthService authService;
	RequestSpecification rs;
	Response response;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("kyc");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}

	@Test(description = "tc_01 - Verify successful SSO route retrieval with valid mandatory fields.", priority = 1,groups= {"e2e","smoke","regression"})
	public void verifyResponseWithValidMandatoryFields_SSORoute() {
		GetSsoRouteKYCRequest getSsoRouteKYCRequest = new GetSsoRouteKYCRequest(JSONUtility.getKYC().getCompany_sso(),
				JSONUtility.getKYC().getWorkflowName(), JSONUtility.getKYC().getSecret_key(),
				JSONUtility.getKYC().getUnique_keys());
		response = authService.getSsoRoute(getSsoRouteKYCRequest);
		logger.info("Response body: " + response.asPrettyString());
		String short_url = response.jsonPath().getString("short_url");
		String url = response.jsonPath().getString("url");
		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL, "Expected status code 200");
	    softAssert.assertNotNull(short_url, Severity.HIGH, "Short URL must not be null");
	    softAssert.assertNotNull(url, Severity.HIGH, "URL must not be null");
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify error when company is missing (required field).", priority = 2,groups= {"smoke","regression"})
	public void verifyResponseWithMissingCompanyfield_SSORoute() {
		GetSsoRouteKYCRequest getSsoRouteKYCRequest = new GetSsoRouteKYCRequest(JSONUtility.getKYC().getWorkflowName(),
				JSONUtility.getKYC().getSecret_key(), JSONUtility.getKYC().getUnique_keys());
		response = authService.getSsoRoute(getSsoRouteKYCRequest);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 500);
			softAssert.assertFail(Severity.CRITICAL,"Server returned HTML error page when company is missing (required field). Response body: "
					+ responseBody);
		} else {
			softAssert.assertEquals(response.getStatusCode(), 400,Severity.CRITICAL,"Status should be 400 Bad Request");
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify error when workflowName is missing (required).", priority = 3,groups= {"sanity","regression"})
	public void verifyResponseWithMissingWorkflowfield_SSORoute() {
		GetSsoRouteKYCRequest getSsoRouteKYCRequest = new GetSsoRouteKYCRequest(JSONUtility.getKYC().getCompany_sso(),
				JSONUtility.getKYC().getSecret_key(), JSONUtility.getKYC().getUnique_keys());
		response = authService.getSsoRoute(getSsoRouteKYCRequest);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 500);
			softAssert.assertFail(Severity.CRITICAL,"Server returned HTML error page when workflowName is missing (required). Response body: "
					+ responseBody);
		} else {
			softAssert.assertEquals(response.getStatusCode(), 400);
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify error when secret_key is missing (required).", priority = 4,groups= {"sanity","regression"})
	public void verifyResponseWithMissingSecretKeyfield_SSORoute() {
		GetSsoRouteKYCRequest getSsoRouteKYCRequest = new GetSsoRouteKYCRequest(JSONUtility.getKYC().getCompany_sso(),
				JSONUtility.getKYC().getWorkflowName(), JSONUtility.getKYC().getUnique_keys());
		response = authService.getSsoRoute(getSsoRouteKYCRequest);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 500);
			softAssert.assertFail(Severity.CRITICAL,"Server returned HTML error page when secret_key is missing (required). Response body: "
					+ responseBody);
		} else {
			softAssert.assertEquals(response.getStatusCode(), 400);
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify error when unique_keys is missing (required).", priority = 5,groups= {"regression"})
	public void verifyResponseWithMissingUniqueKeysfield_SSORoute() {
		GetSsoRouteKYCRequest getSsoRouteKYCRequest = new GetSsoRouteKYCRequest(JSONUtility.getKYC().getCompany_sso(),
				JSONUtility.getKYC().getWorkflowName(), JSONUtility.getKYC().getSecret_key());
		response = authService.getSsoRoute(getSsoRouteKYCRequest);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 500);
			softAssert.assertFail(Severity.CRITICAL,"Server returned HTML error page when unique_keys is missing (required). Response body: "
					+ responseBody);
		} else {
			softAssert.assertEquals(response.getStatusCode(), 400);
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_13 - Verify method not allowed for GET/PUT.", priority = 6,groups= {"regression"})
	public void verifyResponseWithWrongHTTPMethod_GetDataKYC() {

		String company = JSONUtility.getKYC().getWork_flow_key();
		String workflowName = JSONUtility.getKYC().getPage();
		String secret_key = JSONUtility.getKYC().getPer_page();
		String requestBody = "{\n" + "    \"company\": \"" + company + "\",\n" + "    \"workflowName\": \""
				+ workflowName + "\",\n" + "\",\n" + "    \"secret_key\": \"" + secret_key + "\",\n"
				+ "    \"unique_keys\": \"" + JSONUtility.getKYC().getUnique_keys() + "\",\n}";

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.body(requestBody).when().post(AuthService.BASE_PATH_KYC_SSO_ROUTE);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 405);
			softAssert.assertFail(Severity.CRITICAL,"Server returned HTML error page when 'GET' method is used instead of 'POST'. Response body: "
					+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 405);
			softAssert.assertEquals(response.jsonPath().get("msg"), "Use 'POST' method only");
		}
		softAssert.assertAll();
	}
	
	@Test(description = "tc_14 - Verify malformed JSON returns parse error.", priority = 7,groups= {"regression"})
	public void verifyResponseWithMalformedJSON_GetDataKYC() {

		String company = JSONUtility.getKYC().getWork_flow_key();
		String workflowName = JSONUtility.getKYC().getPage();
		String secret_key = JSONUtility.getKYC().getPer_page();
		String requestBody = "{\n" + "    \"company\": \"" + company + "\",\n" + "    \"workflowName\": \""
				+ workflowName + "\",\n" + "\",\n" + "    \"secret_key\": \"" + secret_key + "\",\n"
				+ "    \"unique_keys\": \"" + JSONUtility.getKYC().getUnique_keys() + "\"";

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.body(requestBody).when().post(AuthService.BASE_PATH_KYC_SSO_ROUTE);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 415);
			softAssert.assertFail(Severity.CRITICAL,"Server returned HTML error page when JSON is malformed. Response body: "
					+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 415,Severity.CRITICAL,"Status code should be 415");
			softAssert.assertEquals(response.jsonPath().get("msg"), "Use 'POST' method only");
		}
		softAssert.assertAll();
	}

}