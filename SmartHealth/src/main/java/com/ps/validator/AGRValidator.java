package com.ps.validator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ps.annotation.ValidateAGR;
import com.ps.config.props.SlotsProperties;
import com.ps.constants.IValidationConstants;
import com.ps.dto.request.AGRequest;
import com.ps.dto.request.ASRequest;
import com.ps.entity.SlotInput;
import com.ps.enu.AGMode;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the @ValidateAGR annotation.
 * Validates AGRequest fields based on the selected mode. 
 * Also using SlotsProperties for maximum generation days value.
 */
public class AGRValidator implements ConstraintValidator<ValidateAGR, AGRequest> {
	
	@Autowired
	private SlotsProperties slotsProperties;

	@Override
	public boolean isValid(AGRequest value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		
		if (value == null) {
			buildConstraintViolation(IValidationConstants.AGR_NOT_NULL, "request", context);
			return false;
		}
		if (value.getLastGeneratedOn() != null) {
			buildConstraintViolation(IValidationConstants.LAST_GENERATED_CONSTRAINT, "lastGeneratedOn", context);
			return false;
		}
		if (value.getMode() == null) {
			buildConstraintViolation(IValidationConstants.AGR_MODE_CONSTRAINT, "mode", context);
			return false;
		}
		

		if (value.getMode().equals(AGMode.AUTO) || (value.getMode().equals(AGMode.MANUAL) && value.getManualSlots() == null)) {
			return true;
		}

		LocalDate today = LocalDate.now();
		if (value.getMode().equals(AGMode.MANUAL)) {
			for (ASRequest slot : value.getManualSlots()) {

				LocalDate date = slot.getDate();
				if (date == null) {
					buildConstraintViolation(IValidationConstants.DATE_CONSTRAINT_MANUALLY, "ASRequest.date", context);
					return false;
				}
				if (date.isBefore(today)) {
					buildConstraintViolation(IValidationConstants.DATE_CONSTRAINT_2_MANUALLY, "ASRequest.date", context);
					return false;
				}
				if (date.isAfter(today.plusDays(slotsProperties.getMaximumGenerationDays()))) {
					buildConstraintViolation(IValidationConstants.FUTURE_15DAYS_CONSTRAINTS, "endDate", context);
					return false;
				}
				LocalTime fromTime = slot.getFrom();
				LocalTime toTime = slot.getTo();
				if (fromTime == null) {
					buildConstraintViolation(IValidationConstants.START_TIME_CONSTRAINT, "ASRequest.from", context);
					return false;
				}
				if (toTime == null) {
					buildConstraintViolation(IValidationConstants.END_TIME_CONSTRAINT_2, "ASRequest.to", context);
					return false;
				}
				if (toTime.compareTo(fromTime) <= 0) {					
					buildConstraintViolation(IValidationConstants.END_TIME_CONSTRAINT, "ASRequest.to", context);
					return false;
				}
			}
		}

		if (value.getMode().equals(AGMode.CUSTOM_ONE_TIME) || value.getMode().equals(AGMode.CUSTOM_CONTINUOUS)) {
			LocalDate startDate = value.getStartDate();
			if (startDate == null) {
				buildConstraintViolation(IValidationConstants.START_DATE_MADATORY, "startDate", context);
				return false;
			}

			if (startDate.isBefore(today)) {
				buildConstraintViolation(IValidationConstants.START_DATE_CONSTRAINTS, "startDate", context);
				return false;
			}

			if (value.getMode() == AGMode.CUSTOM_ONE_TIME) {
				LocalDate endDate = value.getEndDate();
				if (endDate == null) {
					buildConstraintViolation(IValidationConstants.ONE_TIME_END_DATE_MADATORY, "endDate", context);
					return false;
				}
				if (endDate.isBefore(today)) {
					buildConstraintViolation(IValidationConstants.END_DATE_CONSTRAINTS, "endDate", context);
					return false;
				}
				if (startDate.until(endDate).getDays() > slotsProperties.getMaximumGenerationDays()) {
					buildConstraintViolation(IValidationConstants.FUTURE_15DAYS_CONSTRAINTS, "endDate", context);
					return false;
				}
			}

			if (value.getMode() == AGMode.CUSTOM_CONTINUOUS) {
				Integer daysAhead = value.getDaysAhead();
				if (daysAhead == null) {
					value.setDaysAhead(0);
					daysAhead = 0;
				}
				if (daysAhead < 0 || daysAhead > slotsProperties.getMaximumGenerationDays()) {
					buildConstraintViolation(IValidationConstants.FUTURE_15DAYS_CONSTRAINTS, "endDate", context);
					return false;
				}
			}

			List<SlotInput> slotInputs = value.getSlotInputs();
			if (slotInputs != null && slotInputs.size() > 0) {
				for (SlotInput slotInput : slotInputs) {
					LocalTime startTime = slotInput.getStartTime();
					LocalTime endTime = slotInput.getEndTime();
					if (startTime == null) {
						buildConstraintViolation(IValidationConstants.START_TIME_CONSTRAINT, "SlotInput.startTime", context);
						return false;
					}
					if (endTime == null) {
						buildConstraintViolation(IValidationConstants.END_TIME_CONSTRAINT_2, "SlotInput.endTime", context);
						return false;
					}
					if (endTime.isBefore(startTime)) {
						buildConstraintViolation(IValidationConstants.END_TIME_CONSTRAINT, "SlotInput.endTime", context);
						return false;
					}
					if (Duration.between(startTime, endTime).toMinutes() < slotInput.getGapInMinutes()) {					
						buildConstraintViolation(IValidationConstants.GAP_IN_MINUTES_CONSTRAINT, "SlotInput.gapInMinutes", context);
						return false;
					}
				}
			} else {
				buildConstraintViolation(IValidationConstants.SLOT_INPUT_CONSTRAINTS, "slotInput", context);
				return false;
			}
		}
		return true;
	}

	private void buildConstraintViolation(String message, String field, ConstraintValidatorContext contex) {
		contex.buildConstraintViolationWithTemplate(message)
		.addPropertyNode(field)
		.addConstraintViolation();
	}
}
