package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.response.ocr.ExtractDataAadhaarOCRResponse;
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
public class ExtractDataAadhaar_OCR extends BaseTest {

	AuthService authService;
	private Response response;
	Logger logger;
	private RequestSpecification rs;
	File aadhaarImage;
	String tokenOCR;
	Gson gson;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("ocr");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
		rs = RestAssured.given().relaxedHTTPSValidation();
		this.tokenOCR = SessionUtility.get("tokenOCR");
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

	@Test(description = "tc_01 - Verify successful Aadhaar OCR extraction with valid inputs.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity",
			"regression" }, dataProvider = "aadhaarFiles", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithValidInputsAadhaar_OCR(File aadhaarImage) {

		if (this.tokenOCR == null) {
			getToken();
		}
		if (!aadhaarImage.exists() || aadhaarImage.length() == 0) {
			throw new RuntimeException("Invalid Aadhaar file: " + aadhaarImage.getName());
		}
		logger.info("===== Running OCR test for file: {} =====", aadhaarImage.getName());

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("adharno", JSONUtility.getOcr().getAdhar())
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob())
				.multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress())
				.multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);
		String responseBody = response.asString();
		logger.info("Response body for {} : {}", aadhaarImage.getName(), responseBody);
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
				"Status code mismatch for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(responseBody, "Response body is null for file: " + aadhaarImage.getName());
		softAssert.assertTrue(responseBody.contains("success"),
				"Success flag missing for file: " + aadhaarImage.getName());
		softAssert.assertTrue(res.isSuccess(), Severity.CRITICAL, "OCR failed for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(res.getExtracted_data().getOcr_address(),
				"Address is null for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(res.getExtracted_data().getOcr_adhar_number(),
				"Aadhaar number is null for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(res.getExtracted_data().getOcr_father_name(),
				"Father name is null for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(res.getExtracted_data().getOcr_dob(),
				"DOB is null for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(res.getExtracted_data().getOcr_gender(),
				"Gender is null for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(res.getExtracted_data().getOcr_name(),
				"Name is null for file: " + aadhaarImage.getName());
		softAssert.assertNotNull(res.getExtracted_data().getOcr_pin_code(),
				"Pincode is null for file: " + aadhaarImage.getName());
		softAssert.assertTrue(res.getExtracted_data().getOcr_father_name().length() > 0, Severity.HIGH,
				"Father name is empty for file: " + aadhaarImage.getName());
		softAssert.assertTrue(res.getExtracted_data().getOcr_address().length() > 0, Severity.HIGH,
				"Address is empty for file: " + aadhaarImage.getName());
		
		if(res.getMatching_results()!=null) {
		double nameMatch = res.getMatching_results().getName_match_percentage();
		double aadhaarMatch = res.getMatching_results().getAdharno_number_match_percentage();
		double addressMatch = res.getMatching_results().getOcr_address_match_percentage();

		logger.info("OCR Quality | File: {} | Name: {}% | Aadhaar: {}% | Address: {}%", aadhaarImage.getName(),
				nameMatch, aadhaarMatch, addressMatch);
		}
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Verify request fails when adharfile field is missing.", priority = 2, groups = {
			"smoke", "sanity", "regression" })
	public void verifyResponseWithMissingAadhaarFileAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("adharno", JSONUtility.getOcr().getAdhar()).multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();

		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype");

		if (isHtml) {
			Assert.fail("HTTP 400 html error when adhaar file is missing; response is: " + responseBody + ","
					+ Severity.CRITICAL);

		} else {
			ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
			softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
			softAssert.assertNotNull(responseBody, "Response body is null");
			softAssert.assertEquals(res.getMsg(), "adharfile is required");
			softAssert.assertAll();
		}
	}

	@Test(description = "tc_10 - Verify invalid Aadhaar number format is rejected.", priority = 3, groups = { "sanity",
			"regression" })
	public void verifyResponseWithWrongAadhaarNumberFormatAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
		String aadhaar = "4318-2933-7118";
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("adharno", aadhaar)
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);
		String responseBody = response.asString();
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);

		softAssert.assertFalse(res.isSuccess(), Severity.INFO, "");
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertEquals(res.getMsg(), "Invalid Aadhaar format");
		softAssert.assertAll();
	}

	@Test(description = "tc_13 - Verify unsupported file type returns error.", priority = 4, groups = { "sanity",
			"regression" })
	public void verifyResponseWithUnsupportedFileAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
		aadhaarImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarPDFSaurabh.exe");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("adharno", JSONUtility.getOcr().getAdhar()).multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);
		String responseBody = response.asString();
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);

		softAssert.assertFalse(res.isSuccess(), Severity.INFO, "");
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertEquals(res.getMsg(), "Unsupported file format; only include .png,.jpeg,.jpg,etc");
		softAssert.assertAll();
	}

	@Test(description = "tc_16 - Verify extraction works when address not provided.", priority = 5, groups = { "sanity",
			"regression" })
	public void verifyResponseWithMissingAddressAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
//		aadhaarImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarPDFSaurabh.exe");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("adharno", JSONUtility.getOcr().getAdhar()).multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);
		String responseBody = response.asString();
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);

		softAssert.assertFalse(res.isSuccess(), Severity.INFO, "");
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertEquals(res.getMsg(), "Address required");
		softAssert.assertAll();
	}

	@Test(description = "tc_17 - Verify XSS in adhaar no/name do not cause security issues.", priority = 6, groups = {
			"sanity", "regression" })
	public void verifyResponseWithXSSPayloadAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
		String aadhaar = "<script>431829337118</script>";
		String name = "Saurabh <test>";
