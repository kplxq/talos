package com.kxd.talos.trace.core.utils;

import java.nio.charset.Charset;

import static java.lang.String.format;

/**
 * Utilities, typically copied in from guava, so as to avoid dependency
 * conflicts.
 */
public final class Util {

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Copy of {@code com.google.common.base.Preconditions#checkNotNull}.
	 */
	public static <T> T checkNotNull(T reference, String errorMessageTemplate,
			Object... errorMessageArgs) {
		if (reference == null) {
			// If either of these parameters is null, the right thing happens
			// anyway
			throw new NullPointerException(format(errorMessageTemplate,
					errorMessageArgs));
		}
		return reference;
	}
	
	public static<T> T checkNotNullDefault(T reference,T defaultValue){
		if (reference == null) {
			// If either of these parameters is null, the right thing happens
			// anyway
			return defaultValue;
		}
		return reference;
	}

	public static String checkNotBlank(String string,
			String errorMessageTemplate, Object... errorMessageArgs) {
		if (checkNotNull(string, errorMessageTemplate, errorMessageArgs).trim()
				.isEmpty()) {
			throw new IllegalArgumentException(format(errorMessageTemplate,
					errorMessageArgs));
		}
		return string;
	}

	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	private Util() { // no instances
	}

	/**
	 * Copy of {@code com.google.common.base.Preconditions#checkArgument}.
	 */
	public static void checkArgument(boolean expression,
			String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalArgumentException(String.format(
					errorMessageTemplate, errorMessageArgs));
		}
	}

	public static String getCallerMethod() {
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] traceList = Thread.currentThread().getStackTrace();
		for (StackTraceElement trace : traceList) {
			sb.append(trace.getClassName() + "." + trace.getMethodName());
		}
		return sb.toString();
	}
}
