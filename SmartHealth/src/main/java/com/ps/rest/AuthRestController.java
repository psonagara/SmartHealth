package com.ps.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constants.IPathConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.request.AdminProfileRequest;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.marker.OnRegister;
import com.ps.service.IAuthService;
import com.ps.util.CommonUtil;

import jakarta.validation.Valid;

/**
 * REST Controller for authentication-related APIs in the application.
 * Handles endpoints for user registration, login, and other auth operations.
 * Also APIs fall under this REST controller doesn't require authentication.
 */
@RestController
@RequestMapping(IPathConstants.AUTH_PATH)
public class AuthRestController {
	
	@Autowired
	private IAuthService authService;

	/**
     * Registers a new patient based on the provided request data.
     *
     * @param request The validated PatientProfileRequest containing patient details.
     * @return ResponseEntity with success or failure message and appropriate HTTP status.
     */
	@PostMapping(IPathConstants.REGISTER_PATIENT)
	public ResponseEntity<?> registerPatient(@RequestBody @Validated(OnRegister.class) PatientProfileRequest request) {
		boolean isRegistered = authService.registerPatient(request);
		String message = isRegistered ? IResponseConstants.PATIENT_REGISTER_SUCCESSFUL : IResponseConstants.PATIENT_REGISTER_FAILED;
		HttpStatus statusCode = isRegistered ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, statusCode);
	}
	
	/**
     * Authenticates a patient and returns a login response with a JWT token.
     *
     * @param request The validated LoginRequest containing user credentials.
     * @return ResponseEntity with LoginResponse and success message.
     */
	@PostMapping(IPathConstants.LOGIN_PATIENT)
	public ResponseEntity<?> loginPatient(@RequestBody @Valid LoginRequest request) {
		return CommonUtil.prepareResponse(authService.loginPatient(request), IResponseConstants.PATIENT_LOGIN_SUCCESSFUL, HttpStatus.OK);
	}
	
	 /**
     * Registers a new doctor based on the provided request data, with a time-based cutoff.
     *
     * @param request The validated DoctorProfileRequest containing doctor details.
     * @return ResponseEntity with success or failure message and appropriate HTTP status.
     */
	@PostMapping(IPathConstants.REGISTER_DOCTOR)
	public ResponseEntity<?> registerDoctor(@RequestBody @Validated(OnRegister.class) DoctorProfileRequest request) {
		boolean isRegistered = authService.registerDoctor(request);
		String message = isRegistered ? IResponseConstants.DOCTOR_REGISTER_SUCCESSFUL : IResponseConstants.DOCTOR_REGISTER_FAILED;
		HttpStatus statusCode = isRegistered ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, statusCode);
	}
	
	/**
     * Authenticates a doctor and returns a login response with a JWT token.
     *
     * @param request The validated LoginRequest containing user credentials.
     * @return ResponseEntity with LoginResponse and success message.
     */
	@PostMapping(IPathConstants.LOGIN_DOCTOR)
	public ResponseEntity<?> loginDoctor(@RequestBody @Valid LoginRequest request) {
		return CommonUtil.prepareResponse(authService.loginDoctor(request), IResponseConstants.DOCTOR_LOGIN_SUCCESSFUL, HttpStatus.OK);
	}
	
	/**
     * Registers a new admin based on the provided request data.
     *
     * @param request The validated AdminProfileRequest containing admin details.
     * @return ResponseEntity with success or failure message and appropriate HTTP status.
     */
	@PostMapping(IPathConstants.REGISTER_ADMIN)
	public ResponseEntity<?> registerAdmin(@RequestBody @Valid AdminProfileRequest request) {
		boolean isRegistered = authService.registerAdmin(request);
		String message = isRegistered ? IResponseConstants.ADMIN_REGISTER_SUCCESSFUL : IResponseConstants.ADMIN_REGISTER_FAILED;
		HttpStatus statusCode = isRegistered ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, statusCode);
	}
	
	/**
     * Authenticates an admin and returns a login response with a JWT token.
     *
     * @param request The validated LoginRequest containing user credentials.
     * @return ResponseEntity with LoginResponse and success message.
     */
	@PostMapping(IPathConstants.LOGIN_ADMIN)
	public ResponseEntity<?> loginAdmin(@RequestBody @Valid LoginRequest request) {
		return CommonUtil.prepareResponse(authService.loginAdmin(request), IResponseConstants.ADMIN_LOGIN_SUCCESSFUL, HttpStatus.OK);
	}
}
