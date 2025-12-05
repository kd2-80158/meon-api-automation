package com.api.models.request.aadhaar;


public class RetrieveAadhaarDataRequest {
	
	private String client_token;
	private String state;
	private boolean status;
	
	public RetrieveAadhaarDataRequest() {
	}
	
	public RetrieveAadhaarDataRequest(String client_token, String state, boolean status) {
		super();
		this.client_token = client_token;
		this.state = state;
		this.status = status;
	}
	public RetrieveAadhaarDataRequest(String client_token, String state) {
		this.client_token = client_token;
		this.state = state;
	}

	public RetrieveAadhaarDataRequest(String client_token) {
		this.client_token = client_token;
	}

	public String getClient_token() {
		return client_token;
	}
	public void setClient_token(String client_token) {
		this.client_token = client_token;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "RetrieveAadhaarDataRequest [client_token=" + client_token + ", state=" + state + ", status=" + status
				+ "]";
	}
	

}
