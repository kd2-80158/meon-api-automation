package com.api.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.api.reporting.DashboardJsonWriter;
import com.api.reporting.DashboardResultMerger;
import com.api.reporting.ExecutionMeta;
import com.api.reporting.TestExecutionContext;

public class DashboardListener implements ITestListener {

	@Override
	public void onStart(ITestContext context) {
		// Initialize dashboard JSON array
		DashboardJsonWriter.init();
	}

	@Override
	public void onTestStart(ITestResult result) {
		TestExecutionContext ctx = TestExecutionContext.get();

		ctx.setTestName(result.getMethod().getMethodName());
		ctx.setTestCaseId(result.getMethod().getDescription());
		ctx.setClassName(result.getTestClass().getName());
		ctx.setApiModule(result.getTestClass().getRealClass().getPackage().getName());

		ctx.setExecutionType(ExecutionMeta.executionType());
		ctx.setExecutionSource(ExecutionMeta.executionSource());
		ctx.setExecutedBy(ExecutionMeta.executedBy());
		ctx.setEnvironment(ExecutionMeta.environment());
		ctx.setBuildId(ExecutionMeta.buildId());

		ctx.setStartTime(System.currentTimeMillis());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		TestExecutionContext ctx = TestExecutionContext.get();
		ctx.setEndTime(System.currentTimeMillis());
		ctx.setActualResult("Test passed");

		DashboardJsonWriter.write(ctx, "PASS");
		TestExecutionContext.clear();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		TestExecutionContext ctx = TestExecutionContext.get();
		ctx.setEndTime(System.currentTimeMillis());
		ctx.setActualResult("Test failed");
		ctx.setError(result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error");
		ctx.setFailureType(classifyFailure(result.getThrowable()));
		DashboardJsonWriter.write(ctx, "FAIL");
		TestExecutionContext.clear();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		TestExecutionContext ctx = TestExecutionContext.get();
		ctx.setEndTime(System.currentTimeMillis());
		ctx.setActualResult("Test skipped");

		DashboardJsonWriter.write(ctx, "SKIPPED");
		TestExecutionContext.clear();
	}

	@Override
	public void onFinish(ITestContext context) {
	    DashboardJsonWriter.finish();
	    DashboardResultMerger.merge();
	}
	
	private String classifyFailure(Throwable t) {
	    if (t == null) return null;

	    String msg = t.getMessage().toLowerCase();

	    if (msg.contains("assert")) return "ASSERTION";
	    if (msg.contains("timeout")) return "ENV";
	    if (msg.contains("otp") || msg.contains("aadhaar")) return "HUMAN_DEPENDENT";
	    if (msg.contains("500") || msg.contains("502")) return "API";

	    return "UNKNOWN";
	}

}
