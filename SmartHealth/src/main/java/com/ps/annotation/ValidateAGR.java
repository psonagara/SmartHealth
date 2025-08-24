package com.ps.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ps.validator.AGRValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom annotation to validate AGRequest objects.
 * Ensures field validity based on the selected AGMode (AUTO, MANUAL, CUSTOM_ONE_TIME, CUSTOM_CONTINUOUS).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AGRValidator.class)
@Documented
public @interface ValidateAGR {

	String message() default "Invalid Request";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
