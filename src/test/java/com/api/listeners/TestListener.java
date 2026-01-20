package com.api.listeners;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.testng.IResultMap;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.api.utility.EmailUtility;
import com.api.utility.ExtentReporterUtility;
import com.api.utility.LoggerUtility;
import com.aventstack.extentreports.Status;

public class TestListener implements ITestListener {

    private final Logger logger = LoggerUtility.getLogger(this.getClass());

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite Started: " + context.getName());
        // ensure report is stored under logs/report.html
        ExtentReporterUtility.setupSparkReporter("logs/report.html");
        logger.info("Extent reporter initialized: " + ExtentReporterUtility.getReportPath());
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Skip config methods
        if (!result.getMethod().isTest()) {
            return;
        }

        logger.info("Starting test: " + result.getMethod().getMethodName());
        logger.info("Description: " + result.getMethod().getDescription());
        // Use fully qualified unique id to avoid collisions across classes with same method names
        String uniqueId = uniqueIdFor(result.getMethod());
        ExtentReporterUtility.createExtentTest(uniqueId, result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (!result.getMethod().isTest()) {
            return; // do not log config methods
        }

        logger.info(result.getMethod().getMethodName() + " PASSED");
        // Ensure we have a test for this result (defensive - create if missing)
        String uniqueId = uniqueIdFor(result.getMethod());
        ExtentReporterUtility.createExtentTest(uniqueId, result.getMethod().getMethodName());
        ExtentReporterUtility.getTest().log(Status.PASS, result.getMethod().getMethodName() + " PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (!result.getMethod().isTest()) {
            return;
        }
        String testName = result.getMethod().getMethodName();

        logger.error(testName + " FAILED");

        // Defensive: ensure an ExtentTest exists for this test
        String uniqueId = uniqueIdFor(result.getMethod());
        ExtentReporterUtility.createExtentTest(uniqueId, testName);

        ExtentReporterUtility.getTest().log(Status.FAIL, testName + " FAILED");

        // Log exception message
        Throwable t = result.getThrowable();
        if (t != null) {
            ExtentReporterUtility.getTest().log(Status.FAIL, "Error: " + t.getMessage());

            // Log full stacktrace inside <pre>
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : t.getStackTrace()) {
                sb.append(element.toString()).append("");
            }
            ExtentReporterUtility.getTest().log(Status.FAIL, "<pre>" + sb.toString() + "</pre>");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (!result.getMethod().isTest()) {
            return;
        }

        logger.warn(result.getMethod().getMethodName() + " SKIPPED");
        String uniqueId = uniqueIdFor(result.getMethod());
        ExtentReporterUtility.createExtentTest(uniqueId, result.getMethod().getMethodName());
        ExtentReporterUtility.getTest().log(Status.SKIP,
                result.getMethod().getMethodName() + " SKIPPED");
    }

    /**
     * Build a unique id for a test method: fullyQualifiedClassName#methodName
     */
    private String uniqueIdFor(ITestNGMethod m) {
        if (m == null) return null;
        String cname;
        try {
            if (m.getTestClass() != null && m.getTestClass().getName() != null) {
                cname = m.getTestClass().getName();
            } else {
                // fallback
                cname = m.getRealClass() != null ? m.getRealClass().getName() : "unknown";
            }
        } catch (Exception e) {
            cname = "unknown";
        }
        return cname + "#" + m.getMethodName();
    }

