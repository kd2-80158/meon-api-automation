package com.api.models.response.esign;
public class FetchDocumentEsignResponse {

	private boolean Success;
	private String full_path;
	private String name_mismatch_error;
	private String aadhaar_mismatch_error;
	private String gender_mismatch_error;
	private int code;
	private String msg;
	
	public FetchDocumentEsignResponse() {
		super();
	}

	public FetchDocumentEsignResponse(boolean success, String full_path, String name_mismatch_error,
			String aadhaar_mismatch_error, String gender_mismatch_error) {
		super();
		Success = success;
		this.full_path = full_path;
		this.name_mismatch_error = name_mismatch_error;
		this.aadhaar_mismatch_error = aadhaar_mismatch_error;
		this.gender_mismatch_error = gender_mismatch_error;
	}

	public boolean isSuccess() {
		return Success;
	}

	public void setSuccess(boolean success) {
		Success = success;
	}

	public String getFull_path() {
		return full_path;
	}

	public void setFull_path(String full_path) {
		this.full_path = full_path;
	}

	public String getName_mismatch_error() {
		return name_mismatch_error;
	}

	public void setName_mismatch_error(String name_mismatch_error) {
		this.name_mismatch_error = name_mismatch_error;
	}

	public String getAadhaar_mismatch_error() {
		return aadhaar_mismatch_error;
	}

	public void setAadhaar_mismatch_error(String aadhaar_mismatch_error) {
		this.aadhaar_mismatch_error = aadhaar_mismatch_error;
	}

	public String getGender_mismatch_error() {
		return gender_mismatch_error;
	}

	public void setGender_mismatch_error(String gender_mismatch_error) {
		this.gender_mismatch_error = gender_mismatch_error;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "FetchDocumentEsignResponse [Success=" + Success + ", full_path=" + full_path + ", name_mismatch_error="
				+ name_mismatch_error + ", aadhaar_mismatch_error=" + aadhaar_mismatch_error
				+ ", gender_mismatch_error=" + gender_mismatch_error + "]";
	}
	
	
	
}
