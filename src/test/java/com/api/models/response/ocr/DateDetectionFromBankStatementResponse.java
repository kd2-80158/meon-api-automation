package com.api.models.response.ocr;

public class DateDetectionFromBankStatementResponse {
	private String msg;
	private boolean success;
	private Data data;

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

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static class Data {
		private boolean Valid;
		private String date;

		public boolean isValid() {
			return Valid;
		}

		public void setValid(boolean valid) {
			Valid = valid;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

	}

}
