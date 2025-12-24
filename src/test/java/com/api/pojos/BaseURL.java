package com.api.pojos;

import java.util.List;

import com.api.models.request.esign.GenerateTokenEsignRequest.Coordinate;
import com.api.models.request.kyc.GetSsoRouteKYCRequest.UniqueKeys;

public class BaseURL {

	// aadhaar
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
	// esign
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
	private String workflowName;
	private String secret_key;
	private UniqueKeys unique_keys;
	private String company_sso;
	private boolean email_notification;

	// kyc
	private String work_flow_key;
	private String page;
	private String per_page;
	private String search;
	private String company;

	// pennydrop
	private PennyDropConfig.Live live;
	private String ifsc;
	private String accountnumber;
	private String accounttype;

	// reversepennydrop
	private String client_id;
	private String client_secret;
	private String version;
	
	//ocr
	private String company_id;
	private String pan;
	private String fathername;
	private String dob;
	private String sources;
	private String req_id;
	private String address;
	private String adhar;
	//facefinder
	private String image_to_be_match;

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getFathername() {
		return fathername;
	}

	public void setFathername(String fathername) {
		this.fathername = fathername;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}

	public String getAccounttype() {
		return accounttype;
	}

	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}

	public PennyDropConfig.Live getLive() {
		return live;
	}

	public void setLive(PennyDropConfig.Live live) {
		this.live = live;
	}

	public String getCompany_sso() {
		return company_sso;
	}

	public void setCompany_sso(String company_sso) {
		this.company_sso = company_sso;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getSecret_key() {
		return secret_key;
	}

	public void setSecret_key(String secret_key) {
		this.secret_key = secret_key;
	}

	public UniqueKeys getUnique_keys() {
		return unique_keys;
	}

	public void setUnique_keys(UniqueKeys unique_keys) {
		this.unique_keys = unique_keys;
	}

	public String getWork_flow_key() {
		return work_flow_key;
	}

	public void setWork_flow_key(String work_flow_key) {
		this.work_flow_key = work_flow_key;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getPer_page() {
		return per_page;
	}

	public void setPer_page(String per_page) {
		this.per_page = per_page;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getReq_id() {
		return req_id;
	}

	public void setReq_id(String req_id) {
		this.req_id = req_id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAdhar() {
		return adhar;
	}

	public void setAdhar(String adhar) {
		this.adhar = adhar;
	}

	public String getImage_to_be_match() {
		return image_to_be_match;
	}

	public void setImage_to_be_match(String image_to_be_match) {
		this.image_to_be_match = image_to_be_match;
	}

}
