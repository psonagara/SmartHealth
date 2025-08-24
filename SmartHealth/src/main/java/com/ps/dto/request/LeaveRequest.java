package com.ps.dto.request;

import java.time.LocalDate;

import com.ps.annotation.ValidateLR;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a leave request for a doctor.
 * Used to transfer leave details (from date, to date, and reason) when a doctor books leave.
 * Validated using the @ValidateLR annotation to ensure future dates, valid ranges, and no Sundays.
 */
@Data
@ValidateLR
public class LeaveRequest {

	private LocalDate from;
	private LocalDate to;
	private String reason;
}
