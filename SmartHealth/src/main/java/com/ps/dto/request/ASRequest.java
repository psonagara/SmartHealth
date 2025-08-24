package com.ps.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a manual slot creation request.
 * Used to specify the date and time range (from and to) for manually creating availability slots.
 * This class is nested within AGRequest for manual slot generation and validated accordingly.
 */
@Data
public class ASRequest {

	private LocalDate date;
	private LocalTime from;
	private LocalTime to;
}
