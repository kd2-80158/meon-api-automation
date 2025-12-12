package com.api.models.response.reversepennydrop;

public class GenerateExportTokenRPDResponse {

	private int code;
	private boolean success;
	private String msg;
	private Data data;
	private String detail;
	
	public GenerateExportTokenRPDResponse() {
		this.detail = "JSON parse error - Expecting ',' delimiter: line 5 column 1 (char 158)";
	}
	
	public GenerateExportTokenRPDResponse(String detail) {
		this.detail = "Unsupported media type \\\"text/plain\\\" in request.";
	}

	public String getDetail() {
		return detail;
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
	
	

	@Override
	public String toString() {
		return "GenerateExportTokenRPDResponse [code=" + code + ", success=" + success + ", msg=" + msg + ", data="
				+ data + "]";
	}



	public static class Data {
		private String token;

		public Data() {
			super();
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
