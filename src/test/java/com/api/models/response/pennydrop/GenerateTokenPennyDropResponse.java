package com.api.models.response.pennydrop;

public class GenerateTokenPennyDropResponse {
	
	private String msg;
	private String status;
	private String token;
	
	public GenerateTokenPennyDropResponse() {
		super();
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@Override
	public String toString() {
		return "GenerateToken_PennyDrop [msg=" + msg + ", status=" + status + ", token=" + token + "]";
	}
	
	
	

}
