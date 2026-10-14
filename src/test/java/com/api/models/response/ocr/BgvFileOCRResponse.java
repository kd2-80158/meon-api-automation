package com.api.models.response.ocr;

import com.google.gson.annotations.SerializedName;

public class BgvFileOCRResponse {

	@SerializedName("extraction_confidence")
	private double extractionConfidence;

	@SerializedName("extraction_source")
	private String extractionSource;

	@SerializedName("form_data")
	private FormData formData;

	@SerializedName("message")
	private String message;

	@SerializedName("report_data")
	private ReportData reportData;

	@SerializedName("result")
	private Result result;

	@SerializedName("status")
	private String status;

	// Getters and Setters for Root
	public double getExtractionConfidence() {
		return extractionConfidence;
	}

	public void setExtractionConfidence(double extractionConfidence) {
		this.extractionConfidence = extractionConfidence;
	}

	public String getExtractionSource() {
		return extractionSource;
	}

	public void setExtractionSource(String extractionSource) {
		this.extractionSource = extractionSource;
	}

	public FormData getFormData() {
		return formData;
	}

	public void setFormData(FormData formData) {
		this.formData = formData;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ReportData getReportData() {
		return reportData;
	}

	public void setReportData(ReportData reportData) {
		this.reportData = reportData;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static class FormData {
		@SerializedName("aadhaar")
		private String aadhaar;

		@SerializedName("address")
		private String address;

		@SerializedName("name")
		private String name;

		@SerializedName("pan")
		private String pan;

		// Getters and Setters
		public String getAadhaar() {
			return aadhaar;
		}

		public void setAadhaar(String aadhaar) {
			this.aadhaar = aadhaar;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPan() {
			return pan;
		}

		public void setPan(String pan) {
			this.pan = pan;
		}
	}

	public static class ReportData {
		@SerializedName("aadhaar_number")
		private String aadhaarNumber;

		@SerializedName("address")
		private String address;

		@SerializedName("address_status")
		private String addressStatus;

		@SerializedName("address_status_raw")
		private String addressStatusRaw;

		@SerializedName("candidate_name")
		private String candidateName;

		@SerializedName("cibil_score")
		private Integer cibilScore;

		@SerializedName("criminal_status")
		private String criminalStatus;

		@SerializedName("criminal_status_raw")
		private String criminalStatusRaw;

		@SerializedName("face_score")
		private Double faceScore;

		@SerializedName("face_status")
		private String faceStatus;

		@SerializedName("face_status_raw")
		private String faceStatusRaw;

		@SerializedName("final_status")
		private String finalStatus;

		@SerializedName("identity_status")
		private String identityStatus;

		@SerializedName("identity_status_raw")
		private String identityStatusRaw;

		@SerializedName("pan_number")
		private String panNumber;

		// Getters and Setters
		public String getAadhaarNumber() {
			return aadhaarNumber;
		}

		public void setAadhaarNumber(String aadhaarNumber) {
			this.aadhaarNumber = aadhaarNumber;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getAddressStatus() {
			return addressStatus;
		}

		public void setAddressStatus(String addressStatus) {
			this.addressStatus = addressStatus;
		}

		public String getAddressStatusRaw() {
			return addressStatusRaw;
		}

		public void setAddressStatusRaw(String addressStatusRaw) {
			this.addressStatusRaw = addressStatusRaw;
		}

		public String getCandidateName() {
			return candidateName;
		}

		public void setCandidateName(String candidateName) {
			this.candidateName = candidateName;
		}

		public Integer getCibilScore() {
			return cibilScore;
		}

		public void setCibilScore(Integer cibilScore) {
			this.cibilScore = cibilScore;
		}

		public String getCriminalStatus() {
			return criminalStatus;
		}

		public void setCriminalStatus(String criminalStatus) {
			this.criminalStatus = criminalStatus;
		}

		public String getCriminalStatusRaw() {
			return criminalStatusRaw;
		}

		public void setCriminalStatusRaw(String criminalStatusRaw) {
			this.criminalStatusRaw = criminalStatusRaw;
		}

		public Double getFaceScore() {
			return faceScore;
		}

		public void setFaceScore(Double faceScore) {
			this.faceScore = faceScore;
		}

		public String getFaceStatus() {
			return faceStatus;
		}

		public void setFaceStatus(String faceStatus) {
			this.faceStatus = faceStatus;
		}

		public String getFaceStatusRaw() {
			return faceStatusRaw;
		}

		public void setFaceStatusRaw(String faceStatusRaw) {
			this.faceStatusRaw = faceStatusRaw;
		}

		public String getFinalStatus() {
			return finalStatus;
		}

		public void setFinalStatus(String finalStatus) {
			this.finalStatus = finalStatus;
		}

		public String getIdentityStatus() {
			return identityStatus;
		}

		public void setIdentityStatus(String identityStatus) {
			this.identityStatus = identityStatus;
		}

		public String getIdentityStatusRaw() {
			return identityStatusRaw;
		}

		public void setIdentityStatusRaw(String identityStatusRaw) {
			this.identityStatusRaw = identityStatusRaw;
		}

		public String getPanNumber() {
			return panNumber;
		}

		public void setPanNumber(String panNumber) {
			this.panNumber = panNumber;
		}
	}

	public static class Result {
		@SerializedName("checks")
		private Checks checks;

		@SerializedName("final_decision")
		private String finalDecision;

		// Getters and Setters
		public Checks getChecks() {
			return checks;
		}

		public void setChecks(Checks checks) {
			this.checks = checks;
		}

		public String getFinalDecision() {
			return finalDecision;
		}

		public void setFinalDecision(String finalDecision) {
			this.finalDecision = finalDecision;
		}
	}

	public static class Checks {
		@SerializedName("address_match")
		private MatchCheck addressMatch;

		@SerializedName("address_status")
		private StatusCheck addressStatus;

		@SerializedName("cibil")
		private CibilCheck cibil;

		@SerializedName("criminal")
		private StatusCheck criminal;

		@SerializedName("face")
		private FaceCheck face;

		@SerializedName("name")
		private MatchCheck name;

		@SerializedName("pan")
		private MatchCheck pan;

		// Getters and Setters
		public MatchCheck getAddressMatch() {
			return addressMatch;
		}

		public void setAddressMatch(MatchCheck addressMatch) {
			this.addressMatch = addressMatch;
		}

		public StatusCheck getAddressStatus() {
			return addressStatus;
		}

		public void setAddressStatus(StatusCheck addressStatus) {
			this.addressStatus = addressStatus;
		}

		public CibilCheck getCibil() {
			return cibil;
		}

		public void setCibil(CibilCheck cibil) {
			this.cibil = cibil;
		}

		public StatusCheck getCriminal() {
			return criminal;
		}

		public void setCriminal(StatusCheck criminal) {
			this.criminal = criminal;
		}

		public FaceCheck getFace() {
			return face;
		}

		public void setFace(FaceCheck face) {
			this.face = face;
		}

		public MatchCheck getName() {
			return name;
		}

		public void setName(MatchCheck name) {
			this.name = name;
		}

		public MatchCheck getPan() {
			return pan;
		}

		public void setPan(MatchCheck pan) {
			this.pan = pan;
		}
	}

	public static class MatchCheck {
		@SerializedName("form_value")
		private String formValue;

		@SerializedName("match_percentage")
		private int matchPercentage;

		@SerializedName("report_value")
		private String reportValue;

		@SerializedName("status")
		private String status;

		// Getters and Setters
		public String getFormValue() {
			return formValue;
		}

		public void setFormValue(String formValue) {
			this.formValue = formValue;
		}

		public int getMatchPercentage() {
			return matchPercentage;
		}

		public void setMatchPercentage(int matchPercentage) {
			this.matchPercentage = matchPercentage;
		}

		public String getReportValue() {
			return reportValue;
		}

		public void setReportValue(String reportValue) {
			this.reportValue = reportValue;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public static class StatusCheck {
		@SerializedName("normalized_status")
		private String normalizedStatus;

		@SerializedName("raw_status")
		private String rawStatus;

		@SerializedName("status")
		private String status;

		// Getters and Setters
		public String getNormalizedStatus() {
			return normalizedStatus;
		}

		public void setNormalizedStatus(String normalizedStatus) {
			this.normalizedStatus = normalizedStatus;
		}

		public String getRawStatus() {
			return rawStatus;
		}

		public void setRawStatus(String rawStatus) {
			this.rawStatus = rawStatus;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public static class CibilCheck {
		@SerializedName("score")
		private int score;

		@SerializedName("status")
		private String status;

		// Getters and Setters
		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

	public static class FaceCheck {
		@SerializedName("face_match_confidence")
		private Double faceMatchConfidence;

		@SerializedName("normalized_status")
		private String normalizedStatus;

		@SerializedName("raw_status")
		private String rawStatus;

		@SerializedName("status")
		private String status;

		// Getters and Setters
		public Double getFaceMatchConfidence() {
			return faceMatchConfidence;
		}

		public void setFaceMatchConfidence(Double faceMatchConfidence) {
			this.faceMatchConfidence = faceMatchConfidence;
		}

		public String getNormalizedStatus() {
			return normalizedStatus;
		}

		public void setNormalizedStatus(String normalizedStatus) {
			this.normalizedStatus = normalizedStatus;
		}

		public String getRawStatus() {
			return rawStatus;
		}

		public void setRawStatus(String rawStatus) {
			this.rawStatus = rawStatus;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}

}