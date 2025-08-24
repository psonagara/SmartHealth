package com.ps.entity;

import com.ps.constants.IValidationConstants;
import com.ps.marker.OnUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Entity representing a medical department.
 * Maps to a table with a unique constraint on name and validation for updates.
 * Also used as DTO for related operations.
 */
@Data
@Entity
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(message = IValidationConstants.DEPARTMENT_ID_REQUIRED, groups = OnUpdate.class)
	private Integer id;
	
	@Column(unique = true, nullable = false)
	@NotBlank(message = IValidationConstants.DEPARTMENT_NAME_REQUIRED, groups = OnUpdate.class)
	private String name;
}
