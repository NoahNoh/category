package com.noah.category.api;

import com.noah.category.dto.CommFormat;
import com.noah.category.dto.ErrorFormat;
import com.noah.category.dto.ResultCode;
import com.noah.category.dto.SuccessFormat;
import com.noah.category.exception.NoahException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class NoahRestControllerAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
		Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
		ServerHttpResponse response) {

		CommFormat res = this.responseObj(true, ResultCode.NOAH_0000, null, body);
		response.setStatusCode(HttpStatus.OK);

		return res;
	}

	@ExceptionHandler({ NoahException.class })
	public CommFormat exceptionHandler(NoahException e) {
		ResultCode code = ResultCode.valueOf(e.getResultCode());
		CommFormat res = this.responseObj(false, code, e.getMessage(), null);
		log.error(e.getMessage());
		return res;
	}

	@ExceptionHandler({ Exception.class })
	public CommFormat exceptionHandler(Exception e) {
		CommFormat res = this.responseObj(false, ResultCode.NOAH_0001, e.getMessage(), null);
		e.printStackTrace();
		return res;
	}

	/**
	 * 공통: 반환 포맷 세팅
	 * @param isSuccess  요청 성공여부
	 * @param code  반환 Code 정보
	 * @param msg  반환 메시지 정보
	 * @param responseData  반환 데이터
	 * @return
	 */
	public CommFormat responseObj(boolean isSuccess, ResultCode code, String msg, Object responseData) {
		if(!isSuccess) {
			return ErrorFormat.builder()
					.success(false)
					.resultCode(code)
					.msg(msg)
					.build();
		}

		return SuccessFormat.builder()
				.success(true)
				.resultCode(code)
				.info(responseData)
				.build();
	}
}