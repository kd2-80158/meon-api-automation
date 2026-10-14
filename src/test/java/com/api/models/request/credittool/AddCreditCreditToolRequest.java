package com.api.models.request.credittool;

public class AddCreditCreditToolRequest {

	private String company_id;
	private String credit;
	private String product;
	private String company;

	public AddCreditCreditToolRequest() {
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "AddCreditCreditToolRequest [company_id=" + company_id + ", credit=" + credit + ", product=" + product
				+ ", company=" + company + "]";
	}

}
