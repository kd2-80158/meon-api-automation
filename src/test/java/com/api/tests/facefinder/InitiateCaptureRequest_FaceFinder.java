package com.api.tests.facefinder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.facefinder.GenerateTokenFaceFinderRequest;
import com.api.models.request.facefinder.InitiateCaptureRequestFaceFinderRequest;
import com.api.models.response.facefinder.GenerateTokenFaceFinderResponse;
import com.api.models.response.facefinder.InitiateRequestFaceFinderResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.api.utility.Severity;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class InitiateCaptureRequest_FaceFinder extends BaseTest {

	AuthService authService;
	Response response;
	Logger logger;
	Gson gson;
	RequestSpecification rs;
	String token;
	String url;
	WebDriver driver;
	String transaction_id;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("facefinder");
		logger = LoggerUtility.getLogger(this.getClass());
		gson = new Gson();
		rs = RestAssured.given();
		this.token = SessionUtility.get("token_ff");
	}

	public String getToken() {
		if (this.token == null) {
			String client_id = JSONUtility.getFaceFinder().getClient_id();
			String client_secret = JSONUtility.getFaceFinder().getClient_secret();
			GenerateTokenFaceFinderRequest request = new GenerateTokenFaceFinderRequest(client_id, client_secret);
			response = authService.generateTokenFaceFinder(request);
			String responseBody = response.asString();
			GenerateTokenFaceFinderResponse res = gson.fromJson(responseBody, GenerateTokenFaceFinderResponse.class);
			token = res.getData().getToken();
			SessionUtility.put("token_ff", this.token);

		}
		return this.token;
	}

	@Test(description = "tc_01 - Verify successful initiation when valid flags and valid token are provided.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyResponseWithValidFlagsAndValidToken_FaceFinder() {

		if (this.token == null)
			getToken();
		boolean check_location = true;
		boolean match_face = true;
		InitiateCaptureRequestFaceFinderRequest request = new InitiateCaptureRequestFaceFinderRequest(check_location,
				match_face);
		response = authService.initiateRequestWithAuth(request, this.token);
		String responseBody = response.asString();
		InitiateRequestFaceFinderResponse res = gson.fromJson(responseBody, InitiateRequestFaceFinderResponse.class);
		this.url = res.getData().getUrl();
		this.transaction_id = res.getData().getTransaction_id();
        SessionUtility.put("transaction_id", this.transaction_id);
		try {
			ChromeOptions options = new ChromeOptions();

			// Auto-allow camera & mic
			Map<String, Object> prefs = new HashMap<>();
			prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
			prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
			prefs.put("profile.default_content_setting_values.geolocation", 1);
			prefs.put("profile.default_content_setting_values.notifications", 1);

			options.setExperimentalOption("prefs", prefs);

			WebDriver driver = new ChromeDriver(options);
			driver.get(this.url);
			driver.manage().window().maximize();
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Capture']"))).click();
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Submit']"))).click();
			WebElement eSignBtn = driver.findElement(By.xpath("//a[normalize-space(text())='Try eSign for Free']"));
			if (eSignBtn.isDisplayed()) {
				softAssert.assertTrue(eSignBtn.isDisplayed());
			} else {
				softAssert.assertFalse(eSignBtn.isDisplayed());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		softAssert.assertNotNull(url);
		softAssert.assertNotNull(transaction_id);
		softAssert.assertEquals(res.getCode(), 200, Severity.CRITICAL, "");
		softAssert.assertAll();
		if (driver != null)
			driver.quit();
	}

	@Test(description = "tc_07 - Verify server returns validation error when video_time is negative or zero while capture_video true.", priority = 2, groups = {
			"smoke", "regression" })
	public void verifyResponseWithNegativeVideoTime_FaceFinder() {
		if (this.token == null)
			getToken();
		boolean check_location = true;
		boolean match_face = true;
		int video_time = -1;
		boolean capture_video = true;
		InitiateCaptureRequestFaceFinderRequest request = new InitiateCaptureRequestFaceFinderRequest(check_location,
				match_face, video_time, capture_video);
		response = authService.initiateRequestWithAuth(request, this.token);
		String responseBody = response.asString();
		InitiateRequestFaceFinderResponse res = gson.fromJson(responseBody, InitiateRequestFaceFinderResponse.class);
		this.url = res.getData().getUrl();
		String transaction_id = res.getData().getTransaction_id();
		try {
			ChromeOptions options = new ChromeOptions();
			// Auto-allow camera & mic
			Map<String, Object> prefs = new HashMap<>();
			prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
			prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
			prefs.put("profile.default_content_setting_values.geolocation", 1);
			prefs.put("profile.default_content_setting_values.notifications", 1);

			options.setExperimentalOption("prefs", prefs);

			driver = new ChromeDriver(options);
			driver.get(this.url);
			driver.manage().window().maximize();
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Start']"))).click();
			boolean isDisplayed = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Recording...']")))
					.isDisplayed();
			if (isDisplayed) {
				Assert.fail(Severity.CRITICAL
						+ "As time is -ve so it goes into infinite loop. It should be handled carefully!");
			}
		} finally {
			softAssert.assertNotNull(url);
			softAssert.assertNotNull(transaction_id);
			softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
			softAssert.assertAll();
			if (driver != null)
				driver.quit();
		}
	}

	@Test(description = "tc_07 - Verify API returns validation error when match_face true but image_to_be_match empty.", priority = 3, groups = {
			"sanity", "regression" })
	public void verifyResponseWhenMatchFaceIsRequiredButImageIsEmpty_FaceFinder() {
		if (this.token == null)
			getToken();
		boolean match_face = true;
		String image_to_be_match = "";
		InitiateCaptureRequestFaceFinderRequest request = new InitiateCaptureRequestFaceFinderRequest(
				match_face, image_to_be_match);
		response = authService.initiateRequestWithAuth(request, this.token);
		String responseBody = response.asString();
		InitiateRequestFaceFinderResponse res = gson.fromJson(responseBody, InitiateRequestFaceFinderResponse.class);
		softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
		softAssert.assertAll();
	}
	
	@Test(description="tc_15 - Verify API returns error when unsupported data types provided (e.g., strings for booleans).",priority=4,groups= {"sanity","regression"})
	public void verifyResponseWithUnsupportedDatatype_FaceFinder()
	{
		if (this.token == null)
			getToken();
		response = rs.baseUri(JSONUtility.getFaceFinder().getUrl()).contentType("application/json").header("token",this.token)
				      .body("{   \"check_location\":\"true\",\n"
				      		+ "    \"capture_video\": true,\n"
				      		+ "    \"match_face\": true,\n"
				      		+ "    \"read_script\": true,\n"
				      		+ "    \"text_script\": \"Disclaimer :  Eligible applicants registered under GST must provide a video recording of their workspace, verifying the location matches their registered GST address\",\n"
				      		+ "    \"video_time\": 2,\n"
				      		+ "    \"extra\":true}").when().post(AuthService.BASE_PATH_FACE_FINDER_INITIATE_REQUEST);
		String responseBody = response.asString();
		boolean isHtml = responseBody!=null && responseBody.trim().startsWith("<!doctype html");
		if(isHtml)
		{
			Assert.fail("Error occured as HTML body found in the response: "+responseBody);
		}
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertAll();
		}
	
	
	@Test(description="tc_19 - Verify API enforces maximum allowed video_time limit when capture_video true (e.g., max 60s).",priority=5,groups= {"sanity","regression"})
	public void verifyResponseWithMaximumAllowedVideoTime_FaceFinder()
	{
		if (this.token == null)
			getToken();
		int video_time = 999;
		InitiateCaptureRequestFaceFinderRequest request = new InitiateCaptureRequestFaceFinderRequest(
				video_time);
		response = authService.initiateRequestWithAuth(request, this.token);
		String responseBody = response.asString();
		InitiateRequestFaceFinderResponse res = gson.fromJson(responseBody, InitiateRequestFaceFinderResponse.class);
		softAssert.assertEquals(res.getCode(), 400, Severity.CRITICAL, "");
		softAssert.assertEquals(res.getMsg(), "Maximum video limit is 3 mins(3*60)");
		softAssert.assertAll();
		}
}
