package com.api.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReporterUtility {

    private static ExtentReports extentReports;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static String reportPath;

    public static void setupSparkReporter(String pathName) {
        reportPath = System.getProperty("user.dir") + "/" + pathName;
        ExtentSparkReporter extentSparkReporter = new ExtentSparkReporter(reportPath);
        extentReports = new ExtentReports();
        extentReports.attachReporter(extentSparkReporter);
    }

    public static void createExtentTest(String testName) {
        ExtentTest test = extentReports.createTest(testName);
        extentTest.set(test);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    /**
     * Return absolute file path of generated report (use after flush).
     */
    public static String getReportPath() {
        return reportPath;
    }

    /**
     * Read and return the HTML content of the generated report.
     * Note: call after flushReport() to ensure file was written.
     */
    public static String getReportHtml() {
        if (reportPath == null) return null;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(reportPath));
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read report HTML at: " + reportPath, e);
        }
    }

    /**
     * return a short plain-text summary you can include in email subject/body.
     * You can expand this later to capture counts from your TestListener.
     */
    public static String getSimpleSummary(String suiteName, int total, int passed, int failed, int skipped) {
        return String.format("%s - Tests: total=%d, passed=%d, failed=%d, skipped=%d", 
                suiteName == null ? "Test Suite" : suiteName, total, passed, failed, skipped);
    }

}
