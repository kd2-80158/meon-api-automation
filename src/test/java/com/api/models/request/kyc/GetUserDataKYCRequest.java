package com.api.models.request.kyc;

public class GetUserDataKYCRequest {

	private String work_flow_key;
	private String page;
	private String per_page;
	private String search;
	private String company;
	
	public GetUserDataKYCRequest() {
		super();
	}

	public GetUserDataKYCRequest(String work_flow_key, String page, String per_page, String search, String company) {
		super();
		this.work_flow_key = work_flow_key;
		this.page = page;
		this.per_page = per_page;
		this.search = search;
		this.company = company;
	}

	public GetUserDataKYCRequest(String page2, String per_page2, String search2, String company2) {
		this.page = page2;
		this.per_page = per_page2;
		this.search = search2;
		this.company = company2;
	}

	public String getWork_flow_key() {
		return work_flow_key;
	}

	public void setWork_flow_key(String work_flow_key) {
		this.work_flow_key = work_flow_key;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getPer_page() {
		return per_page;
	}

	public void setPer_page(String per_page) {
		this.per_page = per_page;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "GetUserDataKYCRequest [work_flow_key=" + work_flow_key + ", page=" + page + ", per_page=" + per_page
				+ ", search=" + search + ", company=" + company + "]";
	}
	
	

}
