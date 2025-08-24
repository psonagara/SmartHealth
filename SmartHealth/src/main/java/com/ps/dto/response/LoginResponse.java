package com.ps.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a Login response.
 * Used to return authentication details (token and role) for
 * Admin, Patient, or Doctor after successful login.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

	private String token;
	private String role;
}
