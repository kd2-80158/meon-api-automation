package com.api.utility;

import org.testng.asserts.SoftAssert;

public class SeveritySoftAssert extends SoftAssert {

    // New overloads (TestNG doesn't have these signatures)
    public void assertEquals(int actual, int expected, Severity severity, String message) {
        super.assertEquals(actual, expected, "[" + severity + "] " + message);
    }
    
    public void assertEquals(Severity severity, int actual, int expected) {
        super.assertEquals(actual, expected, "[" + severity + "] ");
    }

    public void assertEquals(String actual, String expected, Severity severity, String message) {
        super.assertEquals(actual, expected, "[" + severity + "] " + message);
    }

    public void assertNotNull(Object obj, Severity severity, String message) {
        super.assertNotNull(obj, "[" + severity + "] " + message);
    }

    public void assertTrue(boolean condition, Severity severity, String message) {
        super.assertTrue(condition, "[" + severity + "] " + message);
    }

    public void assertFalse(boolean condition, Severity severity, String message) {
        super.assertFalse(condition, "[" + severity + "] " + message);
    }
    
    public void assertFail(Severity severity,String message) {
        super.assertFalse(true,"[" + severity + "] " + message);
    }

    
}

