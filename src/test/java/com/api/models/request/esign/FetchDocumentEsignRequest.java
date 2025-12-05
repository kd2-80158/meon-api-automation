package com.api.models.request.esign;

public class FetchDocumentEsignRequest {
	
	private String token;
	private String mobile;
	
	public FetchDocumentEsignRequest() {
		super();
	}

	public FetchDocumentEsignRequest(String token, String mobile) {
		super();
		this.token = token;
		this.mobile = mobile;
	}
	
	public FetchDocumentEsignRequest(String mobile) {
		this.mobile = mobile;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Override
	public String toString() {
		return "FetchDocumentEsignRequest [token=" + token + ", mobile=" + mobile + "]";
	}

}
