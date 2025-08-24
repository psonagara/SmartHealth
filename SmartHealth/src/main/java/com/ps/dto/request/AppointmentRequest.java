package com.ps.dto.request;

import com.ps.annotation.ValidateAR;
import com.ps.dto.SubProfileDTO;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an appointment booking request.
 * Used to capture details for scheduling an appointment, including doctor, patient, slot, and optional sub-profile information.
 * Validated using the @ValidateAR annotation to ensure required fields and sub-profile consistency.
 */
@Data
@ValidateAR
public class AppointmentRequest {

	private Integer doctorId;
	private Integer patientId;
	private Integer slotId;
	private Boolean isSubProfile;
	private SubProfileDTO subProfile;
	private String note;
}
