package com.api.models.request.pennydrop;

public class BankVerificationPennyDropRequest {

	private String name;
	private String mobile;
	private String ifsc;
	private String accountnumber;
	private String accounttype;

	public BankVerificationPennyDropRequest() {
		super();
	}

	public BankVerificationPennyDropRequest(String name, String mobile, String ifsc, String accountnumber,
			String accounttype) {
		super();
		this.name = name;
		this.mobile = mobile;
		this.ifsc = ifsc;
		this.accountnumber = accountnumber;
		this.accounttype = accounttype;
	}

	public BankVerificationPennyDropRequest(String mobile, String ifsc, String accountnumber, String accounttype) {
		this.mobile = mobile;
		this.ifsc = ifsc;
		this.accountnumber = accountnumber;
		this.accounttype = accounttype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	@Override
	public String toString() {
		return "BankVerificationPennyDropRequest [name=" + name + ", mobile=" + mobile + ", ifsc=" + ifsc
				+ ", accountnumber=" + accountnumber + ", accounttype=" + accounttype + "]";
	}

}
