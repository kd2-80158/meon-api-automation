package com.api.tests.kyc;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.kyc.GenerateAdminTokenKYCRequest;
import com.api.models.request.kyc.GetUserDataKYCRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class GetUserData_KYC extends BaseTest {

	AuthService authService;
	Logger logger;
	Response response;
	String authorization;
	RequestSpecification rs;
	boolean status = false;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("kyc");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
	}

	public void getAuthHeader() {
		try {
			if (!status && SessionUtility.get("access_token") == null) {
				GenerateAdminTokenKYCRequest generateAdminTokenKYCRequest = new GenerateAdminTokenKYCRequest(
						JSONUtility.getKYC().getEmail(), JSONUtility.getKYC().getPassword());
				response = authService.generateAdminToken(generateAdminTokenKYCRequest);
				logger.info("Response body: " + response.asPrettyString());
				String access_token = response.jsonPath().getString("access_token");
				SessionUtility.put("access_token", access_token);
				authorization = SessionUtility.get("access_token");
				status = true;
			} else {
				authorization = SessionUtility.get("access_token");
				status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(description = "tc_01 - Verify successful data retrieval with valid auth and required fields.", priority = 1,groups= {"smoke","regression"})
	public void verifyResponseWithValidAuthAndRequiredFields_GetDataKYC() {
		getAuthHeader();
		GetUserDataKYCRequest getUserDataKYCRequest = new GetUserDataKYCRequest(JSONUtility.getKYC().getWork_flow_key(),
				JSONUtility.getKYC().getPage(), JSONUtility.getKYC().getPer_page(), JSONUtility.getKYC().getSearch(),
				JSONUtility.getKYC().getCompany());
		response = authService.fetchDocumentWithAuthKYC(getUserDataKYCRequest, authorization);
		logger.info("Response:" + response.asPrettyString());

		softAssert.assertEquals(response.getContentType(), "application/json");
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(response.jsonPath().get("data"));
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify invalid/expired token returns 401.", priority = 2,groups= {"e2e","smoke","regression"})
	public void verifyResponseWithInvalidAuthAndRequiredFields_GetDataKYC() {
		if (!status)
			getAuthHeader();
		String invalidAuth = "invalidAuthToken";
		GetUserDataKYCRequest getUserDataKYCRequest = new GetUserDataKYCRequest(JSONUtility.getKYC().getWork_flow_key(),
				JSONUtility.getKYC().getPage(), JSONUtility.getKYC().getPer_page(), JSONUtility.getKYC().getSearch(),
				JSONUtility.getKYC().getCompany());
		response = authService.fetchDocumentWithAuthKYC(getUserDataKYCRequest, invalidAuth);
		String message = response.jsonPath().getString("msg");
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertEquals(message, "Signature verification failed");
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify error when required field work_flow_key is missing..", priority = 3,groups= {"sanity","regression"})
	public void verifyResponseWithMissingWorkFlowKeyField_GetDataKYC() {
		if (!status)
			getAuthHeader();
		GetUserDataKYCRequest getUserDataKYCRequest = new GetUserDataKYCRequest(JSONUtility.getKYC().getPage(),
				JSONUtility.getKYC().getPer_page(), JSONUtility.getKYC().getSearch(),
				JSONUtility.getKYC().getCompany());
		response = authService.fetchDocumentWithAuthKYC(getUserDataKYCRequest, authorization);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.fail(
					"Server returned HTML error page when required field work_flow_key is missing. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(response.getContentType(), "application/json");
			softAssert.assertEquals(response.getStatusCode(), 400);
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify error when required field company is missing.", priority = 4,groups= {"sanity","regression"})
	public void verifyResponseWithMissingCompanyField_GetDataKYC() {
		if (!status)
			getAuthHeader();
		String workFlow = JSONUtility.getKYC().getWork_flow_key();
		String Page = JSONUtility.getKYC().getPage();
		String perPage = JSONUtility.getKYC().getPer_page();
		String search = JSONUtility.getKYC().getSearch();
		String requestBody = "{\n" + "    \"work_flow_key\": \"" + workFlow + "\",\n" + "    \"page\": \"" + Page
				+ "\",\n" + "    \"per_page\": \"" + perPage + "\",\n" + "    \"search\": \"" + search + "\"\n" + "}";

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.header("Authorization", "Bearer" + " " + authorization).body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_GET_DATA);

		logger.info("Response:" + response.asPrettyString());

		softAssert.assertEquals(response.jsonPath().getString("success"), false);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Missing company field");
		softAssert.assertAll();

	}

	@Test(description = "tc_06 - Verify validation when work_flow_key is empty string.", priority = 5,groups= {"regression"})
	public void verifyResponseWithEmptyWorkFlowKeyField_GetDataKYC() {
		if (!status)
			getAuthHeader();
		String workFlow = "";
		GetUserDataKYCRequest getUserDataKYCRequest = new GetUserDataKYCRequest(workFlow,
				JSONUtility.getKYC().getPage(), JSONUtility.getKYC().getPer_page(), JSONUtility.getKYC().getSearch(),
				JSONUtility.getKYC().getCompany());
		response = authService.fetchDocumentWithAuthKYC(getUserDataKYCRequest, authorization);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert
					.fail("Server returned HTML error page when required field work_flow_key is empty. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(response.getContentType(), "application/json");
			softAssert.assertEquals(response.getStatusCode(), 400);
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify error when required pagination field per_page is missing", priority = 6,groups= {"regression"})
	public void verifyResponseWithEmptyCompanyField_GetDataKYC() {
		if (!status)
			getAuthHeader();
		String company = "";
		GetUserDataKYCRequest getUserDataKYCRequest = new GetUserDataKYCRequest(JSONUtility.getKYC().getWork_flow_key(),
				JSONUtility.getKYC().getPage(), JSONUtility.getKYC().getPer_page(), JSONUtility.getKYC().getSearch(),
				company);
		response = authService.fetchDocumentWithAuthKYC(getUserDataKYCRequest, authorization);
		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.fail("Server returned HTML error page when required field company is empty. Response body: "
					+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 400);
			softAssert.assertEquals(response.jsonPath().get("msg"), "Missing company value");
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Verify error when required pagination field per_page is missing.", priority = 7,groups= {"regression"})
	public void verifyResponseWithMissingPerPageField_GetDataKYC() {
		if (!status)
			getAuthHeader();
		String workFlow = JSONUtility.getKYC().getWork_flow_key();
		String page = JSONUtility.getKYC().getPage();
		String company = JSONUtility.getKYC().getCompany();
		String search = JSONUtility.getKYC().getSearch();
		String requestBody = "{\n" + "    \"work_flow_key\": \"" + workFlow + "\",\n" + "    \"page\": \"" + page
				+ "\",\n" + "    \"company\": \"" + company + "\",\n" + "    \"search\": \"" + search + "\"\n" + "}";

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.header("Authorization", "Bearer" + " " + authorization).body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_GET_DATA);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 500);
			softAssert.fail(
					"Server returned HTML error page when required pagination field per_page is missing.. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 400);
			softAssert.assertEquals(response.jsonPath().get("msg"), "Missing company value");
		}
		softAssert.assertAll();

	}

	@Test(description = "tc_10 - Verify server handles non-numeric page/per_page gracefully (validation).", priority = 8,groups= {"regression"})
	public void verifyResponseWithNonNumericPageAndPerPageFields_GetDataKYC() {
		if (!status)
			getAuthHeader();
		String workFlow = JSONUtility.getKYC().getWork_flow_key();
		String page = "abc";
		String perPage = "ten";
		String company = JSONUtility.getKYC().getCompany();
		String search = JSONUtility.getKYC().getSearch();
		String requestBody = "{\n" + "    \"work_flow_key\": \"" + workFlow + "\",\n" + "    \"page\": \"" + page
				+ "\",\n" + "\",\n" + "    \"per_page\": \"" + perPage + "\",\n" + "    \"company\": \"" + company
				+ "\",\n" + "    \"search\": \"" + search + "\"\n" + "}";

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.header("Authorization", "Bearer" + " " + authorization).body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_GET_DATA);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 500);
			softAssert.fail(
					"Server returned HTML error page when non-numeric page/per_page.. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 400);
			softAssert.assertEquals(response.jsonPath().get("msg"), "Non-numeric page and per page fields value");
		}
		softAssert.assertAll();

	}
	
	@Test(description = "tc_11 - Verify API returns validation error when page and per_page are set to '0'.", priority = 9,groups= {"regression"})
	public void verifyResponseWhenPageAndPerPageFieldsAreSetToZero_GetDataKYC() {
		if (!status)
			getAuthHeader();
		String workFlow = JSONUtility.getKYC().getWork_flow_key();
		String page = "0";
		String perPage = "0";
		String company = JSONUtility.getKYC().getCompany();
		String search = JSONUtility.getKYC().getSearch();
		String requestBody = "{\n" + "    \"work_flow_key\": \"" + workFlow + "\",\n" + "    \"page\": \"" + page
				+ "\",\n" + "\",\n" + "    \"per_page\": \"" + perPage + "\",\n" + "    \"company\": \"" + company
				+ "\",\n" + "    \"search\": \"" + search + "\"\n" + "}";

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.header("Authorization", "Bearer" + " " + authorization).body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_GET_DATA);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 500);
			softAssert.fail(
					"Server returned HTML error page when page and per_page are set to '0'. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 400);
			softAssert.assertEquals(response.jsonPath().get("msg"), "page and per_page fields must be greater than zero");
		}
		softAssert.assertAll();

	}
	
	@Test(description="tc_14 - Verify malformed JSON returns parse error.",priority=10,groups= {"regression"})
	public void verifyResponseWithMalformedJSON_GetDataKYC()
	{
		if(!status)
			getAuthHeader();
		String workFlow = JSONUtility.getKYC().getWork_flow_key();
		String page = JSONUtility.getKYC().getPage();
		String perPage = JSONUtility.getKYC().getPer_page();
		String company = JSONUtility.getKYC().getCompany();
		String search = JSONUtility.getKYC().getSearch();
		String requestBody = "{\n" + "    \"work_flow_key\": \"" + workFlow + "\",\n" + "    \"page\": \"" + page
				+ "\",\n" + "\",\n" + "    \"per_page\": \"" + perPage + "\",\n" + "    \"company\": \"" + company
				+ "\",\n" + "    \"search\": \"" + search;

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("application/json")
				.header("Authorization", "Bearer" + " " + authorization).body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_GET_DATA);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 400);
			softAssert.fail(
					"Server returned HTML error page when malformed JSON returns parse error. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 400);
			softAssert.assertEquals(response.jsonPath().get("msg"), "Malformed JSON");
		}
		softAssert.assertAll();	
	}
	
	@Test(description="tc_15 - Verify Content-Type enforcement (non-JSON rejected).",priority=11,groups= {"smoke","regression"})
	public void verifyResponseWithNonJsonContentType_GetDataKYC()
	{
		if(!status)
			getAuthHeader();
		String workFlow = JSONUtility.getKYC().getWork_flow_key();
		String page = JSONUtility.getKYC().getPage();
		String perPage = JSONUtility.getKYC().getPer_page();
		String company = JSONUtility.getKYC().getCompany();
		String search = JSONUtility.getKYC().getSearch();
		String requestBody = "{\n" + "    \"work_flow_key\": \"" + workFlow + "\",\n" + "    \"page\": \"" + page
				+ "\",\n" + "\",\n" + "    \"per_page\": \"" + perPage + "\",\n" + "    \"company\": \"" + company
				+ "\",\n" + "    \"search\": \"" + search;

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("text/plain")
				.header("Authorization", "Bearer" + " " + authorization).body(requestBody).when()
				.post(AuthService.BASE_PATH_KYC_GET_DATA);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 415);
			softAssert.fail(
					"Server returned HTML error page when malformed JSON returns parse error. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 415);
		}
		softAssert.assertAll();	
	}
	
	@Test(description="tc_16 - Verify GET method not allowed (method enforcement).",priority=12,groups= {"smoke","regression"})
	public void verifyResponseWithWrongHTTPMethod_GetDataKYC()
	{
		if(!status)
			getAuthHeader();
		String workFlow = JSONUtility.getKYC().getWork_flow_key();
		String page = JSONUtility.getKYC().getPage();
		String perPage = JSONUtility.getKYC().getPer_page();
		String company = JSONUtility.getKYC().getCompany();
		String search = JSONUtility.getKYC().getSearch();
		String requestBody = "{\n" + "    \"work_flow_key\": \"" + workFlow + "\",\n" + "    \"page\": \"" + page
				+ "\",\n" + "\",\n" + "    \"per_page\": \"" + perPage + "\",\n" + "    \"company\": \"" + company
				+ "\",\n" + "    \"search\": \"" + search;

		response = rs.baseUri(JSONUtility.getKYC().getUrl()).contentType("text/plain")
				.header("Authorization", "Bearer" + " " + authorization).body(requestBody).when()
				.get(AuthService.BASE_PATH_KYC_GET_DATA);

		String responseBody = response.body().asPrettyString();
		String contentType = response.getHeader("Content-Type");
		boolean isHtml = (responseBody != null && responseBody.trim().startsWith("<!doctype html"))
				|| (contentType != null && contentType.contains("html"));
		if (isHtml) {
			softAssert.assertTrue(response.getStatusCode() == 405);
			softAssert.fail(
					"Server returned HTML error page when malformed JSON returns parse error. Response body: "
							+ responseBody);
		} else {
			softAssert.assertEquals(contentType, "application/json");
			softAssert.assertEquals(response.getStatusCode(), 405);
			softAssert.assertEquals(response.jsonPath().get("msg"), "Use 'POST' method only");
		}
		softAssert.assertAll();	
	}
	

}
