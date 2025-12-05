package com.api.models.request.aadhaar;

import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

public class GenerateDigilockerLinkRequest {

//	//    "client_token": "",
//    "redirect_url": "https://live.meon.co.in/",
//    "company_name": "1FINANCE",
//    "documents": "aadhaar,pan",
//    "pan_name":"",
//    "pan_no":""
	@SerializedName("client_token")
	private String client_token;
	@SerializedName("redirect_url")
	private String redirect_url;
	@SerializedName("company_name")
	private String company_name;
	@SerializedName("documents")
	private String[] documents;
	@SerializedName("pan_name")
	private String pan_name;
	@SerializedName("pan_no")
	private String pan_no;

	public GenerateDigilockerLinkRequest() {
	}

	public GenerateDigilockerLinkRequest(String client_token, String redirect_url, String company_name, String[] documents, String pan_name,
			String pan_no) {
		super();
		this.client_token = client_token;
		this.redirect_url = redirect_url;
		this.company_name = company_name;
		this.documents = documents;
		this.pan_name = pan_name;
		this.pan_no = pan_no;
	}

	public GenerateDigilockerLinkRequest(String client_token, String redirect_url, String company_name,
			String[] documents) {
		this.client_token = client_token;
		this.redirect_url = redirect_url;
		this.company_name=company_name;
		this.documents = documents;
	}

	public GenerateDigilockerLinkRequest(String client_token, String redirect_url, String[] documents) {
		this.client_token = client_token;
		this.redirect_url = redirect_url;
		this.documents = documents;
	}

	public GenerateDigilockerLinkRequest(String client_token, String redirect_url, String company_name) {
		this.client_token = client_token;
		this.redirect_url = redirect_url;
		this.company_name=company_name;
	}

	public String getClient_token() {
		return client_token;
	}

	public void setClient_token(String client_token) {
		this.client_token = client_token;
	}

	public String getRedirect_url() {
		return redirect_url;
	}

	public void setRedirect_url(String redirect_url) {
		this.redirect_url = redirect_url;
	}

	public String[] getDocuments() {
		return documents;
	}

	public void setDocuments(String[] documents) {
		this.documents = documents;
	}

	public String getPan_name() {
		return pan_name;
	}

	public void setPan_name(String pan_name) {
		this.pan_name = pan_name;
	}

	public String getPan_no() {
		return pan_no;
	}

	public void setPan_no(String pan_no) {
		this.pan_no = pan_no;
	}

	@Override
	public String toString() {
		return "GenerateDigilockerLinkRequest [client_token=" + client_token + ", redirect_url=" + redirect_url
				+ ", documents=" + Arrays.toString(documents) + ", pan_name=" + pan_name + ", pan_no=" + pan_no + "]";
	}

}
