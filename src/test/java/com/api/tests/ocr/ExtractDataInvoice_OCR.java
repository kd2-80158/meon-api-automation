package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.InvoiceOCRFileDataProvider;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.response.ocr.ExtractDataInvoiceOCRResponse;
import com.api.models.response.ocr.GenerateTokenOCRResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class ExtractDataInvoice_OCR extends BaseTest {

	AuthService authService;
	private Response response;
	Logger logger;
	private RequestSpecification rs;
	File invoiceImage;
	String tokenOCR;
	Gson gson;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("ocr");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
		rs = RestAssured.given();
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

	@Test(description = "tc_01 - Verify successful invoice extraction with valid PDF and auth.", priority = 1, groups = {
			"e2e", "smoke", "sanity", "regression" }, dataProvider = "invoiceFiles",dataProviderClass = InvoiceOCRFileDataProvider.class)
	public void verifyResponseWithValidPDFandAuth(File invoiceImage) {
		if (this.tokenOCR == null)
			getToken();
//		invoiceImage = new File(
//				System.getProperty("user.dir") + "/src/test/resources/testdata/invoice/invoiceTrial.pdf");

		response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + this.tokenOCR)
				.multiPart("sources", JSONUtility.getOcr().getSources()).multiPart("req_id", "testrc12345")
				.multiPart("debit", true).multiPart("file", invoiceImage).when()
				.post(AuthService.BASE_PATH_OCR_EXTRACT_DATA_INVOICE);
		String responseBody = response.asString();
		logger.info("Response body: " + responseBody);
		ExtractDataInvoiceOCRResponse res = gson.fromJson(responseBody, ExtractDataInvoiceOCRResponse.class);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(res.getExtracted_data().getName());
		softAssert.assertNotNull(res.getExtracted_data().getCustomer_gst());
		softAssert.assertNotNull(res.getExtracted_data().getBill_date());
		softAssert.assertNotNull(res.getExtracted_data().getBilling_entity());
		softAssert.assertNotNull(res.getExtracted_data().getGst_no());
		softAssert.assertNotNull(res.getExtracted_data().getToatal_amount());
		softAssert.assertNotNull(res.getExtracted_data().getCgst());
		softAssert.assertNotNull(res.getExtracted_data().getSgst());
		softAssert.assertNotNull(res.getExtracted_data().getIgst());
		softAssert.assertTrue(res.isSuccess());
		softAssert.assertAll();
	}
	
}
