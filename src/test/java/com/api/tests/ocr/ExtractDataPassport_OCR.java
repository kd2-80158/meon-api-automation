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

@Listeners({com.api.listeners.TestListener.class})
public class ExtractDataPassport_OCR extends BaseTest {
	
	AuthService authService;
	private Response response;
	Logger logger;
	private RequestSpecification rs;
	File passportImageFrontPage,passportImageBackPage;
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
	
	@Test(description = "tc_01 - Verify successful passport OCR extraction with valid front and rear images.", priority = 1, alwaysRun=true,groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyPassportExtractionWithValidFormData_OCR() {

		if (this.tokenOCR == null)
			getToken();

		passportImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/passport/Shashank_passport_front.jpeg");
		passportImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/passport/Shashank_passport_back.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", passportImageFrontPage)
				.multiPart("file_rear", passportImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PASSPORT);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertTrue(responseBody.contains("success"), "Success flag missing");
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify request fails when Authorization token is invalid/expired.", priority = 2, groups = {
			"smoke", "sanity", "regression" })
	public void verifyPassportExtractionWithInvalidToken_OCR() {

		if (this.tokenOCR == null)
			getToken();

		passportImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/passport/Shashank_passport_front.jpeg");
		passportImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/passport/Shashank_passport_back.jpeg");
		
		String invalidToken = "invalidToken123";

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + invalidToken)
				.multiPart("file_front", passportImageFrontPage)
				.multiPart("file_rear", passportImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PASSPORT);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertFalse(responseBody.contains("success"), "Signature verification failed");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_09 - Verify request fails for unsupported file type for passport images.", priority = 3, groups = {
			"sanity", "regression" })
	public void verifyPassportExtractionWithUnsupportedFileType_OCR() {

		if (this.tokenOCR == null)
			getToken();

		passportImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarPDFSaurabh.exe");
		passportImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/passport/Shashank_passport_back.jpeg");

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", passportImageFrontPage)
				.multiPart("file_rear", passportImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PASSPORT);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Signature verification failed");
		softAssert.assertAll();
	}
	
	@Test(description = "tc_09 - Verify OCR fails gracefully when different HTTP method(i.e., GET) is used inplace of POST.", priority = 4, groups = {
			"sanity", "regression" })
	public void verifyPassportExtractionWithDifferentHTTPMethod_OCR() {

		if (this.tokenOCR == null)
			getToken();

		passportImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/passport/Shashank_passport_front.jpeg");
		passportImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/passport/Shashank_passport_back.jpeg");

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", passportImageFrontPage)
				.multiPart("file_rear", passportImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.when()
				.get(AuthService.BASE_PATH_OCR_EXTRACT_DATA_PASSPORT);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype html");
		
		if(isHtml)
		{
			Assert.fail("HTML 500 error occurred when when different HTTP method(i.e., GET) is used inplace of POST, the response is: "+responseBody+Severity.CRITICAL);
		}
		
		softAssert.assertEquals(response.getStatusCode(), 405);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Signature verification failed");
		softAssert.assertAll();
	}
}
