package com.api.models.response.credittool;

public class GenerateTokenCreditToolResponse {

	private Data data;

	private String msg;
	private String success;
	private String token;

	public GenerateTokenCreditToolResponse() {
	}

	public GenerateTokenCreditToolResponse(Data data, String msg, String success, String token) {
		super();
		this.data = data;
		this.msg = msg;
		this.success = success;
		this.token = token;
	}

	@Override
	public String toString() {
		return "GenerateTokenCreditToolResponse [data=" + data + ", msg=" + msg + ", success=" + success + ", token="
				+ token + "]";
	}

	public static class Data {
		private String name;
		private String user_type;

		public Data() {
		}

		public Data(String name, String user_type) {
			this.name = name;
			this.user_type = user_type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUser_type() {
			return user_type;
		}

		public void setUser_type(String user_type) {
			this.user_type = user_type;
		}

		@Override
		public String toString() {
			return "Data [name=" + name + ", user_type=" + user_type + "]";
		}

	}

}
