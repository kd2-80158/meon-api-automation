package com.api.base;

import org.testng.annotations.BeforeMethod;

import com.api.utility.SeveritySoftAssert;

public class BaseTest {
    protected SeveritySoftAssert softAssert;

    @BeforeMethod
    public void setupSoftAssert() {
        softAssert = new SeveritySoftAssert();         // If using plain Selenium
 
    }
}
