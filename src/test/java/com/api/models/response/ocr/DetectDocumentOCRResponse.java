package com.api.models.response.ocr;

public class DetectDocumentOCRResponse {

	private Double confidence;
	private String document_type;
	private String match_type;
	private String msg;
	private boolean success;
	private String text_match_type;

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getMatch_type() {
		return match_type;
	}

	public void setMatch_type(String match_type) {
		this.match_type = match_type;
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

	public String getText_match_type() {
		return text_match_type;
	}

	public void setText_match_type(String text_match_type) {
		this.text_match_type = text_match_type;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
}
