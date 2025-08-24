package com.ps.dto.request;

import com.ps.constants.IValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Admin profile request.
 * Used to capture Admin profile details during registration.
 * Includes validation for name, email, phone, and password fields.
 */
@Data
public class AdminProfileRequest {

	@NotBlank(message = IValidationConstants.NAME_REQUIRED)
	private String name;

	@NotBlank(message = IValidationConstants.EMAIL_REQUIRED)
	@Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = IValidationConstants.INVALID_EMAIL)
	private String email;

	@NotBlank(message = IValidationConstants.PHONE_REQUIRED)
	@Pattern(regexp = "^[0-9]{10}$", message = IValidationConstants.INVALID_PHONE)
	private String phone;

	@NotBlank(message = IValidationConstants.PASSWORD_REQUIRED)
	@Size(min = 5, message = IValidationConstants.INVALID_PASSWORD)
	private String password;
}
