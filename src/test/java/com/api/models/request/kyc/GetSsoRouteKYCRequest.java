package com.api.models.request.kyc;

public class GetSsoRouteKYCRequest {

	private String company;
	private String workflowName;
	private String secret_key;
	private UniqueKeys unique_keys;

	public GetSsoRouteKYCRequest() {
		super();
	}

	public GetSsoRouteKYCRequest(String company, String workflowName, String secret_key, UniqueKeys unique_keys) {
		super();
		this.company = company;
		this.workflowName = workflowName;
		this.secret_key = secret_key;
		this.unique_keys = unique_keys;
		}

	public GetSsoRouteKYCRequest(String workflowName, String secret_key, UniqueKeys unique_keys) {
		this.workflowName = workflowName;
		this.secret_key = secret_key;
		this.unique_keys = unique_keys;
	}

	public GetSsoRouteKYCRequest(String company_sso, String workflowName, String secret_key) {
		this.company = company_sso;
		this.workflowName = workflowName;
		this.secret_key = secret_key;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	@Override
	public String toString() {
		return "GetSsoRouteKYCRequest [company=" + company + ", workflowName=" + workflowName + ", secret_key="
				+ secret_key + ", unique_keys=" + unique_keys + "]";
	}

	public static class UniqueKeys {
		private String mobile;

		public UniqueKeys() {
		}

		public UniqueKeys(String mobile) {
			this.mobile = mobile;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		@Override
		public String toString() {
			return "UniqueKeys{" + "mobile='" + mobile + '\'' + '}';
		}
	}

}
