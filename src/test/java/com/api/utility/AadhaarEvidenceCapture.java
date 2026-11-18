package com.api.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;

import com.google.gson.GsonBuilder;

/** Writes limited, redacted browser evidence for failed DigiLocker UI phases. */
public final class AadhaarEvidenceCapture {

	private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")
			.withZone(ZoneOffset.UTC);
	private static final String PAGE_SNAPSHOT_SCRIPT = loadScript("/aadhaar-evidence-snapshot.js");
	private static final String REDACT_FOR_SCREENSHOT_SCRIPT = loadScript("/aadhaar-evidence-redact.js");

	private static String loadScript(String resource) {
		try (InputStream stream = AadhaarEvidenceCapture.class.getResourceAsStream(resource)) {
			if (stream == null) throw new IllegalStateException("Missing evidence resource: " + resource);
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private AadhaarEvidenceCapture() {}

	/** Returns the JSON evidence path, or null if the browser/file system is unavailable. */
	public static Path capture(WebDriver driver, String phase) {
		if (driver == null) return null;
		Path directory = Paths.get(System.getProperty("user.dir"), "target", "aadhaar-evidence");
		String stamp = FILE_TIME.format(Instant.now());
		Path jsonPath = directory.resolve("digilocker-" + stamp + ".json");
		Path screenshotPath = directory.resolve("digilocker-" + stamp + ".png");
		try {
			Files.createDirectories(directory);
			Map<String, Object> evidence = new LinkedHashMap<>();
			evidence.put("capturedAtUtc", Instant.now().toString());
			evidence.put("phase", safePhase(phase));
			evidence.put("url", safeUrl(driver.getCurrentUrl()));
			try {
				Object snapshot = ((JavascriptExecutor) driver).executeScript(PAGE_SNAPSHOT_SCRIPT);
				evidence.put("page", snapshot);
			} catch (RuntimeException ignored) {
				evidence.put("page", Map.of("snapshot", "unavailable"));
			}
			boolean screenshotSaved = false;
			try {
				((JavascriptExecutor) driver).executeScript(REDACT_FOR_SCREENSHOT_SCRIPT);
				File image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				Files.copy(image.toPath(), screenshotPath);
				screenshotSaved = true;
			} catch (Exception ignored) {
				// Keep the structured snapshot even if screenshot capture is unavailable.
			}
			evidence.put("redactedScreenshotSaved", screenshotSaved);
			if (screenshotSaved) evidence.put("screenshotFile", screenshotPath.getFileName().toString());
			Files.writeString(jsonPath, new GsonBuilder().setPrettyPrinting().create().toJson(evidence),
					StandardCharsets.UTF_8);
			return jsonPath;
		} catch (Exception ignored) {
			return null;
		}
	}

	private static String safePhase(String phase) {
		if (phase == null) return "unknown";
		String safe = phase.replaceAll("[^A-Za-z0-9 _-]", "");
		return safe.substring(0, Math.min(safe.length(), 80));
	}

	private static String safeUrl(String value) {
		try {
			URI uri = URI.create(value);
			String path = uri.getPath();
			if (path == null || path.isEmpty()) path = "/";
			String[] parts = path.split("/", -1);
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].length() > 40 || parts[i].matches("(?i).*[0-9a-f]{24,}.*") || parts[i].matches(".*\\d{8,}.*")) {
					parts[i] = "[redacted]";
				}
			}
			return uri.getScheme() + "://" + uri.getHost() + String.join("/", parts);
		} catch (RuntimeException ignored) {
			return "[unavailable]";
		}
	}
}
