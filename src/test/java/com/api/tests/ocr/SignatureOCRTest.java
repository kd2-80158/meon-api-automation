package com.api.tests.ocr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.BaseTest;
import com.api.dataprovider.SignatureOCRDataProvider;
import com.api.models.response.ocr.SignatureOCRResponse;
import com.api.pages.SignatureOCRPage;
import com.api.utility.LoggerUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.response.Response;

@Listeners({ com.api.listeners.TestListener.class })
public class SignatureOCRTest extends BaseTest {

	private SignatureOCRPage signaturePage;

	private Gson gson;

	private Logger logger;

	private File baselineSignature;

	@BeforeMethod(alwaysRun = true)
	public void setup() {

		signaturePage = new SignatureOCRPage();

		gson = new Gson();

		logger = LoggerUtility.getLogger(this.getClass());

		baselineSignature = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/signature/" + "SIGNAtureSaurabh.jpeg");
	}

	// ============================================================
	// TC01 - MULTIPLE IMAGE CLASSIFICATION
	// ============================================================

	@Test(description = "tc_signature_01 - Verify multiple signature images against expected classification", priority = 1, groups = {
			"e2e", "smoke", "sanity",
			"regression" }, dataProvider = "signatureImages", dataProviderClass = SignatureOCRDataProvider.class)
	public void verifyMultipleSignatureImages(File signatureFile, boolean expectedIsSignature,
			boolean expectedSuccess) {

		softAssert.assertNotNull(signatureFile, Severity.CRITICAL, "Signature file received from DataProvider is null");

		if (signatureFile == null) {
			softAssert.assertAll();
			return;
		}

		softAssert.assertTrue(signatureFile.exists(), Severity.CRITICAL,
				"Signature file does not exist: " + signatureFile.getAbsolutePath());

		softAssert.assertTrue(signatureFile.isFile(), Severity.CRITICAL,
				"Signature path is not a file: " + signatureFile.getAbsolutePath());

		softAssert.assertTrue(signatureFile.length() > 0, Severity.CRITICAL,
				"Signature file is empty: " + signatureFile.getName());

		if (!signatureFile.exists() || !signatureFile.isFile() || signatureFile.length() == 0) {

			softAssert.assertAll();
			return;
		}

		logger.info("======================================================");

		logger.info("Running Signature OCR for image: {}", signatureFile.getName());

		logger.info("Expected success: {}, Expected is_signature: {}", expectedSuccess, expectedIsSignature);

		Response response = signaturePage.classifySignature(signatureFile);

		softAssert.assertNotNull(response, Severity.CRITICAL, "Response is null for image: " + signatureFile.getName());

		if (response == null) {
			softAssert.assertAll();
			return;
		}

		logger.info("HTTP Status for {} : {}", signatureFile.getName(), response.getStatusCode());

		logger.info("Response for {} : {}", signatureFile.getName(), response.asPrettyString());

		/*
		 * All files coming from the signature DataProvider are supported image formats,
		 * so classification itself should return HTTP 200.
		 */
		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
				"Expected HTTP 200 for image: " + signatureFile.getName());

		SignatureOCRResponse body = parse(response);

		if (body == null) {
			softAssert.assertAll();
			return;
		}

		softAssert.assertTrue(body.isSuccess() == expectedSuccess, Severity.CRITICAL, "success mismatch for image: "
				+ signatureFile.getName() + "\nExpected: " + expectedSuccess + "\nActual: " + body.isSuccess());

		softAssert.assertTrue(body.isSignature() == expectedIsSignature, Severity.CRITICAL,
				"is_signature mismatch for image: " + signatureFile.getName() + "\nExpected: " + expectedIsSignature
						+ "\nActual: " + body.isSignature());

