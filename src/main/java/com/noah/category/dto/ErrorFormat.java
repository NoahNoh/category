package com.noah.category.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * [공통] 에러반환 Class
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class ErrorFormat extends CommFormat {
	private static final long serialVersionUID = 1L;

	/**
	 * 에러 메시지
	 */
	String msg;

	@Builder
	public ErrorFormat(boolean success, ResultCode resultCode, String msg) {
		this.success = success;
		this.code = resultCode.toString();
		this.msg = msg;
	}
}