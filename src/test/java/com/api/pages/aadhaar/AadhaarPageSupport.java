package com.api.pages.aadhaar;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

abstract class AadhaarPageSupport {

	protected final WebDriver driver;
	protected final WebDriverWait wait;

	AadhaarPageSupport(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(45));
	}

	protected List<WebElement> visibleInputs(By locator) {
		return wait.until(d -> {
			List<WebElement> visible = new ArrayList<>();
			for (WebElement element : d.findElements(locator)) {
				if (element.isDisplayed() && element.isEnabled()) {
					visible.add(element);
				}
			}
			return visible.isEmpty() ? null : visible;
		});
	}

	protected void enterDigitSequence(List<WebElement> inputs, String digits, String fieldName) {
		if (inputs.isEmpty()) {
			throw new IllegalStateException("No visible " + fieldName + " fields were found.");
		}
		if (inputs.size() == 1) {
			inputs.get(0).clear();
			inputs.get(0).sendKeys(digits);
			return;
		}

		int offset = 0;
		for (int index = 0; index < inputs.size() && offset < digits.length(); index++) {
			WebElement input = inputs.get(index);
			int length = parseMaxLength(input.getAttribute("maxlength"));
			if (length < 1) {
				int remainingFields = inputs.size() - index;
				int remainingDigits = digits.length() - offset;
				if (remainingDigits % remainingFields != 0) {
					throw new IllegalStateException("Unable to determine digit grouping for " + fieldName + " fields.");
				}
				length = remainingDigits / remainingFields;
			}
			int end = Math.min(offset + length, digits.length());
			input.clear();
			input.sendKeys(digits.substring(offset, end));
			offset = end;
		}
		if (offset != digits.length()) {
			throw new IllegalStateException("The visible " + fieldName + " fields do not accept the complete code.");
		}
	}

	protected WebElement clickable(By locator) {
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	protected void waitForAnyInput(By locator) {
		wait.until((ExpectedCondition<Boolean>) d -> d.findElements(locator).stream()
				.anyMatch(element -> element.isDisplayed() && element.isEnabled()));
	}

	private int parseMaxLength(String value) {
		if (value == null || value.trim().isEmpty()) {
			return -1;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException ignored) {
			return -1;
		}
	}
}
