package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.response.ocr.DetectDocumentOCRResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class DetectDocument_OCR extends BaseTest{

	AuthService authService;
	Logger logger;
	Gson gson;
	RequestSpecification rs;
	Response response;
	SoftAssert softAssert;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("ocr");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
		rs = RestAssured.given();
		softAssert = new SoftAssert();
		
	}

	@Test(description = "", priority = 1, groups = { "regression",
			"smoke" }, dataProvider = "passprotectedbankStatementForIncomeProof", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithPasswordProtectedDocument_OCR(File imageFile, String password) {
		
		if(!imageFile.exists() || imageFile.length()==0)
			throw new RuntimeException("No file found"+imageFile);
		
		response = rs.baseUri(JSONUtility.getOcr().getUrl())
				.contentType(ContentType.MULTIPART)
				.multiPart("file", imageFile)
				.multiPart("password", password)
				.when()
				.post("/detect_document_api");
		
		String responseBody = response.asPrettyString();
		DetectDocumentOCRResponse res = gson.fromJson(responseBody, DetectDocumentOCRResponse.class);

		System.out.println("Response is: "+response.asPrettyString());
		softAssert.assertEquals(response.getStatusCode(), 200);
		System.out.println("Status code: "+response.getStatusCode());
		softAssert.assertTrue(res.isSuccess());
		System.out.println("Success is: "+res.isSuccess());
		softAssert.assertEquals(res.getDocument_type(), "bank_statement");
		System.out.println("Success is: "+res.isSuccess());
		softAssert.assertEquals(res.getText_match_type(), "pdf_ocr_text");
		System.out.println("Text match type is: "+res.getText_match_type());
		softAssert.assertTrue(res.getConfidence()>0.50);
		System.out.println("Confidence is: "+res.getConfidence());
		softAssert.assertAll();
	}

}
