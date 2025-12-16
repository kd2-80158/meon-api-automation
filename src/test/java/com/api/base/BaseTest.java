package com.api.base;

import org.testng.annotations.BeforeMethod;

import com.api.utility.SeveritySoftAssert;
import com.google.gson.Gson;

public class BaseTest {
    protected SeveritySoftAssert softAssert;
    protected Gson gson;

    @BeforeMethod
    public void setupSoftAssert() {
        softAssert = new SeveritySoftAssert(); 
        gson = new Gson();
 
    }
}
