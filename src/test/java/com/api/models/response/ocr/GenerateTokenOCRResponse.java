package com.api.models.response.ocr;

public class GenerateTokenOCRResponse {

	private boolean success;
	private String token;
	private String msg;

	public GenerateTokenOCRResponse() {
		super();
	}

	public GenerateTokenOCRResponse(boolean success, String token, String msg) {
		super();
		this.success = success;
		this.token = token;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getToken() {
		return token;
	}

	@Override
	public String toString() {
		return "GenerateTokenOCRResponse [success=" + success + ", token=" + token + ", msg=" + msg + "]";
	}

}
