package com.api.models.request.credittool;

import java.util.List;

public class CreditFetchCreditToolRequest {

	private List<Company> companies;

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public static class Company {
		private String company_id;
		private List<String> products;

		public String getCompany_id() {
			return company_id;
		}

		public void setCompany_id(String company_id) {
			this.company_id = company_id;
		}

		public List<String> getProducts() {
			return products;
		}

		public void setProducts(List<String> products) {
			this.products = products;
		}
	}

}
