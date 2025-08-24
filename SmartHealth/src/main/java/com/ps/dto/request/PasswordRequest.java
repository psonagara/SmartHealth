package com.ps.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing password change request.
 * Used for change of password for admin, patient, or doctor.
 */
@Getter
@Setter
public class PasswordRequest {

	private String password;
}
