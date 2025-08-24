package com.ps.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ps.annotation.ValidateADR;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an availability deletion request.
 * Used to capture date and time ranges for bulk slot deletion by doctors.
 * Validated using the custom @ValidateADR annotation.
 */
@Data
@ValidateADR
public class ADRequest {
	
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
}