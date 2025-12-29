package com.api.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.api.reporting.DashboardJsonWriter;
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
        ctx.setApiModule(
                result.getTestClass().getRealClass()
                      .getPackage().getName()
        );
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
        ctx.setError(
                result.getThrowable() != null
                        ? result.getThrowable().getMessage()
                        : "Unknown error"
        );

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
        // Close JSON array properly
        DashboardJsonWriter.finish();
    }
}
