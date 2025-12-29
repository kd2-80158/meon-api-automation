package com.api.assertions;

import org.testng.asserts.SoftAssert;

import com.api.reporting.TestExecutionContext;
import com.api.utility.Severity;

public class DashboardSoftAssert extends SoftAssert {

    @Override
    public void assertEquals(Object actual, Object expected, String message) {

        TestExecutionContext ctx = TestExecutionContext.get();

        ctx.setExpectedResult(message != null ? message : "Expected: " + expected);
        ctx.setActualResult("Actual: " + actual);
        ctx.setSeverity(Severity.CRITICAL.name());

        super.assertEquals(actual, expected, message);
    }

    public void assertEquals(Object actual, Object expected, Severity severity, String message) {

        TestExecutionContext ctx = TestExecutionContext.get();
        ctx.setExpectedResult(message);
        ctx.setActualResult("Actual: " + actual);
        ctx.setSeverity(severity.name());

        super.assertEquals(actual, expected, message);
    }

    @Override
    public void assertTrue(boolean condition, String message) {

        TestExecutionContext ctx = TestExecutionContext.get();
        ctx.setExpectedResult(message);
        ctx.setActualResult("Condition evaluated to: " + condition);
        ctx.setSeverity(Severity.HIGH.name());

        super.assertTrue(condition, message);
    }

    @Override
    public void assertFalse(boolean condition, String message) {

        TestExecutionContext ctx = TestExecutionContext.get();
        ctx.setExpectedResult(message);
        ctx.setActualResult("Condition evaluated to: " + condition);
        ctx.setSeverity(Severity.HIGH.name());

        super.assertFalse(condition, message);
    }
}
