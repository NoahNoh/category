package com.noah.category.util;

import org.springframework.stereotype.Component;

@Component
public class NoahUtils {
	/**
	 * 공통: 기본 로그 메시지 세팅
	 * @param val  로그 값
	 * @return  로그 메시지
	 */
	public String setDefaultLogMessage(Object val) {
		Throwable throwable = new Throwable();
		StackTraceElement[] elements = throwable.getStackTrace();

		String callMethod = elements[1].getMethodName();
		StringBuilder logMsg = new StringBuilder("=======> ");

		logMsg.append(callMethod).append("(").append(val.toString()).append(")");

		return logMsg.toString();
	}
}