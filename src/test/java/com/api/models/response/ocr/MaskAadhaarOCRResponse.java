package com.api.models.response.ocr;

public class MaskAadhaarOCRResponse {

	private String masked_image_base64;
	private boolean success;
	private String msg;

	public MaskAadhaarOCRResponse() {
	}

	public String getMasked_image_base64() {
		return masked_image_base64;
	}

	public void setMasked_image_base64(String masked_image_base64) {
		this.masked_image_base64 = masked_image_base64;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
