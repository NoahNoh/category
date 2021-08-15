package com.noah.category.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validation;
import java.util.Collection;

@Component
public class ValidatorUtils implements Validator {
	private final SpringValidatorAdapter vaildator;

	public ValidatorUtils() {
		this.vaildator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public void validate(Object target, Errors errors) {
		if(target instanceof Collection) {
			Collection<?> collection = (Collection<?>) target;
			for(Object obj : collection) {
				vaildator.validate(obj, errors);
			}
		} else {
			vaildator.validate(target, errors);
		}
	}
}