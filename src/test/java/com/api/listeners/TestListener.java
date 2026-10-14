
package com.api.listeners;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.testng.IResultMap;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.api.utility.EmailUtility;
import com.api.utility.ExtentReporterUtility;
import com.api.utility.LoggerUtility;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class TestListener implements ITestListener {

	private static final String REPORT_PATH = "logs/report.html";

	private static final String EXTENT_TEST_KEY = "extentTest";

	private final Logger logger = LoggerUtility.getLogger(this.getClass());

	// ==========================================
	// TEST CONTEXT INITIALIZATION
	// ==========================================

	@Override
	public void onStart(ITestContext context) {

		logger.info("Test Context Started: {}", context.getName());

		ExtentReporterUtility.setupSparkReporter(REPORT_PATH);

		logger.info("Extent Report Initialized: {}", ExtentReporterUtility.getReportPath());
	}

	// ==========================================
	// TEST START
	// ==========================================

	@Override
	public void onTestStart(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		String methodName = result.getMethod().getMethodName();

		String className = result.getTestClass().getName();

		String description = result.getMethod().getDescription();

		logger.info("Starting Test: {}#{}", className, methodName);

		String uniqueId = uniqueIdFor(result.getMethod());

		/*
		 * Every invocation receives its own ID.
		 *
		 * Important for: - DataProvider tests - Parallel execution - TestNG
		 * invocationCount - Retry executions
		 */
		String invocationId = UUID.randomUUID().toString();

		String displayName = result.getTestClass().getRealClass().getSimpleName() + " - " + methodName;

		ExtentTest test = ExtentReporterUtility.createInvocationTest(uniqueId, invocationId, displayName);

		/*
		 * Save the ExtentTest on this ITestResult. Completion callbacks will reuse it.
		 */
		result.setAttribute(EXTENT_TEST_KEY, test);

		ExtentReporterUtility.assignCategory(result.getTestClass().getRealClass().getSimpleName());

		String[] groups = result.getMethod().getGroups();

		if (groups != null && groups.length > 0) {
			ExtentReporterUtility.assignCategory(groups);
		}

		test.log(Status.INFO, "Test execution started");

		test.log(Status.INFO, "Test Class: " + className);

		test.log(Status.INFO, "Test Method: " + methodName);

		if (description != null && !description.trim().isEmpty()) {

			test.log(Status.INFO, "Description: " + description);
		}

		Object[] parameters = result.getParameters();

		if (parameters != null && parameters.length > 0) {

			test.log(Status.INFO, "Test Parameters Count: " + parameters.length);
		}
	}

	// ==========================================
	// TEST SUCCESS
	// ==========================================

	@Override
	public void onTestSuccess(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		String testName = result.getMethod().getMethodName();

		logger.info("{} PASSED", testName);

		try {

			ExtentTest test = getOrCreateExtentTest(result);

			test.log(Status.PASS, testName + " PASSED");

			logExecutionTime(result, test);

		} finally {

			ExtentReporterUtility.clearCurrentTest();
		}
	}

	// ==========================================
	// TEST FAILURE
	// ==========================================

	@Override
	public void onTestFailure(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		String testName = result.getMethod().getMethodName();

		logger.error("{} FAILED", testName);

		try {

			ExtentTest test = getOrCreateExtentTest(result);

			test.log(Status.FAIL, testName + " FAILED");

			Throwable throwable = result.getThrowable();

			if (throwable != null) {

				logger.error("Test Failure: " + testName, throwable);

				/*
				 * ExtentReports automatically formats exception and stack trace details.
				 */
				test.fail(throwable);
			}

			logExecutionTime(result, test);

		} finally {

			ExtentReporterUtility.clearCurrentTest();
		}
	}

	// ==========================================
	// TEST SKIPPED
	// ==========================================

	@Override
	public void onTestSkipped(ITestResult result) {

		if (!isValidTest(result)) {
			return;
		}

		String testName = result.getMethod().getMethodName();

		logger.warn("{} SKIPPED", testName);

		try {

			ExtentTest test = getOrCreateExtentTest(result);

			test.log(Status.SKIP, testName + " SKIPPED");

			Throwable throwable = result.getThrowable();

			if (throwable != null) {

				logger.warn("Skip Reason: {}", throwable.toString());

				test.skip(throwable);
			}

			logExecutionTime(result, test);

		} finally {

			ExtentReporterUtility.clearCurrentTest();
		}
	}

	// ==========================================
	// EXECUTION TIME
	// ==========================================

	private void logExecutionTime(ITestResult result, ExtentTest test) {

		long startTime = result.getStartMillis();

		long endTime = result.getEndMillis();

		if (startTime <= 0 || endTime < startTime) {
			return;
		}

		long duration = endTime - startTime;

		test.log(Status.INFO, "Test Execution Time: " + duration + " ms");
	}

	// ==========================================
	// EXTENT TEST RECOVERY
	// ==========================================

	private ExtentTest getOrCreateExtentTest(ITestResult result) {

		Object existing = result.getAttribute(EXTENT_TEST_KEY);

		if (existing instanceof ExtentTest) {

			return (ExtentTest) existing;
		}

		/*
		 * Fallback for skipped tests where onTestStart might not have been called.
		 *
		 * A fresh invocation identifier prevents accidental reuse of another test.
		 */
		String uniqueId = uniqueIdFor(result.getMethod());

		String invocationId = UUID.randomUUID().toString();

		String displayName = result.getMethod().getMethodName();

		ExtentTest test = ExtentReporterUtility.createInvocationTest(uniqueId, invocationId, displayName);

		result.setAttribute(EXTENT_TEST_KEY, test);

		return test;
	}

	// ==========================================
	// VALID TEST CHECK
	// ==========================================

	private boolean isValidTest(ITestResult result) {

		return result != null && result.getMethod() != null && result.getMethod().isTest();
	}

	// ==========================================
	// UNIQUE TEST IDENTIFIER
	// ==========================================

	private String uniqueIdFor(ITestNGMethod method) {

		if (method == null) {
			return "unknown#unknown";
		}

		String className = "unknown";

		try {

			if (method.getTestClass() != null && method.getTestClass().getName() != null) {

				className = method.getTestClass().getName();

			} else if (method.getRealClass() != null) {

				className = method.getRealClass().getName();
			}

		} catch (Exception e) {

			logger.warn("Unable to resolve test class: {}", e.getMessage());
		}

		return className + "#" + method.getMethodName();
	}

	// ==========================================
	// UNIQUE METHOD COUNTING
	// ==========================================

	private Set<String> uniqueTestIdsFrom(IResultMap results) {

		Set<String> ids = new HashSet<>();

		if (results == null) {
			return ids;
		}

		for (ITestResult result : results.getAllResults()) {

			if (isValidTest(result)) {

				ids.add(uniqueIdFor(result.getMethod()));
			}
		}

		return ids;
	}

	// ==========================================
	// EXECUTION SUMMARY
	// ==========================================

	private TestSummary buildSummary(ITestContext context) {

		Set<String> passed = uniqueTestIdsFrom(context.getPassedTests());

		Set<String> failed = uniqueTestIdsFrom(context.getFailedTests());

		Set<String> skipped = uniqueTestIdsFrom(context.getSkippedTests());

		/*
		 * Count unique methods with mutually exclusive status precedence:
		 *
		 * FAILED > SKIPPED > PASSED
		 *
		 * A method with any failed invocation is counted as failed.
		 */
		skipped.removeAll(failed);

		passed.removeAll(failed);
		passed.removeAll(skipped);

		Set<String> all = new HashSet<>();

		all.addAll(passed);
		all.addAll(failed);
		all.addAll(skipped);

		return new TestSummary(all.size(), passed.size(), failed.size(), skipped.size());
	}

	// ==========================================
	// TEST CONTEXT FINISH
	// ==========================================

	@Override
	public void onFinish(ITestContext context) {

		logger.info("Test Context Finished: {}", context.getName());

		TestSummary summary = buildSummary(context);

		String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), summary.total, summary.passed,
				summary.failed, summary.skipped);

		/*
		 * Flush before checking or attaching the HTML report.
		 */
		try {

			ExtentReporterUtility.flushReport();

			logger.info("Extent Report Flushed Successfully");

		} catch (Exception e) {

			logger.error("Unable to flush Extent Report", e);
		}

		String reportPath = ExtentReporterUtility.getReportPath();

		Path path = reportPath == null ? null : Paths.get(reportPath);

		boolean reportAvailable = false;

		if (path != null) {

			try {

				reportAvailable = Files.isRegularFile(path) && Files.size(path) > 0;

			} catch (Exception e) {

				logger.warn("Unable to verify report file", e);
			}
		}

		if (!reportAvailable) {

			logger.warn("Extent Report unavailable. " + "Sending summary-only email.");

			sendSummaryOnly(context, summary);
			return;
		}

		String body = buildEmailBody(context.getName(), summary, true, path.getFileName().toString());

		try {

			EmailUtility.sendReportEmail(subject, body, path.toAbsolutePath().toString(),
					EmailUtility.loadRecipientsFromPropsOrDefault());

			logger.info("Extent Report Email Sent: {}", path.toAbsolutePath());

		} catch (Exception e) {

			logger.error("Unable to send report attachment", e);

			sendSummaryOnly(context, summary);
		}
	}

	// ==========================================
	// EMAIL BODY
	// ==========================================

	private String buildEmailBody(String suiteName, TestSummary summary, boolean attached, String attachmentName) {

		StringBuilder body = new StringBuilder();

		body.append("<html><body>");

		body.append("<h2>API Automation Execution Report</h2>");

		body.append("<p>Hi Team,</p>");

		body.append("<p>Automation execution has " + "been completed.</p>");

		body.append("<p><b>Test Context:</b> ").append(escapeHtml(suiteName)).append("</p>");

		body.append(
				"<table border='1' " + "cellpadding='10' " + "cellspacing='0' " + "style='border-collapse:collapse;'>");

		appendRow(body, "Total Tests", summary.total);

		appendRow(body, "Passed", summary.passed);

		appendRow(body, "Failed", summary.failed);

		appendRow(body, "Skipped", summary.skipped);

		body.append("</table>");

		double passPercentage = summary.total == 0 ? 0.0 : (summary.passed * 100.0) / summary.total;

		body.append("<p><b>Pass Percentage:</b> ");

		body.append(String.format(java.util.Locale.ROOT, "%.2f%%", passPercentage));

		body.append("</p>");

		body.append("<p><b>Environment:</b> ").append(escapeHtml(System.getProperty("env", "QA"))).append("</p>");

		if (attached) {

			body.append("<p>Detailed Extent Report Attached: " + "<b>");

			body.append(escapeHtml(attachmentName));

			body.append("</b></p>");

		} else {

			body.append("<p>Detailed HTML report " + "is not available.</p>");
		}

		body.append("<p>Regards,<br/>");
		body.append("API Automation Framework</p>");

		body.append("</body></html>");

		return body.toString();
	}

	private void appendRow(StringBuilder body, String label, int value) {

		body.append("<tr><td>").append(escapeHtml(label)).append("</td><td>").append(value).append("</td></tr>");
	}

	private String escapeHtml(String value) {

		if (value == null) {
			return "";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	// ==========================================
	// SUMMARY-ONLY EMAIL FALLBACK
	// ==========================================

	private void sendSummaryOnly(ITestContext context, TestSummary summary) {

		String subject = ExtentReporterUtility.getSimpleSummary(context.getName(), summary.total, summary.passed,
				summary.failed, summary.skipped);

		String body = buildEmailBody(context.getName(), summary, false, null);

		try {

			EmailUtility.sendReportEmail(subject, body, null, EmailUtility.loadRecipientsFromPropsOrDefault());

			logger.info("Summary-only email sent");

		} catch (Exception e) {

			logger.error("Unable to send summary email", e);
		}
	}

	// ==========================================
	// SUMMARY DATA HOLDER
	// ==========================================

	private static final class TestSummary {

		private final int total;
		private final int passed;
		private final int failed;
		private final int skipped;

		private TestSummary(int total, int passed, int failed, int skipped) {

			this.total = total;
			this.passed = passed;
			this.failed = failed;
			this.skipped = skipped;
		}
	}
}
