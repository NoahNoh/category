package com.noah.category.exception;

import com.noah.category.dto.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class NoahException  extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public String resultCode;

	public NoahException(ResultCode resultCode, String msg) {
		super(msg);
		this.resultCode = resultCode.toString();
	}
}