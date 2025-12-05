package com.api.listeners;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
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
        logger.info("Starting test: " + result.getMethod().getMethodName());
        logger.info("Description: " + result.getMethod().getDescription());
        logger.info("Groups: " + Arrays.toString(result.getMethod().getGroups()));
        // create Extent test
        ExtentReporterUtility.createExtentTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info(result.getMethod().getMethodName() + " PASSED");
        ExtentReporterUtility.getTest().log(Status.PASS, result.getMethod().getMethodName() + " PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        logger.error(testName + " FAILED");

        ExtentReporterUtility.getTest().log(Status.FAIL, testName + " FAILED");

        // Log exception message
        Throwable t = result.getThrowable();
        if (t != null) {
            ExtentReporterUtility.getTest().log(Status.FAIL, "Error: " + t.getMessage());

            // Log full stacktrace inside <pre>
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : t.getStackTrace()) {
                sb.append(element.toString()).append("\n");
            }
            ExtentReporterUtility.getTest().log(Status.FAIL,
                    "<pre>" + sb.toString() + "</pre>");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn(result.getMethod().getMethodName() + " SKIPPED");
        ExtentReporterUtility.getTest().log(Status.SKIP, result.getMethod().getMethodName() + " SKIPPED");
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite Finished: " + context.getName());
        // flush Extent report to disk
        ExtentReporterUtility.flushReport();

        // small wait to allow file system to flush (helps avoid zero-length/partial file)
        try { Thread.sleep(300); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }

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

        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();

        String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), total, passed, failed, skipped);

        String body = "<p>Hi team,</p>"
                + "<p>Execution finished. Summary:</p>"
                + "<pre>" + subject + "</pre>"
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
        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), total, passed, failed, skipped);
        String body = "<p>Hi team,</p><p>Execution finished. Summary:</p><pre>" + subject + "</pre><p>Report file not available.</p>";
        try {
            EmailUtility.sendReportEmail(subject, body, null, EmailUtility.loadRecipientsFromPropsOrDefault());
            logger.info("Summary-only email sent");
        } catch (Exception e) {
            logger.error("Failed to send summary email: " + e.getMessage());
        }
    }
}
