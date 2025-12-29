package com.api.reporting;

public class TestExecutionContext {

	private static final ThreadLocal<TestExecutionContext> CONTEXT = ThreadLocal.withInitial(TestExecutionContext::new);

	private String testCaseId;
	private String testName;
	private String className;
	private String expectedResult;
	private String actualResult;
	private String severity;
	private String apiModule;
	private long startTime;
	private long endTime;
	private String error;
	private Integer httpStatus;
	private Integer apiCode;

	public static TestExecutionContext get() {
		return CONTEXT.get();
	}
	public static void clear() {
		CONTEXT.remove();
	}
	
	public Integer getHttpStatus() {
	    return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
	    this.httpStatus = httpStatus;
	}

	public Integer getApiCode() {
	    return apiCode;
	}

	public void setApiCode(Integer apiCode) {
	    this.apiCode = apiCode;
	}


	// ===== setters =====
	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public void setActualResult(String actualResult) {
		this.actualResult = actualResult;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public void setApiModule(String apiModule) {
		this.apiModule = apiModule;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public void setError(String error) {
		this.error = error;
	}

	// ===== getters =====
	public String getTestCaseId() {
		return testCaseId;
	}

	public String getTestName() {
		return testName;
	}

	public String getClassName() {
		return className;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public String getActualResult() {
		return actualResult;
	}

	public String getSeverity() {
		return severity;
	}

	public String getApiModule() {
		return apiModule;
	}


	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public String getError() {
		return error;
	}
}
