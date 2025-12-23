package com.api.models.request.facefinder;

public class GenerateTokenForExportDataFaceFinderRequest {

	private String client_id;
	private String client_secret;
	private String transaction_id;

	public GenerateTokenForExportDataFaceFinderRequest() {
		super();
	}

	public GenerateTokenForExportDataFaceFinderRequest(String client_id, String client_secret, String transaction_id) {
		super();
		this.client_id = client_id;
		this.client_secret = client_secret;
		this.transaction_id = transaction_id;
	}

	public GenerateTokenForExportDataFaceFinderRequest(String client_id, String client_secret) {
		super();
		this.client_id = client_id;
		this.client_secret = client_secret;
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

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	@Override
	public String toString() {
		return "GenerateTokenForExportDataFaceFinderRequest [client_id=" + client_id + ", client_secret="
				+ client_secret + ", transaction_id=" + transaction_id + "]";
	}

}
