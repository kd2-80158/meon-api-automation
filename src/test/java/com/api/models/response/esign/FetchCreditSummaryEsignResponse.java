package com.api.models.response.esign;

public class FetchCreditSummaryEsignResponse {

	private String message;
	private boolean status;
	private Data data;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static class Data {
		private String company_id;
		private String company_name;
		private int total_count;
		private String last_entry_date;

		public Data() {
		}

		public String getCompany_id() {
			return company_id;
		}

		public void setCompany_id(String company_id) {
			this.company_id = company_id;
		}

		public String getCompany_name() {
			return company_name;
		}

		public void setCompany_name(String company_name) {
			this.company_name = company_name;
		}

		public int getTotal_count() {
			return total_count;
		}

		public void setTotal_count(int total_count) {
			this.total_count = total_count;
		}

		public String getLast_entry_date() {
			return last_entry_date;
		}

		public void setLast_entry_date(String last_entry_date) {
			this.last_entry_date = last_entry_date;
		}

	}

}
