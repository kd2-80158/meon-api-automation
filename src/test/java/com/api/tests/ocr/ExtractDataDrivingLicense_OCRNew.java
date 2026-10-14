package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class ExtractDataDrivingLicense_OCRNew extends BaseTest {

	private Response response;
	private Logger logger;
	private RequestSpecification rs;
	private Gson gson;

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given().relaxedHTTPSValidation();
		gson = new Gson();
	}

	@Test(description = "tc_01 - Verify successful extraction of driving license documents.", priority = 1, groups = {
			"e2e",
			"regression" }, enabled = false, dataProvider = "methodname", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithDLDocuments(File imageFile) {
		if (imageFile == null || imageFile.length() == 0 || !imageFile.exists()) {
			throw new RuntimeException(
					"Invalid Driving license file: " + (imageFile == null ? "null" : imageFile.getAbsolutePath()));
		}
		String baseURL = JSONUtility.getOcr().getLive_url();
		logger.info("===== Running New Driving License OCR =====");
		logger.info("Base URL: {}", baseURL);
		logger.info("Endpoint: /newocr/extract");
		logger.info("Document Type: dl");
		logger.info("File: {}", imageFile.getName());
		logger.info("File size: {} bytes", imageFile.length());

		response = rs.baseUri(baseURL).contentType(ContentType.MULTIPART).multiPart("file", imageFile)
				.multiPart("document_type", "dl").when().post("/newocr/extract");

		String responseBody = response.asPrettyString();
		
		logger.info("HTTP Status: {}", response.getStatusCode());
		logger.info("OCR Response for {} : {}", imageFile.getName(), response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
				"HTTP status mismatch for file: " + imageFile.getName());
		softAssert.assertNotNull(responseBody, "Response body is null");
		softAssert.assertFalse(responseBody.trim().isEmpty(), "Response body is empty");

	}
}
