package com.api.utility;

import java.io.File;
import java.io.FileReader;

import com.api.pojos.BaseURL;
import com.api.pojos.Config;
import com.google.gson.Gson;

public class JSONUtility {

    private static final String CONFIG_PATH = System.getProperty("user.dir") + "/config/config.json";

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

    public static BaseURL getAadhaar() {
        return getProduct("aadhaar");
    }
    
    public static BaseURL getKYC() {
        return getProduct("kyc");
    }
    
    public static BaseURL getEsign()
    {
    	return getProduct("eSign");
    }
    
    public static BaseURL getPennyDrop()
    {
    	System.out.println(">>> READING CONFIG FROM: " + CONFIG_PATH);
    	return getProduct("pennydrop");
    }
    
    public static BaseURL getReversePennyDrop()
    {
    	return getProduct("rpd");
    }
    
    public static BaseURL getFaceFinder()
    {
    	return getProduct("facefinder");
    }
    
    public static BaseURL getOcr()
    {
    	return getProduct("ocr");
    }
}
