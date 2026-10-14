package com.api.models.response.mca;


public class VerifyCompanyMCAResponse {
	
	private String cin;
	private CompanyDetails company_details;
	private boolean company_status;
	
	public VerifyCompanyMCAResponse() {
	}
	
	public VerifyCompanyMCAResponse(String cin, CompanyDetails company_details, boolean company_status) {
		super();
		this.cin = cin;
		this.company_details = company_details;
		this.company_status = company_status;
	}


	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public CompanyDetails getCompany_details() {
		return company_details;
	}

	public void setCompany_details(CompanyDetails company_details) {
		this.company_details = company_details;
	}



	public boolean isCompany_status() {
		return company_status;
	}



	public void setCompany_status(boolean company_status) {
		this.company_status = company_status;
	}



	public static class CompanyDetails
	{
		private float AuthorizedCapital;
		private String CIN;
		private String CompanyCategory;
		private String CompanyClass;
		private String CompanyIndian_Foreign_Company;
		private String CompanyIndustrialClassification;
		private String CompanyName;
		private String CompanyROCcode;
		private String CompanyRegistrationdate_date;
		private String CompanyStateCode;
		private boolean CompanyStatus;
		private String CompanySubCategory;
		private String Listingstatus;
		private float PaidupCapital;
		private String Registered_Office_Address;
		private int nic_code;
		public float getAuthorizedCapital() {
			return AuthorizedCapital;
		}
		public void setAuthorizedCapital(float authorizedCapital) {
			AuthorizedCapital = authorizedCapital;
		}
		public String getCIN() {
			return CIN;
		}
		public void setCIN(String cIN) {
			CIN = cIN;
		}
		public String getCompanyCategory() {
			return CompanyCategory;
		}
		public void setCompanyCategory(String companyCategory) {
			CompanyCategory = companyCategory;
		}
		public String getCompanyClass() {
			return CompanyClass;
		}
		public void setCompanyClass(String companyClass) {
			CompanyClass = companyClass;
		}
		public String getCompanyIndian_Foreign_Company() {
			return CompanyIndian_Foreign_Company;
		}
		public void setCompanyIndian_Foreign_Company(String companyIndian_Foreign_Company) {
			CompanyIndian_Foreign_Company = companyIndian_Foreign_Company;
		}
		public String getCompanyIndustrialClassification() {
			return CompanyIndustrialClassification;
		}
		public void setCompanyIndustrialClassification(String companyIndustrialClassification) {
			CompanyIndustrialClassification = companyIndustrialClassification;
		}
		public String getCompanyName() {
			return CompanyName;
		}
		public void setCompanyName(String companyName) {
			CompanyName = companyName;
		}
		public String getCompanyROCcode() {
			return CompanyROCcode;
		}
		public void setCompanyROCcode(String companyROCcode) {
			CompanyROCcode = companyROCcode;
		}
		public String getCompanyRegistrationdate_date() {
			return CompanyRegistrationdate_date;
		}
		public void setCompanyRegistrationdate_date(String companyRegistrationdate_date) {
			CompanyRegistrationdate_date = companyRegistrationdate_date;
		}
		public String getCompanyStateCode() {
			return CompanyStateCode;
		}
		public void setCompanyStateCode(String companyStateCode) {
			CompanyStateCode = companyStateCode;
		}
		public boolean isCompanyStatus() {
			return CompanyStatus;
		}
		public void setCompanyStatus(boolean companyStatus) {
			CompanyStatus = companyStatus;
		}
		public String getCompanySubCategory() {
			return CompanySubCategory;
		}
		public void setCompanySubCategory(String companySubCategory) {
			CompanySubCategory = companySubCategory;
		}
		public String getListingstatus() {
			return Listingstatus;
		}
		public void setListingstatus(String listingstatus) {
			Listingstatus = listingstatus;
		}
		public float getPaidupCapital() {
			return PaidupCapital;
		}
		public void setPaidupCapital(float paidupCapital) {
			PaidupCapital = paidupCapital;
		}
		public String getRegistered_Office_Address() {
			return Registered_Office_Address;
		}
		public void setRegistered_Office_Address(String registered_Office_Address) {
			Registered_Office_Address = registered_Office_Address;
		}
		public int getNic_code() {
			return nic_code;
		}
		public void setNic_code(int nic_code) {
			this.nic_code = nic_code;
		}
		@Override
		public String toString() {
			return "CompanyDetails [AuthorizedCapital=" + AuthorizedCapital + ", CIN=" + CIN + ", CompanyCategory="
					+ CompanyCategory + ", CompanyClass=" + CompanyClass + ", CompanyIndian_Foreign_Company="
					+ CompanyIndian_Foreign_Company + ", CompanyIndustrialClassification="
					+ CompanyIndustrialClassification + ", CompanyName=" + CompanyName + ", CompanyROCcode="
					+ CompanyROCcode + ", CompanyRegistrationdate_date=" + CompanyRegistrationdate_date
					+ ", CompanyStateCode=" + CompanyStateCode + ", CompanyStatus=" + CompanyStatus
					+ ", CompanySubCategory=" + CompanySubCategory + ", Listingstatus=" + Listingstatus
					+ ", PaidupCapital=" + PaidupCapital + ", Registered_Office_Address=" + Registered_Office_Address
					+ ", nic_code=" + nic_code + "]";
		}
		
		
	}

	@Override
	public String toString() {
		return "VerifyCompanyMCAResponse [cin=" + cin + ", company_details=" + company_details + ", company_status="
				+ company_status + "]";
	}
     
	

}


