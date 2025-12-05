package com.api.utility;

import java.io.File;
import java.io.FileReader;

import com.api.pojos.BaseURL;
import com.api.pojos.Config;
import com.google.gson.Gson;

public class JSONUtility {

    private static final String CONFIG_PATH = System.getProperty("user.dir") + "/config/config.json";

    /**
     * Get any product config like:
     * eSign, aadhaar, facefinder, pennydrop
     */
    public static BaseURL getProduct(String productName) {
        try {
            Gson gson = new Gson();
            File file = new File(CONFIG_PATH);
            FileReader reader = new FileReader(file);

            Config config = gson.fromJson(reader, Config.class);
            return config.getBaseURL().get(productName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load product config: " + productName, e);
        }
    }

    /**
     * Convenience method for aadhaar only.
     */
    public static BaseURL getAadhaar() {
        return getProduct("aadhaar");
    }
}
