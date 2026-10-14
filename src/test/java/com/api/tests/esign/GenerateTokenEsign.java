package com.api.tests.esign;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.esign.GenerateClientTokenEsignRequest;
import com.api.models.request.esign.GenerateTokenEsignRequest;
import com.api.models.request.esign.GenerateTokenEsignRequest.Coordinate;
import com.api.models.response.aadhaar.GenerateClientTokenResponse;
import com.api.models.response.esign.GenerateClientTokenEsignResponse;
import com.api.models.response.esign.GenerateTokenEsignResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public final class GenerateTokenEsign extends BaseTest {

	protected AuthService authService;
	protected Logger logger;
	protected static Response response;
	protected GenerateTokenEsignResponse res;
	protected static String signature;
	protected static String token;
	protected static String eSignUrl;
	protected RequestSpecification rs;
	protected String esignUrl;
	protected String cancelUrl;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("eSign");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given();
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(
				JSONUtility.getEsign().getUsername(), JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		signature = response.jsonPath().getString("signature");
		SessionUtility.put("signature", signature);
	}

	@Test(description = "tc_01 - Verify successful upload when all mandatory fields and valid headers are provided.", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "sanity", "regression" })
	public void verifyResponseWithValidCredentialsEsign() {
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(),
				JSONUtility.getEsign().getPercentage_name_match(), JSONUtility.getEsign().isNeed_aadhaar_match(),
				JSONUtility.getEsign().getAadhaar_number(), JSONUtility.getEsign().isNeed_gender_match(),
				JSONUtility.getEsign().getGender(), JSONUtility.getEsign().isSms_notification(),
				JSONUtility.getEsign().isEmail_notification(), JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		token = response.jsonPath().getString("token");
		SessionUtility.put("token", token);
		eSignUrl = response.jsonPath().getString("esign_url");
		SessionUtility.put("esign_url", eSignUrl);
		softAssert.assertTrue(response.jsonPath().getBoolean("success"), "Expected success=true");

		softAssert.assertEquals(response.jsonPath().getString("message"), "link generated", "Message mismatch");

		softAssert.assertNotNull(response.jsonPath().getString("token"), "Token is not generated");

		softAssert.assertEquals(response.getStatusCode(), 200, "HTTP status mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_02 - Verify upload succeeds when optional fields are absent (not relevant here but treat optional fields).", priority = 2, groups = {
			"smoke", "regression" })
	public void verifyResponseWithRemovingOptionalFieldsEsign() {
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getEsign_type(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() == 200) {
			softAssert.assertEquals(response.getStatusCode(), 200, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		if (signature == null) {
			SessionUtility.put("token", response.jsonPath().getString("token"));
		}
		softAssert.assertTrue(response.jsonPath().get("success"), "Expected success=true");
		softAssert.assertEquals(response.jsonPath().get("message"), "link generated", "Message mismatch");
		softAssert.assertNotNull(response.jsonPath().get("token"), "Token is not generated");
		softAssert.assertAll();
	}

	@Test(description = "tc_03 - Verify API returns error when Content-Type header missing.", priority = 3, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingContentTypeHeaderEsign() {
		String name = JSONUtility.getEsign().getName();
		String documentName = JSONUtility.getEsign().getDocument_name();
		String email = JSONUtility.getEsign().getEmail();
		String mobile = JSONUtility.getEsign().getMobile();
		String reason = JSONUtility.getEsign().getReason();
		String daysToExpire = JSONUtility.getEsign().getDays_to_expire();
		Gson gson = new GsonBuilder().create();
		String coordinate = gson.toJson(JSONUtility.getEsign().getCoordinate());
		String webhook = JSONUtility.getEsign().getWebhook();
		String redirectUrl = JSONUtility.getEsign().getRedirect_url();
		String cancelRedirectUrl = JSONUtility.getEsign().getCancel_redirect_url();
		String esignType = JSONUtility.getEsign().getEsign_type();
		boolean removePreviewPdf = JSONUtility.getEsign().isRemove_preview_pdf();
		boolean needNameMatch = JSONUtility.getEsign().isNeed_name_match();
		boolean isDebit = JSONUtility.getEsign().isDebit();
		int percentageNameMatch = JSONUtility.getEsign().getPercentage_name_match();
		boolean needAadhaarMatch = JSONUtility.getEsign().isNeed_aadhaar_match();
		String aadhaarNumber = JSONUtility.getEsign().getAadhaar_number();
		boolean needGenderMatch = JSONUtility.getEsign().isNeed_gender_match();
		String gender = JSONUtility.getEsign().getGender();
		boolean smsNotification = JSONUtility.getEsign().isSms_notification();
		boolean emailNotification = JSONUtility.getEsign().isEmail_notification();
		String documentData = JSONUtility.getEsign().getDocument_data();

		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).header("signature", signature)
				.body("{\n" + "  \"name\": \"" + name + "\",\n" + "  \"document_name\": \"" + documentName + "\",\n"
						+ "  \"email\": \"" + email + "\",\n" + "  \"mobile\": \"" + mobile + "\",\n"
						+ "  \"reason\": \"" + reason + "\",\n" + "  \"days_to_expire\": " + daysToExpire + ",\n"
						+ "  \"coordinate\": " + coordinate + ",\n" + "  \"webhook\": \"" + webhook + "\",\n"
						+ "  \"redirect_url\": \"" + redirectUrl + "\",\n" + "  \"cancel_redirect_url\": \""
						+ cancelRedirectUrl + "\",\n" + "  \"esign_type\": \"" + esignType + "\",\n"
						+ "  \"remove_preview_pdf\": " + removePreviewPdf + ",\n" + "  \"need_name_match\": "
						+ needNameMatch + ",\n" + "  \"debit\": " + isDebit + ",\n" + "  \"percentage_name_match\": "
						+ percentageNameMatch + ",\n" + "  \"need_aadhaar_match\": " + needAadhaarMatch + ",\n"
						+ "  \"aadhaar_number\": \"" + aadhaarNumber + "\",\n" + "  \"need_gender_match\": "
						+ needGenderMatch + ",\n" + "  \"gender\": \"" + gender + "\",\n" + "  \"sms_notification\": "
						+ smsNotification + ",\n" + "  \"email_notification\": " + emailNotification + ",\n"
						+ "  \"document_data\": \"" + documentData + "\"\n" + "}")
				.when().post(AuthService.BASE_PATH_TOKEN_ESIGN);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertEquals(response.jsonPath().get("message"), "Something went wrong", "Message mismatch");
		softAssert.assertFalse(response.jsonPath().get("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("token"), "", "token should not be generated");
		softAssert.assertAll();
	}

	@Test(description = "tc_04 - Verify API returns error when signature header missing.", priority = 4, groups = {
			"sanity", "regression" })
	public void verifyResponseWithMissingSignatureHeaderEsign() {
		String name = JSONUtility.getEsign().getName();
		String documentName = JSONUtility.getEsign().getDocument_name();
		String email = JSONUtility.getEsign().getEmail();
		String mobile = JSONUtility.getEsign().getMobile();
		String reason = JSONUtility.getEsign().getReason();
		String daysToExpire = JSONUtility.getEsign().getDays_to_expire();
		Gson gson = new GsonBuilder().create();
		String coordinate = gson.toJson(JSONUtility.getEsign().getCoordinate());
		String webhook = JSONUtility.getEsign().getWebhook();
		String redirectUrl = JSONUtility.getEsign().getRedirect_url();
		String cancelRedirectUrl = JSONUtility.getEsign().getCancel_redirect_url();
		String esignType = JSONUtility.getEsign().getEsign_type();
		boolean removePreviewPdf = JSONUtility.getEsign().isRemove_preview_pdf();
		boolean needNameMatch = JSONUtility.getEsign().isNeed_name_match();
		boolean isDebit = JSONUtility.getEsign().isDebit();
		int percentageNameMatch = JSONUtility.getEsign().getPercentage_name_match();
		boolean needAadhaarMatch = JSONUtility.getEsign().isNeed_aadhaar_match();
		String aadhaarNumber = JSONUtility.getEsign().getAadhaar_number();
		boolean needGenderMatch = JSONUtility.getEsign().isNeed_gender_match();
		String gender = JSONUtility.getEsign().getGender();
		boolean smsNotification = JSONUtility.getEsign().isSms_notification();
		boolean emailNotification = JSONUtility.getEsign().isEmail_notification();
		String documentData = JSONUtility.getEsign().getDocument_data();

		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).contentType("application/json")
				.body("{\n" + "  \"name\": \"" + name + "\",\n" + "  \"document_name\": \"" + documentName + "\",\n"
						+ "  \"email\": \"" + email + "\",\n" + "  \"mobile\": \"" + mobile + "\",\n"
						+ "  \"reason\": \"" + reason + "\",\n" + "  \"days_to_expire\": " + daysToExpire + ",\n"
						+ "  \"coordinate\": " + coordinate + ",\n" + "  \"webhook\": \"" + webhook + "\",\n"
						+ "  \"redirect_url\": \"" + redirectUrl + "\",\n" + "  \"cancel_redirect_url\": \""
						+ cancelRedirectUrl + "\",\n" + "  \"esign_type\": \"" + esignType + "\",\n"
						+ "  \"remove_preview_pdf\": " + removePreviewPdf + ",\n" + "  \"need_name_match\": "
						+ needNameMatch + ",\n" + "  \"debit\": " + isDebit + ",\n" + "  \"percentage_name_match\": "
						+ percentageNameMatch + ",\n" + "  \"need_aadhaar_match\": " + needAadhaarMatch + ",\n"
						+ "  \"aadhaar_number\": \"" + aadhaarNumber + "\",\n" + "  \"need_gender_match\": "
						+ needGenderMatch + ",\n" + "  \"gender\": \"" + gender + "\",\n" + "  \"sms_notification\": "
						+ smsNotification + ",\n" + "  \"email_notification\": " + emailNotification + ",\n"
						+ "  \"document_data\": \"" + documentData + "\"\n" + "}")
				.when().post(AuthService.BASE_PATH_TOKEN_ESIGN);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 401);
		}
		softAssert.assertEquals(response.jsonPath().get("message"), "Header Missing", "Message mismatch");
		softAssert.assertFalse(response.jsonPath().get("success"), "Expected success=false");
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify API returns error when signature is invalid or tampered.", priority = 5, groups = {
			"sanity", "regression" })
	public void verifyResponseWithInvalidSignatureHeaderEsign() {
		String name = JSONUtility.getEsign().getName();
		String documentName = JSONUtility.getEsign().getDocument_name();
		String email = JSONUtility.getEsign().getEmail();
		String mobile = JSONUtility.getEsign().getMobile();
		String reason = JSONUtility.getEsign().getReason();
		String daysToExpire = JSONUtility.getEsign().getDays_to_expire();
		Gson gson = new GsonBuilder().create();
		String coordinate = gson.toJson(JSONUtility.getEsign().getCoordinate());
		String webhook = JSONUtility.getEsign().getWebhook();
		String redirectUrl = JSONUtility.getEsign().getRedirect_url();
		String cancelRedirectUrl = JSONUtility.getEsign().getCancel_redirect_url();
		String esignType = JSONUtility.getEsign().getEsign_type();
		boolean removePreviewPdf = JSONUtility.getEsign().isRemove_preview_pdf();
		boolean needNameMatch = JSONUtility.getEsign().isNeed_name_match();
		boolean isDebit = JSONUtility.getEsign().isDebit();
		int percentageNameMatch = JSONUtility.getEsign().getPercentage_name_match();
		boolean needAadhaarMatch = JSONUtility.getEsign().isNeed_aadhaar_match();
		String aadhaarNumber = JSONUtility.getEsign().getAadhaar_number();
		boolean needGenderMatch = JSONUtility.getEsign().isNeed_gender_match();
		String gender = JSONUtility.getEsign().getGender();
		boolean smsNotification = JSONUtility.getEsign().isSms_notification();
		boolean emailNotification = JSONUtility.getEsign().isEmail_notification();
		String documentData = JSONUtility.getEsign().getDocument_data();

		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).contentType("application/json")
				.header("signature", "invalidSignature")
				.body("{\n" + "  \"name\": \"" + name + "\",\n" + "  \"document_name\": \"" + documentName + "\",\n"
						+ "  \"email\": \"" + email + "\",\n" + "  \"mobile\": \"" + mobile + "\",\n"
						+ "  \"reason\": \"" + reason + "\",\n" + "  \"days_to_expire\": " + daysToExpire + ",\n"
						+ "  \"coordinate\": " + coordinate + ",\n" + "  \"webhook\": \"" + webhook + "\",\n"
						+ "  \"redirect_url\": \"" + redirectUrl + "\",\n" + "  \"cancel_redirect_url\": \""
						+ cancelRedirectUrl + "\",\n" + "  \"esign_type\": \"" + esignType + "\",\n"
						+ "  \"remove_preview_pdf\": " + removePreviewPdf + ",\n" + "  \"need_name_match\": "
						+ needNameMatch + ",\n" + "  \"debit\": " + isDebit + ",\n" + "  \"percentage_name_match\": "
						+ percentageNameMatch + ",\n" + "  \"need_aadhaar_match\": " + needAadhaarMatch + ",\n"
						+ "  \"aadhaar_number\": \"" + aadhaarNumber + "\",\n" + "  \"need_gender_match\": "
						+ needGenderMatch + ",\n" + "  \"gender\": \"" + gender + "\",\n" + "  \"sms_notification\": "
						+ smsNotification + ",\n" + "  \"email_notification\": " + emailNotification + ",\n"
						+ "  \"document_data\": \"" + documentData + "\"\n" + "}")
				.when().post(AuthService.BASE_PATH_TOKEN_ESIGN);
		String responseBody = response.asString();
		
		if (response.getStatusCode() >= 401) {
			softAssert.assertEquals(response.getStatusCode(), 401, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 401);
		}
		softAssert.assertEquals(response.jsonPath().get("message"), "Not enough segments", "Message mismatch");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify API returns error when document_data is missing.", priority = 6, groups = {
			"regression" })
	public void verifyResponseWithMissingDocumentDataEsign() {
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(),
				JSONUtility.getEsign().getPercentage_name_match(), JSONUtility.getEsign().isNeed_aadhaar_match(),
				JSONUtility.getEsign().getAadhaar_number(), JSONUtility.getEsign().isNeed_gender_match(),
				JSONUtility.getEsign().getGender(), JSONUtility.getEsign().isSms_notification(),
				JSONUtility.getEsign().isEmail_notification());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "Something went wrong", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_07 - Verify API returns error when document_data is not valid base64.", priority = 7, groups = {
			"regression" })
	public void verifyResponseWithInvalidBase64DocumentDataEsign() {

		String document_data = "//dsfdfssaffsaf////+saffs";
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(),
				JSONUtility.getEsign().getPercentage_name_match(), JSONUtility.getEsign().isNeed_aadhaar_match(),
				JSONUtility.getEsign().getAadhaar_number(), JSONUtility.getEsign().isNeed_gender_match(),
				JSONUtility.getEsign().getGender(), JSONUtility.getEsign().isSms_notification(),
				JSONUtility.getEsign().isEmail_notification(), document_data);

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "unable to convert base64 to pdf Invalid base64-encoded string: number of data characters (25) cannot be 1 more than a multiple of 4",
				"Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_09 - Verify API validates email format and returns error on invalid email.", priority = 8, groups = {
			"regression" })
	public void verifyResponseWithInvalidEmailFormatEsign() {

		String invalidEmail = "saurabh.gmail.com";
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(), invalidEmail,
				JSONUtility.getEsign().getMobile(), JSONUtility.getEsign().getReason(),
				JSONUtility.getEsign().getDays_to_expire(), JSONUtility.getEsign().getCoordinate(),
				JSONUtility.getEsign().getWebhook(), JSONUtility.getEsign().getRedirect_url(),
				JSONUtility.getEsign().getCancel_redirect_url(), JSONUtility.getEsign().getEsign_type(),
				JSONUtility.getEsign().isRemove_preview_pdf(), JSONUtility.getEsign().isNeed_name_match(),
				JSONUtility.getEsign().isDebit(), JSONUtility.getEsign().getPercentage_name_match(),
				JSONUtility.getEsign().isNeed_aadhaar_match(), JSONUtility.getEsign().getAadhaar_number(),
				JSONUtility.getEsign().isNeed_gender_match(), JSONUtility.getEsign().getGender(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "invalid email format", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_10 - Verify API validates mobile number format and returns error on invalid mobile.", priority = 9, groups = {
			"regression" })
	public void verifyResponseWithInvalidMobileFormatEsign() {

		String invalidPhone = "abcd";
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), invalidPhone, JSONUtility.getEsign().getReason(),
				JSONUtility.getEsign().getDays_to_expire(), JSONUtility.getEsign().getCoordinate(),
				JSONUtility.getEsign().getWebhook(), JSONUtility.getEsign().getRedirect_url(),
				JSONUtility.getEsign().getCancel_redirect_url(), JSONUtility.getEsign().getEsign_type(),
				JSONUtility.getEsign().isRemove_preview_pdf(), JSONUtility.getEsign().isNeed_name_match(),
				JSONUtility.getEsign().isDebit(), JSONUtility.getEsign().getPercentage_name_match(),
				JSONUtility.getEsign().isNeed_aadhaar_match(), JSONUtility.getEsign().getAadhaar_number(),
				JSONUtility.getEsign().isNeed_gender_match(), JSONUtility.getEsign().getGender(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "invalid mobile format", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_11 - Verify API validates coordinate objects and returns error when coordinates missing page_number.", priority = 10, groups = {
			"regression" })
	public void verifyResponseWithMissingPageNumberCordinatesEsign() {

		GenerateTokenEsignRequest.Coordinate c1 = new Coordinate("130", "100", "50", "103");
		GenerateTokenEsignRequest.Coordinate c2 = new Coordinate("130", "100", "50", "103");

		List<GenerateTokenEsignRequest.Coordinate> coordinate = Arrays.asList(c1, c2);

		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(), coordinate,
				JSONUtility.getEsign().getWebhook(), JSONUtility.getEsign().getRedirect_url(),
				JSONUtility.getEsign().getCancel_redirect_url(), JSONUtility.getEsign().getEsign_type(),
				JSONUtility.getEsign().isRemove_preview_pdf(), JSONUtility.getEsign().isNeed_name_match(),
				JSONUtility.getEsign().isDebit(), JSONUtility.getEsign().getPercentage_name_match(),
				JSONUtility.getEsign().isNeed_aadhaar_match(), JSONUtility.getEsign().getAadhaar_number(),
				JSONUtility.getEsign().isNeed_gender_match(), JSONUtility.getEsign().getGender(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());
		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "Something went wrong", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_12 - Verify API returns error when coordinate values are non-numeric or negative.", priority = 11, groups = {
			"regression" })
	public void verifyResponseWithNonNumericAndNegativeCordinatesValuesEsign() {

		GenerateTokenEsignRequest.Coordinate c1 = new Coordinate("1", "abc", "def", "-30", "103");
		GenerateTokenEsignRequest.Coordinate c2 = new Coordinate("2", "abc", "def", "-30", "103");

		List<GenerateTokenEsignRequest.Coordinate> coordinate = Arrays.asList(c1, c2);

		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(), coordinate,
				JSONUtility.getEsign().getWebhook(), JSONUtility.getEsign().getRedirect_url(),
				JSONUtility.getEsign().getCancel_redirect_url(), JSONUtility.getEsign().getEsign_type(),
				JSONUtility.getEsign().isRemove_preview_pdf(), JSONUtility.getEsign().isNeed_name_match(),
				JSONUtility.getEsign().isDebit(), JSONUtility.getEsign().getPercentage_name_match(),
				JSONUtility.getEsign().isNeed_aadhaar_match(), JSONUtility.getEsign().getAadhaar_number(),
				JSONUtility.getEsign().isNeed_gender_match(), JSONUtility.getEsign().getGender(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "Something went wrong", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_15 - Verify API handles redirect_url and cancel_redirect_url properly in response/flow.", priority = 12, groups = {
			"regression" })
	public void verifyResponseForRedirectionOfCancelAndRedirectUrlEsign() {
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(),
				JSONUtility.getEsign().getPercentage_name_match(), JSONUtility.getEsign().isNeed_aadhaar_match(),
				JSONUtility.getEsign().getAadhaar_number(), JSONUtility.getEsign().isNeed_gender_match(),
				JSONUtility.getEsign().getGender(), JSONUtility.getEsign().isSms_notification(),
				JSONUtility.getEsign().isEmail_notification(), JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		esignUrl = response.jsonPath().getString("esign_url");
		cancelUrl = JSONUtility.getEsign().getCancel_redirect_url();
		String responseBody = response.asString();
		if (response.getStatusCode() == 200) {
			softAssert.assertEquals(response.getStatusCode(), 200, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 200);
		}
		softAssert.assertTrue(response.jsonPath().getBoolean("success"));
		softAssert.assertEquals(response.jsonPath().getBoolean("message"), "link generated");
		softAssert.assertNotNull(response.jsonPath().get("token"), "Token is not generated");
		String currentUrl = authService.openEsignUrlAndClickOnCancelBtn(esignUrl, cancelUrl);
		String expectedCancelUrl = "https://meon.co.in";
		softAssert.assertTrue(currentUrl.contains(expectedCancelUrl),
				"Cancel redirect URL mismatch. Expected contains: " + expectedCancelUrl + " but got: " + currentUrl);
		softAssert.assertAll();
	}

	@Test(description = "tc_16 - Verify API returns error when days_to_expire is non-numeric or out of range.", priority = 13, groups = {
			"regression" })
	public void verifyResponseWithNonNumericDateToExpireFieldEsign() {

		String daysToExpire = "one"; // non-numeric
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), daysToExpire, JSONUtility.getEsign().getCoordinate(),
				JSONUtility.getEsign().getWebhook(), JSONUtility.getEsign().getRedirect_url(),
				JSONUtility.getEsign().getCancel_redirect_url(), JSONUtility.getEsign().getEsign_type(),
				JSONUtility.getEsign().isRemove_preview_pdf(), JSONUtility.getEsign().isNeed_name_match(),
				JSONUtility.getEsign().isDebit(), JSONUtility.getEsign().getPercentage_name_match(),
				JSONUtility.getEsign().isNeed_aadhaar_match(), JSONUtility.getEsign().getAadhaar_number(),
				JSONUtility.getEsign().isNeed_gender_match(), JSONUtility.getEsign().getGender(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() == 200) {
			softAssert.assertEquals(response.getStatusCode(), 200, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			if (res.getCode() > 0) {
				logger.info("Status code present: " + res.getCode());
				softAssert.assertEquals(res.getCode(), 200);
			}
		}
		softAssert.assertFalse(response.jsonPath().get("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "Invalid days_to_expire", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_18 - Verify API enforces percentage_name_match numeric range 0-100.", priority = 14, groups = {
			"regression" })
	public void verifyResponseWithOutOfRangePercentageNameMatchEsign() {

		int percentageNameMatch = 150; // non-numeric
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(), percentageNameMatch,
				JSONUtility.getEsign().isNeed_aadhaar_match(), JSONUtility.getEsign().getAadhaar_number(),
				JSONUtility.getEsign().isNeed_gender_match(), JSONUtility.getEsign().getGender(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().get("message"), "percentage_name_match should be between 0 and 100",
				"Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_19 - Verify API percentage_name_match provide valid response during float value.", priority = 15, groups = {
			"regression" })
	public void verifyValidResponseWithFloatingValueOfPercentageNameMatchEsign() {
		String name = JSONUtility.getEsign().getName();
		String documentName = JSONUtility.getEsign().getDocument_name();
		String email = JSONUtility.getEsign().getEmail();
		String mobile = JSONUtility.getEsign().getMobile();
		String reason = JSONUtility.getEsign().getReason();
		String daysToExpire = JSONUtility.getEsign().getDays_to_expire();
		Gson gson = new GsonBuilder().create();
		String coordinate = gson.toJson(JSONUtility.getEsign().getCoordinate());
		String webhook = JSONUtility.getEsign().getWebhook();
		String redirectUrl = JSONUtility.getEsign().getRedirect_url();
		String cancelRedirectUrl = JSONUtility.getEsign().getCancel_redirect_url();
		String esignType = JSONUtility.getEsign().getEsign_type();
		boolean removePreviewPdf = JSONUtility.getEsign().isRemove_preview_pdf();
		boolean needNameMatch = JSONUtility.getEsign().isNeed_name_match();
		boolean isDebit = JSONUtility.getEsign().isDebit();
		float percentageNameMatch = (float) 89.50;
		boolean needAadhaarMatch = JSONUtility.getEsign().isNeed_aadhaar_match();
		String aadhaarNumber = JSONUtility.getEsign().getAadhaar_number();
		boolean needGenderMatch = JSONUtility.getEsign().isNeed_gender_match();
		String gender = JSONUtility.getEsign().getGender();
		boolean smsNotification = JSONUtility.getEsign().isSms_notification();
		boolean emailNotification = JSONUtility.getEsign().isEmail_notification();
		String documentData = JSONUtility.getEsign().getDocument_data();

		Response response = rs.baseUri(JSONUtility.getEsign().getUrl()).header("signature", signature)
				.body("{\n" + "  \"name\": \"" + name + "\",\n" + "  \"document_name\": \"" + documentName + "\",\n"
						+ "  \"email\": \"" + email + "\",\n" + "  \"mobile\": \"" + mobile + "\",\n"
						+ "  \"reason\": \"" + reason + "\",\n" + "  \"days_to_expire\": " + daysToExpire + ",\n"
						+ "  \"coordinate\": " + coordinate + ",\n" + "  \"webhook\": \"" + webhook + "\",\n"
						+ "  \"redirect_url\": \"" + redirectUrl + "\",\n" + "  \"cancel_redirect_url\": \""
						+ cancelRedirectUrl + "\",\n" + "  \"esign_type\": \"" + esignType + "\",\n"
						+ "  \"remove_preview_pdf\": " + removePreviewPdf + ",\n" + "  \"need_name_match\": "
						+ needNameMatch + ",\n" + "  \"debit\": " + isDebit + ",\n" + "  \"percentage_name_match\": "
						+ percentageNameMatch + ",\n" + "  \"need_aadhaar_match\": " + needAadhaarMatch + ",\n"
						+ "  \"aadhaar_number\": \"" + aadhaarNumber + "\",\n" + "  \"need_gender_match\": "
						+ needGenderMatch + ",\n" + "  \"gender\": \"" + gender + "\",\n" + "  \"sms_notification\": "
						+ smsNotification + ",\n" + "  \"email_notification\": " + emailNotification + ",\n"
						+ "  \"document_data\": \"" + documentData + "\"\n" + "}")
				.when().post(AuthService.BASE_PATH_TOKEN_ESIGN);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertEquals(response.jsonPath().getString("message"), "percentage_name_match should be a float value", "Message mismatch");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertAll();
	}

	@Test(description = "tc_20 - Verify API validates aadhaar_number format when need_aadhaar_match is true.", priority = 16, groups = {
			"regression" })
	public void verifyResponseWithInvalidAadhaarNumberFormatEsign() {

		String aadhaarNumber = "12ab"; // non-numeric
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(),
				JSONUtility.getEsign().getPercentage_name_match(), JSONUtility.getEsign().isNeed_aadhaar_match(),
				aadhaarNumber, JSONUtility.getEsign().isNeed_gender_match(), JSONUtility.getEsign().getGender(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "invalid aadhaar format", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_21 - Verify API enforces gender values only allowed set (e.g., M/F/Other) when need_gender_match is true.", priority = 17, groups = {
			"regression" })
	public void verifyResponseWithMissingGenderFieldWhenTrueEsign() {
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(),
				JSONUtility.getEsign().getPercentage_name_match(), JSONUtility.getEsign().isNeed_aadhaar_match(),
				JSONUtility.getEsign().getAadhaar_number(), JSONUtility.getEsign().isNeed_gender_match(),
				JSONUtility.getEsign().isSms_notification(), JSONUtility.getEsign().isEmail_notification(),
				JSONUtility.getEsign().getDocument_data());

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "invalid gender value", "Message mismatch");
		softAssert.assertAll();
	}

	@Test(description = "tc_22 - Verify API rejects requests when document_data decoded PDF is corrupted or unreadable.", priority = 18, groups = {
			"regression" })
	public void verifyResponseWithInvalidDocumentData() {

		String documentData = "invalidDocumentFormat"; // non-numeric
		GenerateTokenEsignRequest generateTokenEsignRequest = new GenerateTokenEsignRequest(
				JSONUtility.getEsign().getName(), JSONUtility.getEsign().getDocument_name(),
				JSONUtility.getEsign().getEmail(), JSONUtility.getEsign().getMobile(),
				JSONUtility.getEsign().getReason(), JSONUtility.getEsign().getDays_to_expire(),
				JSONUtility.getEsign().getCoordinate(), JSONUtility.getEsign().getWebhook(),
				JSONUtility.getEsign().getRedirect_url(), JSONUtility.getEsign().getCancel_redirect_url(),
				JSONUtility.getEsign().getEsign_type(), JSONUtility.getEsign().isRemove_preview_pdf(),
				JSONUtility.getEsign().isNeed_name_match(), JSONUtility.getEsign().isDebit(),
				JSONUtility.getEsign().getPercentage_name_match(), JSONUtility.getEsign().isNeed_aadhaar_match(),
				JSONUtility.getEsign().getAadhaar_number(), JSONUtility.getEsign().isNeed_gender_match(),
				JSONUtility.getEsign().getGender(), JSONUtility.getEsign().isSms_notification(),
				JSONUtility.getEsign().isEmail_notification(), documentData);

		response = authService.generateTokenEsignWithAuth(generateTokenEsignRequest, signature);
		String responseBody = response.asString();
		if (response.getStatusCode() >= 400) {
			softAssert.assertEquals(response.getStatusCode(), 400, "HTTP status mismatch");
		} else {
			res = gson.fromJson(responseBody, GenerateTokenEsignResponse.class);
			logger.info("Status code present: " + res.getCode());
			softAssert.assertEquals(res.getCode(), 400);
		}
		softAssert.assertFalse(response.jsonPath().getBoolean("success"), "Expected success=false");
		softAssert.assertEquals(response.jsonPath().getString("message"), "unable to convert base64 to pdf Invalid base64-encoded string: number of data characters (21) cannot be 1 more than a multiple of 4",
				"Message mismatch");
		softAssert.assertAll();
	}

}
