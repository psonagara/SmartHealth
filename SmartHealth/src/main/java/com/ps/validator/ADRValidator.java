package com.ps.validator;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.ps.annotation.ValidateADR;
import com.ps.config.props.SlotsProperties;
import com.ps.constants.IValidationConstants;
import com.ps.dto.request.ADRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the @ValidateADR annotation.
 * Validates that startDate is not after endDate and startTime is not after endTime (if both are provided).
 */
public class ADRValidator implements ConstraintValidator<ValidateADR, ADRequest> {
	
	@Autowired
	private SlotsProperties slotsProperties;

	@Override
	public boolean isValid(ADRequest value, ConstraintValidatorContext context) {
		
		context.disableDefaultConstraintViolation();
		if (value == null) {
			buildConstraintViolation(IValidationConstants.ADR_NOT_NULL, "request", context);
			return false;
		}
		
		LocalDate startDate = value.getStartDate();
		if (startDate == null) {
			buildConstraintViolation(IValidationConstants.START_DATE_MADATORY, "startDate", context);
			return false;
		}
		
		LocalDate endDate = value.getEndDate();
		if (endDate == null) {
			buildConstraintViolation(IValidationConstants.END_DATE_CONSTRAINTS_2, "endDate", context);
			return false;
		}
		if (endDate.isBefore(startDate)) {
			buildConstraintViolation(IValidationConstants.END_DATE_CONSTRAINTS_3, "endDate", context);
			return false;
		}
		if (LocalDate.now().until(endDate).getDays() > slotsProperties.getMaximumGenerationDays()) {
			buildConstraintViolation(IValidationConstants.FUTURE_15DAYS_CONSTRAINTS_2, "endDate", context);
			return false;
		}
		
		LocalTime startTime = value.getStartTime();
		LocalTime endTime = value.getEndTime();
		if (startTime != null && endTime != null) {
			if (startTime.isAfter(endTime)) {
				buildConstraintViolation(IValidationConstants.END_TIME_CONSTRAINTS, "endTime", context);
				return false;
			}
		}
		return true;
	}

	private void buildConstraintViolation(String message, String field, ConstraintValidatorContext context) {
		context.buildConstraintViolationWithTemplate(message)
		.addPropertyNode(field)
		.addConstraintViolation();
	}
}
