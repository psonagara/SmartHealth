package com.ps.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ps.validator.LRValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom annotation to validate LeaveRequest objects.
 * Ensures required fields, future dates, valid date ranges, and excludes Sundays.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LRValidator.class)
@Documented
public @interface ValidateLR {

	String message() default "Invalid Request";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
