package com.api.pojos;

public class BaseURL {

    private String url;
    private Integer MAX_ATTEMPT;

    private String company_name;
    private String secret_token;

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
}
