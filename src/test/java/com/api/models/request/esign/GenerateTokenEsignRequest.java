package com.api.models.request.esign;

import java.util.List;

import com.api.models.request.esign.GenerateTokenEsignRequest.Coordinate;

public class GenerateTokenEsignRequest {

	private String name;
	private String document_name;
	private String email;
	private String mobile;
	private String reason;
	private String days_to_expire;
	private List<Coordinate> coordinate;
	private String webhook;
	private String redirect_url;
	private String cancel_redirect_url;
	private String esign_type;
	private boolean remove_preview_pdf;
	private boolean need_name_match;
	private boolean debit;
	private int percentage_name_match;
	private boolean need_aadhaar_match;
	private String aadhaar_number;
	private boolean need_gender_match;
	private String gender;
	private String document_data;
	private boolean sms_notification;
	private boolean email_notification;

	public GenerateTokenEsignRequest() {
		super();
	}

	public GenerateTokenEsignRequest(String name, String document_name, String email, String mobile, String reason,
			String days_to_expire, List<Coordinate> coordinate, String webhook, String redirect_url,
			String cancel_redirect_url, String esign_type, boolean remove_preview_pdf, boolean need_name_match,
			boolean debit, int percentage_name_match, boolean need_aadhaar_match, String aadhaar_number,
			boolean need_gender_match, String gender, boolean sms_notification, boolean email_notification,
			String document_data) {
		this.name = name;
		this.document_name = document_name;
		this.email = email;
		this.mobile = mobile;
		this.reason = reason;
		this.days_to_expire = days_to_expire;
		this.coordinate = coordinate;
		this.webhook = webhook;
		this.redirect_url = redirect_url;
		this.cancel_redirect_url = cancel_redirect_url;
		this.esign_type = esign_type;
		this.remove_preview_pdf = remove_preview_pdf;
		this.need_name_match = need_name_match;
		this.debit = debit;
		this.percentage_name_match = percentage_name_match;
		this.need_aadhaar_match = need_aadhaar_match;
		this.aadhaar_number = aadhaar_number;
		this.need_gender_match = need_gender_match;
		this.gender = gender;
		this.sms_notification = sms_notification;
		this.email_notification = email_notification;
		this.document_data = document_data;
	}

	public GenerateTokenEsignRequest(String name, String document_name, String email, String mobile, String reason,
			String days_to_expire, List<Coordinate> coordinate, String webhook, String redirect_url, String esign_type,
			String document_data) {
		this.name = name;
		this.document_name = document_name;
		this.email = email;
		this.mobile = mobile;
		this.reason = reason;
		this.days_to_expire = days_to_expire;
		this.coordinate = coordinate;
		this.webhook = webhook;
		this.redirect_url = redirect_url;
		this.esign_type = esign_type;
		this.document_data = document_data;

	}

	public GenerateTokenEsignRequest(String name, String document_name, String email, String mobile, String reason,
			String days_to_expire, List<Coordinate> coordinate, String webhook, String redirect_url,
			String cancel_redirect_url, String esign_type, boolean remove_preview_pdf, boolean need_name_match,
			boolean debit, int percentage_name_match, boolean need_aadhaar_match, String aadhaar_number,
			boolean need_gender_match, String gender, boolean sms_notification, boolean email_notification) {
		this.name = name;
		this.document_name = document_name;
		this.email = email;
		this.mobile = mobile;
		this.reason = reason;
		this.days_to_expire = days_to_expire;
		this.coordinate = coordinate;
		this.webhook = webhook;
		this.redirect_url = redirect_url;
		this.cancel_redirect_url = cancel_redirect_url;
		this.esign_type = esign_type;
		this.remove_preview_pdf = remove_preview_pdf;
		this.need_name_match = need_name_match;
		this.debit = debit;
		this.percentage_name_match = percentage_name_match;
		this.need_aadhaar_match = need_aadhaar_match;
		this.aadhaar_number = aadhaar_number;
		this.need_gender_match = need_gender_match;
		this.gender = gender;
		this.sms_notification = sms_notification;
		this.email_notification = email_notification;
	}

	public GenerateTokenEsignRequest(String name2, String document_name2, String email2, String mobile2, String reason2,
			String days_to_expire2, List<Coordinate> coordinate2, String webhook2, String redirect_url2,
			String cancel_redirect_url2, String esign_type2, boolean remove_preview_pdf2, boolean need_name_match2,
			boolean debit2, int percentage_name_match2, boolean need_aadhaar_match2, String aadhaar_number2,
			boolean need_gender_match2, boolean sms_notification2, boolean email_notification2, String document_data2) {
		this.name = name2;
		this.document_name = document_name2;
		this.email = email2;
		this.mobile = mobile2;
		this.reason = reason2;
		this.days_to_expire = days_to_expire2;
		this.coordinate = coordinate2;
		this.webhook = webhook2;
		this.redirect_url = redirect_url2;
		this.cancel_redirect_url = cancel_redirect_url2;
		this.esign_type = esign_type2;
		this.remove_preview_pdf = remove_preview_pdf2;
		this.need_name_match = need_name_match2;
		this.debit = debit2;
		this.percentage_name_match = percentage_name_match2;
		this.need_aadhaar_match = need_aadhaar_match2;
		this.aadhaar_number = aadhaar_number2;
		this.need_gender_match = need_gender_match2;
		this.sms_notification = sms_notification2;
		this.email_notification = email_notification2;
	}

