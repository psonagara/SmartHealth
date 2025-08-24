package com.ps.dto.response;

import java.time.LocalDateTime;

import com.ps.dto.SubProfileDTO;
import com.ps.enu.AppointmentStatus;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Appointment Information response.
 * Used to provide details of an appointment.
 * Includes patient and sub-profile information.
 */
@Data
public class AIResponse {

	private LocalDateTime bookingTime;
	private LocalDateTime updatedTime;
	private AppointmentStatus status;
	private String note;
	private PatientProfileResponse patient;
	private SubProfileDTO subProfile;
}
