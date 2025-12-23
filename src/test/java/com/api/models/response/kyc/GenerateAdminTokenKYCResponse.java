package com.api.models.response.kyc;

public class GenerateAdminTokenKYCResponse {

	private String access_token;
	private String admin_email;
	private String admin_mobile;
	private String admin_name;
	private String admin_permission;
	private String company;
	private String company_id;
	private String refresh_token;
	private boolean success;
	private int token_revoke_time;
	private int code;
	private String msg;

	public GenerateAdminTokenKYCResponse() {
		super();
	}

	public GenerateAdminTokenKYCResponse(String access_token, String admin_email, String admin_mobile,
			String admin_name, String admin_permission, String company, String company_id, String refresh_token,
			boolean success, int token_revoke_time) {
		super();
		this.access_token = access_token;
		this.admin_email = admin_email;
		this.admin_mobile = admin_mobile;
		this.admin_name = admin_name;
		this.admin_permission = admin_permission;
		this.company = company;
		this.company_id = company_id;
		this.refresh_token = refresh_token;
		this.success = success;
		this.token_revoke_time = token_revoke_time;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getAdmin_email() {
		return admin_email;
	}

	public void setAdmin_email(String admin_email) {
		this.admin_email = admin_email;
	}

	public String getAdmin_mobile() {
		return admin_mobile;
	}

	public void setAdmin_mobile(String admin_mobile) {
		this.admin_mobile = admin_mobile;
	}

	public String getAdmin_name() {
		return admin_name;
	}

	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}

	public String getAdmin_permission() {
		return admin_permission;
	}

	public void setAdmin_permission(String admin_permission) {
		this.admin_permission = admin_permission;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getToken_revoke_time() {
		return token_revoke_time;
	}

	public void setToken_revoke_time(int token_revoke_time) {
		this.token_revoke_time = token_revoke_time;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "GenerateAdminTokenKYCResponse [access_token=" + access_token + ", admin_email=" + admin_email
				+ ", admin_mobile=" + admin_mobile + ", admin_name=" + admin_name + ", admin_permission="
				+ admin_permission + ", company=" + company + ", company_id=" + company_id + ", refresh_token="
				+ refresh_token + ", success=" + success + ", token_revoke_time=" + token_revoke_time + "]";
	}

}
