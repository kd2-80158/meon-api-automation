package com.api.models.response.ocr;

import java.util.List;

public class ExtractDataInvoiceOCRResponse {

	private ExtractedData extracted_data;
	private boolean success;
	private String msg;

	public ExtractedData getExtracted_data() {
		return extracted_data;
	}

	public void setExtracted_data(ExtractedData extracted_data) {
		this.extracted_data = extracted_data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public static class ExtractedData {

		private String Customer_gst;
		private String bill_date;
		private String billing_entity;
		private String cgst;
		private String gst_no;
		private int igst;
		private String name;
		private List<String> sgst;
		private int toatal_amount;

		public String getCustomer_gst() {
			return Customer_gst;
		}

		public void setCustomer_gst(String customer_gst) {
			Customer_gst = customer_gst;
		}

		public String getBill_date() {
			return bill_date;
		}

		public void setBill_date(String bill_date) {
			this.bill_date = bill_date;
		}

		public String getBilling_entity() {
			return billing_entity;
		}

		public void setBilling_entity(String billing_entity) {
			this.billing_entity = billing_entity;
		}

		public String getCgst() {
			return cgst;
		}

		public void setCgst(String cgst) {
			this.cgst = cgst;
		}

		public String getGst_no() {
			return gst_no;
		}

		public void setGst_no(String gst_no) {
			this.gst_no = gst_no;
		}

		public int getIgst() {
			return igst;
		}

		public void setIgst(int igst) {
			this.igst = igst;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getSgst() {
			return sgst;
		}

		public void setSgst(List<String> sgst) {
			this.sgst = sgst;
		}

		public int getToatal_amount() {
			return toatal_amount;
		}

		public void setToatal_amount(int toatal_amount) {
			this.toatal_amount = toatal_amount;
		}
	}

	public String getMsg() {
		return msg;
	}

}
