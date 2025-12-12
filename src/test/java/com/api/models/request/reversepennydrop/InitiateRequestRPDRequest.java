package com.api.models.request.reversepennydrop;


public class InitiateRequestRPDRequest {
	
	private String redirect_url;
	private String version;
	
	public InitiateRequestRPDRequest() {
		super();
	}

	public InitiateRequestRPDRequest(String redirect_url, String version) {
		super();
		this.redirect_url = redirect_url;
		this.version = version;
	}

	public InitiateRequestRPDRequest(String version2) {
		this.version = version2;
	}

	public String getRedirect_url() {
		return redirect_url;
	}

	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "InitiateRequestRPDRequest [redirect_url=" + redirect_url + ", version=" + version + "]";
	}

}