	public boolean isSms_notification() {
		return sms_notification;
	}

	public void setSms_notification(boolean sms_notification) {
		this.sms_notification = sms_notification;
	}

	public boolean isEmail_notification() {
		return email_notification;
	}

	public void setEmail_notification(boolean email_notification) {
		this.email_notification = email_notification;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocument_name() {
		return document_name;
	}

	public void setDocument_name(String document_name) {
		this.document_name = document_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDays_to_expire() {
		return days_to_expire;
	}

	public void setDays_to_expire(String days_to_expire) {
		this.days_to_expire = days_to_expire;
	}

	public List<Coordinate> getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(List<Coordinate> coordinate) {
		this.coordinate = coordinate;
	}

	public String getWebhook() {
		return webhook;
	}

	public void setWebhook(String webhook) {
		this.webhook = webhook;
	}

	public String getRedirect_url() {
		return redirect_url;
	}

	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}

	public String getCancel_redirect_url() {
		return cancel_redirect_url;
	}

	public void setCancel_redirect_url(String cancel_redirect_url) {
		this.cancel_redirect_url = cancel_redirect_url;
	}

	public String getEsign_type() {
		return esign_type;
	}

	public void setEsign_type(String esign_type) {
		this.esign_type = esign_type;
	}

	public boolean isRemove_preview_pdf() {
		return remove_preview_pdf;
	}

	public void setRemove_preview_pdf(boolean remove_preview_pdf) {
		this.remove_preview_pdf = remove_preview_pdf;
	}

	public boolean isNeed_name_match() {
		return need_name_match;
	}

	public void setNeed_name_match(boolean need_name_match) {
		this.need_name_match = need_name_match;
	}

	public boolean isDebit() {
		return debit;
	}

	public void setDebit(boolean debit) {
		this.debit = debit;
	}

	public int getPercentage_name_match() {
		return percentage_name_match;
	}

	public void setPercentage_name_match(int percentage_name_match) {
		this.percentage_name_match = percentage_name_match;
	}

	public boolean isNeed_aadhaar_match() {
		return need_aadhaar_match;
	}

	public void setNeed_aadhaar_match(boolean need_aadhaar_match) {
		this.need_aadhaar_match = need_aadhaar_match;
	}

	public String getAadhaar_number() {
		return aadhaar_number;
	}

	public void setAadhaar_number(String aadhaar_number) {
		this.aadhaar_number = aadhaar_number;
	}

	public boolean isNeed_gender_match() {
		return need_gender_match;
	}

	public void setNeed_gender_match(boolean need_gender_match) {
		this.need_gender_match = need_gender_match;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDocument_data() {
		return document_data;
	}

	public void setDocument_data(String document_data) {
		this.document_data = document_data;
	}

	@Override
	public String toString() {
		return "EsignRequest{" + "name='" + name + '\'' + ", document_name='" + document_name + '\'' + ", email='"
				+ email + '\'' + ", mobile='" + mobile + '\'' + ", reason='" + reason + '\'' + ", days_to_expire='"
				+ days_to_expire + '\'' + ", coordinate=" + coordinate + ", webhook='" + webhook + '\''
				+ ", redirect_url='" + redirect_url + '\'' + ", cancel_redirect_url='" + cancel_redirect_url + '\''
				+ ", esign_type='" + esign_type + '\'' + ", remove_preview_pdf=" + remove_preview_pdf
				+ ", need_name_match=" + need_name_match + ", debit=" + debit + ", percentage_name_match="
				+ percentage_name_match + ", need_aadhaar_match=" + need_aadhaar_match + ", aadhaar_number="
				+ aadhaar_number + ", need_gender_match=" + need_gender_match + ", gender='" + gender + '\''
				+ ", document_data='" + document_data + '\'' + '}';
	}

	// ----------------- INNER CLASS -----------------

	public static class Coordinate {

		private String page_number;
		private String x_coordinate;
		private String y_coordinate;
		private String height;
		private String width;

		public Coordinate(String x_coordinate, String y_coordinate, String height, String width) {
			this.x_coordinate = x_coordinate;
			this.y_coordinate = y_coordinate;
			this.height = height;
			this.width = width;
		}
		
		

		public Coordinate(String page_number, String x_coordinate, String y_coordinate, String height, String width) {
			super();
			this.page_number = page_number;
			this.x_coordinate = x_coordinate;
			this.y_coordinate = y_coordinate;
			this.height = height;
			this.width = width;
		}



		public String getPage_number() {
			return page_number;
		}

		public void setPage_number(String page_number) {
			this.page_number = page_number;
		}

		public String getX_coordinate() {
			return x_coordinate;
		}

		public void setX_coordinate(String x_coordinate) {
			this.x_coordinate = x_coordinate;
		}

		public String getY_coordinate() {
			return y_coordinate;
		}

		public void setY_coordinate(String y_coordinate) {
			this.y_coordinate = y_coordinate;
		}

		public String getHeight() {
			return height;
		}

		public void setHeight(String height) {
			this.height = height;
		}

		public String getWidth() {
			return width;
		}

		public void setWidth(String width) {
			this.width = width;
		}

		@Override
		public String toString() {
			return "Coordinate{" + "page_number='" + page_number + '\'' + ", x_coordinate='" + x_coordinate + '\''
					+ ", y_coordinate='" + y_coordinate + '\'' + ", height='" + height + '\'' + ", width='" + width
					+ '\'' + '}';
		}
	}
}
