package com.ps.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ps.enu.LeaveStatus;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a Leave response.
 * Used to return details of a doctor's leave request, including dates, status, and reason.
 */
@Data
public class LeaveResponse {

	private Integer id;
	private LocalDate from;
	private LocalDate to;
	private LeaveStatus status;
	private Integer days;
	private String reason;
	private LocalDateTime creationTime;
	private LocalDateTime updationTime;
}
