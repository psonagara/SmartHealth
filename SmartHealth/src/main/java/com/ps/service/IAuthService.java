package com.ps.service;

import com.ps.dto.request.AdminProfileRequest;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.LoginResponse;

public interface IAuthService {

	boolean registerPatient(PatientProfileRequest request);
	LoginResponse loginPatient(LoginRequest request);
	boolean registerDoctor(DoctorProfileRequest request);
	LoginResponse loginDoctor(LoginRequest request);
	boolean registerAdmin(AdminProfileRequest request);
	LoginResponse loginAdmin(LoginRequest request);
}
