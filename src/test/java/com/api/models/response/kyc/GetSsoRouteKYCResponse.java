package com.api.models.response.kyc;

public class GetSsoRouteKYCResponse {
	
	private String short_url;
	private String url;
	
	public GetSsoRouteKYCResponse() {
		super();
	}

	public GetSsoRouteKYCResponse(String short_url, String url) {
		super();
		this.short_url = short_url;
		this.url = url;
	}

	public String getShort_url() {
		return short_url;
	}

	public void setShort_url(String short_url) {
		this.short_url = short_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "GetSsoRouteKYCResponse [short_url=" + short_url + ", url=" + url + "]";
	}
	
	

}
