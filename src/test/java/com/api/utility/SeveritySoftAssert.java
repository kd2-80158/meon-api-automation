package com.api.utility;

import org.testng.asserts.SoftAssert;

import com.api.reporting.TestExecutionContext;

public class SeveritySoftAssert extends SoftAssert {

    // =========================
    // INTERNAL HELPER
    // =========================
    private void record(Object actual, Object expected, Severity severity, String message) {
        TestExecutionContext ctx = TestExecutionContext.get();

        if (ctx != null) {
            ctx.setSeverity(severity.name());
            ctx.setExpectedResult(message != null ? message : "Expected: " + expected);
            ctx.setActualResult("Actual: " + actual);
        }
    }

    private void recordCondition(boolean condition, Severity severity, String message) {
        TestExecutionContext ctx = TestExecutionContext.get();

        if (ctx != null) {
            ctx.setSeverity(severity.name());
            ctx.setExpectedResult(message);
            ctx.setActualResult("Condition evaluated to: " + condition);
        }
    }

    // =========================
    // OVERRIDDEN ASSERTIONS
    // =========================

    public void assertEquals(int actual, int expected, Severity severity, String message) {
        record(actual, expected, severity, message);
        super.assertEquals(actual, expected, "[" + severity + "] " + message);
    }

    public void assertEquals(Severity severity, int actual, int expected) {
        record(actual, expected, severity, "Expected value should match");
        super.assertEquals(actual, expected, "[" + severity + "]");
    }

    public void assertEquals(String actual, String expected, Severity severity, String message) {
        record(actual, expected, severity, message);
        super.assertEquals(actual, expected, "[" + severity + "] " + message);
    }

    public void assertNotNull(Object obj, Severity severity, String message) {
        record(obj, "NOT NULL", severity, message);
        super.assertNotNull(obj, "[" + severity + "] " + message);
    }

    public void assertTrue(boolean condition, Severity severity, String message) {
        recordCondition(condition, severity, message);
        super.assertTrue(condition, "[" + severity + "] " + message);
    }

    public void assertFalse(boolean condition, Severity severity, String message) {
        recordCondition(condition, severity, message);
        super.assertFalse(condition, "[" + severity + "] " + message);
    }

    public void assertFail(Severity severity, String message) {
        recordCondition(false, severity, message);
        super.assertFalse(true, "[" + severity + "] " + message);
    }
}
