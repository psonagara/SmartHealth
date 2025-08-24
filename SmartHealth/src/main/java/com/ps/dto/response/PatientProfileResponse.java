package com.ps.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an patient profile response.
 * Used to return patient profile details (ID, name, email, phone, etc.) 
 * when an patient views their profile.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientProfileResponse {

	private Integer id;
	private String name;
	private String email;
	private String phone;
	private LocalDate dob;
	private String gender;
	private Double height;
	private Integer weight;
	private String profilePicPath;
}
