package com.api.models.request.reversepennydrop;

public class GenerateTokenRPDRequest {
	
	private String client_id;
	private String client_secret;
	
	public GenerateTokenRPDRequest()
	{
		super();
	}
	public GenerateTokenRPDRequest(String client_id, String client_secret) {
		super();
		this.client_id = client_id;
		this.client_secret = client_secret;
	}
	public GenerateTokenRPDRequest(String client_secret2) {
		this.client_id = client_secret2;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getClient_secret() {
		return client_secret;
	}
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	@Override
	public String toString() {
		return "GenerateTokenRPDRequest [client_id=" + client_id + ", client_secret=" + client_secret + "]";
	}

}
