package com.api.utility;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * Thread-safe ExtentReporter utility that prevents duplicate ExtentTest creation.
 * Usage:
 *   - ExtentReporterUtility.setupSparkReporter("logs/report.html");
 *   - ExtentReporterUtility.createExtentTest(uniqueId, displayName);
 *   - ExtentReporterUtility.getTest().info("...");
 *   - ExtentReporterUtility.flushReport();
 *
 * Note: For de-dup to work reliably, pass a stable uniqueId (e.g. className#methodName).
 */
public final class ExtentReporterUtility {

    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();
    private static final Map<String, ExtentTest> createdTests = new ConcurrentHashMap<>();
    private static volatile ExtentReports extent;
    private static volatile String reportPath;

    private ExtentReporterUtility() { /* no instances */ }

    /**
     * Initialize Extent spark reporter with given path.
     * Call once in your TestListener.onStart(...)
     */
    public static synchronized void setupSparkReporter(String path) {
        if (extent != null) {
            // already initialised, update path if required
            reportPath = path;
            return;
        }
        ExtentSparkReporter spark = new ExtentSparkReporter(path);
        // optional: configure the reporter (title, theme, etc.)
        spark.config().setReportName("API Test Report");
        spark.config().setDocumentTitle("API Test Results");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        reportPath = path;
    }

    /**
     * Create or return an existing ExtentTest identified by uniqueId.
     * Display name is used for the visible label in the report.
     *
     * @param uniqueId stable identifier for the test (eg: fully.qualified.ClassName#methodName)
     * @param displayName the readable test name shown in report
     */
    public static ExtentTest createExtentTest(String uniqueId, String displayName) {
        Objects.requireNonNull(uniqueId, "uniqueId must not be null");
        // create only once per uniqueId
        ExtentTest existing = createdTests.get(uniqueId);
        if (existing != null) {
            // store on ThreadLocal so getTest() returns a test for the current thread
            currentTest.set(existing);
            return existing;
        }

        // create a new test and record it
        synchronized (createdTests) {
            // double-check in synchronized block
            existing = createdTests.get(uniqueId);
            if (existing != null) {
                currentTest.set(existing);
                return existing;
            }
            if (extent == null) {
                // ensure extent exists (fallback)
                setupSparkReporter("logs/report.html");
            }
            ExtentTest t = extent.createTest(displayName == null ? uniqueId : displayName);
            createdTests.put(uniqueId, t);
            currentTest.set(t);
            return t;
        }
    }

    /**
     * Backwards-compatible overload (uses displayName as unique id).
     * Prefer the (uniqueId, displayName) overload to avoid collisions.
     */
    public static ExtentTest createExtentTest(String displayName) {
        String uid = displayName == null ? "unnamed-test" : displayName;
        return createExtentTest(uid, displayName);
    }

    /**
     * Returns the ExtentTest associated with the current thread (if any).
     */
    public static ExtentTest getTest() {
        return currentTest.get();
    }
    

    /**
     * Flushes the report to disk (call at end of suite).
     */
    public static synchronized void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    /**
     * Returns the path passed to setupSparkReporter()
     */
    public static String getReportPath() {
        return reportPath;
    }

    /**
     * Small helper used by your TestListener to produce a short summary subject string.
     * Example output: "API Test - Tests: total=42, passed=40, failed=1, skipped=1"
     */
    public static String getSimpleSummary(String suiteName, int total, int passed, int failed, int skipped) {
        return String.format("%s - Tests: total=%d, passed=%d, failed=%d, skipped=%d",
                suiteName == null ? "Tests" : suiteName, total, passed, failed, skipped);
    }
}