		/*
		 * POSITIVE SIGNATURE RESPONSE
		 *
		 * { "is_signature": true, "result": {...}, "success": true }
		 */
		if (body.isSignature()) {

			softAssert.assertTrue(body.isSuccess(), Severity.CRITICAL,
					"success must be true when signature is detected: " + signatureFile.getName());

			softAssert.assertNotNull(body.getResult(), Severity.HIGH,
					"result must be present when is_signature=true for image: " + signatureFile.getName());

			if (body.getResult() != null) {

				softAssert.assertNotNull(body.getResult().getChecks(), Severity.HIGH,
						"checks must be present when signature is detected: " + signatureFile.getName());

				softAssert.assertNotNull(body.getResult().getFeatures(), Severity.HIGH,
						"features must be present when signature is detected: " + signatureFile.getName());
			}

			/*
			 * VALID IMAGE BUT NO SIGNATURE
			 *
			 * { "is_signature": false, "msg": "No signature detected in the image.",
			 * "success": true }
			 */
		} else {

			softAssert.assertTrue(body.isSuccess(), Severity.CRITICAL,
					"API processing should still be successful when no signature is detected: "
							+ signatureFile.getName());

			softAssert.assertNotNull(body.getMsg(), Severity.HIGH,
					"msg must be returned when no signature is detected: " + signatureFile.getName());

			if (body.getMsg() != null) {

				softAssert.assertTrue(body.getMsg().equalsIgnoreCase("No signature detected in the image."),
						Severity.HIGH, "Unexpected no-signature message for image: " + signatureFile.getName()
								+ "\nActual msg: " + body.getMsg());
			}

			/*
			 * result is intentionally NOT asserted here.
			 *
			 * Current API contract does not return result/checks/features when
			 * is_signature=false.
			 */
		}

