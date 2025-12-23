package com.api.models.response.esign;

public class GenerateClientTokenEsignResponse {
	
	private String signature;
	private String message;
	private boolean status;
	private int code;
	
	public GenerateClientTokenEsignResponse() {
	}
	
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public GenerateClientTokenEsignResponse(String signature, String message, boolean status) {
		super();
		this.signature = signature;
		this.message = message;
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "GenerateClientTokenResponse [signature=" + signature + ", message=" + message + ", status=" + status
				+ "]";
	}
	
	
	
	

}
