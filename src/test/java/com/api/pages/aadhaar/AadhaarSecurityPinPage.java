package com.api.pages.aadhaar;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class AadhaarSecurityPinPage extends AadhaarPageSupport {

	private static final By PIN_FIELDS = By.cssSelector(
			"input[type='password'], input[id*='pin'], input[name*='pin']");
	private static final By DONE_BUTTON = By.xpath("//button[normalize-space()='Done']");

	public AadhaarSecurityPinPage(WebDriver driver) {
		super(driver);
	}

	public void waitUntilLoaded() {
		waitForAnyInput(PIN_FIELDS);
	}

	public void enterAndContinue(String securityPin) {
		List<WebElement> fields = visibleInputs(PIN_FIELDS);
		enterDigitSequence(fields, securityPin, "security PIN");
		clickable(DONE_BUTTON).click();
	}
}
