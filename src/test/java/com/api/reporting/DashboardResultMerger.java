package com.api.reporting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class DashboardResultMerger {

    private static final Gson gson = new Gson();
    private static final Type LIST_TYPE =
            new TypeToken<List<Map<String, Object>>>() {}.getType();

    public static void merge() {
        List<Map<String, Object>> merged = new ArrayList<>();

        read("dashboard/dashboard-results-local.json", merged);
        read("dashboard/dashboard-results-ci.json", merged);

        write("dashboard/dashboard-results.json", merged);
    }

    private static void read(String path, List<Map<String, Object>> target) {
        try {
            File f = new File(path);
            if (!f.exists()) return;

            try (Reader r = new FileReader(f)) {
                List<Map<String, Object>> data = gson.fromJson(r, LIST_TYPE);
                if (data != null) target.addAll(data);
            }
        } catch (Exception ignored) {}
    }

    private static void write(String path, List<Map<String, Object>> data) {
        try {
            new File(path).getParentFile().mkdirs();
            try (Writer w = new FileWriter(path)) {
                gson.toJson(data, w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
