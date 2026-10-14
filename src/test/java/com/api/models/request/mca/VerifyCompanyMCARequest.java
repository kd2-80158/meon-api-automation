package com.api.models.request.mca;

public class VerifyCompanyMCARequest {
	
	private String cin;
	
	public VerifyCompanyMCARequest() {
	}

	public VerifyCompanyMCARequest(String cin) {
		super();
		this.cin = cin;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	@Override
	public String toString() {
		return "VerifyCompanyMCARequest [cin=" + cin + "]";
	}

}
