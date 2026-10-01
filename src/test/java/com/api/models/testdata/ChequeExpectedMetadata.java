package com.api.models.testdata;

public class ChequeExpectedMetadata {

    private String account_number;
    private String ifsc;
    private String micr;
    private String name;

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getMicr() {
        return micr;
    }

    public void setMicr(String micr) {
        this.micr = micr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChequeExpectedMetadata [account_number=" + account_number
                + ", ifsc=" + ifsc
                + ", micr=" + micr
                + ", name=" + name + "]";
    }
}