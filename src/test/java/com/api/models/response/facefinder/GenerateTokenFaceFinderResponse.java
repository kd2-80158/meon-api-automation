package com.api.models.response.facefinder;

public class GenerateTokenFaceFinderResponse {

	private int code;
	private boolean success;
	private String msg;
	private String detail;
	private Data data;
	public GenerateTokenFaceFinderResponse() {
		super();
	}
	public String getDetail() {
		return detail;
	}
	public GenerateTokenFaceFinderResponse(String detail) {
		super();
		this.detail = detail;
	}
	public GenerateTokenFaceFinderResponse(int code, boolean success, String msg, Data data) {
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
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}


	@Override
	public String toString() {
		return "GenerateTokenFaceFinderResponse [code=" + code + ", success=" + success + ", msg=" + msg + ", data="
				+ data + "]";
	}


	public static class Data
	{
		private String token;
        public Data()
        {
        	super();
        }
        
		public Data(String token) {
			super();
			this.token = token;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		@Override
		public String toString() {
			return "Data [token=" + token + "]";
		}
		
	}

}
