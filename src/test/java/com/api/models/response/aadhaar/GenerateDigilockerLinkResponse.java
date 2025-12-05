package com.api.models.response.aadhaar;

public class GenerateDigilockerLinkResponse {

	//
//    "code": 200,
//    "msg": "URL Generated Successfully",
//    "status": "success",
//    "success": true,
//    "url": "https://api.digitallocker.gov.in/public/oauth2/1/authorize?dl_flow=signup&response_type=code&client_id=QI36C81C9D&
	private int code;
	private String msq;
	private String status;
	private boolean success;
	private String url;
	
	public GenerateDigilockerLinkResponse() {
	}

	public GenerateDigilockerLinkResponse(int code, String msq, String status, boolean success, String url) {
		super();
		this.code = code;
		this.msq = msq;
		this.status = status;
		this.success = success;
		this.url = url;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsq() {
		return msq;
	}

	public void setMsq(String msq) {
		this.msq = msq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "GenerateDigilockerLinkResponse [code=" + code + ", msq=" + msq + ", status=" + status + ", success="
				+ success + ", url=" + url + "]";
	}

}
