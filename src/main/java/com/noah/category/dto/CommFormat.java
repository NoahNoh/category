package com.noah.category.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * [공통] 반환 포맷관련 Class_210712
 */
@Data
public class CommFormat implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 요청 성공여부
	 */
	boolean success;

	/**
	 * 반환 Code 값
	 */
	String code;
}