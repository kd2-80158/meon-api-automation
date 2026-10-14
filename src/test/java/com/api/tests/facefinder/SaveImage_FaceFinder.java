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

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		authService = new AuthService("facefinder");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
	}

	public String generateToken() {

//		String client_id = JSONUtility.getFaceFinder().getClient_id();
//		String client_secret = JSONUtility.getFaceFinder().getClient_secret();
		String liveClient_id = JSONUtility.getFaceFinder().getLiveclient_id();
		String liveClient_secret = JSONUtility.getFaceFinder().getLiveclient_secret();

		GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(liveClient_id, liveClient_secret);

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

	@DataProvider(name = "faceFinderImagesLive")
	public Object[][] faceFinderImagesLive() {

		String basePath = System.getProperty("user.dir") + "/src/test/resources/testdata/aetram_imagequality/";

		String latitude = "28.6227892";
		String longitude = "77.3662837";

		return new Object[][] {

				{ basePath + "3695622da23941babc9f32bd929ef9ac_b77a348690fd47a9a7457ff18257f264.jpg", latitude,
						longitude },

				{ basePath + "48b4dfae3bca4cb4aa427d4eb2e10feb_2269c5085810481da036567a8f2eaa01.jpg", latitude,
						longitude },

				{ basePath + "4b2505d55ab6473ca06529375fc2cce1_416e6efa55fe47579f66e2d510d7e8a3.jpg", latitude,
						longitude },

				{ basePath + "4b2505d55ab6473ca06529375fc2cce1_b7cc3b66b3a640a398453216233ad74b.jpg", latitude,
						longitude },

				{ basePath + "512ada31b4f845ce9081ea890597c32b_2b96eea270a34c7d848cff3164e1fb32.jpg", latitude,
						longitude },

				{ basePath + "512ada31b4f845ce9081ea890597c32b_374833b16f434849bfecd9b018dbc2ac.jpg", latitude,
						longitude },

				{ basePath + "512ada31b4f845ce9081ea890597c32b_85b3d8f05bbb446aaaee1dc1d0638f46.jpg", latitude,
						longitude },

				{ basePath + "512ada31b4f845ce9081ea890597c32b_e24eda5df2a84b53ab52b22243ae1b17.jpg", latitude,
						longitude },

				{ basePath + "535a320f0c6f430ca757f005623e9075_486a1fe546b440d7adaf9542e5a8e111.jpg", latitude,
						longitude },

				{ basePath + "58f662001ce445c689e283548a9425f9_616dec9895f14b489edc7b8b746d503d.jpg", latitude,
						longitude },

				{ basePath + "591a907fc9de4e40a5ff2195d5278685_001639e1feab4a2ab68c2936ea932d1b.jpg", latitude,
						longitude },

				{ basePath + "591a907fc9de4e40a5ff2195d5278685_a1ebee7927e34f8b9ef5f7a9c86ab951.jpg", latitude,
						longitude },

				{ basePath + "592c958766264d7b85ccbffd6172498f_2dda7d883d4a4963bbcc64c053bf4ee0.jpg", latitude,
						longitude },

				{ basePath + "8949d643a92a43e7be3f9442ba7f7387_02101c1335694a2bafa7b745f2ca948b.jpg", latitude,
						longitude },

				{ basePath + "8b4af689ac3f41c39c9fe6b13a29d5be_9a40a0ed7826456c89b26c80774f4e28.jpg", latitude,
						longitude },

				{ basePath + "8f0fbb12be624e75b1779f435283365e_7cfe729fb4264c98b382003bacc39839.jpg", latitude,
						longitude },

				{ basePath + "91dfe392eeb14d058a96241aeda3ae59_29671fb391234ff7b88741ab9a3cafa3.jpg", latitude,
						longitude },

				{ basePath + "91dfe392eeb14d058a96241aeda3ae59_4d57599c7b594c9abaaaa5e0a28d2e53.jpg", latitude,
						longitude },

				{ basePath + "91dfe392eeb14d058a96241aeda3ae59_b14e33bdf76447fb8a51c5cc6a5cd41a.jpg", latitude,
						longitude },

				{ basePath + "9bb0facd535940b795c30ddbcfa6f452_680504123af4414d8024051ac3cb829e.jpg", latitude,
						longitude },

				{ basePath + "9e766a357735444cb13a2410d426a872_090300ea50fd4cbda9d547a90be02fe9.jpg", latitude,
						longitude },

				{ basePath + "b7c043991bc64ba798222282996d1dda_35a458f0b0e0410ea046c3a3c1d0f9e3.jpg", latitude,
						longitude },

				{ basePath + "b7c043991bc64ba798222282996d1dda_acf2a65d969f403e8f277cb247a2695f.jpg", latitude,
						longitude },

				{ basePath + "dfc865f3906a46749b88381945642109_055d76786e34472280f3b0134fa551b2.jpg", latitude,
						longitude },

				{ basePath + "dfc865f3906a46749b88381945642109_72547036a92349fbb86cac5e2ab6e1ca.jpg", latitude,
						longitude },

				{ basePath + "f25a38d9640347f4aa2c3b516655cbfa_4369160bc8f24c3784c9ec02f346db6c.jpg", latitude,
						longitude } };
	}

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
				.multiPart("longitude", longitude).when().post("/backend/save_image/" + transactionId).then().extract()
				.response();

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

	@Test(dataProvider = "faceFinderImagesLive", description = "Verify FaceFinder live save image API with Aetram image-quality dataset", groups = {
			"e2e", "smoke", "regression" })
	public void verifyResponseWithMultipleImagesLive(String imagePath, String latitude, String longitude) {

		File imageFile = new File(imagePath);

		softAssert.assertTrue(imageFile.exists(), "Image file does not exist: " + imagePath);

		softAssert.assertTrue(imageFile.isFile(), "Path is not a file: " + imagePath);

		softAssert.assertTrue(imageFile.length() > 0, "Image file is empty: " + imagePath);

		if (!imageFile.exists() || !imageFile.isFile() || imageFile.length() == 0) {

			softAssert.assertAll();
			return;
		}

		logger.info("==============================================");

		logger.info("Running FaceFinder LIVE test for image: {}", imageFile.getName());

		String token = generateToken();

		softAssert.assertNotNull(token, "Generated FaceFinder token is null");

		softAssert.assertFalse(token == null || token.trim().isEmpty(), "Generated FaceFinder token is empty");

		String transactionId = generateTransactionId(token);

		softAssert.assertNotNull(transactionId, "Transaction ID is null for image: " + imageFile.getName());

		softAssert.assertFalse(transactionId == null || transactionId.trim().isEmpty(),
				"Transaction ID is empty for image: " + imageFile.getName());

		String liveBaseUrl = JSONUtility.getFaceFinder().getUrl();

		logger.info("FaceFinder LIVE Base URL: {}", liveBaseUrl);

		logger.info("Latitude: {} | Longitude: {}", latitude, longitude);

		response = RestAssured.given().relaxedHTTPSValidation().baseUri(liveBaseUrl).contentType(ContentType.MULTIPART)

				.multiPart("image", imageFile)

				.multiPart("latitude", latitude)

				.multiPart("longitude", longitude)

				.when().post("/backend/save_image/" + transactionId)

				.then().extract().response();

		logger.info("Image Name: {}", imageFile.getName());

		logger.info("Transaction ID: {}", transactionId);

		logger.info("Status Code: {}", response.statusCode());

		logger.info("Response: {}", response.asPrettyString());

		ExtentReporterUtility.addImageToReport(imageFile.getName(), imageFile.getAbsolutePath());

		softAssert.assertNotNull(response, "Response is null for image: " + imageFile.getName());

		softAssert.assertTrue(response.statusCode() < 500, "Server error returned for image: " + imageFile.getName()
				+ " | Status: " + response.statusCode() + " | Response: " + response.asPrettyString());

		softAssert.assertTrue(
				response.statusCode() == 200 || response.statusCode() == 400 || response.statusCode() == 401
						|| response.statusCode() == 422,
				"Unexpected HTTP status for image: " + imageFile.getName() + " | Actual: " + response.statusCode());

		String responseBody = response.asString();

		softAssert.assertNotNull(responseBody, "Response body is null for: " + imageFile.getName());

		softAssert.assertFalse(responseBody == null || responseBody.trim().isEmpty(),
				"Response body is empty for: " + imageFile.getName());

		String contentType = response.getContentType();

		softAssert.assertTrue(contentType != null && contentType.toLowerCase().contains("application/json"),
				"Expected JSON response but received: " + contentType + " for image: " + imageFile.getName());

		if (contentType != null && contentType.toLowerCase().contains("application/json")) {

			Boolean success = response.jsonPath().getBoolean("success");

			String message = response.jsonPath().getString("msg");

			softAssert.assertNotNull(success, "'success' field is missing for image: " + imageFile.getName());

			softAssert.assertNotNull(message, "'msg' field is missing for image: " + imageFile.getName());

			softAssert.assertFalse(message == null || message.trim().isEmpty(),
					"'msg' field is empty for image: " + imageFile.getName());

			if (Boolean.TRUE.equals(success)) {

				softAssert.assertEquals(response.statusCode(), 200,
						"Successful response must return HTTP 200 " + "for image: " + imageFile.getName());

				softAssert.assertEquals(message, "Image Saved Successfully",
						"Unexpected success message for image: " + imageFile.getName());

				logger.info("IMAGE ACCEPTED | {} | {}", imageFile.getName(), message);

			} else {

				softAssert.assertTrue(
						response.statusCode() == 200 || response.statusCode() == 400 || response.statusCode() == 422,
						"Unexpected status for rejected image: " + imageFile.getName() + " | Status: "
								+ response.statusCode());
				softAssert.assertEquals(message, "No face detected. Please ensure your face is fully visible and properly lit, then try again.");

				logger.info("IMAGE REJECTED | {} | {}", imageFile.getName(), message);
			}
		}

		String lowerBody = responseBody == null ? "" : responseBody.toLowerCase();

		softAssert.assertFalse(lowerBody.contains("<!doctype html"),
				"HTML error page returned for image: " + imageFile.getName());

		softAssert.assertFalse(lowerBody.contains("traceback"),
				"Backend traceback exposed for image: " + imageFile.getName());

		softAssert.assertFalse(lowerBody.contains("internal server error"),
				"Internal Server Error exposed for image: " + imageFile.getName());

		softAssert.assertAll();
	}
}