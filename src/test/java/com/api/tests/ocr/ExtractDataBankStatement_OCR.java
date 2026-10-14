package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.response.ocr.DateDetectionFromBankStatementResponse;
import com.api.models.response.ocr.ExtractBankDataOCRResponse;
import com.api.models.response.ocr.GenerateTokenOCRResponse;
import com.api.models.testdata.BankStatementExpectedMetadata;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ExtractDataBankStatement_OCR extends BaseTest {

	AuthService authService;
	Logger logger;
	RequestSpecification rs;
	Gson gson;
	Response response;
	String tokenOCR;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("ocr");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given().relaxedHTTPSValidation();
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

	@Test(description = "tc_income_ocr_01 - Verify OCR accepts valid 6-month bank statement with matching name", priority = 4, alwaysRun = false, groups = {
			"e2e", "smoke",
			"regression" }, dataProvider = "bankStatementForIncomeProof", dataProviderClass = OCRFileDataProvider.class)
	public void verifyDateDetectionFromIncomeDocument(File imageFile) {

		if (this.tokenOCR == null)
			getToken();

		if (!imageFile.exists() || imageFile.length() == 0)
			throw new RuntimeException("Failed to find bank statment file" + imageFile.getName());

		logger.info("===== Running OCR test for file: {} =====", imageFile.getName());

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("file", imageFile)
				.multiPart("sources", "kyc").multiPart("req_id", "123456").when()
				.post(AuthService.BASE_PATH_OCR_BANK_STATEMENT);
		logger.info("Response is: " + response.asPrettyString());
		String responseBody = response.asPrettyString();
		DateDetectionFromBankStatementResponse bankStatementResponse = gson.fromJson(responseBody,
				DateDetectionFromBankStatementResponse.class);
		softAssert.assertTrue(bankStatementResponse.getData().isValid());
		softAssert.assertTrue(bankStatementResponse.isSuccess());
		softAssert.assertAll();
	}
	
	@Test(description = "Verify exact Bank Statement extraction", priority = 1, alwaysRun = true, groups = { "e2e",
			"smoke",
			"regression" }, dataProvider = "bankStatementExtractionRegression", dataProviderClass = OCRFileDataProvider.class)
	public void verifyBankStatementExtractionNew(File bankStatement, BankStatementExpectedMetadata expected)
	{
		if (!bankStatement.exists() || bankStatement.length() == 0) {

			throw new RuntimeException("Bank Statement file missing: " + bankStatement.getAbsolutePath());
		}
		
		response = rs.baseUri(JSONUtility.getOcr().getNewLive_url()).contentType(ContentType.MULTIPART)
				     .multiPart("document_type","bank")
				     .multiPart("file",bankStatement)
				     .when()
				     .post("/newocr/extract");
		
		logger.info("Response: {}", response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200, "HTTP Status mismatch");
		
		logger.info("Response: {}", response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200, "HTTP Status mismatch");

		ExtractBankDataOCRResponse actual = gson.fromJson(response.asString(), ExtractBankDataOCRResponse.class);

		softAssert.assertNotNull(actual, "Response object is null");

		if (actual != null) {

			softAssert.assertTrue(actual.isSuccess(), "success must be true");

			softAssert.assertNotNull(actual.getExtracted_data(), "extracted_data is missing");

			if (actual.getExtracted_data() != null) {

				String actualName = normalizeName(actual.getExtracted_data().getOcr_Account_Holder_Name());

				String expectedName = normalizeName(expected.getAccountHolderName());

				softAssert.assertEquals(actualName, expectedName,
						"Account Holder Name mismatch for " + bankStatement.getName());

				String actualIfsc = actual.getExtracted_data().getOcr_IFSC();

				softAssert.assertNotNull(actualIfsc, "IFSC is null");

				if (actualIfsc != null) {

					softAssert.assertEquals(actualIfsc.trim().toUpperCase(),

							expected.getIfsc().trim().toUpperCase(),

							"IFSC mismatch for " + bankStatement.getName());
				}

				String actualAccount = actual.getExtracted_data().getOcr_account_number();

				softAssert.assertNotNull(actualAccount, "Account Number is null");

				if (actualAccount != null) {

					softAssert.assertEquals(actualAccount.replace(" ", "").replace("-", "").trim(),

							expected.getAccountNumber().replace(" ", "").replace("-", "").trim(),

							"Account Number mismatch for " + bankStatement.getName());
				}

				String actualMicr = actual.getExtracted_data().getOcr_MICR();

				if (expected.isMicrPresent()) {

					softAssert.assertNotNull(actualMicr, "MICR is null");

					if (actualMicr != null) {

						softAssert.assertEquals(actualMicr.trim(), expected.getMicr().trim(),

								"MICR mismatch for " + bankStatement.getName());
					}

				} else {

					softAssert.assertTrue(actualMicr == null || actualMicr.trim().isEmpty(),

							"MICR should not be extracted " + "because it is absent in source. " + "Actual: "
									+ actualMicr);
				}
			}
		}
		
		
	}

	@Test(description = "Verify exact Bank Statement extraction", priority = 6, alwaysRun = false, groups = { "e2e",
			"smoke",
			"regression" }, dataProvider = "bankStatementExtractionRegression", dataProviderClass = OCRFileDataProvider.class)
	public void verifyBankStatementExtraction(File bankStatement, BankStatementExpectedMetadata expected) {

		if (this.tokenOCR == null) {
			getToken();
		}

		if (!bankStatement.exists() || bankStatement.length() == 0) {

			throw new RuntimeException("Bank Statement file missing: " + bankStatement.getAbsolutePath());
		}

		String reqId = "bank_ocr_" + System.currentTimeMillis() + "_" + bankStatement.getName();

		logger.info("===== Running Bank Statement OCR =====");

		logger.info("File: {}", bankStatement.getName());

		logger.info("Expected Data: {}", expected);

		response = RestAssured.given().relaxedHTTPSValidation().baseUri(JSONUtility.getOcr().getLive_url())
				.header("Authorization", "Bearer " + this.tokenOCR).contentType(ContentType.MULTIPART)
				.multiPart("sources", "kyc").multiPart("req_id", reqId).multiPart("bankprooffile", bankStatement).when()
				.post("/extract_bank_data");

		logger.info("Response: {}", response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200, "HTTP Status mismatch");

		ExtractBankDataOCRResponse actual = gson.fromJson(response.asString(), ExtractBankDataOCRResponse.class);

		softAssert.assertNotNull(actual, "Response object is null");

		if (actual != null) {

			softAssert.assertTrue(actual.isSuccess(), "success must be true");

			softAssert.assertNotNull(actual.getExtracted_data(), "extracted_data is missing");

			if (actual.getExtracted_data() != null) {

				String actualName = normalizeName(actual.getExtracted_data().getOcr_Account_Holder_Name());

				String expectedName = normalizeName(expected.getAccountHolderName());

				softAssert.assertEquals(actualName, expectedName,
						"Account Holder Name mismatch for " + bankStatement.getName());

				String actualIfsc = actual.getExtracted_data().getOcr_IFSC();

				softAssert.assertNotNull(actualIfsc, "IFSC is null");

				if (actualIfsc != null) {

					softAssert.assertEquals(actualIfsc.trim().toUpperCase(),

							expected.getIfsc().trim().toUpperCase(),

							"IFSC mismatch for " + bankStatement.getName());
				}

				String actualAccount = actual.getExtracted_data().getOcr_account_number();

				softAssert.assertNotNull(actualAccount, "Account Number is null");

				if (actualAccount != null) {

					softAssert.assertEquals(actualAccount.replace(" ", "").replace("-", "").trim(),

							expected.getAccountNumber().replace(" ", "").replace("-", "").trim(),

							"Account Number mismatch for " + bankStatement.getName());
				}

				String actualMicr = actual.getExtracted_data().getOcr_MICR();

				if (expected.isMicrPresent()) {

					softAssert.assertNotNull(actualMicr, "MICR is null");

					if (actualMicr != null) {

						softAssert.assertEquals(actualMicr.trim(), expected.getMicr().trim(),

								"MICR mismatch for " + bankStatement.getName());
					}

				} else {

					softAssert.assertTrue(actualMicr == null || actualMicr.trim().isEmpty(),

							"MICR should not be extracted " + "because it is absent in source. " + "Actual: "
									+ actualMicr);
				}
			}
		}

		softAssert.assertAll();
	}

	private String normalizeName(String value) {

		if (value == null) {
			return null;
		}

		String normalized = value.trim().replaceAll("\\s+", " ").toUpperCase();

		normalized = normalized.replaceFirst("^(MR|MRS|MS|MISS|DR)\\.?\\s+", "");
		return normalized.trim();
	}
}
