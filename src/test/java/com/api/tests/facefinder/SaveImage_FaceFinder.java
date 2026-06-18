package com.api.tests.facefinder;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.facefinder.GenerateTokenFaceFinderRequest;
import com.api.models.request.facefinder.InitiateCaptureRequestFaceFinderRequest;
import com.api.models.response.facefinder.GenerateTokenFaceFinderResponse;
import com.api.models.response.facefinder.InitiateRequestFaceFinderResponse;
import com.api.utility.ExtentReporterUtility;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Listeners({ com.api.listeners.TestListener.class })
public class SaveImage_FaceFinder extends BaseTest {

	Response response;
	AuthService authService;
	Logger logger;
	Gson gson;
	String token;
	String transaction_id;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("facefinder");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
	}

	public String generateToken() {

		String client_id = JSONUtility.getFaceFinder().getClient_id();
		String client_secret = JSONUtility.getFaceFinder().getClient_secret();

		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_id, client_secret);

		response = authService.generateTokenFaceFinder(request);

		String responseBody = response.asString();

		GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);

		token = res.getData().getToken();

		SessionUtility.put("token_ff", token);

		logger.info("Generated Token : " + token);

		return token;
	}

	public String generateTransactionId(String token) {

		boolean check_location = false;
		boolean match_face = false;

		InitiateCaptureRequestFaceFinderRequest request = new InitiateCaptureRequestFaceFinderRequest(check_location,
				match_face);

		response = authService.initiateRequestWithAuth(request, token);

		String responseBody = response.asString();

		InitiateRequestFaceFinderResponse res = gson.fromJson(responseBody, InitiateRequestFaceFinderResponse.class);

		transaction_id = res.getData().getTransaction_id();

		System.out.println("================================");
		System.out.println("Initiate Response:");
		System.out.println(response.asPrettyString());
		System.out.println("================================");

		SessionUtility.put("transaction_id", transaction_id);

		logger.info("Generated Transaction ID : " + transaction_id);

		return transaction_id;
	}

	@DataProvider(name = "faceFinderImages")
	public Object[][] faceFinderImages() {

		String basePath = System.getProperty("user.dir") + "/src/test/resources/facefinderimages/";

		return new Object[][] {

				{ basePath + "normal_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "normal_face_02.jpg", "28.6227892", "77.3662837" },
				{ basePath + "multiple_faces_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "multiple_faces_02.jpg", "28.6227892", "77.3662837" },
				{ basePath + "half_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "half_face_02.jpg", "28.6227892", "77.3662837" },
				{ basePath + "eyes_closed_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "glasses_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "glasses_02.jpg", "28.6227892", "77.3662837" },
				{ basePath + "sunglasses_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "blur_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "dark_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "eyes_wallpaper_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "no_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "mask_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "side_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "tilted_face_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "low_resolution_01.jpg", "28.6227892", "77.3662837" },
				{ basePath + "eyes_closed_02.jpg", "28.6227892", "77.3662837" },
				{ basePath + "invalid_file.txt", "28.6227892", "77.3662837" }

		};
	}

	@DataProvider(name = "maskImages")
	public Object[][] maskImages() {

		String basePath = System.getProperty("user.dir") + "/src/test/resources/maskimages/";

		return new Object[][] {

				{ basePath + "mask_01.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_02.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_03.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_04.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_05.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_06.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_07.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_08.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_09.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_10.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_11.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_12.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_13.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_14.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_15.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_16.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_17.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_18.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_19.png", "28.6227892", "77.3662837" },
				{ basePath + "mask_20.png", "28.6227892", "77.3662837" }

		};
	}
	
//	@DataProvider(name = "maskImages")
//	public Object[][] maskImages() {
//
//		String basePath = System.getProperty("user.dir") + "/src/test/resources/4kmaskimages/";
//
//		return new Object[][] {
//
//				{ basePath + "mask_01_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_02_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_03_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_04_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_05_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_06_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_07_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_08_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_09_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_10_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_11_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_12_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_13_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_14_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_15_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_16_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_17_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_18_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_19_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_20_4k.png", "28.6227892", "77.3662837" },
//				{ basePath + "mask_21_4k.png", "28.6227892", "77.3662837" }
//
//		};
//	}

	@Test(dataProvider = "faceFinderImages", description = "Verify save image API response with multiple image scenarios")
	public void verifyResponseWithMultipleImages(String imagePath, String latitude, String longitude) {

		String token = generateToken();

		String transactionId = generateTransactionId(token);

		File imageFile = new File(imagePath);

		response = RestAssured.given().baseUri(JSONUtility.getFaceFinder().getUrl()).contentType(ContentType.MULTIPART)

				.multiPart("image", imageFile).multiPart("latitude", latitude).multiPart("longitude", longitude)

				.when()

				.post("/backend/save_image/" + transactionId)

				.then().extract().response();

		System.out.println("====================================");
		System.out.println("Image Name     : " + imageFile.getName());
		System.out.println("Transaction ID : " + transactionId);
		System.out.println("Status Code    : " + response.statusCode());
		System.out.println("Response       : ");
		System.out.println(response.asPrettyString());
		ExtentReporterUtility.addImageToReport(imageFile.getName(), imageFile.getAbsolutePath());

		softAssert.assertNotNull(response);

		softAssert.assertTrue(response.statusCode() == 200 || response.statusCode() == 400
				|| response.statusCode() == 401 || response.statusCode() == 422, "Unexpected Status Code");

		if (response.contentType().contains("application/json")) {

			boolean isSuccess = response.jsonPath().getBoolean("success");

			String message = response.jsonPath().getString("msg");

			if (isSuccess) {

				softAssert.assertEquals(message, "Image Saved Successfully");
				logger.info("SUCCESS : " + imageFile.getName());
				logger.info("Response for " + imageFile.getName() + " is: " + response.asPrettyString());

			} else {
				logger.info("FAILED IMAGE : " + imageFile.getName());
				logger.info("FAILURE MESSAGE : " + message);
			}

			softAssert.assertTrue(response.asPrettyString().length() > 0, "Empty JSON Response");
		}

		softAssert.assertAll();
	}

	@Test(dataProvider = "maskImages", description = "Verify FaceFinder mask detection")
	public void verifyMaskDetection(String imagePath, String latitude, String longitude) {

		String token = generateToken();
		String transactionId = generateTransactionId(token);

		File imageFile = new File(imagePath);

		Response response = RestAssured.given().baseUri(JSONUtility.getFaceFinder().getUrl())
				.contentType(ContentType.MULTIPART).multiPart("image", imageFile).multiPart("latitude", latitude)
				.multiPart("longitude", longitude).when().post("/backend/save_image/" + transactionId).then()
				.extract().response();

		logger.info("Image : {}", imageFile.getName());
		logger.info("Response : {}", response.asPrettyString());

		ExtentReporterUtility.addImageToReport(imageFile.getName(), imageFile.getAbsolutePath());

		softAssert.assertEquals(response.statusCode(), 200);

		String responseBody = response.asPrettyString();
		logger.info("response body is: " + responseBody);

		String actualMaskDetected = response.jsonPath().getString("msg");
		String expectedMaskDetected = "We detected a mask covering your face. Please remove it and try again.";
		softAssert.assertEquals(actualMaskDetected, expectedMaskDetected,
				"Mask detection validation failed for " + imageFile.getName());

		softAssert.assertAll();
	}
}