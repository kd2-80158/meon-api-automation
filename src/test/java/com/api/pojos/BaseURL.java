package com.api.pojos;

import java.util.List;

import com.api.models.request.esign.GenerateTokenEsignRequest.Coordinate;

public class BaseURL {

    private String url;
    private Integer MAX_ATTEMPT;

    private String company_name;
    private String secret_token;
	private String client_token;
	private String redirect_url;
	private String[] documents;
	private String pan_name;
	private String pan_no;
	private String username;
	private String password;
	private String state;
	private String name;
	private String document_name;
	private String email;
	private String mobile;
	private String reason;
	private String days_to_expire;
	private List<Coordinate> coordinate;
	private String webhook;
	//esign
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
	public String getName() {
		return name;
	}

	public String getDocument_name() {
		return document_name;
	}

	public String getEmail() {
		return email;
	}

	public String getMobile() {
		return mobile;
	}

	public String getReason() {
		return reason;
	}

	public String getDays_to_expire() {
		return days_to_expire;
	}

	public List<Coordinate> getCoordinate() {
		return coordinate;
	}

	public String getWebhook() {
		return webhook;
	}

	public String getCancel_redirect_url() {
		return cancel_redirect_url;
	}

	public String getEsign_type() {
		return esign_type;
	}

	public boolean isRemove_preview_pdf() {
		return remove_preview_pdf;
	}

	public boolean isNeed_name_match() {
		return need_name_match;
	}

	public boolean isDebit() {
		return debit;
	}

	public int getPercentage_name_match() {
		return percentage_name_match;
	}

	public boolean isNeed_aadhaar_match() {
		return need_aadhaar_match;
	}

	public String getAadhaar_number() {
		return aadhaar_number;
	}

	public boolean isNeed_gender_match() {
		return need_gender_match;
	}

	public String getGender() {
		return gender;
	}

	public String getDocument_data() {
		return document_data;
	}

    public boolean isSms_notification() {
		return sms_notification;
	}

	public boolean isEmail_notification() {
		return email_notification;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getClient_token() {
		return client_token;
	}

	public String getRedirect_url() {
		return redirect_url;
	}

	public String[] getDocuments() {
		return documents;
	}

	public String getPan_name() {
		return pan_name;
	}

	public String getPan_no() {
		return pan_no;
	}

	public String getUrl() {
        return url;
    }

    public Integer getMAX_ATTEMPT() {
        return MAX_ATTEMPT;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getSecret_token() {
        return secret_token;
    }

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
    
}

