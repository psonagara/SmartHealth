package com.ps.dto.response;

import java.util.List;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a Doctor Search response.
 * Used to return doctor details (e.g., ID, name, degrees, specializations) when a patient searches for doctors.
 */
@Data
public class DSResponse {

	private Integer id;
	private String name;
	private List<String> degrees;
	private List<String> specializations;
	private List<String> departments;
	private Integer yearOfExperience;
	private String profilePicPath;
}
