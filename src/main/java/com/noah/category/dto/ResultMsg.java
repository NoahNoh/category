package com.noah.category.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * [공통] API 결과 메시지 Class_210712
 */
@Data
public class ResultMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * API 결과 메시지정보
	 */
	private String resultMsg;

	@Builder
	public ResultMsg(String info) {
		this.resultMsg = info;
	}
}