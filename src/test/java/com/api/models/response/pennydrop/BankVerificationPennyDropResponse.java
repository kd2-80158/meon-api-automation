package com.api.models.response.pennydrop;

public class BankVerificationPennyDropResponse {

	private int code;
	private Data data;
	private String msg;
	private boolean status;

	public BankVerificationPennyDropResponse() {
		super();
	}

	public BankVerificationPennyDropResponse(int code, Data data, String msg, boolean status) {
		super();
		this.code = code;
		this.data = data;
		this.msg = msg;
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "BankVerificationPennyDropResponse [code=" + code + ", data=" + data + ", msg=" + msg + ", status="
				+ status + "]";
	}

	public static class Data {

		private AccountDetails account_details;
		private AdditionalInfo additional_info;
		private CustomerDetails customer_details;

		public Data() {
			super();
		}

		public Data(AccountDetails account_details, AdditionalInfo additional_info, CustomerDetails customer_details) {
			super();
			this.account_details = account_details;
			this.additional_info = additional_info;
			this.customer_details = customer_details;
		}

		public AccountDetails getAccount_details() {
			return account_details;
		}

		public void setAccount_details(AccountDetails account_details) {
			this.account_details = account_details;
		}

		public AdditionalInfo getAdditional_info() {
			return additional_info;
		}

		public void setAdditional_info(AdditionalInfo additional_info) {
			this.additional_info = additional_info;
		}

		public CustomerDetails getCustomer_details() {
			return customer_details;
		}

		public void setCustomer_details(CustomerDetails customer_details) {
			this.customer_details = customer_details;
		}

		@Override
		public String toString() {
			return "Data [account_details=" + account_details + ", additional_info=" + additional_info
					+ ", customer_details=" + customer_details + "]";
		}

	}

	public static class AccountDetails {

		private String account_number;
		private String bank_address;
		private String bank_name;
		private String branch_name;
		private String ifsc_code;
		private String micr_code;

		public AccountDetails() {
			super();
		}

		public AccountDetails(String account_number, String bank_address, String bank_name, String branch_name,
				String ifsc_code, String micr_code) {
			super();
			this.account_number = account_number;
			this.bank_address = bank_address;
			this.bank_name = bank_name;
			this.branch_name = branch_name;
			this.ifsc_code = ifsc_code;
			this.micr_code = micr_code;
		}

		public String getAccount_number() {
			return account_number;
		}

		public void setAccount_number(String account_number) {
			this.account_number = account_number;
		}

		public String getBank_address() {
			return bank_address;
		}

		public void setBank_address(String bank_address) {
			this.bank_address = bank_address;
		}

		public String getBank_name() {
			return bank_name;
		}

		public void setBank_name(String bank_name) {
			this.bank_name = bank_name;
		}

		public String getBranch_name() {
			return branch_name;
		}

		public void setBranch_name(String branch_name) {
			this.branch_name = branch_name;
		}

		public String getIfsc_code() {
			return ifsc_code;
		}

		public void setIfsc_code(String ifsc_code) {
			this.ifsc_code = ifsc_code;
		}

		public String getMicr_code() {
			return micr_code;
		}

		public void setMicr_code(String micr_code) {
			this.micr_code = micr_code;
		}

		@Override
		public String toString() {
			return "AccountDetails [account_number=" + account_number + ", bank_address=" + bank_address
					+ ", bank_name=" + bank_name + ", branch_name=" + branch_name + ", ifsc_code=" + ifsc_code
					+ ", micr_code=" + micr_code + "]";
		}

	}

	public static class AdditionalInfo {
		private String transaction_id;
		private String transaction_status;

		public AdditionalInfo() {
			super();
		}

		public AdditionalInfo(String transaction_id, String transaction_status) {
			super();
			this.transaction_id = transaction_id;
			this.transaction_status = transaction_status;
		}

		public String getTransaction_id() {
			return transaction_id;
		}

		public void setTransaction_id(String transaction_id) {
			this.transaction_id = transaction_id;
		}

		public String getTransaction_status() {
			return transaction_status;
		}

		public void setTransaction_status(String transaction_status) {
			this.transaction_status = transaction_status;
		}

		@Override
		public String toString() {
			return "AdditionalInfo [transaction_id=" + transaction_id + ", transaction_status=" + transaction_status
					+ "]";
		}

	}

	public static class CustomerDetails {

		private String registered_name;
		private String utr;

		public CustomerDetails() {
			super();
		}

		public CustomerDetails(String registered_name, String utr) {
			super();
			this.registered_name = registered_name;
			this.utr = utr;
		}

		public String getRegistered_name() {
			return registered_name;
		}

		public void setRegistered_name(String registered_name) {
			this.registered_name = registered_name;
		}

		public String getUtr() {
			return utr;
		}

		public void setUtr(String utr) {
			this.utr = utr;
		}

		@Override
		public String toString() {
			return "CustomerDetails [registered_name=" + registered_name + ", utr=" + utr + "]";
		}

	}
}
