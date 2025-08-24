package com.ps.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Availability Details response.
 * Used when to view details about a specific availability slot, including slot details and associated appointments.
 * Contains an AVResponse for slot information and a list of AIResponse for appointment details.
 */
@Data
public class ADResponse {

	private AVResponse avResponse;
	private List<AIResponse> appointmentInfo;
	private Map<String, Object> additionalInfo;
}
