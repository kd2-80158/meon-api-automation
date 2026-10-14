package com.api.tests.esign;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.models.request.esign.GenerateClientTokenEsignRequest;
import com.api.models.request.esign.GenerateTokenEsignRequest;
import com.api.models.request.esign.SetAutoReminderRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.api.utility.SessionUtility;

import io.restassured.response.Response;

@Listeners({ com.api.listeners.TestListener.class })
public class SetAutoReminderEsign extends BaseTest {

	String token;
	Logger logger;
	Response response;
	AuthService authService;

	@BeforeMethod
	public void setup() {
		authService = new AuthService("eSign");
		logger = LoggerUtility.getLogger(this.getClass());
		token = SessionUtility.get("signature");
	}

	public String getToken() {
		GenerateClientTokenEsignRequest generateClientTokenEsignRequest = new GenerateClientTokenEsignRequest(
				JSONUtility.getEsign().getUsername(), JSONUtility.getEsign().getPassword());
		response = authService.generateClientTokenEsign(generateClientTokenEsignRequest);
		this.token = response.jsonPath().getString("signature");
		SessionUtility.put("token", token);
		return token;
	}

	@Test(description = "tc_01 -Verify setting auto reminder with valid data", priority = 1, groups = { "e2e", "smoke",
			"sanity", "regression" })
	public void verifyResponseWithValidData_Esign() {

		if (token == null) {
			getToken();
		}
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				JSONUtility.getEsign().getDays_to_expire1(), JSONUtility.getEsign().getEveryValue(),
				JSONUtility.getEsign().getEveryUnit(), JSONUtility.getEsign().getTime(),
				JSONUtility.getEsign().isEnablePersonalizeMsg(), JSONUtility.getEsign().getPersonalMsg());
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(response.jsonPath().getString("msg"));
		softAssert.assertTrue(response.jsonPath().getBoolean("success"));
		softAssert.assertNotNull(response.jsonPath().getLong("starts_in_seconds"));
		softAssert.assertAll();
	}

	@Test(description = "tc_05 - Verify enable personalized message", priority = 2, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWhenEnablePersonalizedMessage_Esign() {
		if (this.token == null)
			getToken();
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				JSONUtility.getEsign().getDays_to_expire1(), JSONUtility.getEsign().getEveryValue(),
				JSONUtility.getEsign().getEveryUnit(), JSONUtility.getEsign().getTime(), true, "test");
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is :" + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(response.jsonPath().getString("msg"));
		softAssert.assertTrue(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_06 - Verify disable personalized message", priority = 3, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWhenDisablePersonalizedMessage_Esign() {
		if (this.token == null)
			getToken();
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				JSONUtility.getEsign().getDays_to_expire1(), JSONUtility.getEsign().getEveryValue(),
				JSONUtility.getEsign().getEveryUnit(), JSONUtility.getEsign().getTime(), false, "");
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is :" + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 200);
		softAssert.assertNotNull(response.jsonPath().getString("msg"));
		softAssert.assertTrue(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_14 - Verify invalid days_to_expire (negative value)", priority = 4, groups = { "e2e",
			"sanity", "regression" })
	public void verifyResponseWithInvalidDaysToExpire_Esign() {
		if (this.token == null)
			getToken();
		int invalidDaysToExpire = -1;
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				invalidDaysToExpire, JSONUtility.getEsign().getEveryValue(), JSONUtility.getEsign().getEveryUnit(),
				JSONUtility.getEsign().getTime(), JSONUtility.getEsign().isEnablePersonalizeMsg(),
				JSONUtility.getEsign().getPersonalMsg());
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "days_to_expire must be greater than zero");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_16 - Verify invalid everyUnit", priority = 5, groups = { "e2e", "sanity", "regression" })
	public void verifyResponseWithInvalidEveryvalue_Esign() {
		if (this.token == null)
			getToken();
		int invalidEveryValue = 0;
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				JSONUtility.getEsign().getDays_to_expire1(), invalidEveryValue, JSONUtility.getEsign().getEveryUnit(),
				JSONUtility.getEsign().getTime(), JSONUtility.getEsign().isEnablePersonalizeMsg(),
				JSONUtility.getEsign().getPersonalMsg());
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "every_value must be greater than zero");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_16 - Verify invalid everyUnit", priority = 6, groups = { "e2e", "sanity", "regression" })
	public void verifyResponseWithInvalidEveryunit_Esign() {
		if (this.token == null)
			getToken();
		String invalidEveryUnit = "year";
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				JSONUtility.getEsign().getDays_to_expire1(), JSONUtility.getEsign().getEveryValue(), invalidEveryUnit,
				JSONUtility.getEsign().getTime(), JSONUtility.getEsign().isEnablePersonalizeMsg(),
				JSONUtility.getEsign().getPersonalMsg());
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "every_unit value must be day or hour");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_17 - Verify invalid time format", priority = 7, groups = { "e2e", "sanity", "regression" })
	public void verifyResponseWithInvalidTime_Esign() {
		if (this.token == null)
			getToken();
		String invalidTime = "23:61";
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				JSONUtility.getEsign().getDays_to_expire1(), JSONUtility.getEsign().getEveryValue(),
				JSONUtility.getEsign().getEveryUnit(), invalidTime, JSONUtility.getEsign().isEnablePersonalizeMsg(),
				JSONUtility.getEsign().getPersonalMsg());
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "time must be in HH:MM format");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_18 - Verify missing time field", priority = 8, groups = { "e2e", "sanity", "regression" })
	public void verifyResponseWithMissingTimeField_Esign() {
		if (this.token == null)
			getToken();
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				JSONUtility.getEsign().getDays_to_expire1(), JSONUtility.getEsign().getEveryValue(),
				JSONUtility.getEsign().getEveryUnit(), JSONUtility.getEsign().isEnablePersonalizeMsg(),
				JSONUtility.getEsign().getPersonalMsg());
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "Missing required fields ['time']");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

	@Test(description = "tc_19 - Verify large days_to_expire value", priority = 9, groups = { "e2e", "sanity",
			"regression" })
	public void verifyResponseWithVeryLargeDaysToExpire_Esign() {
		if (this.token == null)
			getToken();
		int invalidDaysToExpire = 1112234;
		SetAutoReminderRequest request = new SetAutoReminderRequest(JSONUtility.getEsign().getDocument_id(),
				invalidDaysToExpire, JSONUtility.getEsign().getEveryValue(), JSONUtility.getEsign().getEveryUnit(),
				JSONUtility.getEsign().getTime(), JSONUtility.getEsign().isEnablePersonalizeMsg(),
				JSONUtility.getEsign().getPersonalMsg());
		response = authService.setAutoReminderEsign(request, token);
		String responseBody = response.body().asPrettyString();
		System.out.println("Response is: " + responseBody);
		softAssert.assertEquals(response.getStatusCode(), 400);
		softAssert.assertEquals(response.jsonPath().getString("msg"), "days_to_expire must not be greater than 30");
		softAssert.assertFalse(response.jsonPath().getBoolean("success"));
		softAssert.assertAll();
	}

}
