package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.response.ocr.DateDetectionFromBankStatementResponse;
import com.api.models.response.ocr.GenerateTokenOCRResponse;
import com.api.models.response.ocr.MaskAadhaarOCRResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class DateDetectionFromIncomeProof_OCR extends BaseTest {

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

	@Test(description = "tc_income_ocr_01 - Verify OCR accepts valid 6-month bank statement with matching name", priority = 1, alwaysRun = true, groups = {
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
}
