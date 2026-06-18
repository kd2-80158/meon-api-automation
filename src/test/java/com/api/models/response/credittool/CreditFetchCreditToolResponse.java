package com.api.models.response.credittool;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CreditFetchCreditToolResponse {
	

	    @SerializedName("data")
	    private List<CreditData> data;

	    @SerializedName("msg")
	    private String msg;

	    @SerializedName("success")
	    private boolean success;

	    public List<CreditData> getData() {
	        return data;
	    }

	    public void setData(List<CreditData> data) {
	        this.data = data;
	    }

	    public String getMsg() {
	        return msg;
	    }

	    public void setMsg(String msg) {
	        this.msg = msg;
	    }

	    public boolean isSuccess() {
	        return success;
	    }

	    public void setSuccess(boolean success) {
	        this.success = success;
	    }

	    public static class CreditData {

	        @SerializedName("all_credits")
	        private Object allCredits;

	        @SerializedName("client_id")
	        private String clientId;

	        @SerializedName("client_name")
	        private String clientName;

	        @SerializedName("company_id")
	        private String companyId;

	        @SerializedName("current_credits")
	        private Double currentCredits;

	        @SerializedName("metric_key")
	        private String metricKey;

	        @SerializedName("product")
	        private String product;

	        public Object getAllCredits() {
	            return allCredits;
	        }

	        public void setAllCredits(Object allCredits) {
	            this.allCredits = allCredits;
	        }

	        public String getClientId() {
	            return clientId;
	        }

	        public void setClientId(String clientId) {
	            this.clientId = clientId;
	        }

	        public String getClientName() {
	            return clientName;
	        }

	        public void setClientName(String clientName) {
	            this.clientName = clientName;
	        }

	        public String getCompanyId() {
	            return companyId;
	        }

	        public void setCompanyId(String companyId) {
	            this.companyId = companyId;
	        }

	        public Double getCurrentCredits() {
	            return currentCredits;
	        }

	        public void setCurrentCredits(Double currentCredits) {
	            this.currentCredits = currentCredits;
	        }

	        public String getMetricKey() {
	            return metricKey;
	        }

	        public void setMetricKey(String metricKey) {
	            this.metricKey = metricKey;
	        }

	        public String getProduct() {
	            return product;
	        }

	        public void setProduct(String product) {
	            this.product = product;
	        }
	    }
	}

