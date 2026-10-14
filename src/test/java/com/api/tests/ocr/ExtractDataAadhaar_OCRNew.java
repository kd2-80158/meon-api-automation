package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.response.ocr.NewOCRExtractResponseAadhaar;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class ExtractDataAadhaar_OCRNew extends BaseTest {

	private Response response;
	private Logger logger;
	private RequestSpecification rs;
	private Gson gson;

	@BeforeMethod(alwaysRun = true)
	public void setup() {

		logger = LoggerUtility.getLogger(this.getClass());

		gson = new Gson();

		rs = RestAssured.given().relaxedHTTPSValidation();
	}

	@Test(description = "tc_01 - Verify successful Aadhaar OCR extraction with valid Aadhaar file.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity",
			"regression" }, dataProvider = "aadhaarFiles", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithValidInputsAadhaarOCR(File aadhaarImage) {

		if (aadhaarImage == null || !aadhaarImage.exists() || aadhaarImage.length() == 0) {

			throw new RuntimeException(
					"Invalid Aadhaar file: " + (aadhaarImage == null ? "null" : aadhaarImage.getAbsolutePath()));
		}

		String baseUrl = JSONUtility.getOcr().getUrl();

		logger.info("===== Running New Aadhaar OCR =====");
		logger.info("Base URL: {}", baseUrl);
		logger.info("Endpoint: /newocr/extract");
		logger.info("File: {}", aadhaarImage.getName());
		logger.info("File size: {} bytes", aadhaarImage.length());

		response = rs.baseUri(baseUrl).contentType(ContentType.MULTIPART)

				.multiPart("document_type", "aadhaar")

				.multiPart("file", aadhaarImage)

				.when().post("/extract_adhar_data");

		String responseBody = response.asString();

		logger.info("HTTP Status: {}", response.getStatusCode());

		logger.info("OCR Response for {} : {}", aadhaarImage.getName(), response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
				"HTTP status mismatch for file: " + aadhaarImage.getName());

		softAssert.assertNotNull(responseBody, "Response body is null");

		softAssert.assertFalse(responseBody.trim().isEmpty(), "Response body is empty");

		NewOCRExtractResponseAadhaar res = gson.fromJson(responseBody, NewOCRExtractResponseAadhaar.class);

		softAssert.assertNotNull(res, "Response model is null");

		if (res != null) {

			softAssert.assertTrue(res.isSuccess(), Severity.CRITICAL, "OCR success flag must be true");

			softAssert.assertEquals(res.getDocument_type(), "aadhaar", "document_type mismatch");

			softAssert.assertEquals(res.getFilename(), aadhaarImage.getName(), "Filename mismatch");

			softAssert.assertNotNull(res.getData(), "data object must not be null");

			if (res.getData() != null) {

				softAssert.assertNotNull(res.getData().getAadhaar_number(), "Aadhaar number was not extracted");

				softAssert.assertFalse(res.getData().getAadhaar_number().trim().isEmpty(), "Aadhaar number is empty");

				softAssert.assertNotNull(res.getData().getName(), "Name was not extracted");

				softAssert.assertFalse(res.getData().getName().trim().isEmpty(), "Name is empty");

				softAssert.assertNotNull(res.getData().getDate_of_birth(), "Date of birth was not extracted");

				softAssert.assertNotNull(res.getData().getGender(), "Gender was not extracted");


				logger.info(
						"OCR Extracted Data | " + "Name: {} | " + "Aadhaar: {} | " + "DOB: {} | " + "Gender: {} | "
								+ "Father Name: {} | " + "Address: {}",
						res.getData().getName(), res.getData().getAadhaar_number(), res.getData().getDate_of_birth(),
						res.getData().getGender(), res.getData().getFather_name(), res.getData().getAddress());
			}
		}

		softAssert.assertAll();
	}
	
	//for testing aadhaar data
	@Test(description = "tc_02 - Verify successful Aadhaar OCR extraction with valid Aadhaar file.", priority = 2, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity",
			"regression" }, dataProvider = "aadhaarMaskedFiles", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithValidInputsAadhaarOCRNew(File aadhaarImage) {

		if (aadhaarImage == null || !aadhaarImage.exists() || aadhaarImage.length() == 0) {

			throw new RuntimeException(
					"Invalid Aadhaar file: " + (aadhaarImage == null ? "null" : aadhaarImage.getAbsolutePath()));
		}

		String baseUrl = JSONUtility.getOcr().getNewLive_url();

		logger.info("===== Running New Aadhaar OCR =====");
		logger.info("Base URL: {}", baseUrl);
		logger.info("Endpoint: /newocr/extract");
		logger.info("File: {}", aadhaarImage.getName());
		logger.info("File size: {} bytes", aadhaarImage.length());

		response = rs.baseUri(baseUrl).contentType(ContentType.MULTIPART)

				.multiPart("document_type", "aadhaar")

				.multiPart("file", aadhaarImage)

				.when().post("/newocr/extract");

		String responseBody = response.asString();

		logger.info("HTTP Status: {}", response.getStatusCode());

		logger.info("OCR Response for {} : {}", aadhaarImage.getName(), response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
				"HTTP status mismatch for file: " + aadhaarImage.getName());

		softAssert.assertNotNull(responseBody, "Response body is null");

		softAssert.assertFalse(responseBody.trim().isEmpty(), "Response body is empty");

		NewOCRExtractResponseAadhaar res = gson.fromJson(responseBody, NewOCRExtractResponseAadhaar.class);

		softAssert.assertNotNull(res, "Response model is null");

		if (res != null) {

			softAssert.assertTrue(res.isSuccess(), Severity.CRITICAL, "OCR success flag must be true");

			softAssert.assertEquals(res.getDocument_type(), "aadhaar", "document_type mismatch");

			softAssert.assertEquals(res.getFilename(), aadhaarImage.getName(), "Filename mismatch");

			softAssert.assertNotNull(res.getData(), "data object must not be null");

			if (res.getData() != null) {

				softAssert.assertNotNull(res.getData().getAadhaar_number(), "Aadhaar number was not extracted");

				softAssert.assertFalse(res.getData().getAadhaar_number().trim().isEmpty(), "Aadhaar number is empty");

				softAssert.assertNotNull(res.getData().getName(), "Name was not extracted");

				softAssert.assertFalse(res.getData().getName().trim().isEmpty(), "Name is empty");

				softAssert.assertNotNull(res.getData().getDate_of_birth(), "Date of birth was not extracted");

				softAssert.assertNotNull(res.getData().getGender(), "Gender was not extracted");

				logger.info(
						"OCR Extracted Data | " + "Name: {} | " + "Aadhaar: {} | " + "DOB: {} | " + "Gender: {} | "
								+ "Father Name: {} | " + "Address: {}",
						res.getData().getName(), res.getData().getAadhaar_number(), res.getData().getDate_of_birth(),
						res.getData().getGender(), res.getData().getFather_name(), res.getData().getAddress());
			}
		}
		softAssert.assertAll();
	}
}