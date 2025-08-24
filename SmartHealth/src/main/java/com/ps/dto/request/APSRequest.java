package com.ps.dto.request;

import com.ps.constants.IValidationConstants;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Admin Patient Search request.
 * Used by Admin to filter and search patients based on optional criteria such as name, email, phone, etc.
 * All fields are optional, allowing flexible filtering with any combination of parameters.
 */
@Data
public class APSRequest {

	private Integer id;
	
	@Size(max = 100, message = IValidationConstants.NAME_CONSTRAINT)
	private String name;
	
	@Size(max = 255, message = IValidationConstants.EMAIL_CONSTRAINT)
	private String email;
	
	@Size(max = 10, message = IValidationConstants.PHONE_CONSTRAINT)
	private String phone;
	
	@Size(max = 7, message = IValidationConstants.GENDER_CONSTRAINT)
	private String gender;
	
	private Boolean profileComplete;
	private Boolean isActive;
}
