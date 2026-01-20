package com.api.base;

import static io.restassured.RestAssured.given;

import java.awt.Desktop;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;

import com.api.filters.LoggingFilter;
import com.api.pojos.BaseURL;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class BaseService { // wrapper for RestAssured
	protected static String BASE_URL;
	Logger logger = LoggerUtility.getLogger(this.getClass());
	private RequestSpecification rs;
	static {
		RestAssured.filters(new LoggingFilter());
	}

	/**
	 * Constructor that accepts product name (e.g. "eSign", "aadhaar", "facefinder",
	 * "pennydrop") It will load the URL from your config.json via JSONUtility and
	 * initialize RequestSpecification.
	 */
	public BaseService(String product) {
		try {
			System.out.println("Printing product: " + product);
			if (product == null || product.trim().isEmpty()) {
				System.out.println("Inside IF-block product: " + product);
				throw new IllegalArgumentException("Product must be provided (e.g., 'aadhaar' or 'eSign').");
			}
			String key = product.trim();
			System.out.println("Printing product: " + key);
			setBaseURL(key); // builds rs with url, auth, tokens, content-type, accept
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize BaseService for product: " + product, e);
		}
	}

	private void setBaseURL(String product) {
		if (product == null || product.trim().isEmpty()) {
			throw new IllegalArgumentException("product must be provided");
		}
		String key = product.trim();

		BaseURL config = JSONUtility.getProduct(key);
		if (config == null || config.getUrl() == null || config.getUrl().trim().isEmpty()) {
			throw new IllegalStateException("URL not found for product: " + key);
		}

		String url = config.getUrl().trim();
		RequestSpecification spec = given().baseUri(url).contentType(ContentType.JSON).accept(ContentType.JSON);

		// Attach token headers if present
		if (config.getSecret_token() != null && !config.getSecret_token().isEmpty()) {
			spec = spec.header("X-Secret-Token", config.getSecret_token());
		}
		if (config.getClient_token() != null && !config.getClient_token().isEmpty()) {
			spec = spec.header("X-Client-Token", config.getClient_token());
		}

		this.rs = spec;
		BASE_URL = url;
	}

	// Without AUTH
	protected Response postRequestAadhaar(Object tokenRequest, String endpoint) {
		return rs.relaxedHTTPSValidation().body(tokenRequest).post(endpoint);
	}

	protected Response postRequestEsign(Object tokenRequest, String endpoint) {
		return rs.body(tokenRequest).post(endpoint);
	}

	protected Response postRequestKYC(Object tokenRequest, String endpoint) {
		return rs.body(tokenRequest).post(endpoint);
	}
	
	protected Response postRequestFaceFinder(Object tokenRequest, String endpoint) {
		return rs.body(tokenRequest).post(endpoint);
	}
	
	protected Response postRequestOCR(Object tokenRequest, String endpoint) {
		return rs.relaxedHTTPSValidation().body(tokenRequest).post(endpoint);
	}
	
	protected Response postRequestReversePennyDrop(Object tokenRequest, String endpoint)
	{
		return rs.body(tokenRequest).post(endpoint);
	}
	
	protected Response postRequestReversePennyDropWithAuth(Object tokenRequest, String endpoint, String token)
	{
		logger.info("token in Base service:" + token);
		return rs.header("token", token).body(tokenRequest).post(endpoint);
	}
	
    protected Response postRequestFaceFinderWithAuth(Object tokenRequest, String endpoint, String token)
    {
    	return rs.header("token",token).body(tokenRequest).post(endpoint);
    }

	// With AUTH
	protected Response postRequestEsignAuth(Object tokenRequest, String endpoint, String signature) {
		logger.info("token in Base service:" + signature);
		return rs.header("signature", signature).body(tokenRequest).post(endpoint);
	}

	// fixed: now actually performs GET (was calling post previously)
	protected Response getRequest(Object tokenRequest, String endpoint) {
		return rs.contentType(ContentType.JSON).body(tokenRequest).get(endpoint);
	}

	protected Response postRequestWithAuth(Object requestPayload, String endpoint, String authToken) {
		return rs.contentType(ContentType.JSON).header("signature", authToken).body(requestPayload).post(endpoint);
	}

	protected Response postRequestWithAuthKYC(Object requestPayload, String endpoint, String bearerToken) {
		return rs.contentType(ContentType.JSON).header("Authorization", "Bearer" + " " + bearerToken)
				.body(requestPayload).post(endpoint);
	}


	protected Response postRequestWithAuthPD(Object requestPayload, String endpoint, String bearerToken) {
		return rs.contentType(ContentType.JSON).header("Authorization", "Bearer" + " " + bearerToken)
				.body(requestPayload).post(endpoint);
	}

	protected Response putRequestWithAuth(Object requestPayLoad, String endpoint, String authToken) {
		return rs.contentType(ContentType.JSON).header("token", authToken).body(requestPayLoad).put(endpoint);
	}

	protected Response getRequestWithAuth(String endpoint, String authToken) {
		return rs.contentType(ContentType.JSON).header("token", authToken).get(endpoint);
	}

	protected Response postRequestPennyDrop(Object tokenRequest, String endpoint) {
		return rs.contentType(ContentType.JSON).body(tokenRequest).post(endpoint);
	}
}
