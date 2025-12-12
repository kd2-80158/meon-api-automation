package com.api.models.request.pennydrop;

public class GenerateTokenPennyDropRequest {

	protected Live live;
	
	public GenerateTokenPennyDropRequest() {
		super();
	}

	public GenerateTokenPennyDropRequest(Live live) {
		super();
		this.live = live;
	}


	public Live getLive() {
		return live;
	}

	public void setLive(Live live) {
		this.live = live;
	}
	
	

	@Override
	public String toString() {
		return "GenerateToken_PennyDrop [live=" + live + "]";
	}



	public static class Live {
		private String username;
		private String password;
		
		public Live() {
			super();
		}

		public Live(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "Live [username=" + username + ", password=" + password + "]";
		}
	}
	

}
