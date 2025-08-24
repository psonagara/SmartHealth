package com.ps.dto.request;

import java.time.LocalDate;

import com.ps.constants.IValidationConstants;
import com.ps.marker.OnRegister;
import com.ps.marker.OnUpdate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a patient profile request.
 * Used for patient registration (OnRegister) and profile updates (OnUpdate).
 * Validation groups ensure required fields and constraints vary by operation.
 */
@Data
public class PatientProfileRequest {

	@NotBlank(message = IValidationConstants.NAME_REQUIRED, groups = {OnRegister.class, OnUpdate.class})
	@Size(max = 100, message = IValidationConstants.NAME_CONSTRAINT, groups = {OnRegister.class, OnUpdate.class})
	private String name;

	@NotBlank(message = IValidationConstants.EMAIL_REQUIRED, groups = OnRegister.class)
	@Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = IValidationConstants.INVALID_EMAIL, groups = OnRegister.class)
	@Size(max = 255, message = IValidationConstants.EMAIL_CONSTRAINT, groups = OnRegister.class)
	private String email;

	@NotBlank(message = IValidationConstants.PHONE_REQUIRED, groups = OnRegister.class)
	@Pattern(regexp = "^[0-9]{10}$", message = IValidationConstants.INVALID_PHONE, groups = OnRegister.class)
	private String phone;

	@NotBlank(message = IValidationConstants.PASSWORD_REQUIRED , groups = OnRegister.class)
	@Size(min = 5, message = IValidationConstants.INVALID_PASSWORD, groups = OnRegister.class)
	private String password;

	@NotNull(message = IValidationConstants.DOB_REQUIRED, groups = OnUpdate.class)
	private LocalDate dob;

	@NotBlank(message = IValidationConstants.GENDER_REQUIRED, groups = OnUpdate.class)
	@Size(max = 7, message = IValidationConstants.GENDER_CONSTRAINT, groups = OnRegister.class)
	private String gender;

	@NotNull(message = IValidationConstants.HEIGHT_REQUIRED, groups = OnUpdate.class)
	private Double height;

	@NotNull(message = IValidationConstants.WEIGHT_REQUIRED, groups = OnUpdate.class)
	private Integer weight;
}
