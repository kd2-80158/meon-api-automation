package com.api.models.testdata;

public class BankStatementExpectedMetadata {

    private String accountHolderName;
    private String ifsc;
    private boolean ifscPresent;
    private String micr;
    private String accountNumber;
    private String bankName;
    private boolean micrPresent;

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getIfsc() {
        return ifsc;
    }

    public boolean isIfscPresent() {
        return ifscPresent;
    }

    public String getMicr() {
        return micr;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public boolean isMicrPresent() {
        return micrPresent;
    }

    @Override
    public String toString() {
        return "BankStatementExpectedMetadata{" +
                "accountHolderName='" + accountHolderName + '\'' +
                ", ifsc='" + ifsc + '\'' +
                ", ifscPresent=" + ifscPresent +
                ", micr='" + micr + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", bankName='" + bankName + '\'' +
                ", micrPresent=" + micrPresent +
                '}';
    }
}