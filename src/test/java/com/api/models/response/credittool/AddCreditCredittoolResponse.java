package com.api.models.response.credittool;

public class AddCreditCredittoolResponse {

	private Data data;
	private String msg;
	private boolean success;
	
	public AddCreditCredittoolResponse() {
	}

	public Data getData() {
		return data;
	}



	public void setData(Data data) {
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



	public static class Data {
		private int code;
		
		public Data() {
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return "Data [code=" + code + "]";
		}
		
		
	}

}
