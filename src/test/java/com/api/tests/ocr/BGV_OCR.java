package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.response.ocr.BgvFileOCRResponse;
import com.api.models.testdata.BgvExpectedMetadata;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class BGV_OCR extends BaseTest {

	AuthService authService;
	RequestSpecification rs;
	Logger logger;
	Gson gson;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("ocr");
		rs = RestAssured.given();
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
	}

	@Test(description = "tc_01 - Verify BGV report with valid PDF", priority = 1, alwaysRun = true, groups = { "e2e",
			"smoke", "regression" }, dataProvider = "bgvfiles", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithValidPDFFile(File bgvFile, BgvExpectedMetadata expectedData) {

		if (!bgvFile.exists() || bgvFile.length() == 0)
			throw new RuntimeException("No file found " + bgvFile.getName());

		Response response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
				.multiPart("file", bgvFile).multiPart("name", expectedData.getInputName())
				.multiPart("address", expectedData.getInputAddress()).multiPart("pan", expectedData.getInputPan())
				.multiPart("aadhaar", "431829337118").when().post(AuthService.BASE_PATH_OCR_BGV);

		logger.info("Response received for " + bgvFile.getName() + " is: " + response.asPrettyString());
		String responseBody = response.asString();

		softAssert.assertEquals(response.getStatusCode(), 200, "Verify system API success return status code.");

		BgvFileOCRResponse bgvResponse = gson.fromJson(responseBody, BgvFileOCRResponse.class);

		softAssert.assertNotNull(bgvResponse, "Response object verification framework should deserialize correctly.");
		softAssert.assertEquals(bgvResponse.getStatus(), expectedData.getExpectedStatus(),
				"Assert runtime transaction process status flag.");
		softAssert.assertTrue(bgvResponse.getExtractionConfidence() >= expectedData.getExpectedConfidenceThreshold(),
				"Verify target processing alignment precision threshold matches validation parameters boundary bounds checking rules.");

		softAssert.assertEquals(bgvResponse.getFormData().getName(), expectedData.getInputName(),
				"Verify mirrored target profile dataset user name attribute tracking.");
		softAssert.assertEquals(bgvResponse.getFormData().getPan(), expectedData.getInputPan(),
				"Verify mirrored target data asset user identification key check.");

		softAssert.assertEquals(bgvResponse.getReportData().getCandidateName(), expectedData.getInputName(),
				"Verify processed backend data output target candidate reference tracking name.");
		softAssert.assertEquals(bgvResponse.getReportData().getCibilScore(),
				Integer.valueOf(expectedData.getExpectedCibilScore()),
				"Verify extracted credit verification metric profiling output data points accuracy metrics.");
		softAssert.assertEquals(bgvResponse.getResult().getFinalDecision(), expectedData.getExpectedFinalDecision(),
				"Verify absolute decision processing pipelines final criteria rules state checks alignment evaluation.");

		softAssert.assertEquals(bgvResponse.getResult().getChecks().getName().getMatchPercentage(),
				expectedData.getExpectedMatchPercentage(),
				"Verify extracted document analytics string processing match accuracy boundary parameters.");
		softAssert.assertAll();
	}
}
