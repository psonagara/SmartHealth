package com.ps.dto.request;

import com.ps.constants.IValidationConstants;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Specialization;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Admin Doctor Search request.
 * Used by Admin to filter and search doctors based on optional criteria such as name, email, phone, etc.
 * All fields are optional, allowing flexible filtering with any combination of parameters.
 */
@Data
public class ADSRequest {

	private Integer id;
	
	@Size(max = 100, message = IValidationConstants.NAME_CONSTRAINT)
	private String name;
	
	@Size(max = 255, message = IValidationConstants.EMAIL_CONSTRAINT)
	private String email;
	
	@Size(max = 10, message = IValidationConstants.PHONE_CONSTRAINT)
	private String phone;
	
	@Size(max = 7, message = IValidationConstants.GENDER_CONSTRAINT)
	private String gender;
	
	private Degree degree;
	private Specialization specialization; 
	private Department department;
	
	@Min(value = 0, message = IValidationConstants.ADS_YOE_CONSTRAINT)
	private Integer yearOfExperience;
	
	@Size(max = 50, message = IValidationConstants.REG_NUMBER_CONSTRAINT)
	private String registrationNumber;
	
	private Boolean profileComplete; 
	private Boolean isActive;
}
