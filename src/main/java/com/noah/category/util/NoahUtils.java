package com.noah.category.util;

import org.springframework.stereotype.Component;

@Component
public class NoahUtils {
	/**
	 * 공통: 목록의 현재 페이지 유효한 값으로 세팅
	 * @param page  현재 페이지
	 * @param limit  페이지 제한 수
	 * @return
	 */
	public int setPage(int page, int limit) {
		if(page < 1) {
			page = 1;
		}
		page = (page-1) * limit;
		return page;
	}
	/**
	 * 공통: 목록의 페이지 제한 수 유효한 값으로 세팅
	 * @param limit  페이지 제한 수
	 * @return
	 */
	public int setLimit(int limit) {
		if(limit < 1) {
			limit = 1;
		}
		return limit;
	}

	/**
	 * 공통: 기본 로그 메시지 세팅
	 * @param val  로그 값
	 * @return
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