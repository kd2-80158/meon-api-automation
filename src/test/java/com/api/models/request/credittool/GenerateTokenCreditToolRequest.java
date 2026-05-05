package com.api.models.request.credittool;

public class GenerateTokenCreditToolRequest {

	private String email;
	private String password;

	public GenerateTokenCreditToolRequest() {
	}

	public GenerateTokenCreditToolRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "GenerateTokenCreditToolRequest [email=" + email + ", password=" + password + "]";
	}

}
