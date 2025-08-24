package com.ps.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ps.validator.ADRValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom annotation to validate ADRequest objects.
 * Ensures valid date and time ranges for availability deletion requests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ADRValidator.class)
@Documented
public @interface ValidateADR {

	String message() default "Invalid Request";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
