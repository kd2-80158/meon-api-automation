package com.api.models.response.esign;

public class GenerateTokenEsignResponse {
	
//	//{
//    "token": "babedd02-a327-4b21-9dc7-32cea4ce5c00",
//    "esign_url": "https://esignuat.meon.co.in/EsignServices/eSign/babedd02-a327-4b21-9dc7-32cea4ce5c00",
//    "pdf_reviewer": null,
//    "success": true,
//    "message": "link generated"

	private String token;
	private String esign_url;
	private boolean pdf_reviewer;
	private boolean success;
	private String message;
	
	public GenerateTokenEsignResponse() {
		super();
	}

	public GenerateTokenEsignResponse(String token, String esign_url, boolean pdf_reviewer, boolean success,
			String message) {
		super();
		this.token = token;
		this.esign_url = esign_url;
		this.pdf_reviewer = pdf_reviewer;
		this.success = success;
		this.message = message;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEsign_url() {
		return esign_url;
	}

	public void setEsign_url(String esign_url) {
		this.esign_url = esign_url;
	}

	public boolean isPdf_reviewer() {
		return pdf_reviewer;
	}

	public void setPdf_reviewer(boolean pdf_reviewer) {
		this.pdf_reviewer = pdf_reviewer;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "GenerateTokenEsignResponse [token=" + token + ", esign_url=" + esign_url + ", pdf_reviewer="
				+ pdf_reviewer + ", success=" + success + ", message=" + message + "]";
	}
	
	
	
	

}
