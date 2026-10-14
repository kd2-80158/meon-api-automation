package com.api.tests.ocr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.response.ocr.NewOCRExtractResponseCheque;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class ExtractDataCancelledCheque_OCRNew extends BaseTest {

	private RequestSpecification rs;
	private Logger logger;
	private Gson gson;
	private Response response;

	@BeforeMethod(alwaysRun = true)
	public void setup() {

		rs = RestAssured.given().relaxedHTTPSValidation();

		logger = LoggerUtility.getLogger(this.getClass());

		gson = new Gson();
	}

	@Test(description = "tc_01 - Verify OCR extraction of cancelled cheque against expected JSON.", groups = { "e2e",
			"regression" }, dataProvider = "cancelledChequeFiles", dataProviderClass = OCRFileDataProvider.class)
	public void verifyCancelledChequeResponse(File cheque) {

		softAssert.assertNotNull(cheque, Severity.CRITICAL, "Cancelled cheque file received from DataProvider is null");

		if (cheque == null) {
			softAssert.assertAll();
			return;
		}

		softAssert.assertTrue(cheque.exists(), Severity.CRITICAL,
				"Cancelled cheque file does not exist: " + cheque.getAbsolutePath());

		softAssert.assertTrue(cheque.isFile(), Severity.CRITICAL,
				"Cancelled cheque path is not a valid file: " + cheque.getAbsolutePath());

		if (!cheque.exists() || !cheque.isFile()) {
			softAssert.assertAll();
			return;
		}

		logger.info("Executing cancelled cheque OCR for file: {}", cheque.getAbsolutePath());

		File expectedDataFile = new File(System.getProperty("user.dir")
				+ "/src/test/resources/testdata/expectedDataJSON/" + "cheque_expected_data.json");

		softAssert.assertTrue(expectedDataFile.exists(), Severity.CRITICAL,
				"Cheque expected-data JSON file does not exist: " + expectedDataFile.getAbsolutePath());

		softAssert.assertTrue(expectedDataFile.isFile(), Severity.CRITICAL,
				"Cheque expected-data JSON path is not a valid file: " + expectedDataFile.getAbsolutePath());

		if (!expectedDataFile.exists() || !expectedDataFile.isFile()) {

			softAssert.assertAll();
			return;
		}

		logger.info("Expected cheque data JSON file: {}", expectedDataFile.getAbsolutePath());

		String expectedJsonContent = null;

		try {
			expectedJsonContent = new String(Files.readAllBytes(expectedDataFile.toPath()), StandardCharsets.UTF_8);
		} catch (IOException e) {

			softAssert.assertTrue(false, Severity.CRITICAL, "Unable to read cheque expected-data JSON file: "
					+ expectedDataFile.getAbsolutePath() + "\nException: " + e.getMessage());

			softAssert.assertAll();
			return;
		}

		softAssert.assertNotNull(expectedJsonContent, Severity.CRITICAL, "Expected cheque JSON content is null");

		if (expectedJsonContent == null) 
		{
			softAssert.assertAll();
			return;
		}

		softAssert.assertFalse(expectedJsonContent.trim().isEmpty(), Severity.CRITICAL,"Expected cheque JSON content is empty");

		if (expectedJsonContent.trim().isEmpty()) {
			softAssert.assertAll();
			return;
		}

		JsonObject expectedRoot = null;

		try {
			expectedRoot = gson.fromJson(expectedJsonContent, JsonObject.class);
		} catch (Exception e) {
		softAssert.assertTrue(false, Severity.CRITICAL,"Unable to parse cheque_expected_data.json" + "\nException: " + e.getMessage());
			softAssert.assertAll();
			return;
		}

		softAssert.assertNotNull(expectedRoot, Severity.CRITICAL, "Parsed expected cheque JSON object is null");
		if (expectedRoot == null) {
			softAssert.assertAll();
			return;
		}
		String actualChequeFileName = cheque.getName();

		JsonObject expectedChequeData = findExpectedChequeData(expectedRoot, actualChequeFileName);

		softAssert.assertNotNull(expectedChequeData, Severity.CRITICAL,
				"No expected data found for cheque: " + actualChequeFileName + "\nCheck cheque_expected_data.json");

		if (expectedChequeData == null) {

			logger.error("No expected JSON entry found for cheque file: {}", actualChequeFileName);

			softAssert.assertAll();
			return;
		}

		logger.info("Expected data found for {} : {}", cheque.getName(), gson.toJson(expectedChequeData));

		String expectedAccountNumber = getJsonString(expectedChequeData, "account_number");

		String expectedIfsc = getJsonString(expectedChequeData, "ifsc");

		String expectedMicr = getJsonString(expectedChequeData, "micr");

		String expectedName = getJsonString(expectedChequeData, "name");

		logger.info("Expected values for {} => " + "Account Number: {}, " + "IFSC: {}, " + "MICR: {}, " + "Name: {}",
				cheque.getName(), expectedAccountNumber, expectedIfsc, expectedMicr, expectedName);

		response = rs.baseUri(JSONUtility.getOcr().getLive_url()).contentType(ContentType.MULTIPART)
				.multiPart("document_type", "bank").multiPart("file", cheque).when().post("/newocr/extract");

		softAssert.assertNotNull(response, Severity.CRITICAL,
				"OCR API response object is null for file: " + cheque.getName());

		if (response == null) {
			softAssert.assertAll();
			return;
		}

		String actualResponseBody = response.asString();

		logger.info("Actual OCR Response for {} : {}", cheque.getName(), response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
				"HTTP status mismatch for file: " + cheque.getName());

		softAssert.assertNotNull(actualResponseBody, Severity.CRITICAL,
				"Actual OCR response body is null for file: " + cheque.getName());

		if (actualResponseBody == null) {
			softAssert.assertAll();
			return;
		}

		softAssert.assertFalse(actualResponseBody.trim().isEmpty(), Severity.CRITICAL,
				"Actual OCR response body is empty for file: " + cheque.getName());

		if (actualResponseBody.trim().isEmpty()) {
			softAssert.assertAll();
			return;
		}

		NewOCRExtractResponseCheque actualResponse = null;
		try {
			actualResponse = gson.fromJson(actualResponseBody, NewOCRExtractResponseCheque.class);
		} catch (Exception e) {

			softAssert.assertTrue(false, Severity.CRITICAL, "Unable to deserialize actual OCR response for file: "
					+ cheque.getName() + "\nException: " + e.getMessage());
		}

		softAssert.assertNotNull(actualResponse, Severity.CRITICAL,
				"Actual OCR response model is null for file: " + cheque.getName());

		if (actualResponse == null) {
			softAssert.assertAll();
			return;
		}

		softAssert.assertTrue(actualResponse.getSuccess(), Severity.CRITICAL,"Cheque OCR success flag must be true for file: " + cheque.getName());

		softAssert.assertNotNull(actualResponse.getData(), Severity.CRITICAL,"OCR data object is null for file: " + cheque.getName());

		if (actualResponse.getData() == null) {
			softAssert.assertAll();
			return;
		}

		softAssert.assertEquals(actualResponse.getData().getAccount_number(),expectedAccountNumber,Severity.HIGH,
       "Account Number mismatch for file: " + cheque.getName() + "\nExpected: " + expectedAccountNumber
						+ "\nActual: " + actualResponse.getData().getAccount_number());

		softAssert.assertEquals(actualResponse.getData().getIfsc(),expectedIfsc,Severity.HIGH,
				"IFSC mismatch for file: " + cheque.getName() + "\nExpected: " + expectedIfsc + "\nActual: "
						+ actualResponse.getData().getIfsc());

		softAssert.assertEquals(actualResponse.getData().getMicr(),expectedMicr,Severity.HIGH,
                "MICR mismatch for file: " + cheque.getName() + "\nExpected: " + expectedMicr + "\nActual: "
						+ actualResponse.getData().getMicr());

		softAssert.assertEquals(actualResponse.getData().getName(),expectedName,Severity.HIGH,
				"Account Holder Name mismatch for file: " + cheque.getName() + "\nExpected: " + expectedName
						+ "\nActual: " + actualResponse.getData().getName());
		softAssert.assertAll();
	}

	private JsonObject findExpectedChequeData(JsonObject root, String actualFileName) {

		if (root.has(actualFileName)) {
			return root.getAsJsonObject(actualFileName);
		}
		String normalizedActual = normalizeFileName(actualFileName);
		for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
			String expectedFileName = entry.getKey();
			String normalizedExpected = normalizeFileName(expectedFileName);
			if (normalizedActual.equalsIgnoreCase(normalizedExpected)) {
				logger.info("Matched actual file '{}' " + "with expected JSON key '{}'", actualFileName,
						expectedFileName);
				return entry.getValue().getAsJsonObject();
			}
		}
		return null;
	}

	private String normalizeFileName(String fileName) {
		if (fileName == null) {
			return null;
		}
		return fileName.trim().replaceAll("\\(\\d+\\)(?=\\.[^.]+$)", "");
	}

	private String getJsonString(JsonObject jsonObject, String key) {
		if (jsonObject == null) {
			return null;
		}
		if (!jsonObject.has(key)) {
			return null;
		}
		JsonElement element = jsonObject.get(key);
		if (element == null || element.isJsonNull()) {

			return null;
		}
		return element.getAsString();
	}
}