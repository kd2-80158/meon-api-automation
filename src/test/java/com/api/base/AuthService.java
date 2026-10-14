package com.api.base;

import static io.restassured.RestAssured.given;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.asserts.SoftAssert;

import com.api.models.request.aadhaar.GenerateClientTokenRequest;
import com.api.models.request.aadhaar.GenerateDigilockerLinkRequest;
import com.api.models.request.aadhaar.RetrieveAadhaarDataRequest;
import com.api.models.request.esign.FetchDocumentEsignRequest;
import com.api.models.request.esign.GenerateClientTokenEsignRequest;
import com.api.models.request.esign.GenerateTokenEsignRequest;
import com.api.models.request.facefinder.GenerateTokenFaceFinderRequest;
import com.api.models.request.facefinder.GenerateTokenForExportDataFaceFinderRequest;
import com.api.models.request.facefinder.InitiateCaptureRequestFaceFinderRequest;
import com.api.models.request.kyc.GenerateAdminTokenKYCRequest;
import com.api.models.request.kyc.GetSsoRouteKYCRequest;
import com.api.models.request.kyc.GetUserDataKYCRequest;
import com.api.models.request.ocr.GenerateTokenOCRRequest;
import com.api.models.request.pennydrop.BankVerificationPennyDropRequest;
import com.api.models.request.pennydrop.GenerateTokenPennyDropRequest;
import com.api.models.request.reversepennydrop.GenerateExportTokenRPDRequest;
import com.api.models.request.reversepennydrop.GenerateTokenRPDRequest;
import com.api.models.request.reversepennydrop.InitiateRequestRPDRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class AuthService extends BaseService {

	Logger logger = LoggerUtility.getLogger(this.getClass());
	GenerateClientTokenRequest tokenRequest;
	Response response;
	WebDriver driver;
	protected SoftAssert softAssert;

	public static final String BASE_URI_ESIGN = "https://esignuat.meon.co.in";
	public static final String BASE_URI_AADHAAR = "https://digilocker-uat.meon.co.in";
	public static final String BASE_PATH = "/get_access_token";
	public static final String BASE_PATH_DIGILOCKER_LINK = "/digi_url";
	public static final String BASE_PATH_RETRIEVE_DATA = "/v2/send_entire_data";
	public static final String BASE_PATH_ESIGN = "/EsignServices/auth";
	public static final String BASE_PATH_TOKEN_ESIGN = "/EsignServices/uploadPDF";
	public static final String BASE_PATH_FETCH_DOCUMENT_ESIGN = "/EsignServices/get_pdf_url";
	public static final String BASE_URI_KYC = "https://live.meon.co.in";
	public static final String BASE_PATH_KYC_ADMIN_TOKEN = "/generate_admin_token";
	public static final String BASE_PATH_KYC_GET_DATA = "/get_user_data_new";
	public static final String BASE_PATH_KYC_SSO_ROUTE = "/get_sso_route";
	public static final String BASE_PATH_PENNYDROP_TOKEN = "/generate_token";
	public static final String BASE_PATH_PENNYDROP_BANK_VERIFICATION = "/api/pennydrop";
	public static final String BASE_PATH_REVERSEPENNYDROP_TOKEN = "/api/generate-token";
	public static final String BASE_PATH_REVERSEPENNYDROP_INITIATE_REQUEST = "/api/initiate-request";
	public static final String BASE_PATH_REVERSEPENNYDROP_EXPORT_TOKEN = "/api/generate-token";
	public static final String BASE_PATH_REVERSEPENNYDROP_GET_DATA = "/api/export-data";
	public static final String BASE_PATH_FACE_FINDER_GENERATE_TOKEN = "/backend/generate_token_for_ipv_credentials";
	public static final String BASE_PATH_FACE_FINDER_INITIATE_REQUEST = "/backend/initiate_request";
	public static final String BASE_PATH_FACE_FINDER_GENERATE_TOKEN_FOR_EXPORT = "/backend/generate_token_for_ipv_credentials";
	public static final String BASE_PATH_FACE_FINDER_EXPORT_DATA = "/backend/export_data";
	public static final String BASE_PATH_OCR_GENERATE_TOKEN = "/get_token";
	public static final String BASE_PATH_OCR_EXTRACT_DATA_INVOICE = "/extract_invoice_data";
	public static final String BASE_PATH_OCR_EXTRACT_DATA_PAN_CARD = "/extract_pan_details";
	public static final String BASE_PATH_OCR_EXTRACT_DATA_AADHAAR_CARD = "/extract_adhar_details"; //data or details
	public static final String BASE_PATH_OCR_EXTRACT_DATA_PASSPORT = "/api/extract-passport-info";
	public static final String BASE_PATH_OCR_EXTRACT_DATA_VOTERID = "/extract_voterid_data";
	
	
	public AuthService(String product) {
		super(product);
		softAssert = new SoftAssert();
	}

	@AfterMethod(alwaysRun = true)
	public void assertAllSoft() {
		if (softAssert != null) {
			softAssert.assertAll();
		}
	}

	public String getClientToken() {
		tokenRequest = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),
				JSONUtility.getAadhaar().getSecret_token());

		response = generateClientToken(tokenRequest);
		logger.info("Response: " + response.asPrettyString());

		// extracting the client token
		String clientToken = response.jsonPath().getString("client_token");

		if (clientToken == null || clientToken.isEmpty()) {
			logger.error("Client token and state are not received, Response is: " + response.asPrettyString());
		} else {
			logger.info("Client token received...." + clientToken);
		}
		return clientToken;
	}

	public String getClientState() {
		String state = response.jsonPath().getString("state");

		if (state == null || state.isEmpty()) {
			logger.error("Client token and state are not received, Response is: " + response.asPrettyString());
		} else {
			logger.info("State received...." + state);
		}
		return state;
	}

	public Response generateClientTokenWithPlainText(Object requestObject) {
		String requestBody;
		if (requestObject instanceof String) {
			requestBody = (String) requestObject;
		} else {
			requestBody = new Gson().toJson(requestObject); // convert POJO -> JSON string
		}

		return given().baseUri(BASE_URL).header("Content-Type", "text/plain").body(requestBody) // now always a String
				.when().post(BASE_PATH);
	}

	public Response generateClientTokenWithNoBody() {
		return given().baseUri(BASE_URL).contentType("application/json").when().post(BASE_PATH); // no .body()
	}

	public Response generateClientTokenAadhaarWithRawJson(String rawJson) {
		return given().baseUri(BASE_URL).relaxedHTTPSValidation().contentType("application/json").body(rawJson).when().post(BASE_PATH);
	}

	public Response generateTokenFaceFinderWithRawJson(String rawJson) {
		return given().baseUri(BASE_URL).contentType("application/json").body(rawJson).when().post(BASE_PATH_FACE_FINDER_GENERATE_TOKEN);
	}

	public Response retrieveDataAadhaarWithRawJson(String rawJson) {
		return given().baseUri("https://digilocker-uat.meon.co.in") // or baseURL.getUrl() depending on your class
				.contentType("application/json").body(rawJson).when().post(BASE_PATH_RETRIEVE_DATA);
	}

	public Response generateClientTokenEsignWithRawJson(String rawJson) {
		return given().baseUri(BASE_URI_ESIGN) // or baseURL.getUrl() depending on your class
				.contentType("application/json").body(rawJson).when().post(BASE_PATH_ESIGN);
	}

	public Response generateClientTokenWithGetMethod(Object requestObject) {
		return given().baseUri(BASE_URL).contentType("application/json").body(requestObject) // server will ignore body,
																								// but included
																								// intentionally for
																								// test
				.when().get("/get_access_token"); // <-- GET instead of POST
	}

	public Response generateClientToken(GenerateClientTokenRequest tokenRequest) {
		return postRequestAadhaar(tokenRequest, BASE_PATH);
	}

	// token esign
	public Response generateTokenEsignWithAuth(GenerateTokenEsignRequest tokenRequest, String signature) {
		logger.info("token received in authservice" + signature);
		return postRequestEsignAuth(tokenRequest, BASE_PATH_TOKEN_ESIGN, signature);
	}

	public Response retrieveData(RetrieveAadhaarDataRequest tokenRequest) {
		return postRequestAadhaar(tokenRequest, BASE_PATH_RETRIEVE_DATA);
	}

	public Response generateClientToken(GenerateDigilockerLinkRequest generateDigilockerLinkRequest) {
		return postRequestAadhaar(generateDigilockerLinkRequest, BASE_PATH_DIGILOCKER_LINK);
	}

	public Response generateClientToken(RequestSpecification rs) {
		// TODO Auto-generated method stub
		return null;
	}

	public Response generateClientTokenEsign(GenerateClientTokenEsignRequest generateClientTokenRequest) {
		return postRequestEsign(generateClientTokenRequest, BASE_PATH_ESIGN);
	}

	public String openEsignUrlAndClickOnCancelBtn(String eSignUrl, String cancelUrl) {
		// Step 2: Start Selenium WebDriver
		driver = new FirefoxDriver();
		driver.manage().window().maximize();
		driver.get(eSignUrl);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement cancelButton = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Cancel')]")));
		cancelButton.click();
		wait.until(ExpectedConditions.urlContains("meon.co.in"));
		String currentUrl = driver.getCurrentUrl().trim();
		System.out.println("Redirected URL after Cancel: " + currentUrl);
		driver.quit();
		return currentUrl;
	}

	public void getEsign(String url) {
		WebDriver driver = new FirefoxDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		try {
			driver.get(url);
			driver.manage().window().maximize();

			// click submit/login
			WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit-btn")));
			loginBtn.click();

			// click the "check" button (explicit wait)
			WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("check")));
			checkBtn.click();

			// fill vid and send OTP
			WebElement vidInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("vid")));
			vidInput.clear();
			vidInput.sendKeys("431829337118");

			WebElement otpBtn = wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//button[contains(normalize-space(text()),'SEND OTP')]")));
			otpBtn.click();

			// wait for "Verify OTP" button to appear and be clickable
			WebElement verifyBtn = wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//button[contains(normalize-space(text()),'Verify OTP')]")));

			// click verify and then wait for the final expected result.
			verifyBtn.click();

			// Option A: If verification triggers a navigation (same window), wait for
			// expected element/text
			// e.g., wait for an H1 that contains "Page not found"
			boolean success = false;
			try {
				WebElement expectedText = wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'E Sign Done')]")));
				success = expectedText.isDisplayed();
			} catch (Exception te) {
				// expected element not found in same window within timeout
				success = false;
			}

			// Option B: If verification opens a new window/tab, wait for new window and
			// switch
			if (!success) {
				// wait up to 10s for a second window to appear
				try {
					wait.withTimeout(Duration.ofSeconds(10)).until(driver1 -> driver1.getWindowHandles().size() > 1);
					// switch to the most recent window
					String lastWindow = driver.getWindowHandles().stream().reduce((first, second) -> second)
							.orElse(null);
					if (lastWindow != null) {
						driver.switchTo().window(lastWindow);
						// then wait for the expected element in new window
						WebElement expectedTextNew = wait.until(ExpectedConditions
								.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'E Sign Done')]")));
						success = expectedTextNew.isDisplayed();
					}
				} catch (Exception ignored) {
					// no new window appeared or element not found there either
				}
			}

			// Option C: If verification causes URL change / title change, you can also wait
			// for that
			if (!success) {
				try {
					// replace with the URL or title fragment you expect after successful flow
					wait.until(ExpectedConditions.urlContains("expected-url-fragment"));
					success = true;
				} catch (Exception ignored) {
				}
			}

			// final assertion and logging
			logger.info("Verification completed. success = {}", success);
			softAssert.assertTrue(success, "Not successful journey after clicking Verify");

			// required: assertAll so soft assertions are evaluated
			softAssert.assertAll();
		} finally {
			// always quit driver
			driver.quit();
		}
	}

	public Response fetchDocumentWithAuth(FetchDocumentEsignRequest fetchDocumentEsignRequest, String signature) {
		return postRequestEsignAuth(fetchDocumentEsignRequest, BASE_PATH_FETCH_DOCUMENT_ESIGN, signature);

	}

	public Response fetchDocument(FetchDocumentEsignRequest fetchDocumentEsignRequest) {
		return postRequestEsign(fetchDocumentEsignRequest, BASE_PATH_FETCH_DOCUMENT_ESIGN);

	}

	public Response generateAdminToken(GenerateAdminTokenKYCRequest generateAdminTokenKYCRequest) {
		return postRequestKYC(generateAdminTokenKYCRequest, BASE_PATH_KYC_ADMIN_TOKEN);

	}

	public Response fetchDocumentWithAuthKYC(GetUserDataKYCRequest getUserDataKYCRequest, String bearerToken) {
		return postRequestWithAuthKYC(getUserDataKYCRequest, BASE_PATH_KYC_GET_DATA, bearerToken);

	}

	public Response bankVerification(BankVerificationPennyDropRequest request) {
		return postRequestPennyDrop(request, BASE_PATH_PENNYDROP_BANK_VERIFICATION);
	}

	public Response bankVerificationWithAuth(BankVerificationPennyDropRequest request, String bearerToken) {
		return postRequestWithAuthPD(request, BASE_PATH_PENNYDROP_BANK_VERIFICATION, bearerToken);
	}

	public Response getSsoRoute(GetSsoRouteKYCRequest getSsoRouteKYCRequest) {
		return postRequestKYC(getSsoRouteKYCRequest, BASE_PATH_KYC_SSO_ROUTE);
	}

	public Response generateTokenPennyDrop(GenerateTokenPennyDropRequest request) {
		return postRequestPennyDrop(request, BASE_PATH_PENNYDROP_TOKEN);

	}

	public Response generateTokenRPD(GenerateTokenRPDRequest request) {
		return postRequestReversePennyDrop(request, BASE_PATH_REVERSEPENNYDROP_TOKEN);
	}

	public Response initiateRequest(InitiateRequestRPDRequest request, String authRPD) {
		logger.info("Inside Initiate Request - token is: " + authRPD);
		return postRequestReversePennyDropWithAuth(request, BASE_PATH_REVERSEPENNYDROP_INITIATE_REQUEST, authRPD);
	}

	public Response generateExportTokenRPD(GenerateExportTokenRPDRequest request) {
		return postRequestReversePennyDrop(request, BASE_PATH_REVERSEPENNYDROP_EXPORT_TOKEN);

	}

	public Response generateTokenFaceFinder(GenerateTokenFaceFinderRequest request) {

		return postRequestFaceFinder(request, BASE_PATH_FACE_FINDER_GENERATE_TOKEN);
	}

	public Response initiateRequestWithAuth(InitiateCaptureRequestFaceFinderRequest request, String token) {
		return postRequestFaceFinderWithAuth(request,BASE_PATH_FACE_FINDER_INITIATE_REQUEST,token);
	}

	public Response generateTokenForExportDataWithAuth(GenerateTokenForExportDataFaceFinderRequest request,String token) {
		return postRequestFaceFinderWithAuth(request,BASE_PATH_FACE_FINDER_GENERATE_TOKEN_FOR_EXPORT,token);
	}

	public Response generateTokenForExportData(GenerateTokenForExportDataFaceFinderRequest request) {
		return postRequestFaceFinder(request, BASE_PATH_FACE_FINDER_GENERATE_TOKEN_FOR_EXPORT);
	}

	public Response generateTokenOCR(GenerateTokenOCRRequest request) {
		return postRequestOCR(request,BASE_PATH_OCR_GENERATE_TOKEN);
	}
	

}
