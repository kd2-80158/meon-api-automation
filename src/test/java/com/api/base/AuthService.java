package com.api.base;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.Listeners;

import com.api.models.request.GenerateClientTokenRequest;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

@Listeners({com.api.listeners.TestListener.class})
public class AuthService extends BaseService {
	
	Logger logger = LoggerUtility.getLogger(this.getClass());
	
	private static final String BASE_PATH = "/get_access_token";
	
	public String getClientToken()
	{
		GenerateClientTokenRequest tokenRequest = new GenerateClientTokenRequest(JSONUtility.getAadhaar().getCompany_name(),JSONUtility.getAadhaar().getSecret_token());
		
		Response response = generateClientToken(tokenRequest);
		logger.info("Response: "+response.asPrettyString());
		
		//extracting the client token
		String clientToken =response.jsonPath().getString("client_token");
		
		if(clientToken==null || clientToken.isEmpty())
		{
			logger.error("Client token not received, Response is: "+response.asPrettyString());
		}
		else
		{
			logger.info("Client token received...."+clientToken);
		}
		return clientToken;
	}
	
	public Response generateClientTokenWithPlainText(Object requestObject) {
	    String requestBody;
	    if (requestObject instanceof String) {
	        requestBody = (String) requestObject;
	    } else {
	        requestBody = new Gson().toJson(requestObject); // convert POJO -> JSON string
	    }

	    return given()
	            .baseUri(BASE_URL)
	            .header("Content-Type", "text/plain")
	            .body(requestBody)                 // now always a String
	            .when()
	            .post(BASE_PATH);        // ensure endpoint is correct
	}
	
	public Response generateClientTokenWithNoBody() {
	    return given()
	            .baseUri(BASE_URL)
	            .contentType("application/json")
	            .when()
	            .post(BASE_PATH);   // no .body()
	}
	

public Response generateClientTokenWithRawJson(String rawJson) {
    return given()
            .baseUri(BASE_URL)          // or baseURL.getUrl() depending on your class
            .contentType("application/json")
            .body(rawJson)
            .when()
            .post(BASE_PATH);
}

public Response generateClientTokenWithGetMethod(Object requestObject) {
    return given()
            .baseUri(BASE_URL)
            .contentType("application/json")
            .body(requestObject)            // server will ignore body, but included intentionally for test
            .when()
            .get("/get_access_token");      // <-- GET instead of POST
}

	
	public Response generateClientToken(GenerateClientTokenRequest tokenRequest)
	{
		return postRequest(tokenRequest, BASE_PATH);
	}
	
}
