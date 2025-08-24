package com.ps.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ps.enu.AGMode;
import com.ps.enu.SlotStatus;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Availability View response.
 * Used to provide details of an availability slot, also utilized when a doctor views their slots.
 * Includes JSON serialization settings to exclude null fields.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AVResponse {

	private Integer id;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private SlotStatus status;
	private AGMode mode;
	private LocalDateTime created;
	private LocalDateTime updated;
}
