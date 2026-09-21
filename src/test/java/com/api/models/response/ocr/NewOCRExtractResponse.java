package com.api.models.response.ocr;

public class NewOCRExtractResponse {

	private Data data;
	private String document_type;
	private String filename;
	private boolean success;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public static class Data {

		private String aadhaar_number;
		private String address;
		private String date_of_birth;
		private String father_name;
		private String gender;
		private String name;

		public String getAadhaar_number() {
			return aadhaar_number;
		}

		public void setAadhaar_number(String aadhaar_number) {

			this.aadhaar_number = aadhaar_number;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {

			this.address = address;
		}

		public String getDate_of_birth() {
			return date_of_birth;
		}

		public void setDate_of_birth(String date_of_birth) {

			this.date_of_birth = date_of_birth;
		}

		public String getFather_name() {
			return father_name;
		}

		public void setFather_name(String father_name) {

			this.father_name = father_name;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {

			this.gender = gender;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {

			this.name = name;
		}
	}
}