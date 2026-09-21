package com.api.models.response.ocr;

public class ExtractBankDataOCRResponse {

    private ExtractedData extracted_data;

    private String msg;

    private boolean success;

    public ExtractedData getExtracted_data() {
        return extracted_data;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public static class ExtractedData {

        private String ocr_Account_Holder_Name;

        private String ocr_IFSC;

        private String ocr_MICR;

        private String ocr_account_number;

        public String getOcr_Account_Holder_Name() {
            return ocr_Account_Holder_Name;
        }

        public String getOcr_IFSC() {
            return ocr_IFSC;
        }

        public String getOcr_MICR() {
            return ocr_MICR;
        }

        public String getOcr_account_number() {
            return ocr_account_number;
        }
    }
}