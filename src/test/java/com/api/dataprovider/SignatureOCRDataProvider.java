package com.api.dataprovider;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.testng.annotations.DataProvider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SignatureOCRDataProvider {

	private static final String SIGNATURE_FOLDER = System.getProperty("user.dir")
			+ "/src/test/resources/testdata/signature";

	private static final String EXPECTED_DATA_FILE = System.getProperty("user.dir")
			+ "/src/test/resources/testdata/expectedDataJSON/signature_expected_data.json";

	private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png");

	@DataProvider(name = "signatureImages")
	public static Object[][] signatureImages() {

		File imageFolder = new File(SIGNATURE_FOLDER);
		File expectedFile = new File(EXPECTED_DATA_FILE);

		if (!imageFolder.exists() || !imageFolder.isDirectory()) {
			throw new IllegalStateException("Signature image folder not found: " + imageFolder.getAbsolutePath());
		}

		if (!expectedFile.exists() || !expectedFile.isFile()) {
			throw new IllegalStateException(
					"Signature expected-data JSON not found: " + expectedFile.getAbsolutePath());
		}

		JsonObject expectedRoot;

		try (FileReader reader = new FileReader(expectedFile)) {

			expectedRoot = new Gson().fromJson(reader, JsonObject.class);

		} catch (Exception e) {

			throw new IllegalStateException(
					"Unable to read/parse signature expected-data JSON: " + expectedFile.getAbsolutePath(), e);
		}

		if (expectedRoot == null) {
			throw new IllegalStateException("signature_expected_data.json parsed as null");
		}

		File[] imageFiles = imageFolder.listFiles(file -> file.isFile() && isSupportedImage(file.getName()));

		if (imageFiles == null || imageFiles.length == 0) {
			throw new IllegalStateException("No signature test images found in: " + imageFolder.getAbsolutePath());
		}

		Arrays.sort(imageFiles, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

		List<Object[]> data = new ArrayList<>();
		List<String> missingExpectedEntries = new ArrayList<>();

		for (File image : imageFiles) {

			String fileName = image.getName();

			if (!expectedRoot.has(fileName)) {

				missingExpectedEntries.add(fileName);
				continue;
			}

			JsonElement expectedElement = expectedRoot.get(fileName);

			if (expectedElement == null || !expectedElement.isJsonObject()) {

				throw new IllegalStateException("Expected-data entry must be JSON object for file: " + fileName);
			}

			JsonObject expectedData = expectedElement.getAsJsonObject();

			if (!expectedData.has("is_signature") || !expectedData.has("success")) {

				throw new IllegalStateException(
						"Expected-data entry for " + fileName + " must contain 'is_signature' and 'success'");
			}

			boolean expectedIsSignature = expectedData.get("is_signature").getAsBoolean();

			boolean expectedSuccess = expectedData.get("success").getAsBoolean();

			data.add(new Object[] { image, expectedIsSignature, expectedSuccess });
		}

		/*
		 * Strict validation: every image must have corresponding expected JSON data.
		 */
		if (!missingExpectedEntries.isEmpty()) {

			throw new IllegalStateException("Expected data missing for signature image(s): " + missingExpectedEntries
					+ "\nUpdate file: " + expectedFile.getAbsolutePath());
		}

		if (data.isEmpty()) {
			throw new IllegalStateException("No valid signature test data created");
		}

		return data.toArray(new Object[0][]);
	}

	private static boolean isSupportedImage(String fileName) {

		String lower = fileName.toLowerCase();

		return SUPPORTED_EXTENSIONS.stream().anyMatch(lower::endsWith);
	}
}