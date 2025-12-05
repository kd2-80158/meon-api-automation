package com.api.base;

import static io.restassured.RestAssured.given;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Listeners;

import com.api.filters.LoggingFilter;
import com.api.pojos.BaseURL;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({com.api.listeners.TestListener.class})
public class BaseService { // wrapper for RestAssured

    // base uri (not final so it can be loaded dynamically)
    protected static String BASE_URL;
    Logger logger = LoggerUtility.getLogger(this.getClass());
    // instance variable
    private RequestSpecification rs;

    // static initialization for filters (runs once)
    static {
        RestAssured.filters(new LoggingFilter());
    }
    /**
     * Constructor that accepts product name (e.g. "eSign", "aadhaar", "facefinder", "pennydrop")
     * It will load the URL from your config.json via JSONUtility and initialize RequestSpecification.
     */
    public BaseService(String product) {
        try {
            if (product == null || product.trim().isEmpty()) {
                throw new IllegalArgumentException("Product must be provided (e.g., 'aadhaar' or 'eSign').");
            }

            String key = product.trim();
            setBaseURL(key); // builds rs with url, auth, tokens, content-type, accept

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize BaseService for product: " + product, e);
        }
    }
    
    public boolean openUrlAndWaitWithTimeout(String url, Logger logger, int timeoutSeconds) {
        // timeoutSeconds <= 0 means wait indefinitely
        ExecutorService ex = Executors.newSingleThreadExecutor();
        try {
            // open URL in default browser if possible
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                    logger.info("Opened browser for URL: " + url);
                } else {
                    logger.info("Desktop not supported — please open URL manually: " + url);
                    System.out.println("Open manually: " + url);
                }
            } catch (Exception e) {
                logger.warn("Could not open browser automatically. Please open URL manually: " + url + " - " + e.getMessage());
                System.out.println("Open manually: " + url);
            }

            logger.info("After completing the Digilocker steps in browser, press ENTER to continue (timeout " 
                        + (timeoutSeconds <= 0 ? "infinite" : timeoutSeconds + "s") + ")...");
            System.out.println("\n⚠️  Waiting for user to finish Digilocker flow... Press ENTER to continue.");

            Callable<Boolean> readTask = () -> {
                try {
                    if (System.console() != null) {
                        System.console().readLine();
                        return true;
                    } 
                } catch (Exception e) {
                    return false;
                }
				return false;
            };

            Future<Boolean> f = ex.submit(readTask);
            try {
                if (timeoutSeconds <= 0) {
                    // wait indefinitely
                    return f.get();
                } else {
                    Boolean pressed = f.get(timeoutSeconds, TimeUnit.SECONDS);
                    return pressed != null && pressed;
                }
            } catch (ExecutionException ee) {
                logger.error("Error while waiting for user input: " + ee.getMessage(), ee);
                return false;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.warn("Waiting thread interrupted.");
                return false;
            } catch (java.util.concurrent.TimeoutException te) {
                // user did not press Enter within timeout
                f.cancel(true);
                logger.warn("Timeout waiting for user input (" + timeoutSeconds + "s).");
                return false;
            }
        } finally {
            ex.shutdownNow();
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

        // Build a fresh RequestSpecification for this product (do NOT rely on global RestAssured.baseURI)
        RequestSpecification spec = given()
                .baseUri(url)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        // ---- REMOVE automatic basic auth ----
        // if (config.getUsername() != null && config.getPassword() != null) {
        //     spec = spec.auth().preemptive().basic(config.getUsername(), config.getPassword());
        // }

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

    protected Response postRequestAadhaar(Object tokenRequest, String endpoint) {
        return rs.body(tokenRequest).post(endpoint);
    }
    protected Response postRequestEsign(Object tokenRequest, String endpoint) {
        return rs.body(tokenRequest).post(endpoint);
    }
    

    protected Response postRequestEsignAuth(Object tokenRequest, String endpoint,String signature) {
        logger.info("token in Base service:"+signature);
        return rs.header("signature",signature).body(tokenRequest).post(endpoint);
    }
    
    
    // fixed: now actually performs GET (was calling post previously)
    protected Response getRequest(Object tokenRequest, String endpoint) {
        return rs.contentType(ContentType.JSON).body(tokenRequest).get(endpoint);
    }

    protected Response postRequestWithAuth(Object requestPayload, String endpoint, String authToken) {
        return rs.contentType(ContentType.JSON).header("signature", authToken).body(requestPayload).post(endpoint);
    }

    protected Response putRequestWithAuth(Object requestPayLoad, String endpoint, String authToken) {
        return rs.contentType(ContentType.JSON).header("token", authToken).body(requestPayLoad).put(endpoint);
    }

    protected Response getRequestWithAuth(String endpoint, String authToken) {
        return rs.contentType(ContentType.JSON).header("token", authToken).get(endpoint);
    }
}
