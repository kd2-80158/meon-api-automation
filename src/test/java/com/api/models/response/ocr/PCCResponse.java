package com.api.models.response.ocr;

public class PCCResponse {

	private String document_name;
	private boolean success;
	private Verification verification;
	private String code;
	private String msg;

	public static class Verification {
		private String expires_at;
		private int expiry_days;
		private String final_status;
		private String issue_date;
		private String raw;

		public String getExpires_at() {
			return expires_at;
		}

		public void setExpires_at(String expires_at) {
			this.expires_at = expires_at;
		}

		public int getExpiry_days() {
			return expiry_days;
		}

		public void setExpiry_days(int expiry_days) {
			this.expiry_days = expiry_days;
		}

		public String getFinal_status() {
			return final_status;
		}

		public void setFinal_status(String final_status) {
			this.final_status = final_status;
		}

		public String getIssue_date() {
			return issue_date;
		}

		public void setIssue_date(String issue_date) {
			this.issue_date = issue_date;
		}

		public String getRaw() {
			return raw;
		}

		public void setRaw(String raw) {
			this.raw = raw;
		}

		@Override
		public String toString() {
			return "Verification [expires_at=" + expires_at + ", expiry_days=" + expiry_days + ", final_status="
					+ final_status + ", issue_date=" + issue_date + ", raw=" + raw + "]";
		}

	}

	public String getDocument_name() {
		return document_name;
	}

	public void setDocument_name(String document_name) {
		this.document_name = document_name;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Verification getVerification() {
		return verification;
	}

	public void setVerification(Verification verification) {
		this.verification = verification;
	}
	
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "PCCResponse [document_name=" + document_name + ", success=" + success + ", verification=" + verification
				+ "]";
	}

}
