package com.api.reporting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardJsonWriter {

    private static final Gson gson =
            new GsonBuilder().setPrettyPrinting().create();

    private static final String FILE_PATH = "logs/dashboard-results.json";
    private static boolean initialized = false;

    // Call once at suite start
    public static synchronized void init() {
        try {
            new File("logs").mkdirs();
            try (FileWriter fw = new FileWriter(FILE_PATH, false)) {
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

            try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
                fw.write(gson.toJson(json));
                fw.write(",\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * STATUS RESOLUTION RULE (FINAL):
     * 1️⃣ API response code (res.getCode())
     * 2️⃣ HTTP status (response.getStatusCode())
     * 3️⃣ 0
     */
    private static int resolveStatusCode(TestExecutionContext ctx) {

        if (ctx.getApiCode() != null && ctx.getApiCode() > 0) {
            return ctx.getApiCode();
        }

        if (ctx.getHttpStatus() != null && ctx.getHttpStatus() > 0) {
            return ctx.getHttpStatus();
        }

        return 0;
    }

    // Call once at suite end
    public static synchronized void finish() {
        try (RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "rw")) {
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
