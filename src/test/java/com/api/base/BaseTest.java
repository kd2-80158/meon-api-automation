package com.api.base;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.api.utility.SeveritySoftAssert;
import com.google.gson.Gson;

public class BaseTest {
	protected SeveritySoftAssert softAssert;
	protected Gson gson;

//	@BeforeSuite(alwaysRun = true)
//	public void renameSuiteAndTest(ITestContext context) {
//		String testType = context.getCurrentXmlTest().getParameter("testType");
//
//		if (testType == null || testType.isEmpty()) {
//			testType = "Regression"; // default
//		}
//		String formatted = testType.substring(0, 1).toUpperCase() + testType.substring(1).toLowerCase();
//		context.getSuite().getXmlSuite().setName("API Test - " + formatted + " Suite");
//		context.getCurrentXmlTest().setName("API Test - " + formatted + " Test");
//		System.out.println(" Runtime Renamed: API Test - " + formatted);
//	}

	@BeforeMethod
	public void setupSoftAssert() {
		softAssert = new SeveritySoftAssert();
		gson = new Gson();

	}
}