//		aadhaarImage = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/AdhaarSaurabhCombined.jpg");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("adharno", aadhaar)
				.multiPart("name", name).multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);
		String responseBody = response.asString();
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);

		softAssert.assertTrue(res.isSuccess(), Severity.INFO, "");
		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL, "");
		softAssert.assertAll();
	}

	@Test(description = "tc_20 - Verify API does not expose internal details or HTML error pages on malformed requests.", priority = 7, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMalformedRequestAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype");

		if (isHtml) {
			Assert.fail("HTTP 400 html error when adhaar file is missing; response is: " + responseBody + ","
					+ Severity.CRITICAL);

		} else {
			ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
			softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
			softAssert.assertNotNull(responseBody, "Response body is null");
			softAssert.assertEquals(res.getMsg(), "adharfile is required");
			softAssert.assertAll();
		}
	}

	@Test(description = "tc_21 - Verify request behavior when adharno field is missing.", priority = 8, groups = {
			"smoke", "sanity", "regression" })
	public void verifyResponseWithMissingAadhaarNumberFieldAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
//		aadhaarImage = new File(
//				System.getProperty("user.dir") + "/src/test/resources/testdata/AdhaarSaurabhCombined.jpg");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();

		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(res.getMsg(), "adharno is required");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_22 - Verify request behavior when adharno value is empty.", priority = 9, groups = {
			"sanity", "regression" })
	public void verifyResponseWithOmittingAadhaarNumberFieldAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
//		aadhaarImage = new File(
//				System.getProperty("user.dir") + "/src/test/resources/testdata/AdhaarSaurabhCombined.jpg");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("adharno", "", new byte[0])
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();
		logger.info("Response: " + responseBody);
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(res.getMsg(), "adharno is required");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_23 - Verify request behavior when name field is missing.", priority = 10, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingNameFieldAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
//		aadhaarImage = new File(
//				System.getProperty("user.dir") + "/src/test/resources/testdata/AdhaarSaurabhCombined.jpg");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("adharno", JSONUtility.getOcr().getAdhar())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();

		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(res.getMsg(), "name is required");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_24 - Verify request behavior when name value is empty.", priority = 11, groups = { "sanity",
			"regression" })
	public void verifyResponseWithOmittingNameFieldAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();

//		aadhaarImage = new File(
//				System.getProperty("user.dir") + "/src/test/resources/testdata/AdhaarSaurabhCombined.jpg");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("name", "")
				.multiPart("adharno", JSONUtility.getOcr().getAdhar())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();
		logger.info("Response: " + responseBody);
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(res.getMsg(), "name cannot be empty");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_25 - Verify request behavior when fathername field is missing.", priority = 12, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingFatherNameFieldAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
//			aadhaarImage = new File(
//					System.getProperty("user.dir") + "/src/test/resources/testdata/AdhaarSaurabhCombined.jpg");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("adharno", JSONUtility.getOcr().getAdhar()).multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();

		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(res.getMsg(), "fathername is required");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_26 - Verify request behavior when fathername value is empty.", priority = 13, groups = {
			"sanity", "regression" })
	public void verifyResponseWithOmittingFatherNameValueAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();

//		aadhaarImage = new File(
//				System.getProperty("user.dir") + "/src/test/resources/testdata/AdhaarSaurabhCombined.jpg");
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("fathername", "")
				.multiPart("adharno", JSONUtility.getOcr().getAdhar()).multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("dob", JSONUtility.getOcr().getDob()).multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();
		logger.info("Response: " + responseBody);
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(res.getMsg(), "fathername cannot be empty");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertAll();
	}

	@Test(description = "tc_27 - Verify request behavior when dob field is missing.", priority = 14, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingDOBFieldAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();
		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("adharno", JSONUtility.getOcr().getAdhar()).multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();
		boolean isHtml = responseBody != null && responseBody.trim().startsWith("<!doctype");
		if (isHtml) {
			Assert.fail(
					"500 Error occured when DOB field is missing as response is: " + responseBody + Severity.CRITICAL);
		} else {
			ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
			softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
			softAssert.assertNotNull(responseBody, "Response body is null");
			softAssert.assertEquals(res.getMsg(), "DOB is required");
			softAssert.assertFalse(res.isSuccess());
			softAssert.assertAll();
		}
	}

	@Test(description = "tc_28 - Verify request behavior when dob value is empty.", priority = 15, groups = { "sanity",
			"regression" })
	public void verifyResponseWithOmittingDOBValueAadhaar_OCR() {
		if (this.tokenOCR == null)
			getToken();

		aadhaarImage = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/AadhaarDigitalSaurabh.jpeg");
		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("fathername", JSONUtility.getOcr().getFathername())
				.multiPart("adharno", JSONUtility.getOcr().getAdhar())
				.multiPart("name", JSONUtility.getOcr().getName())
				.multiPart("dob", "").multiPart("sources", JSONUtility.getOcr().getSources())
				.multiPart("req_id", JSONUtility.getOcr().getReq_id())
				.multiPart("company", JSONUtility.getOcr().getCompany())
				.multiPart("address", JSONUtility.getOcr().getAddress()).multiPart("adharfile", aadhaarImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD);

		String responseBody = response.asString();
		logger.info("Response: " + responseBody);
		ExtractDataAadhaarOCRResponse res = gson.fromJson(responseBody, ExtractDataAadhaarOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL, "");
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertEquals(res.getMsg(), "DOB cannot be empty");
		softAssert.assertFalse(res.isSuccess());
		softAssert.assertAll();
	}
}
