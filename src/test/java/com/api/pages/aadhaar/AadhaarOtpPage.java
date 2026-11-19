package com.api.pages.aadhaar;

import java.util.List;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class AadhaarOtpPage extends AadhaarPageSupport {

	private static final By OTP_FIELDS = By.cssSelector(
			"input[autocomplete='one-time-code'], input[id*='otp'], input[name*='otp'], "
					+ "input[type='tel'], input[type='number'], input[inputmode='numeric']");
	private static final By OTP_REQUEST_BUTTON = By.id("otp_button");
	private static final By RESEND_BUTTON = By.xpath(
			"//button[contains(normalize-space(.), 'Resend')] | //a[contains(normalize-space(.), 'Resend')]");
	private static final By VERIFY_BUTTON = By.xpath(
			"//button[normalize-space()='Verify' or normalize-space()='Submit' or normalize-space()='Next' or @id='otp_button']"
					+ " | //input[@type='submit' and (@value='Verify' or @value='Submit' or @value='Next')]");

	public AadhaarOtpPage(WebDriver driver) {
		super(driver);
	}

	public void waitUntilLoaded() {
		wait.until(d -> hasVisibleInput(d) || d.findElements(OTP_REQUEST_BUTTON).stream()
				.anyMatch(element -> element.isDisplayed() && element.isEnabled()));
		if (!hasVisibleInput(driver)) {
			clickable(OTP_REQUEST_BUTTON).click();
		}
		waitForAnyInput(OTP_FIELDS);
	}

	public int visibleOtpFieldCount() {
		return visibleInputs(OTP_FIELDS).size();
	}

	public void resendOtp() {
		clickable(RESEND_BUTTON).click();
	}

	public void enterAndVerify(String otp) {
		if (otp == null || !otp.matches("[0-9]{6}")) {
			throw new IllegalArgumentException("OTP must contain exactly six digits.");
		}
		List<WebElement> fields = visibleInputs(OTP_FIELDS);
		if (fields.size() > otp.length()) {
			throw new IllegalStateException("Found more visible numeric inputs than OTP digits; refusing to type into an ambiguous form.");
		}
		WebElement firstField = fields.get(0);
		firstField.click();
		enterDigitSequence(fields, otp, "OTP");
		new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
			String entered = fields.stream().map(field -> field.getAttribute("value") == null ? "" : field.getAttribute("value"))
					.reduce("", String::concat).replaceAll("\\D", "");
			return otp.equals(entered);
		});
		WebElement verify = new WebDriverWait(driver, Duration.ofSeconds(15))
				.until(ExpectedConditions.elementToBeClickable(VERIFY_BUTTON));
		verify.click();
	}

	private boolean hasVisibleInput(WebDriver currentDriver) {
		return currentDriver.findElements(OTP_FIELDS).stream()
				.anyMatch(element -> element.isDisplayed() && element.isEnabled());
	}
}
