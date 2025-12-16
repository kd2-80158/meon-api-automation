package com.api.models.response.aadhaar;

public class GenerateClientTokenResponse {
	
	private String clientToken;
	private boolean status;
	private String state;
	private String msg;
	private int code;

	public GenerateClientTokenResponse(String clientToken, boolean status, String state) {
		super();
		this.clientToken = clientToken;
		this.status = status;
		this.state = state;
	}
	
	public GenerateClientTokenResponse(int code, boolean status, String msg) {
		
		this.code = code;
		this.status = status;
		this.msg = msg;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return "GenerateClientTokenResponse [clientToken=" + clientToken + ", status=" + status + ", state=" + state
				+ "]";
	}
	
	

}
