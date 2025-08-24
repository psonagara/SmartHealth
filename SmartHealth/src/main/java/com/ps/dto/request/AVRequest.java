package com.ps.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an Availability View request.
 * Used by Doctor to view and search Availability Slot based on optional criteria from and to date.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AVRequest {

	private LocalDate from;
	private LocalDate to;
}