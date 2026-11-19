package com.api.pages.aadhaar;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class AadhaarConsentPage extends AadhaarPageSupport {
	private static final String LOWER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String UPPER = "abcdefghijklmnopqrstuvwxyz";
	private static final By OPTIONS = By.xpath("//*[self::label or self::button or @role='checkbox' or self::div or self::span or self::li][contains(translate(normalize-space(string(.)), '" + LOWER + "', '" + UPPER + "'), 'aadhaar card')]");
	private static final By ALLOW = By.xpath("//*[self::button or self::input or @role='button'][translate(normalize-space(@value), '" + LOWER + "', '" + UPPER + "')='allow' or translate(normalize-space(string(.)), '" + LOWER + "', '" + UPPER + "')='allow']");
	public AadhaarConsentPage(WebDriver driver) { super(driver); }
	public boolean isAadhaarCardOptionVisible() {
		for (WebElement candidate : driver.findElements(OPTIONS)) {
			try {
				if (candidate.isDisplayed() && candidate.isEnabled()) return true;
			} catch (org.openqa.selenium.StaleElementReferenceException ignored) {
				// The DigiLocker page is still transitioning; the caller will poll again.
			}
		}
		return false;
	}
	public void selectAadhaarCardAndAllow() {
		WebElement option = wait.until(d -> shortestVisible(d.findElements(OPTIONS)));
		List<WebElement> boxes = option.findElements(By.xpath(".//input[@type='checkbox']"));
		WebElement box = boxes.isEmpty() ? checkboxForLabel(option) : boxes.get(0);
		if (box != null && !box.isSelected()) option.click();
		if (box != null && !box.isSelected()) throw new IllegalStateException("Aadhaar Card consent could not be selected.");
		if (box == null) option.click();
		clickable(ALLOW).click();
	}
	private WebElement shortestVisible(List<WebElement> matches) {
		WebElement shortest = null; int length = Integer.MAX_VALUE;
		for (WebElement candidate : matches) {
			if (!candidate.isDisplayed() || !candidate.isEnabled()) continue;
			String text = candidate.getText(); int candidateLength = text == null ? Integer.MAX_VALUE : text.trim().length();
			if (candidateLength < length) { shortest = candidate; length = candidateLength; }
		}
		return shortest;
	}
	private WebElement checkboxForLabel(WebElement label) {
		String forId = label.getAttribute("for"); if (forId == null || forId.trim().isEmpty()) return null;
		List<WebElement> matches = driver.findElements(By.id(forId)); if (matches.isEmpty()) return null;
		WebElement element = matches.get(0); return "checkbox".equalsIgnoreCase(element.getAttribute("type")) ? element : null;
	}
}
