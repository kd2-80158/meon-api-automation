package com.api.models.response.ocr;

import com.google.gson.annotations.SerializedName;

public class PANOCRData {

	@SerializedName("pan_number")
	private String panNumber;

	@SerializedName("name")
	private String name;

	@SerializedName("father_name")
	private String fatherName;

	@SerializedName("date_of_birth")
	private String dateOfBirth;

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public String toString() {
		return "PANOCRData{" + "panNumber='" + maskPan(panNumber) + '\'' + ", name='" + name + '\'' + ", fatherName='"
				+ fatherName + '\'' + ", dateOfBirth='" + dateOfBirth + '\'' + '}';
	}

	private String maskPan(String pan) {

		if (pan == null || pan.trim().isEmpty()) {
			return null;
		}

		String value = pan.trim();

		if (value.length() < 6) {
			return "****";
		}

		return value.substring(0, 3) + "*****" + value.substring(value.length() - 2);
	}
}