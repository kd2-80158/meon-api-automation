package com.api.models.response.ocr;

public class NewOCRExtractResponseCheque {
	
	private Data data;
	private String document_type;
	private String filename;
	private boolean success;
	private String error;
	private String msg;
	
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
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}
	@Override
	public String toString() {
		return "NewOCRExtractResponseCheque [data=" + data + ", document_type=" + document_type + ", filename="
				+ filename + ", success=" + success + "]";
	}

	public static class Data
	{
		private String account_number;
		private String ifsc;
		private String micr;
		private String name;
		public String getAccount_number() {
			return account_number;
		}
		public void setAccount_number(String account_number) {
			this.account_number = account_number;
		}
		public String getIfsc() {
			return ifsc;
		}
		public void setIfsc(String ifsc) {
			this.ifsc = ifsc;
		}
		public String getMicr() {
			return micr;
		}
		public void setMicr(String micr) {
			this.micr = micr;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return "Data [account_number=" + account_number + ", ifsc=" + ifsc + ", micr=" + micr + ", name=" + name
					+ "]";
	}

}

	public String getMsg() {
		return msg;
	}
}