package com.ps.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ps.validator.ARValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom annotation to validate AppointmentRequest objects.
 * Ensures required fields (doctorId, patientId, slotId) and sub-profile details (if isSubProfile is true) are valid.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ARValidator.class)
@Documented
public @interface ValidateAR {

	String message() default "Invalid Request";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
