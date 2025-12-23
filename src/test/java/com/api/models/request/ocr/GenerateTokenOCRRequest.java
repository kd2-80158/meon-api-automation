package com.api.models.request.ocr;

public class GenerateTokenOCRRequest {

	private String company_id;
	private String email;
	private String password;

	public GenerateTokenOCRRequest() {
		super();
	}

	public GenerateTokenOCRRequest(String company_id, String email, String password) {
		super();
		this.company_id = company_id;
		this.email = email;
		this.password = password;
	}

	public GenerateTokenOCRRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
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
		return "GenerateTokenOCRRequest [company_id=" + company_id + ", email=" + email + ", password=" + password
				+ "]";
	}

}