    /**
     * Convert an IResultMap (passed/failed/skipped) to a set of unique test ids,
     * and only keep ones that are actual test methods (not config methods).
     */
    private Set<String> uniqueTestIdsFrom(IResultMap results) {
        Set<String> ids = new HashSet<>();
        if (results == null) return ids;
        for (ITestResult r : results.getAllResults()) {
            ITestNGMethod m = r.getMethod();
            if (m != null && m.isTest()) {
                String id = uniqueIdFor(m);
                if (id != null) ids.add(id);
            }
        }
        return ids;
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite Finished: " + context.getName());
        // flush Extent report to disk
        ExtentReporterUtility.flushReport();

        // small wait to allow file system to flush (helps avoid zero-length/partial file)
        try {
            Thread.sleep(300);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        // get report path and send email with attachment
        String reportPath = ExtentReporterUtility.getReportPath();
        Path p = (reportPath == null) ? null : Paths.get(reportPath);
        if (p == null) {
            logger.warn("No report path available; sending summary-only email");
            sendSummaryOnly(context);
            return;
        }

        // ensure file exists and is non-empty before sending
        try {
            if (!Files.exists(p) || Files.size(p) == 0) {
                logger.warn("Report file missing or empty: " + p.toAbsolutePath());
                sendSummaryOnly(context);
                return;
            }
        } catch (Exception e) {
            logger.warn("Error checking report file: " + e.getMessage());
            sendSummaryOnly(context);
            return;
        }

        // read html for inline preview (safe, don't depend on it)
        String reportHtml = "";
        try {
            reportHtml = Files.readString(p, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn("Failed to read report HTML: " + e.getMessage());
        }

        // --- NEW: compute UNIQUE test method counts from result maps ---
        IResultMap passedMap = context.getPassedTests();
        IResultMap failedMap = context.getFailedTests();
        IResultMap skippedMap = context.getSkippedTests();

        Set<String> passedIds = uniqueTestIdsFrom(passedMap);
        Set<String> failedIds = uniqueTestIdsFrom(failedMap);
        Set<String> skippedIds = uniqueTestIdsFrom(skippedMap);

        // total unique test methods = union of the three sets
        Set<String> allUnique = new HashSet<>();
        allUnique.addAll(passedIds);
        allUnique.addAll(failedIds);
        allUnique.addAll(skippedIds);

        int totalUnique = allUnique.size();
        int passedUnique = passedIds.size();
        int failedUnique = failedIds.size();
        int skippedUnique = skippedIds.size();

        String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), totalUnique, passedUnique, failedUnique, skippedUnique);

        String body = "<p>Hi team,</p>" + "<p>Execution finished. Summary:</p>" + "<pre>" + subject + "</pre>"
                + "<p>Attached: <b>" + p.getFileName().toString() + "</b></p>"
                + (reportHtml.isBlank() ? "<p>(report preview not available)</p>" : "<hr/>" + reportHtml);

        // send email with the report.html as attachment (no zip)
        try {

            EmailUtility.sendReportEmail(subject, body, p.toAbsolutePath().toString(),
                    EmailUtility.loadRecipientsFromPropsOrDefault());
            logger.info("Email sent with attachment: " + p.toAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to send email with report attachment: " + e.getMessage());
            // fallback: send summary-only
            sendSummaryOnly(context);
        }
    }

    private void sendSummaryOnly(ITestContext context) {
        IResultMap passedMap = context.getPassedTests();
        IResultMap failedMap = context.getFailedTests();
        IResultMap skippedMap = context.getSkippedTests();

        Set<String> passedIds = uniqueTestIdsFrom(passedMap);
        Set<String> failedIds = uniqueTestIdsFrom(failedMap);
        Set<String> skippedIds = uniqueTestIdsFrom(skippedMap);

        Set<String> allUnique = new HashSet<>();
        allUnique.addAll(passedIds);
        allUnique.addAll(failedIds);
        allUnique.addAll(skippedIds);

        int totalUnique = allUnique.size();
        int passedUnique = passedIds.size();
        int failedUnique = failedIds.size();
        int skippedUnique = skippedIds.size();

        String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), totalUnique, passedUnique, failedUnique, skippedUnique);
        String body = "<p>Hi team,</p><p>Execution finished. Summary:</p><pre>" + subject
                + "</pre><p>Report file not available.</p>";
        try {
            EmailUtility.sendReportEmail(subject, body, null, EmailUtility.loadRecipientsFromPropsOrDefault());
            logger.info("Summary-only email sent");
        } catch (Exception e) {
            logger.error("Failed to send email with report attachment", e);
        }
    }
}
