package com.api.models.request;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class GenerateClientTokenRequest {
	@SerializedName("company_name")
	private String companyName;
	
	@SerializedName("secret_token")
	private String secretToken;
	
	public GenerateClientTokenRequest(String companyName)
	{
		this.companyName = companyName;
	}
	
	public GenerateClientTokenRequest(String companyName,String secretToken) {
		this.companyName = companyName;
		this.secretToken = secretToken;
	}

	public GenerateClientTokenRequest() {
	}


	public GenerateClientTokenRequest(Map<String, Object> body) {
	}


	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSecretToken() {
		return secretToken;
	}

	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}

	@Override
	public String toString() {
		return "GenerateClientTokenRequest [companyName=" + companyName + ", secretToken=" + secretToken + "]";
	}
	
	
	
	
	

}
