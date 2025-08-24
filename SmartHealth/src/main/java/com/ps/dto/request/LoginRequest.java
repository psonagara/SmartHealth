package com.ps.dto.request;

import com.ps.constants.IValidationConstants;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a login request.
 * Used for authentication of admin, patient, or doctor, accepting either an email or phone as the user identifier.
 * Includes validation to ensure required fields are provided.
 */
@Data
public class LoginRequest {
	
	@NotBlank(message = IValidationConstants.EMAIL_OR_PHONE_REQUIRED)
	private String user;
	
	@NotBlank(message = IValidationConstants.PASSWORD_REQUIRED)
	private String password;
}
