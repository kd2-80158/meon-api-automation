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
public class ExtractDataVoterID_OCR extends BaseTest {

	AuthService authService;
	private Response response;
	Logger logger;
	private RequestSpecification rs;
	File voterIDImageFrontPage,voterIDImageBackPage;
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
	
	@Test(description="tc_vid_01 - Verify successful Voter ID OCR extraction with valid front and back images",priority=1,alwaysRun=true,groups= {"e2e","smoke","sanity","regression"})
	public void verifyResponseWithValidCredentials_OCR()
	{
		if (this.tokenOCR == null)
			getToken();

		voterIDImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voter_id_front.jpeg");
		voterIDImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voterid_back.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", voterIDImageFrontPage)
				.multiPart("file_back", voterIDImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources","kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertTrue(responseBody.contains("success"), "Success flag missing");
		
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_Name"),"RAHUL KUSHWAH");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_father_name"),"Rammilan Kushwah");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_voterid"),"RLK1742311");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_address"),"22,chhavani hospital,gwalior tehsil gwalior,district gwalior m.p. -474006");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_pin_code"),"474006");
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_03 - Verify request fails when Authorization token is invalid or expired",priority=2, groups= {"smoke","sanity","regresssion"})
	public void verifyResponseWithInvalidAuthorizationToken_OCR()
	{
		String invalidToken = "invalidToken";

		voterIDImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voter_id_front.jpeg");
		voterIDImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voterid_back.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + invalidToken)
				.multiPart("file_front", voterIDImageFrontPage)
				.multiPart("file_back", voterIDImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources","kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 401);
		softAssert.assertEquals(responseBody.contains("msg"), "Signature verification failed");
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_04 - Verify request fails when file_front is missing",priority=3, groups= {"sanity","regresssion"})
	public void verifyResponseWithMissingFrontImage_OCR()
	{
		if(this.tokenOCR==null)
			getToken();

		voterIDImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voterid_back.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_back", voterIDImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources","kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);		 
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<html");
		if(isHtml)
		{
			Assert.fail("Error occured giving HTML body as response when file_front is missing with response"+responseBody+Severity.CRITICAL);
		}
		else {
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(responseBody.contains("msg"), "file_front is required");
		}
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_05 - Verify request fails when file_back is missing",priority=4, groups= {"sanity","regresssion"})
	public void verifyResponseWithMissingBackImage_OCR()
	{
		if(this.tokenOCR==null)
			getToken();

		voterIDImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voter_id_front.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", voterIDImageFrontPage)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources","kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);		 
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html>");
		if(isHtml)
		{
			Assert.fail("Error occured giving HTML body as response when file_back is missing with response"+responseBody+Severity.CRITICAL);
		}
		else {
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().get("msg"), "file_back is required");
		}
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_06 - Verify request fails when both voter ID images are missing",priority=5, groups= {"sanity","regresssion"})
	public void verifyResponseWithMissingBothFrontAndBackImages_OCR()
	{
		if(this.tokenOCR==null)
			getToken();

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources","kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);		 
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html>");
		if(isHtml)
		{
			Assert.fail("Error occured giving HTML body as response when both front_file and back_file are missing with response"+responseBody+Severity.CRITICAL);
		}
		else {
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().get("msg"), "file_front is required");
		}
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_07 - Verify request fails when sources is missing",priority=6,groups= {"sanity","regression"})
	public void verifyResponseWithMissingSourcesField_OCR()
	{
		if (this.tokenOCR == null)
			getToken();

		voterIDImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voter_id_front.jpeg");
		voterIDImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voterid_back.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", voterIDImageFrontPage)
				.multiPart("file_back", voterIDImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(responseBody.contains("msg"), "Please provide sources");
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_09 - Verify request fails for unsupported file type for voter ID images",priority=7,groups= {"sanity","regression"})
	public void verifyResponseWithUnsupportedFileTypeForVoterIDImages_OCR()
	{
		if (this.tokenOCR == null)
			getToken();

		voterIDImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/invalidfile.csv");
		voterIDImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/invalidfile.csv");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", voterIDImageFrontPage)
				.multiPart("file_back", voterIDImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources", "kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(response.jsonPath().get("msg"), "Unsupported file type");
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_11 - Verify OCR extraction with rotated voter ID image",priority=8,groups= {"sanity","regression"})
	public void verifyResponseWithRotatedVoterIDImages_OCR()
	{
		if (this.tokenOCR == null)
			getToken();

		voterIDImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voter_id_front_rotated.jpeg");
		voterIDImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voterid_back_rotated.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", voterIDImageFrontPage)
				.multiPart("file_back", voterIDImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources", "kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertTrue(responseBody.contains("success"), "Expected true but found false");

		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_Name"),"RAHUL KUSHWAH");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_father_name"),"Rammilan Kushwah");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_voterid"),"RLK1742311");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_address"),"22,chhavani hospital,gwalior tehsil gwalior,district gwalior m.p. -474006");
		softAssert.assertEquals(response.jsonPath().getString("ExtractedData.ocr_pin_code"),"474006");
		softAssert.assertAll();
	}
	
	@Test(description="tc_vid_13 - Verify API response time for valid voter ID OCR request",priority=9,groups= {"sanity","regression"})
	public void verifyResponseTimeForVoterIDOCRAPI_OCR()
	{
		if (this.tokenOCR == null)
			getToken();

		voterIDImageFrontPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voter_id_front.jpeg");
		voterIDImageBackPage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/voterid/voterid_back.jpeg");
		

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("file_front", voterIDImageFrontPage)
				.multiPart("file_back", voterIDImageBackPage)
				.multiPart("req_id", "9936142801sudh2")
				.multiPart("sources","kyc")
				.when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_VOTERID);

		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertTrue(response.getTime()<5000,"Actual response time is: "+response.getTime());
		softAssert.assertAll();
	}
}
