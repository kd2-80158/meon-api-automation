//package com.api.utility;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//
//public class ApiVerifier {
//
//	private static final ThreadLocal<ApiVerifier> tl = ThreadLocal.withInitial(ApiVerifier::new);
//
//	public static ApiVerifier get() {
//		return tl.get();
//	}
//
//	private static class Failure {
//		Severity severity;
//		String message;
//
//		Failure(Severity s, String m) {
//			this.severity = s;
//			this.message = m;
//		}
//	}
//
//	private final List<Failure> failures = new ArrayList<>();
//
//	public void check(boolean condition, Severity severity, String message) {
//		if (!condition) {
//			failures.add(new Failure(severity, message));
//		}
//	}
//
//	public void assertAll() {
//		if (failures.isEmpty()) {
//			tl.remove();
//			return;
//		}
//
//		StringBuilder sb = new StringBuilder("API Validation Report:\n");
//
//		failures.stream().sorted(Comparator.comparing(f -> f.severity))
//				.forEach(f -> sb.append(f.severity).append(": ").append(f.message).append("\n"));
//
//		tl.remove();
//		throw new AssertionError(sb.toString());
//	}
//}
