package com.ps.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.ps.annotation.ValidateAGR;
import com.ps.entity.SlotInput;
import com.ps.enu.AGMode;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Availability Generation request.
 * Used to generate doctor availability slots based on different modes (AUTO, MANUAL, CUSTOM_ONE_TIME, CUSTOM_CONTINUOUS).
 * All fields are validated based on the selected mode using the @ValidateAGR annotation.
 */
@Data
@ValidateAGR
public class AGRequest {

	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate lastGeneratedOn;
	private Integer daysAhead;
	private List<SlotInput> slotInputs;
	private AGMode mode;
	private boolean skipHoliday;
	private List<ASRequest> manualSlots;
}
