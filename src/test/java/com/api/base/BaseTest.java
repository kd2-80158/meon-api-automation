package com.api.base;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.asserts.SoftAssert;

import com.api.utility.SessionUtility;

@Listeners({com.api.listeners.TestListener.class})
public class BaseTest {
	
	protected SoftAssert softAssert;

	@BeforeMethod(alwaysRun = true)
	public void initSoftAssert() {
		softAssert = new SoftAssert();
	}
}