		softAssert.assertAll();
	}

	// ============================================================
	// TC02
	// ============================================================

	@Test(description = "tc_signature_02 - Verify response schema for valid signature", priority = 2, groups = {
			"regression" })
	public void verifyResponseSchema() {

		if (!validateBaselineSignature()) {
			return;
		}

		Response response = signaturePage.classifySignature(baselineSignature);

		softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL, "HTTP status must be 200");

		SignatureOCRResponse body = parse(response);

		if (body != null) {

			softAssert.assertNotNull(body.getResult(), Severity.CRITICAL, "result is required");

			if (body.getResult() != null) {

				softAssert.assertNotNull(body.getResult().getChecks(), Severity.CRITICAL, "checks is required");

				softAssert.assertNotNull(body.getResult().getFeatures(), Severity.CRITICAL, "features is required");
			}
		}

		softAssert.assertAll();
	}

	// ============================================================
	// TC03
	// ============================================================

	@Test(description = "tc_signature_03 - Verify all baseline signature checks are true", priority = 3, groups = {
			"regression" })
	public void verifyBaselineChecks() {

		if (!validateBaselineSignature()) {
			return;
		}

		SignatureOCRResponse body = parse(signaturePage.classifySignature(baselineSignature));

		if (body != null && body.getResult() != null && body.getResult().getChecks() != null) {

			SignatureOCRResponse.Checks checks = body.getResult().getChecks();

			softAssert.assertTrue(checks.isLimitedStrokeComponents(), Severity.HIGH,
					"limited_stroke_components should be true");

			softAssert.assertTrue(checks.isMostlyPlainBackground(), Severity.HIGH,
					"mostly_plain_background should be true");

			softAssert.assertTrue(checks.isNotDocumentLike(), Severity.HIGH, "not_document_like should be true");

			softAssert.assertTrue(checks.isPlausibleInkCoverage(), Severity.HIGH,
					"plausible_ink_coverage should be true");

		} else {

			softAssert.assertTrue(false, Severity.CRITICAL, "checks object is missing");
		}

		softAssert.assertAll();
	}

	// ============================================================
	// TC04
	// ============================================================

	@Test(description = "tc_signature_04 - Verify numerical feature values are within valid ranges", priority = 4, groups = {
			"regression" })
	public void verifyFeatureRanges() {

		if (!validateBaselineSignature()) {
			return;
		}

		SignatureOCRResponse body = parse(signaturePage.classifySignature(baselineSignature));

		if (body != null && body.getResult() != null && body.getResult().getFeatures() != null) {

			SignatureOCRResponse.Features features = body.getResult().getFeatures();

			softAssert.assertTrue(inUnitRange(features.getBackgroundShare()), Severity.HIGH,
					"background_share must be between 0 and 1");

			softAssert.assertTrue(features.getComponentCount() >= 0, Severity.HIGH, "component_count must be >= 0");

			softAssert.assertTrue(inUnitRange(features.getForegroundRatio()), Severity.HIGH,
					"foreground_ratio must be between 0 and 1");

			softAssert.assertTrue(inUnitRange(features.getPaperAreaRatio()), Severity.HIGH,
					"paper_area_ratio must be between 0 and 1");

			softAssert.assertTrue(features.getStrokeBboxAspect() >= 0, Severity.HIGH,
					"stroke_bbox_aspect must be >= 0");

			softAssert.assertTrue(features.getTextLikeRows() >= 0, Severity.HIGH, "text_like_rows must be >= 0");

		} else {

			softAssert.assertTrue(false, Severity.CRITICAL, "features object is missing");
		}

		softAssert.assertAll();
	}

	// ============================================================
	// TC05
	// ============================================================

	@Test(description = "tc_signature_05 - Verify missing signature part is rejected", priority = 5, groups = {
			"negative", "regression" })
	public void verifyMissingSignaturePart() {

		Response response = signaturePage.postWithoutSignature();

		assertClientErrorNoServerCrash(response, "Missing signature part");

		softAssert.assertAll();
	}

	// ============================================================
	// TC06
	// ============================================================

	@Test(description = "tc_signature_06 - Verify zero byte signature is rejected", priority = 6, groups = { "negative",
			"regression" })
	public void verifyEmptySignatureFile() throws IOException {

		File file = createTempFile("empty-signature", ".jpeg", new byte[0]);

		Response response = signaturePage.classifySignature(file);

		assertRejectedOrControlled(response, "Zero-byte signature");

		softAssert.assertAll();
	}

	// ============================================================
	// TC07
	// ============================================================

	@Test(description = "tc_signature_07 - Verify wrong multipart key is rejected", priority = 7, groups = { "negative",
			"regression" })
	public void verifyWrongMultipartKey() {

		if (!validateBaselineSignature()) {
			return;
		}

		Response response = signaturePage.classifyWithPartName("file", baselineSignature);

		assertClientErrorNoServerCrash(response, "Wrong multipart field name");

		softAssert.assertAll();
	}

	// ============================================================
	// TC08
	// ============================================================

	@Test(description = "tc_signature_08 - Verify text file is rejected", priority = 8, groups = { "negative",
			"regression" })
	public void verifyTextFileRejected() throws IOException {

		File file = createTempFile("signature", ".txt", "This is not an image".getBytes(StandardCharsets.UTF_8));

		Response response = signaturePage.classifySignature(file);

		assertRejectedOrControlled(response, "Text file");

		softAssert.assertAll();
	}

	// ============================================================
	// TC09
	// ============================================================

	@Test(description = "tc_signature_09 - Verify fake JPEG is rejected", priority = 9, groups = { "negative",
			"security", "regression" })
	public void verifyFakeJpegRejected() throws IOException {

		File file = createTempFile("fake-signature", ".jpeg", "not really jpeg data".getBytes(StandardCharsets.UTF_8));

		Response response = signaturePage.classifySignature(file);

		assertRejectedOrControlled(response, "Fake JPEG");

		softAssert.assertAll();
	}

	// ============================================================
	// TC10
	// ============================================================

	@Test(description = "tc_signature_10 - Verify PDF file is rejected with invalid file type response", priority = 10, groups = {
			"negative", "regression" })
	public void verifyPdfRejected() throws IOException {

		byte[] pdf = ("%PDF-1.4\n" + "1 0 obj\n" + "<<>>\n" + "endobj\n" + "%%EOF").getBytes(StandardCharsets.US_ASCII);

		File pdfFile = createTempFile("signature", ".pdf", pdf);

		Response response = signaturePage.classifySignature(pdfFile);

		softAssert.assertNotNull(response, Severity.CRITICAL, "PDF rejection response must not be null");

		if (response == null) {
			softAssert.assertAll();
			return;
		}

		logger.info("PDF invalid-file response: {}", response.asPrettyString());

		softAssert.assertEquals(response.getStatusCode(), 400, Severity.CRITICAL,
				"PDF must return HTTP 400 Bad Request");

		SignatureOCRResponse body = parse(response);

		if (body != null) {

			softAssert.assertTrue(!body.isSuccess(), Severity.CRITICAL, "success must be false for unsupported PDF");

			softAssert.assertNotNull(body.getMsg(), Severity.HIGH, "Invalid file type response must contain msg");

			if (body.getMsg() != null) {

				softAssert.assertTrue(body.getMsg().contains("Invalid file type provided"), Severity.HIGH,
						"Expected invalid file type message. Actual: " + body.getMsg());

				softAssert.assertTrue(
						body.getMsg().contains("jpg") && body.getMsg().contains("jpeg") && body.getMsg().contains("png")
								&& body.getMsg().contains("webp") && body.getMsg().contains("tiff"),
						Severity.MEDIUM, "Allowed file extensions are incomplete. Actual: " + body.getMsg());
			}
		}

		softAssert.assertAll();
	}

	// ============================================================
	// TC11
	// ============================================================

	@Test(description = "tc_signature_11 - Verify SVG is handled safely", priority = 11, groups = { "negative",
			"security", "regression" })
	public void verifySvgHandledSafely() throws IOException {

		String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" " + "width=\"20\" height=\"20\">"
				+ "<text x=\"1\" y=\"12\">sig</text>" + "</svg>";

		Response response = signaturePage
				.classifySignature(createTempFile("signature", ".svg", svg.getBytes(StandardCharsets.UTF_8)));

		assertRejectedOrControlled(response, "SVG input");

		softAssert.assertAll();
	}

	// ============================================================
	// TC12
	// ============================================================

	@Test(description = "tc_signature_12 - Verify blank white image is not signature", priority = 12, groups = {
			"negative", "regression" })
	public void verifyBlankWhiteImageNotSignature() throws IOException {

		Response response = signaturePage.classifySignature(createSolidImage(Color.WHITE, 800, 400, "blank-white"));

		assertNonSignatureOrControlled(response, "Blank white image");

		softAssert.assertAll();
	}

	// ============================================================
	// TC13
	// ============================================================

	@Test(description = "tc_signature_13 - Verify solid black image is not signature", priority = 13, groups = {
			"negative", "edge", "regression" })
	public void verifySolidBlackImageNotSignature() throws IOException {

		Response response = signaturePage.classifySignature(createSolidImage(Color.BLACK, 800, 400, "solid-black"));

		assertNonSignatureOrControlled(response, "Solid black image");

		softAssert.assertAll();
	}

	// ============================================================
	// TC14
	// ============================================================

	@Test(description = "tc_signature_14 - Verify tiny image does not cause server error", priority = 14, groups = {
			"edge", "regression" })
	public void verifyTinyImageHandledGracefully() throws IOException {

		Response response = signaturePage.classifySignature(createSolidImage(Color.WHITE, 1, 1, "tiny"));

		softAssert.assertTrue(response.getStatusCode() < 500, Severity.CRITICAL,
				"Tiny image must not cause server error. Status=" + response.getStatusCode());

		if (response.getStatusCode() == 200) {

			SignatureOCRResponse body = parse(response);

			if (body != null) {

				softAssert.assertTrue(!body.isSignature(), Severity.HIGH,
						"1x1 image must not be classified as signature");
			}
		}

		softAssert.assertAll();
	}

	// ============================================================
	// TC15
	// ============================================================

	@Test(description = "tc_signature_15 - Verify GET method is rejected", priority = 15, groups = { "negative",
			"regression" })
	public void verifyGetMethodRejected() {

		Response response = signaturePage.get();

		softAssert.assertEquals(response.getStatusCode(), 405, Severity.HIGH,
				"GET should return 405 Method Not Allowed");

		softAssert.assertAll();
	}

	// ============================================================
	// TC16
	// ============================================================

	@Test(description = "tc_signature_16 - Verify PUT method is rejected", priority = 16, groups = { "negative",
			"regression" })
	public void verifyPutMethodRejected() {

		Response response = signaturePage.put();

		softAssert.assertEquals(response.getStatusCode(), 405, Severity.HIGH,
				"PUT should return 405 Method Not Allowed");

		softAssert.assertAll();
	}

	// ============================================================
	// TC17
	// ============================================================

	@Test(description = "tc_signature_17 - Verify DELETE method is rejected", priority = 17, groups = { "negative",
			"regression" })
	public void verifyDeleteMethodRejected() {

		Response response = signaturePage.delete();

		softAssert.assertEquals(response.getStatusCode(), 405, Severity.HIGH,
				"DELETE should return 405 Method Not Allowed");

		softAssert.assertAll();
	}

	// ============================================================
	// TC18
	// ============================================================

	@Test(description = "tc_signature_18 - Verify JSON request is rejected", priority = 18, groups = { "negative",
			"regression" })
	public void verifyJsonBodyRejected() {

		Response response = signaturePage.postJson("{\"signature\":\"not-a-file\"}");

		assertClientErrorNoServerCrash(response, "JSON body instead of multipart");

		softAssert.assertAll();
	}

	// ============================================================
	// TC19
	// ============================================================

	@Test(description = "tc_signature_19 - Verify repeated valid requests are consistent", priority = 19, groups = {
			"edge", "regression" })
	public void verifyRepeatedRequestConsistency() {

		if (!validateBaselineSignature()) {
			return;
		}

		Boolean expectedSignature = null;

		for (int i = 1; i <= 3; i++) {

			Response response = signaturePage.classifySignature(baselineSignature);

			softAssert.assertEquals(response.getStatusCode(), 200, Severity.CRITICAL,
					"Iteration " + i + " should return HTTP 200");

			SignatureOCRResponse body = parse(response);

			if (body != null) {

				softAssert.assertTrue(body.isSuccess(), Severity.HIGH, "Iteration " + i + " success should be true");

				if (expectedSignature == null) {

					expectedSignature = body.isSignature();

				} else {

					softAssert.assertTrue(expectedSignature.booleanValue() == body.isSignature(), Severity.HIGH,
							"is_signature changed between repeated requests");
				}
			}
		}

		if (expectedSignature != null) {

			softAssert.assertTrue(expectedSignature.booleanValue(), Severity.HIGH,
					"Baseline signature should consistently classify as true");
		}

		softAssert.assertAll();
	}

	// ============================================================
	// TC20
	// ============================================================

	@Test(description = "tc_signature_20 - Verify concurrent valid signature requests", priority = 20, groups = {
			"edge", "regression" })
	public void verifyConcurrentRequests() throws Exception {

		if (!validateBaselineSignature()) {
			return;
		}

		ExecutorService executor = Executors.newFixedThreadPool(5);

		List<Callable<Response>> tasks = new ArrayList<>();

		for (int i = 0; i < 5; i++) {

			tasks.add(() -> new SignatureOCRPage().classifySignature(baselineSignature));
		}

		List<Future<Response>> futures = executor.invokeAll(tasks);

		executor.shutdown();

		int index = 1;

		for (Future<Response> future : futures) {

			Response response = future.get();

			softAssert.assertTrue(response.getStatusCode() < 500, Severity.CRITICAL,
					"Concurrent request " + index + " returned server error: " + response.getStatusCode());

			if (response.getStatusCode() == 200) {

				SignatureOCRResponse body = parse(response);

				if (body != null) {

					softAssert.assertTrue(body.isSuccess(), Severity.HIGH,
							"Concurrent request " + index + " success should be true");

					softAssert.assertTrue(body.isSignature(), Severity.HIGH,
							"Concurrent request " + index + " should classify baseline as signature");
				}
			}

			index++;
		}

		softAssert.assertAll();
	}

	// ============================================================
	// HELPER METHODS
	// ============================================================

	private boolean validateBaselineSignature() {

		softAssert.assertNotNull(baselineSignature, Severity.CRITICAL, "Baseline signature File object is null");

		if (baselineSignature == null) {

			softAssert.assertAll();
			return false;
		}

		softAssert.assertTrue(baselineSignature.exists(), Severity.CRITICAL,
				"Baseline signature file is missing: " + baselineSignature.getAbsolutePath());

		softAssert.assertTrue(baselineSignature.isFile(), Severity.CRITICAL,
				"Baseline signature path is not a file: " + baselineSignature.getAbsolutePath());

		softAssert.assertTrue(baselineSignature.length() > 0, Severity.CRITICAL, "Baseline signature is empty");

		if (!baselineSignature.exists() || !baselineSignature.isFile() || baselineSignature.length() == 0) {

			softAssert.assertAll();
			return false;
		}

		return true;
	}

	private SignatureOCRResponse parse(Response response) {

		softAssert.assertNotNull(response, Severity.CRITICAL, "Response object must not be null");

		if (response == null) {
			return null;
		}

		String responseBody = response.asString();

		logger.info("Signature OCR response: {}", response.asPrettyString());

		softAssert.assertNotNull(responseBody, Severity.CRITICAL, "Response body must not be null");

		if (responseBody == null || responseBody.trim().isEmpty()) {

			softAssert.assertTrue(false, Severity.CRITICAL, "Response body must not be empty");

			return null;
		}

		try {

			SignatureOCRResponse parsed = gson.fromJson(responseBody, SignatureOCRResponse.class);

			softAssert.assertNotNull(parsed, Severity.CRITICAL, "Unable to parse Signature OCR response");

			return parsed;

		} catch (Exception e) {

			softAssert.assertTrue(false, Severity.CRITICAL, "Response is not valid expected JSON: " + e.getMessage());

			return null;
		}
	}

	private void assertClientErrorNoServerCrash(Response response, String scenario) {

		softAssert.assertNotNull(response, Severity.CRITICAL, scenario + ": response is null");

		if (response == null) {
			return;
		}

		int status = response.getStatusCode();

		softAssert.assertTrue(status >= 400 && status < 500, Severity.HIGH,
				scenario + " should return 4xx, actual=" + status);

		softAssert.assertTrue(status < 500, Severity.CRITICAL, scenario + " must not return server error");
	}

	private void assertRejectedOrControlled(Response response, String scenario) {

		softAssert.assertNotNull(response, Severity.CRITICAL, scenario + ": response is null");

		if (response == null) {
			return;
		}

		int status = response.getStatusCode();

		softAssert.assertTrue(status < 500, Severity.CRITICAL, scenario + " must not return 5xx. Actual=" + status);

		if (status == 200) {

			SignatureOCRResponse body = parse(response);

			if (body != null) {

				softAssert.assertTrue(!body.isSignature(), Severity.HIGH,
						scenario + " must not be classified as signature");
			}

		} else {

			softAssert.assertTrue(status >= 400 && status < 500, Severity.HIGH,
					scenario + " should return 4xx when rejected");
		}
	}

	private void assertNonSignatureOrControlled(Response response, String scenario) {

		softAssert.assertNotNull(response, Severity.CRITICAL, scenario + ": response is null");

		if (response == null) {
			return;
		}

		int status = response.getStatusCode();

		softAssert.assertTrue(status < 500, Severity.CRITICAL, scenario + " must not return 5xx. Actual=" + status);

		if (status == 200) {

			SignatureOCRResponse body = parse(response);

			if (body != null) {

				softAssert.assertTrue(!body.isSignature(), Severity.HIGH,
						scenario + " should not be classified as signature");
			}

		} else {

			softAssert.assertTrue(status >= 400 && status < 500, Severity.HIGH,
					scenario + " should process as non-signature or return 4xx");
		}
	}

	private boolean inUnitRange(double value) {

		return !Double.isNaN(value) && !Double.isInfinite(value) && value >= 0.0 && value <= 1.0;
	}

	private File createTempFile(String prefix, String suffix, byte[] content) throws IOException {

		File file = File.createTempFile(prefix, suffix);

		file.deleteOnExit();

		Files.write(file.toPath(), content);

		return file;
	}

	private File createSolidImage(Color color, int width, int height, String name) throws IOException {

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		int rgb = color.getRGB();

		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				image.setRGB(x, y, rgb);
			}
		}

		File file = File.createTempFile(name, ".png");

		file.deleteOnExit();

		ImageIO.write(image, "png", file);

		return file;
	}
}