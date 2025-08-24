package com.ps.dto.response;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an response related to particular Appointment.
 * Used to provide details of doctor, patient and slot of a particular appointment.
 */
@Data
public class ApDResponse {

	private DSResponse doctor;
	private PatientProfileResponse patient;
	private AVResponse slot;
}
