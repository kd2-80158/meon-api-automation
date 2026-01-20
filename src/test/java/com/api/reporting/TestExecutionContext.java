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

	// execution metadata
	private String executionType;
	private String executionSource;
	private String executedBy;
	private String environment;
	private String buildId;

	// classification
	private String failureType;
	private int retryCount;

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

	public String getExecutionType() {
		return executionType;
	}

	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}

	public String getExecutionSource() {
		return executionSource;
	}

	public void setExecutionSource(String executionSource) {
		this.executionSource = executionSource;
	}

	public String getExecutedBy() {
		return executedBy;
	}

	public void setExecutedBy(String executedBy) {
		this.executedBy = executedBy;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getFailureType() {
		return failureType;
	}

	public void setFailureType(String failureType) {
		this.failureType = failureType;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

}
