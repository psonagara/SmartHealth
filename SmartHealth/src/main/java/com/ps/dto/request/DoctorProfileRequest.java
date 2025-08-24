package com.ps.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.ps.constants.IValidationConstants;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Specialization;
import com.ps.marker.OnRegister;
import com.ps.marker.OnUpdate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a doctor profile request.
 * Used for doctor registration (OnRegister) and profile updates (OnUpdate).
 * Validation groups ensure required fields and constraints vary by operation.
 */
@Data
public class DoctorProfileRequest {

	@NotBlank(message = IValidationConstants.NAME_REQUIRED, groups = {OnRegister.class, OnUpdate.class})
	private String name;

	@NotBlank(message = IValidationConstants.EMAIL_REQUIRED, groups = OnRegister.class)
	@Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = IValidationConstants.INVALID_EMAIL, groups = OnRegister.class)
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
	@Size(max = 7, message = IValidationConstants.GENDER_CONSTRAINT, groups = OnUpdate.class)
	private String gender;
	
	@Valid
	@Size(min = 1, message = IValidationConstants.DEGREE_REQUIRED, groups = OnUpdate.class)
	private List<Degree> degrees;
	
	@Valid
	private List<Specialization> specializations;
	
	@Valid
	@Size(min = 1, message = IValidationConstants.DEPARTMENT_REQUIRED, groups = OnUpdate.class)
	private List<Department> departments;
	
	@Min(value = 0, message = IValidationConstants.NEGATIVE_EXPERIENCE_NOT_ALLOWED, groups = OnUpdate.class)
	@Max(value = 100, message = IValidationConstants.VALID_EXPERIENCE, groups = OnUpdate.class)
	private Integer yearOfExperience;

	@NotBlank(message = IValidationConstants.ADDRESS_REQUIRED, groups = OnUpdate.class)
	@Size(max = 500, message = IValidationConstants.ADDRESS_CONSTRAINT, groups = OnUpdate.class)
	private String address;
	
	@Size(max = 50, message = IValidationConstants.REG_NUMBER_CONSTRAINT, groups = OnUpdate.class)
	private String registrationNumber;
}
