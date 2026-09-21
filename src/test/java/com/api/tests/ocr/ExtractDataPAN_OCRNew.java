package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.response.ocr.PANOCRExtractResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class ExtractDataPAN_OCRNew extends BaseTest {

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

	@Test(description = "tc_01 - Verify successful PAN OCR extraction with valid PAN file.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity",
			"regression" }, dataProvider = "panFiles", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithValidInputsPANOCR(File panImage) {

		/*
		 * Validate PAN test file
		 */
		if (panImage == null || !panImage.exists() || panImage.length() == 0) {

			throw new RuntimeException("Invalid PAN file: " + (panImage == null ? "null" : panImage.getAbsolutePath()));
		}

		String baseUrl = JSONUtility.getOcr().getNewLive_url();

		logger.info("===== Running New PAN OCR =====");
		logger.info("Base URL: {}", baseUrl);
		logger.info("Endpoint: /newocr/extract");
		logger.info("Document Type: pan");
		logger.info("File: {}", panImage.getName());
		logger.info("File size: {} bytes", panImage.length());

		response = rs.baseUri(baseUrl).contentType(ContentType.MULTIPART)

				.multiPart("document_type", "pan")

				.multiPart("file", panImage)

				.when().post("/newocr/extract");

		/*
		 * RESPONSE
		 */
		String responseBody = response.asString();

		logger.info("HTTP Status: {}", response.getStatusCode());

		logger.info("OCR Response for {} : {}", panImage.getName(), response.asPrettyString());

		/*
		 * HTTP VALIDATION
		 */
		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
				"HTTP status mismatch for file: " + panImage.getName());

		softAssert.assertNotNull(responseBody, "Response body is null");

		softAssert.assertFalse(responseBody.trim().isEmpty(), "Response body is empty");

		/*
		 * CONVERT JSON RESPONSE INTO PAN RESPONSE MODEL
		 */
		PANOCRExtractResponse res = gson.fromJson(responseBody, PANOCRExtractResponse.class);

		softAssert.assertNotNull(res, "Response model is null");

		if (res != null) {

			/*
			 * SUCCESS FLAG
			 */
			softAssert.assertTrue(res.isSuccess(), Severity.CRITICAL, "OCR success flag must be true");

			/*
			 * DOCUMENT TYPE
			 */
			softAssert.assertEquals(res.getDocumentType(), "pan", "document_type mismatch");

			/*
			 * FILE NAME
			 */
			softAssert.assertEquals(res.getFilename(), panImage.getName(), "Filename mismatch");

			/*
			 * DATA OBJECT
			 */
			softAssert.assertNotNull(res.getData(), "data object must not be null");

			if (res.getData() != null) {

				/*
				 * PAN NUMBER
				 */
				softAssert.assertNotNull(res.getData().getPanNumber(), "PAN number was not extracted");

				if (res.getData().getPanNumber() != null) {

					softAssert.assertFalse(res.getData().getPanNumber().trim().isEmpty(), "PAN number is empty");

					softAssert.assertTrue(
							res.getData().getPanNumber().trim().toUpperCase().matches("^[A-Z]{5}[0-9]{4}[A-Z]$"),
							Severity.CRITICAL, "Invalid PAN number format");
				}

				softAssert.assertNotNull(res.getData().getName(), "Name was not extracted");

				if (res.getData().getName() != null) {

					softAssert.assertFalse(res.getData().getName().trim().isEmpty(), "Name is empty");
				}

				softAssert.assertNotNull(res.getData().getDateOfBirth(), "Date of birth was not extracted");

				if (res.getData().getDateOfBirth() != null) {

					softAssert.assertFalse(res.getData().getDateOfBirth().trim().isEmpty(), "Date of birth is empty");
				}

//				softAssert.assertNotNull(res.getData().getFatherName(), "Father name was not extracted");
//
//				if (res.getData().getFatherName() != null) {
//
//					softAssert.assertFalse(res.getData().getFatherName().trim().isEmpty(), "Father name is empty");
//				}

				logger.info(
						"OCR Extracted PAN Data | " + "Name: {} | " + "PAN: {} | " + "DOB: {} | " + "Father Name: {}",
						res.getData().getName(), maskPan(res.getData().getPanNumber()),
						res.getData().getDateOfBirth(), res.getData().getFatherName());
			}
		}

		softAssert.assertAll();
	}

	private String maskPan(String panNumber) {

		if (panNumber == null || panNumber.trim().isEmpty()) {

			return "N/A";
		}

		String pan = panNumber.trim().toUpperCase();

		if (pan.length() < 6) {
			return "****";
		}

		return pan.substring(0, 3) + "*****" + pan.substring(pan.length() - 2);
	}
}