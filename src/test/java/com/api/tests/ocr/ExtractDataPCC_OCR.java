package com.api.tests.ocr;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.response.ocr.GenerateTokenOCRResponse;
import com.api.models.response.ocr.PCCResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class ExtractDataPCC_OCR extends BaseTest {

	Logger logger = LoggerUtility.getLogger(this.getClass());

	AuthService authService;
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

	@Test(description = "tc_pcc_ocr_01 - Verify PCC OCR API response dynamically", priority = 4, alwaysRun = true, groups = {
			"e2e", "smoke",
			"regression" }, dataProvider = "pccDocuments", dataProviderClass = OCRFileDataProvider.class)
	public void verifyPCCDocumentExtraction(File imageFile) {

		if (this.tokenOCR == null || this.tokenOCR.trim().isEmpty()) {
			getToken();
		}

		if (imageFile == null || !imageFile.exists() || !imageFile.isFile() || imageFile.length() == 0) {
			throw new IllegalArgumentException("PCC document missing or empty: " + imageFile);
		}

		logger.info("===== Running PCC OCR Test =====");
		logger.info("File: {}", imageFile.getName());

		String reqId = "PCC_Testing_" + System.currentTimeMillis();

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR).multiPart("pcc_document", imageFile)
				.multiPart("req_id", reqId).when().post(AuthService.BASE_PATH_OCR_PCC);

		logger.info("PCC OCR Response: {}", response.asPrettyString());

		// softAssert.assertEquals(response.getStatusCode(), 200, "HTTP status mismatch
		// for " + imageFile.getName());

		PCCResponse actual;

		try {
			actual = gson.fromJson(response.asString(), PCCResponse.class);
		} catch (Exception e) {
			logger.error("PCC response parsing failed", e);
			throw new AssertionError("Unable to deserialize PCC response", e);
		}

		softAssert.assertNotNull(actual, "Response object is null");

		if (actual == null) {
			softAssert.assertAll();
			return;
		}

		// Validate common fields
		softAssert.assertEquals(actual.getDocument_name(), imageFile.getName(), "Document name mismatch");

		softAssert.assertNotNull(actual.getSuccess(), "Success field is missing");

		if (actual.getSuccess() == false) {
			softAssert.assertAll();
			return;
		}

		if (Boolean.TRUE.equals(actual.getSuccess())) {

			// Successful PCC processing
			logger.info("PCC successfully processed: {}", imageFile.getName());

			softAssert.assertNotNull(actual.getVerification(), "Verification object is missing");

			if (actual.getVerification() != null) {

				PCCResponse.Verification verification = actual.getVerification();

				softAssert.assertNotNull(verification.getIssue_date(), "Issue date is missing");

				softAssert.assertNotNull(verification.getExpires_at(), "Expiration date is missing");

				softAssert.assertNotNull(verification.getExpiry_days(), "Expiry days are missing");

				softAssert.assertNotNull(verification.getFinal_status(), "Final status is missing");

				softAssert.assertNotNull(verification.getRaw(), "Raw verification status is missing");

				// Validate field formats
				if (verification.getIssue_date() != null) {
					softAssert.assertTrue(verification.getIssue_date().matches("\\d{2}/\\d{2}/\\d{4}"),
							"Invalid issue date format");
				}

				if (verification.getExpires_at() != null) {
					softAssert.assertTrue(verification.getExpires_at().matches("[A-Za-z]{3} \\d{4}"),
							"Invalid expiry date format");
				}

				if (verification.getIssue_date() != null && verification.getExpires_at() != null) {

					try {

						DateTimeFormatter issueDateFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
								.withResolverStyle(ResolverStyle.STRICT);

						DateTimeFormatter expiryDateFormatter = DateTimeFormatter.ofPattern("MMM uuuu", Locale.ENGLISH);

						// Parse issue date
						LocalDate issueDate = LocalDate.parse(verification.getIssue_date().trim(), issueDateFormatter);

						// Calculate expected expiry date
						LocalDate calculatedExpiryDate = issueDate.plusDays(verification.getExpiry_days());

						// Format to match API response: Sep 2026
						String expectedExpiryDate = calculatedExpiryDate.format(expiryDateFormatter);

						String actualExpiryDate = verification.getExpires_at().trim();

						logger.info("PCC Issue Date: {}", issueDate);
						logger.info("PCC Expiry Days: {}", verification.getExpiry_days());
						logger.info("Calculated Expiry Date: {}", calculatedExpiryDate);
						logger.info("Expected expires_at: {}", expectedExpiryDate);
						logger.info("Actual expires_at: {}", actualExpiryDate);

						softAssert.assertEquals(actualExpiryDate, expectedExpiryDate,
								"PCC expiry date mismatch for file: " + imageFile.getName() + " | Issue Date: "
										+ verification.getIssue_date() + " | Expiry Days: "
										+ verification.getExpiry_days() + " | Calculated Expiry: "
										+ calculatedExpiryDate);

					} catch (Exception e) {

						logger.error("PCC expiry date calculation failed for: {}", imageFile.getName(), e);

						softAssert.assertTrue(false, "Unable to validate PCC expiry date for " + imageFile.getName()
								+ " | Reason: " + e.getMessage());
					}
				}

				if (verification.getFinal_status() != null) {
					softAssert.assertTrue(
							verification.getFinal_status().equalsIgnoreCase("verified")
									|| verification.getFinal_status().equalsIgnoreCase("unverified"),
							"Unexpected final verification status");
				}

			}

		} else {

			// Rejected PCC document
			logger.warn("PCC rejected: {}, code: {}, message: {}", imageFile.getName(), actual.getCode(),
					actual.getMsg());

			softAssert.assertNotNull(actual.getCode(), "Error code is missing");

			softAssert.assertNotNull(actual.getMsg(), "Error message is missing");

			if (actual.getCode() != null) {

				switch (actual.getCode().toLowerCase()) {

				case "unverified":
					softAssert.assertEquals(actual.getMsg(), "The document is not a verified PCC document.",
							"Invalid unverified message");
					break;

				case "not_readable":
					softAssert.assertEquals(actual.getMsg(),
							"The document is not readable. Provide document with relevant english text.",
							"Invalid not_readable message");
					break;

				case "discrepancy":
					softAssert.assertEquals(actual.getMsg(), "The PCC document has discrepancies.",
							"Invalid discrepancy message");
					break;

				default:
					softAssert.assertTrue(false, "Unexpected PCC error code: " + actual.getCode());
					break;
				}
			}
		}

		softAssert.assertAll();
	}

}
