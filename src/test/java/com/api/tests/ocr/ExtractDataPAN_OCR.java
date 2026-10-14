package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.response.ocr.ExtractDataPANOCRResponse;
import com.api.models.response.ocr.GenerateTokenOCRResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class ExtractDataPAN_OCR extends BaseTest {

	AuthService authService;
	private Response response;
	Logger logger;
	private RequestSpecification rs;
	File panImage;
	String tokenOCR;
	Gson gson;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("ocr");
		rs = RestAssured.given();
		this.tokenOCR = SessionUtility.get("tokenOCR");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
	}

	public String getToken() {
		if (this.tokenOCR == null) {
			String company_id = JSONUtility.getOcr().getCompany_id();
			String email = JSONUtility.getOcr().getEmail();
			String password = JSONUtility.getOcr().getPassword();
			GenerateTokenOCRRequest request = new GenerateTokenOCRRequest(company_id, email, password);
			response = authService.generateTokenOCR(request);
			String responseBody = response.asString();
			GenerateTokenOCRResponse res = gson.fromJson(responseBody, GenerateTokenOCRResponse.class);
			this.tokenOCR = res.getToken();
		}
		return this.tokenOCR;
	}

	@Test(description = "tc_01 - Verify PAN OCR extraction with valid form-data and image", priority = 1, groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyPanExtractionWithValidFormData_OCR() {

		if (this.tokenOCR == null)
			getToken();

		panImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/PANcardSaurabh.jpeg");

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pan", JSONUtility.getOcr().getPan())
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("panfile", panImage).when().post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertTrue(responseBody.contains("success"), "Success flag missing");
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify request fails when fathername field is missing.", priority = 2, groups = {
			"smoke", "sanity", "regression" })
	public void verifyResponseWithMissingFatherName_OCR() {

		if (this.tokenOCR == null)
			getToken();

		panImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/PANcardSaurabh.jpeg");

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pan", JSONUtility.getOcr().getPan())
				.multiPart("name", JSONUtility.getOcr().getName()).multiPart("dob", JSONUtility.getOcr().getDob())
				.multiPart("sources", JSONUtility.getOcr().getSources()).multiPart("panfile", panImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD);
		String responseBody = response.asString();
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<html");

		if (isHtml) {
			Assert.fail("Html error 504 Gateway Timeout when fathername field is missing, response: " + responseBody
					+ "," + Severity.CRITICAL);
		}
//		ExtractDataPANOCRResponse res = gson.fromJson(responseBody, ExtractDataPANOCRResponse.class);

		softAssert.assertEquals(response.getStatusCode(), 400);
//		softAssert.assertNotNull(responseBody, "Response body is null");
//		softAssert.assertEquals(res.getMsg(), "Missing fathername field");
		softAssert.assertAll();
	}

	@Test(description = "tc_08 - Verify request fails when both tokens invalid.", priority = 3, groups = { "smoke",
			"sanity", "regression" })
	public void verifyResponseWithBothTokensInvalid_OCR() {

		if (this.tokenOCR == null)
			getToken();

		panImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/PANcardSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pan", JSONUtility.getOcr().getPan())
				.multiPart("name", JSONUtility.getOcr().getName()).multiPart("dob", JSONUtility.getOcr().getDob())
				.multiPart("sources", JSONUtility.getOcr().getSources()).multiPart("panfile", panImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD);
		String responseBody = response.asString();
		ExtractDataPANOCRResponse res = gson.fromJson(responseBody, ExtractDataPANOCRResponse.class);

		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertFalse(responseBody.contains("success"), "Success flag missing");
		softAssert.assertEquals(res.getMsg(), "Unauthorized token");
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Verify request fails when panfile field missing.", priority = 4, groups = { "sanity",
			"regression" })
	public void verifyResponseWithMissingPanfileField_OCR() {

		if (this.tokenOCR == null)
			getToken();

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pan", JSONUtility.getOcr().getPan())
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.when().post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD);
		String responseBody = response.asString();
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype html");

		if (isHtml) {
			Assert.fail("Html error 400 Bad Request when panfile field is missing, response: " + responseBody + ","
					+ Severity.CRITICAL);
		}
		ExtractDataPANOCRResponse res = gson.fromJson(responseBody, ExtractDataPANOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertFalse(responseBody.contains("success"), "Success flag missing");
		softAssert.assertEquals(res.getMsg(), "Missing panfile field");
		softAssert.assertAll();
	}

	@Test(description = "tc_11 - Verify request fails with unsupported file type.", priority = 5, groups = { "smoke",
			"sanity", "regression" })
	public void verifyResponseWithUnsupportedFileType_OCR() {

		if (this.tokenOCR == null)
			getToken();
		String filePath = System.getProperty("user.dir") + "/src/test/resources/testdata/Demo.csv";

		File panImage = new File(filePath);
		if (!panImage.exists()) {
			logger.error("No Such File found!!");
			return;
		}
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pan", JSONUtility.getOcr().getPan())
				.multiPart("name", JSONUtility.getOcr().getName()).multiPart("dob", JSONUtility.getOcr().getDob())
				.multiPart("sources", JSONUtility.getOcr().getSources()).multiPart("panfile", panImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD);
		String responseBody = response.asString();
		logger.info(responseBody);
		ExtractDataPANOCRResponse res = gson.fromJson(responseBody, ExtractDataPANOCRResponse.class);

		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertTrue(responseBody.contains("success"), "Success flag missing");
		softAssert.assertEquals(res.getMsg(), "Unsupported file type");
		softAssert.assertAll();
	}

	@Test(description = "tc_17 - Verify DOB in future returns validation error.", priority = 6, groups = { "sanity",
			"regression" })
	public void verifyResponseWhenFutureDateInserted_OCR() {

		if (this.tokenOCR == null)
			getToken();

		panImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/PANcardSaurabh.jpeg");
		String dob = "01/01/2030";
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pan", JSONUtility.getOcr().getPan())
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername()).multiPart("dob", dob)
				.multiPart("sources", JSONUtility.getOcr().getSources()).multiPart("panfile", panImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		ExtractDataPANOCRResponse res = gson.fromJson(responseBody, ExtractDataPANOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertTrue(responseBody.contains("success"), "Success flag missing");
		softAssert.assertEquals(res.getMsg(), "Invalid DOB");
		softAssert.assertAll();
	}

	@Test(description = "tc_18 - Verify API handles scripting in any field(pan,name, etc) safely.", priority = 6, groups = {
			"sanity", "regression" })
	public void verifyResponseWithScripting_OCR() {

		if (this.tokenOCR == null)
			getToken();
		String pan = "<script>AZIPC1120C</script>";
		panImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/PANcardSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pan", pan)
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("panfile", panImage).when().post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		ExtractDataPANOCRResponse res = gson.fromJson(responseBody, ExtractDataPANOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertTrue(responseBody.contains("success"), "Success flag missing");
		softAssert.assertEquals(res.getMsg(), "Invalid DOB");
		softAssert.assertAll();
	}
}
