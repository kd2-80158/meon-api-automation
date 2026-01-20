package com.api.reporting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardJsonWriter {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static String resolveFilePath() {
	    return ExecutionMeta.isCI()
	            ? "dashboard/dashboard-results-ci.json"
	            : "dashboard/dashboard-results-local.json";
	}
	static String filePath = resolveFilePath();
	private static boolean initialized = false;

	public static synchronized void init() {
		try {
			new File("logs").mkdirs();
			try (FileWriter fw = new FileWriter(filePath, false)) {
				fw.write("[\n");
			}
			initialized = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Call for every test
	public static synchronized void write(TestExecutionContext ctx, String status) {

		try {
			if (!initialized) {
				init();
			}

			int finalStatusCode = resolveStatusCode(ctx);

			Map<String, Object> json = new LinkedHashMap<>();
			json.put("testCaseId", ctx.getTestCaseId());
			json.put("testName", ctx.getTestName());
			json.put("className", ctx.getClassName());
			json.put("apiModule", ctx.getApiModule());
			json.put("expectedResult", ctx.getExpectedResult());
			json.put("actualResult", ctx.getActualResult());
			json.put("severity", ctx.getSeverity());
			json.put("status", status);
			json.put("httpStatus", finalStatusCode);
			json.put("error", ctx.getError());
			json.put("executionTimeMs", ctx.getEndTime() - ctx.getStartTime());
			json.put("timestamp", Instant.now().toString());
			json.put("executionType", ctx.getExecutionType());
			json.put("executionSource", ctx.getExecutionSource());
			json.put("executedBy", ctx.getExecutedBy());
			json.put("environment", ctx.getEnvironment());
			json.put("buildId", ctx.getBuildId());
			json.put("failureType", ctx.getFailureType());
			json.put("retryCount", ctx.getRetryCount());

			try (FileWriter fw = new FileWriter(filePath, true)) {
				fw.write(gson.toJson(json));
				fw.write(",\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static int resolveStatusCode(TestExecutionContext ctx) {

		if (ctx.getApiCode() != null && ctx.getApiCode() > 0) {
			return ctx.getApiCode();
		}

		if (ctx.getHttpStatus() != null && ctx.getHttpStatus() > 0) {
			return ctx.getHttpStatus();
		}

		return 0;
	}
	public static synchronized void finish() {
		try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
			long length = raf.length();

			// Remove last comma
			if (length > 2) {
				raf.setLength(length - 2);
			}

			raf.seek(raf.length());
			raf.writeBytes("\n]\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
