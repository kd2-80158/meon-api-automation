package com.api.models.response.facefinder;

public class GenerateTokenForExportDataFaceFinderResponse {
	private int code;
	private boolean success;
	private String msg;
	private Data data;

	public GenerateTokenForExportDataFaceFinderResponse() {
		super();
	}

	public GenerateTokenForExportDataFaceFinderResponse(int code, boolean success, String msg, Data data) {
		super();
		this.code = code;
		this.success = success;
		this.msg = msg;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMsg() {
		return msg;
	}

	public Data getData() {
		return data;
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
