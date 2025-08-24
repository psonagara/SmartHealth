package com.ps.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Specialization;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Admin Doctor Search response.
 * Used to return detailed doctor profile information when an Admin searches for or views doctor details.
 * Includes availability generation preferences via AGPreferenceResponse and excludes null fields in JSON.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ADSResponse {

	private Integer id;
	private String name;
	private String email;
	private String phone;
	private String dob;
	private String gender;
	private List<Degree> degrees;
	private List<Specialization> specializations;
	private List<Department> departments;
	private Integer yearOfExperience;
	private String profilePicPath;
	private String address;
	private String registrationNumber;
	private Boolean profileComplete;
	private Boolean isActive;
	private AGPreferenceResponse agPreferenceResponse;
	private LocalDateTime creationTime;
	private LocalDateTime updationTime;
}
