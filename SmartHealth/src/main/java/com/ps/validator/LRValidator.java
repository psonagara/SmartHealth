package com.ps.validator;

import java.time.DayOfWeek;
import java.time.LocalDate;

import com.ps.annotation.ValidateLR;
import com.ps.constants.IValidationConstants;
import com.ps.dto.request.LeaveRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the @ValidateLR annotation.
 * Validates LeaveRequest fields, ensuring from date is in the future, to date is not before from date,
 * reason is provided, and neither date falls on a Sunday.
 */
public class LRValidator implements ConstraintValidator<ValidateLR, LeaveRequest> {

	@Override
	public boolean isValid(LeaveRequest value, ConstraintValidatorContext context) {
		
		context.disableDefaultConstraintViolation();
		if (value == null) {
			buildConstraintViolation(IValidationConstants.LR_NOT_NULL, "request", context);
			return false;
		}
		LocalDate from = value.getFrom();
		if (from == null) {
			buildConstraintViolation(IValidationConstants.LEAVE_FROM_DATE_CONSTRAINT, "from", context);
			return false;
		}
		if (from.isBefore(LocalDate.now())) {
			buildConstraintViolation(IValidationConstants.LEAVE_FROM_DATE_CONSTRAINT_2, "from", context);
			return false;
		}
		LocalDate to = value.getTo();
		if (to != null && to.isBefore(from)) {
			buildConstraintViolation(IValidationConstants.LEAVE_TO_DATE_CONSTRAINT, "to", context);
			return false;
		}
		String reason = value.getReason();
		if (reason == null || reason.isBlank()) {
			buildConstraintViolation(IValidationConstants.LEAVE_REASON_CONSTRAINT, "reason", context);
			return false;
		}
		if (reason.length() > 500) {
			buildConstraintViolation(IValidationConstants.LEAVE_REASON_CONSTRAINT_2, "reason", context);
			return false;
		}
		if (to == null) {
			to = from;
			value.setTo(to);
		}
		if (from.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			buildConstraintViolation(IValidationConstants.LEAVE_FROM_TO_DATE_CONSTRAINT, "from", context);
			return false;
		}
		if (to.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			buildConstraintViolation(IValidationConstants.LEAVE_FROM_TO_DATE_CONSTRAINT, "to", context);
			return false;
		}
		return true;
	}
	
	private void buildConstraintViolation(String message, String field, ConstraintValidatorContext context) {
		context.buildConstraintViolationWithTemplate(message)
			   .addPropertyNode(field)
			   .addConstraintViolation();
	}
}
