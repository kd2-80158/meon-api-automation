package com.api.pages.aadhaar;

import java.net.URI;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public final class AadhaarNumberPage extends AadhaarPageSupport {

	private static final java.util.Set<String> APPROVED_HOSTS = java.util.Set.of(
			"digilocker.meon.co.in", "accounts.digitallocker.gov.in");
	private static final By AADHAAR_FIELDS = By.cssSelector("input[id^='aadhaar_'], input[name*='aadhaar']");
	private static final By NEXT_BUTTON = By.xpath("//button[normalize-space()='Next']");

	public AadhaarNumberPage(WebDriver driver) {
		super(driver);
	}

	public void open(String digilockerUrl) {
		URI uri = URI.create(digilockerUrl);
		if (!"https".equalsIgnoreCase(uri.getScheme())
				|| !APPROVED_HOSTS.contains(uri.getHost().toLowerCase(java.util.Locale.ROOT))) {
			throw new IllegalArgumentException("The generated URL is not an approved DigiLocker account URL.");
		}
		driver.navigate().to(digilockerUrl);
		visibleInputs(AADHAAR_FIELDS);
	}

	public void enterNumberAndContinue(String aadhaarNumber) {
		List<WebElement> fields = visibleInputs(AADHAAR_FIELDS);
		enterDigitSequence(fields, aadhaarNumber, "Aadhaar number");
		clickable(NEXT_BUTTON).click();
	}
}
