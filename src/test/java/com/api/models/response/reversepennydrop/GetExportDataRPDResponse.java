package com.api.models.response.reversepennydrop;

public class GetExportDataRPDResponse {
	private String msg;
	private int code;
	private Data data;
	private boolean success;

	public String getMsg() {
		return msg;
	}

	public int getCode() {
		return code;
	}

	public Data getData() {
		return data;
	}

	public boolean isSuccess() {
		return success;
	}

	@Override
	public String toString() {
		return "GetExportDataRPDResponse [msg=" + msg + ", code=" + code + ", data=" + data + ", success=" + success
				+ "]";
	}

	public static class Data {
		private AccountDetails account_details;
		private AdditionalInfo additional_info;
		private CustomerDetails customer_details;

		public AccountDetails getAccount_details() {
			return account_details;
		}

		public AdditionalInfo getAdditional_info() {
			return additional_info;
		}

		public CustomerDetails getCustomer_details() {
			return customer_details;
		}

		@Override
		public String toString() {
			return "Data [account_details=" + account_details + ", additional_info=" + additional_info
					+ ", customer_details=" + customer_details + "]";
		}

		public static class AccountDetails {
			private String bank_name;
			private String ifsc_code;
			private String micr_code;
			private String branch_name;
			private String bank_address;
			private String account_number;

			public String getBank_name() {
				return bank_name;
			}

			public String getIfsc_code() {
				return ifsc_code;
			}

			public String getMicr_code() {
				return micr_code;
			}

			public String getBranch_name() {
				return branch_name;
			}

			public String getBank_address() {
				return bank_address;
			}

			public String getAccount_number() {
				return account_number;
			}

			@Override
			public String toString() {
				return "AccountDetails [bank_name=" + bank_name + ", ifsc_code=" + ifsc_code + ", micr_code="
						+ micr_code + ", branch_name=" + branch_name + ", bank_address=" + bank_address
						+ ", account_number=" + account_number + "]";
			}
		}

		public static class AdditionalInfo {
			private String transaction_id;
			private String transaction_status;

			public String getTransaction_id() {
				return transaction_id;
			}

			public String getTransaction_status() {
				return transaction_status;
			}

			@Override
			public String toString() {
				return "AdditionalInfo [transaction_id=" + transaction_id + ", transaction_status=" + transaction_status
						+ "]";
			}

		}

		public static class CustomerDetails {
			private String utr;
			private String upi_id;
			private String registered_name;

			public String getUtr() {
				return utr;
			}

			public String getUpi_id() {
				return upi_id;
			}

			public String getRegistered_name() {
				return registered_name;
			}

			@Override
			public String toString() {
				return "CustomerDetails [utr=" + utr + ", upi_id=" + upi_id + ", registered_name=" + registered_name
						+ "]";
			}

		}

	}

}
