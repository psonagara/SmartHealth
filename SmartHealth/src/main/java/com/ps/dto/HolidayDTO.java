package com.ps.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ps.constants.IValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * Data Transfer Object (DTO) representing a holiday record.
 * Used to transfer holiday details, including date and reason.
 */
@Data
public class HolidayDTO {

	private Integer id;
	
	@NotNull(message = IValidationConstants.HOLIDAY_DATE_CONSTRAINT)
	private LocalDate holidayDate;
	
	@NotBlank(message = IValidationConstants.HOLIDAY_REASON_CONSTRAINT)
	private String reason;
	
	private LocalDateTime creationTime;
}
