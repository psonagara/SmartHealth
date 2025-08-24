package com.ps.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ps.dto.SubProfileDTO;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an Admin Patient Search response.
 * Used to return detailed patient profile information when an Admin searches for or views patient details.
 * Includes sub profile details via SubProfileDTO and excludes null fields in JSON.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APSResponse {

	private Integer id;
	private String name;
	private String email;
	private String phone;
	private String dob;
	private String gender;
	private Double height;
	private Integer weight;
	private String profilePicPath;
	private List<SubProfileDTO> subProfiles;
	private Boolean profileComplete;
	private Boolean isActive;
	private LocalDateTime creationTime;
	private LocalDateTime updationTime;
}
