package com.ps.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.ps.entity.SlotInput;
import com.ps.enu.AGMode;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Availability Generation Preference response.
 * Used to provide details about how availability slots are generated for a doctor.
 * Includes mode, dates, slot inputs, and holiday skipping preferences.
 */
@Data
public class AGPreferenceResponse {

	private Integer id;
	private AGMode mode;
	private Integer daysAhead;
	private List<SlotInput> slotInputs;
	private LocalDate startDate;
	private LocalDate endDate;
	private Boolean skipHoliday;
}
