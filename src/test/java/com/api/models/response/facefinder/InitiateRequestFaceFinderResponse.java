package com.api.models.response.facefinder;

public class InitiateRequestFaceFinderResponse {

	private int code;
	private boolean success;
	private String msg;
	private Data data;

	public InitiateRequestFaceFinderResponse() {
		super();
	}

	public InitiateRequestFaceFinderResponse(int code, boolean success, String msg, Data data) {
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

	@Override
	public String toString() {
		return "InitiateRequestFaceFinderResponse [code=" + code + ", success=" + success + ", msg=" + msg + ", data="
				+ data + "]";
	}

	public static class Data {
		private String url;
		private String transaction_id;

		public Data() {
			super();
		}

		public Data(String url, String transaction_id) {
			super();
			this.url = url;
			this.transaction_id = transaction_id;
		}

		public String getUrl() {
			return url;
		}

		public String getTransaction_id() {
			return transaction_id;
		}

		@Override
		public String toString() {
			return "Data [url=" + url + ", transaction_id=" + transaction_id + "]";
		}

	}

}
