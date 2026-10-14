package com.api.models.request.esign;

public class FetchCreditSummaryEsignRequest {
	
	private String company_id;
	private String from_date;
	private String to_date;
	
	public FetchCreditSummaryEsignRequest() {
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getFrom_date() {
		return from_date;
	}

	public void setFrom_date(String from_date) {
		this.from_date = from_date;
	}

	public String getTo_date() {
		return to_date;
	}

	public void setTo_date(String to_date) {
		this.to_date = to_date;
	}

	@Override
	public String toString() {
		return "FetchCreditSummaryEsignRequest [company_id=" + company_id + ", from_date=" + from_date + ", to_date="
				+ to_date + "]";
	}
	
	

}
