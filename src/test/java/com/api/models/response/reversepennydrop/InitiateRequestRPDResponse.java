package com.api.models.response.reversepennydrop;

public class InitiateRequestRPDResponse {

	private int code;
	private String status;
	private boolean success;
	private String msg;
	private Data data;

	public InitiateRequestRPDResponse() {
		super();
	}

	public int getCode() {
		return code;
	}

	public String getStatus() {
		return status;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMsg() {
		return msg;
	}

	public Data getData() {
		return data;
	}

	@Override
	public String toString() {
		return "InitiateRequestRPDResponse [code=" + code + ", status=" + status + ", success=" + success + ", msg="
				+ msg + ", data=" + data + "]";
	}

	public static class Data {
		private String transaction_id;
		private String qr_page;
		private String payment_link;
		private Links ios_links;

		public Data() {
			super();
		}

		public String getTransaction_id() {
			return transaction_id;
		}

		public String getQr_page() {
			return qr_page;
		}


		public String getPayment_link() {
			return payment_link;
		}


		public Links getIos_links() {
			return ios_links;
		}

		@Override
		public String toString() {
			return "Data [transaction_id=" + transaction_id + ", qr_page=" + qr_page + ", payment_link=" + payment_link
					+ ", ios_links=" + ios_links + "]";
		}

		public static class Links {
			private String paytm;
			private String phonepe;
			private String gpay;
			private String bhim;
			private String whatsapp;

			public Links() {
				super();
			}

			public String getPaytm() {
				return paytm;
			}


			public String getPhonepe() {
				return phonepe;
			}

			public String getGpay() {
				return gpay;
			}

			public String getBhim() {
				return bhim;
			}

			public String getWhatsapp() {
				return whatsapp;
			}

			@Override
			public String toString() {
				return "Links [paytm=" + paytm + ", phonepe=" + phonepe + ", gpay=" + gpay + ", bhim=" + bhim
						+ ", whatsapp=" + whatsapp + "]";
			}

		}
	}

}
