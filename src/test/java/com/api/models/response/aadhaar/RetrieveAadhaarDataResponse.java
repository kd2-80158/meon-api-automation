package com.api.models.response.aadhaar;

import com.api.pojos.AadhaarData;

public class RetrieveAadhaarDataResponse {
	
	private String msg;
	private boolean success;
	private AadhaarData data;
	
	public RetrieveAadhaarDataResponse() {
	}
	
	public RetrieveAadhaarDataResponse(String msg, boolean success, AadhaarData data) {
		super();
		this.msg = msg;
		this.success = success;
		this.data = data;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public AadhaarData getData() {
		return data;
	}
	public void setData(AadhaarData data) {
		this.data = data;
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder("RetrieveAadhaarDataResponse{");
	    sb.append("msg=").append(msg).append(", ");
	    sb.append("success=").append(success).append(", ");
	    if (data != null) {
	        sb.append("data=").append(data.toString());
	    } else {
	        sb.append("data=null");
	    }
	    sb.append("}");
	    return sb.toString();
	}

	
	
	
	

}
