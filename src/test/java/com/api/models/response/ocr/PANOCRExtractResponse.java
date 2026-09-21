package com.api.models.response.ocr;

import com.google.gson.annotations.SerializedName;

public class PANOCRExtractResponse {

	@SerializedName("success")
	private boolean success;

	@SerializedName("document_type")
	private String documentType;

	@SerializedName("filename")
	private String filename;

	@SerializedName("data")
	private PANOCRData data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public PANOCRData getData() {
		return data;
	}

	public void setData(PANOCRData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "PANOCRExtractResponse{" + "success=" + success + ", documentType='" + documentType + '\''
				+ ", filename='" + filename + '\'' + ", data=" + data + '}';
	}
}
