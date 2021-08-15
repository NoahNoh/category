package com.noah.category.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * [공통] 성공반환 Class
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class SuccessFormat extends CommFormat {
	private static final long serialVersionUID = 1L;

	/**
	 * 반환할 데이터
	 */
	private Object data;

	@Builder
	public SuccessFormat(boolean success, ResultCode resultCode, Object info) {
		this.success = success;
		this.code = resultCode.toString();
		this.data = info;
	}
}