package com.api.dataprovider;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.lang.reflect.Type;
import org.testng.annotations.DataProvider;

import com.api.models.testdata.BankStatementExpectedMetadata;
import com.api.models.testdata.BgvExpectedMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OCRFileDataProvider {

	@DataProvider(name = "aadhaarFiles")
	public static Object[][] aadhaarFilesProvider() {

		File folder = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/eadhaar");

		File[] files = folder
				.listFiles(file -> file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")
						|| file.getName().endsWith(".png") || file.getName().endsWith(".pdf")));

		if (files == null || files.length == 0) {
			throw new RuntimeException("No Aadhaar files found in testdata/eadhaar");
		}

		Object[][] data = new Object[files.length][1];
		for (int i = 0; i < files.length; i++) {
			data[i][0] = files[i];
		}

		return data;
	}

	@DataProvider(name = "aadhaarMaskedFiles")
	public static Object[][] aadhaarMaskFilesProvider() {

		File folder = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/aadhaar");

		File[] files = folder
				.listFiles(file -> file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")
						|| file.getName().endsWith(".png") || file.getName().endsWith(".pdf")));

		if (files == null || files.length == 0) {
			throw new RuntimeException("No Aadhaar files found in testdata/aadhaar");
		}

		Object[][] data = new Object[files.length][1];
		for (int i = 0; i < files.length; i++) {
			data[i][0] = files[i];
		}
		return data;
	}

	@DataProvider(name = "bankStatementForIncomeProof")
	public static Object[][] IncomeDocumentProvider() {

		File folder = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/bankstatement");

		File[] files = folder
				.listFiles(file -> file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")
						|| file.getName().endsWith(".png") || file.getName().endsWith(".pdf")));

		if (files == null || files.length == 0) {
			throw new RuntimeException("No Bank Statement files found in testdata/bankstatement");
		}

		Object[][] data = new Object[files.length][1];
		for (int i = 0; i < files.length; i++) {
			data[i][0] = files[i];
		}
		return data;
	}

	@DataProvider(name = "bankStatementExtractionRegression")
	public static Object[][] bankStatementExtractionRegression() {

		File folder = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/bankstatement");

		File[] files = folder.listFiles(file -> {

			String name = file.getName().toLowerCase();

			return file.isFile() && (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
					|| name.endsWith(".pdf"));
		});

		if (files == null || files.length == 0) {

			throw new RuntimeException("No Bank Statement files found in: " + folder.getAbsolutePath());
		}

		Map<String, BankStatementExpectedMetadata> metadataMap;

		try {

			Gson gson = new Gson();

			File jsonFile = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/"
					+ "bankstatement_expected_data.json");

			if (!jsonFile.exists() || jsonFile.length() == 0) {

				throw new RuntimeException(
						"Bank Statement expected data JSON " + "not found or empty: " + jsonFile.getAbsolutePath());
			}

			Type mapType = new TypeToken<Map<String, BankStatementExpectedMetadata>>() {
			}.getType();

			try (FileReader reader = new FileReader(jsonFile)) {

				metadataMap = gson.fromJson(reader, mapType);
			}

		} catch (Exception e) {

			throw new RuntimeException("Failed to load Bank Statement " + "expected data JSON: " + e.getMessage(), e);
		}

		if (metadataMap == null || metadataMap.isEmpty()) {

			throw new RuntimeException("bankstatement_expected_data.json is empty");
		}

		Object[][] data = new Object[files.length][2];

		for (int i = 0; i < files.length; i++) {

			String fileName = files[i].getName();

			if (!metadataMap.containsKey(fileName)) {

				throw new RuntimeException("Missing expected Bank Statement " + "metadata for file: " + fileName);
			}

			data[i][0] = files[i];

			data[i][1] = metadataMap.get(fileName);
		}

		return data;
	}

	@DataProvider(name = "passprotectedbankStatementForIncomeProof")
	public static Object[][] PassProtectedIncomeDocumentProvider() {

		File folder = new File(
				System.getProperty("user.dir") + "/src/test/resources/testdata/passwordprotectedbankstatement");

		File[] files = folder
				.listFiles(file -> file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")
						|| file.getName().endsWith(".png") || file.getName().endsWith(".pdf")));

		if (files == null || files.length == 0) {
			throw new RuntimeException("No Bank Statement files found in testdata/bankstatement");
		}

		Object[][] data = new Object[files.length][2];
		for (int i = 0; i < files.length; i++) {
			data[i][0] = files[i];
			data[i][1] = "315148375";
		}
		return data;
	}

	@DataProvider(name = "bgvfiles")
	public static Object[][] BGVReport() {
		File folder = new File(System.getProperty("user.dir") + "/src/test/resources/testdata/bgv");

		File[] files = folder
				.listFiles(file -> file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")
						|| file.getName().endsWith(".png") || file.getName().endsWith(".pdf")));

		if (files == null || files.length == 0) {
			throw new RuntimeException("No BGV files found in testdata/bgv");
		}

		Map<String, BgvExpectedMetadata> metadataMap;
		try {
			Gson gson = new Gson();
			File jsonFile = new File(
					System.getProperty("user.dir") + "/src/test/resources/testdata/bgv_expected_data.json");
			Type mapType = new TypeToken<Map<String, BgvExpectedMetadata>>() {
			}.getType();
			try (FileReader reader = new FileReader(jsonFile)) {
				metadataMap = gson.fromJson(reader, mapType);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to load expected assertions metadata JSON config file: " + e.getMessage());
		}

		Object[][] data = new Object[files.length][2];
		for (int i = 0; i < files.length; i++) {
			data[i][0] = files[i];

			String fileName = files[i].getName();
			if (!metadataMap.containsKey(fileName)) {
				throw new RuntimeException(
						"Missing assertion dataset mapping metadata profile entry inside JSON for file: " + fileName);
			}
			data[i][1] = metadataMap.get(fileName);
		}
		return data;
	}

	@DataProvider(name = "panFiles")
	public static Object[][] panFiles() {

		File directory = new File("src/test/resources/testdata/pan");

		File[] files = directory.listFiles(file -> file.isFile() && isSupportedOcrFile(file));

		if (files == null || files.length == 0) {
			throw new RuntimeException("No PAN OCR test files found at: " + directory.getAbsolutePath());
		}

		Object[][] data = new Object[files.length][1];

		for (int i = 0; i < files.length; i++) {
			data[i][0] = files[i];
		}

		return data;
	}

	private static boolean isSupportedOcrFile(File file) {

		String name = file.getName().toLowerCase();

		return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".pdf");
	}
}
