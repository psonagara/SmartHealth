package com.ps.dto.response;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an admin profile response.
 * Used to return Admin profile details (ID, name, email, phone, and profile picture path) 
 * when an Admin views their profile.
 */
@Data
public class AdminProfileResponse {

	private Integer id;
	private String name;
	private String email;
	private String phone;
	private String profilePicPath;
}
