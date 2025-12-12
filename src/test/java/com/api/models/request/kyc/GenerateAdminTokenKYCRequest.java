package com.api.models.request.kyc;

public class GenerateAdminTokenKYCRequest {
	
	private String email;
	private String password;
	
	public GenerateAdminTokenKYCRequest() {
		super();
	}

	public GenerateAdminTokenKYCRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public GenerateAdminTokenKYCRequest(String password) {
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
		return "GenerateAdminTokenKYCRequest [email=" + email + ", password=" + password + "]";
	}
	
	
	

}
