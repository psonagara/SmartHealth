package com.ps.dto.request;

import java.time.LocalDate;

import com.ps.constants.IValidationConstants;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Specialization;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a Doctor Search request.
 * Used by patients to search for doctors based on optional criteria such as name, degree, department, specialization, and date.
 * All fields are optional, enabling flexible searching.
 */
@Data
public class DSRequest {

	@Size(max = 100, message = IValidationConstants.NAME_CONSTRAINT)
	private String name;
	
	private Degree degree;
	private Department department;
	private Specialization specialization;
	private LocalDate date;
}
