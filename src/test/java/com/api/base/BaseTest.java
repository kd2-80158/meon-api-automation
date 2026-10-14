package com.api.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.api.reporting.TestExecutionContext;
import com.api.utility.SeveritySoftAssert;
import com.google.gson.Gson;

import io.restassured.response.Response;

public class BaseTest {
	protected SeveritySoftAssert softAssert;
	protected Gson gson;
	protected Response response;

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
	
	@AfterMethod(alwaysRun = true)
	public void afterEachTest() {
	    if (response != null) {
	        TestExecutionContext.get()
	            .setHttpStatus(response.getStatusCode());
	    }
	}
}
