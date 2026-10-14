package com.api.models.response.mca;

public class GenerateTokenMCAResponse {

	private String access_token;
	private int expires_in;
	private String token_type;

	public GenerateTokenMCAResponse() {
	}

	public GenerateTokenMCAResponse(String access_token, int expires_in, String token_type) {
		super();
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.token_type = token_type;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	@Override
	public String toString() {
		return "GenerateTokenMCAResponse [access_token=" + access_token + ", expires_in=" + expires_in + ", token_type="
				+ token_type + "]";
	}

}
