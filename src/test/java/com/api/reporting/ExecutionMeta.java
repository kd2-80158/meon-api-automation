package com.api.reporting;

public final class ExecutionMeta {

	private ExecutionMeta() {
	}

	public static boolean isCI() {
		return System.getenv("GITHUB_ACTIONS") != null;
	}

	public static String executionType() {
		return isCI() ? "CI" : "LOCAL";
	}

	public static String executionSource() {
		return isCI() ? "github-actions" : "manual";
	}

	public static String executedBy() {
		return isCI() ? "ci-bot" : System.getProperty("user.name");
	}

	public static String environment() {
		return System.getProperty("env", "uat");
	}

	public static String buildId() {
		return System.getenv().getOrDefault("GITHUB_RUN_ID", "local");
	}
}
