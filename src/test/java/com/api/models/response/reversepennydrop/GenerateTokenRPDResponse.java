package com.api.models.response.reversepennydrop;

public class GenerateTokenRPDResponse {

	private int code;
	private boolean success;
	private String msg;
	private Data data;

	public GenerateTokenRPDResponse() {
		super();
	}

	public GenerateTokenRPDResponse(int code, boolean success, String msg, Data data) {
		super();
		this.code = code;
		this.success = success;
		this.msg = msg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public boolean getSuccess() {
		return success;
	}
	public String getMsg() {
		return msg;
	}


	public Data getData() {
		return data;
	}
	@Override
	public String toString() {
		return "GenerateTokenRPDResponse [code=" + code + ", success=" + success + ", msg=" + msg + ", data=" + data
				+ "]";
	}

	public static class Data {
		private String token;

		public Data() {
			super();
		}

		public Data(String token) {
			super();
			this.token = token;
		}

		public String getToken() {
			return token;
		}

		@Override
		public String toString() {
			return "Data [token=" + token + "]";
		}

	}

}
