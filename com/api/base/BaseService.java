package com.api.base;

import static io.restassured.RestAssured.*;

import org.testng.annotations.Listeners;

import com.api.filters.LoggingFilter;
import com.api.pojos.BaseURL;
import com.api.utility.JSONUtility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({com.api.listeners.TestListener.class})
public class BaseService { // wrapper for RestAssured

    // base uri (not final so it can be loaded dynamically)
    protected static String BASE_URL;

    // instance variable
    private RequestSpecification rs;

    // static initialization for filters (runs once)
    static {
        RestAssured.filters(new LoggingFilter());
    }

    /**
     * Default constructor: loads product from system property: -Dproduct=<productName>
     * Falls back to "eSign" if none provided.
     */
    public BaseService() {
        this(System.getProperty("product", "aadhaar"));
    }

    /**
     * Constructor that accepts product name (e.g. "eSign", "aadhaar", "facefinder", "pennydrop")
     * It will load the URL from your config.json via JSONUtility and initialize RequestSpecification.
     */
    public BaseService(String product) {
        try {
            BaseURL baseConfig = JSONUtility.getProduct(product); // expects your utility method
            if (baseConfig == null || baseConfig.getUrl() == null) {
                throw new IllegalStateException("No configuration found for product: " + product);
            }
            BASE_URL = baseConfig.getUrl();

            // set RestAssured baseURI if you want
            RestAssured.baseURI = BASE_URL;

            // create request specification for instance methods
            rs = given().baseUri(BASE_URL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize BaseService for product: " + product, e);
        }
    }

    protected Response postRequest(Object tokenRequest, String endpoint) {
        return rs.contentType(ContentType.JSON).body(tokenRequest).post(endpoint);
    }
    
    protected Response getRequest(Object tokenRequest, String endpoint) {
        return rs.contentType(ContentType.JSON).body(tokenRequest).post(endpoint);
    }

    protected Response postRequestWithAuth(Object requestPayload, String endpoint, String authToken) {
        return rs.contentType(ContentType.JSON).header("token", authToken).body(requestPayload).post(endpoint);
    }

    protected Response putRequestWithAuth(Object requestPayLoad, String endpoint, String authToken) {
        return rs.contentType(ContentType.JSON).header("token", authToken).body(requestPayLoad).put(endpoint);
    }

    protected Response getRequestWithAuth(String endpoint, String authToken) {
        return rs.contentType(ContentType.JSON).header("token", authToken).get(endpoint);
    }
}
