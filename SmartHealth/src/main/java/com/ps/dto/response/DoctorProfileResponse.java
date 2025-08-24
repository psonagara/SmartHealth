package com.ps.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Specialization;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an doctor profile response.
 * Used to return Doctor profile details (ID, name, email, phone, etc.) 
 * when an Doctor views their profile.
 */
@Data
public class DoctorProfileResponse {

	private Integer id;
	private String name;
	private String email;
	private String phone;
	private LocalDate dob;
	private String gender;
	private List<Degree> degrees;
	private List<Department> departments;
	private List<Specialization> specializations;
	private Integer yearOfExperience;
	private String profilePicPath;
	private String address;
	private String registrationNumber;
}
