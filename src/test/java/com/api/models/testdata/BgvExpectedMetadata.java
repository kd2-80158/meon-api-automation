package com.api.models.testdata;

public class BgvExpectedMetadata {
	private String inputName;
	private String inputAddress;
	private String inputPan;
	private String inputAadhaar;
	private double expectedConfidenceThreshold;
	private String expectedStatus;
	private int expectedCibilScore;
	private String expectedFinalDecision;
	private int expectedMatchPercentage;


	public String getInputName() {
		return inputName;
	}

	public String getInputAddress() {
		return inputAddress;
	}

	public String getInputPan() {
		return inputPan;
	}

	public String getInputAadhaar() {
		return inputAadhaar;
	}

	public double getExpectedConfidenceThreshold() {
		return expectedConfidenceThreshold;
	}

	public String getExpectedStatus() {
		return expectedStatus;
	}

	public int getExpectedCibilScore() {
		return expectedCibilScore;
	}

	public String getExpectedFinalDecision() {
		return expectedFinalDecision;
	}

	public int getExpectedMatchPercentage() {
		return expectedMatchPercentage;
	}
}