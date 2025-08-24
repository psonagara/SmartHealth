package com.ps.rest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IPathConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LeaveRequest;
import com.ps.dto.request.PasswordRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.enu.LeaveStatus;
import com.ps.marker.OnUpdate;
import com.ps.service.IProfileService;
import com.ps.util.CommonUtil;

@RestController
@RequestMapping(IPathConstants.PROFILE_PATH)
public class ProfileRestController {

	@Autowired
	private IProfileService profileService;

	/**
	 * Retrieve profile information like name, email etc. based on role.
	 * 
	 * @param role user's role for whom profile information requested
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_PROFILE)
	public ResponseEntity<?> getProfile(@PathVariable(IRequestConstants.ROLE) String role) {
		if (role.isBlank()) 
			return CommonUtil.prepareResponseWithMessage(IResponseConstants.PROVIDE_ROLE_FOR_PROFILE, HttpStatus.BAD_REQUEST);
		
		switch (role) {
			case IPatientConstants.PATIENT_ROLE:
				return CommonUtil.prepareResponseWithContent(profileService.getPatientProfile(), HttpStatus.OK);
			
			case IDoctorConstants.DOCTOR_ROLE:
				return CommonUtil.prepareResponseWithContent(profileService.getDoctorProfile(), HttpStatus.OK);
	
			case IAdminConstants.ADMIN_ROLE:
				return CommonUtil.prepareResponseWithContent(profileService.getAdminProfile(), HttpStatus.OK);
	
			default:
				return CommonUtil.prepareResponseWithMessage(IResponseConstants.INVALID_ROLE, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Update profile of patient based on data given in input request body.
	 * 
	 * @param request contains updated data of patient
	 * @return
	 */
	@PutMapping(IPathConstants.UPDATE_PATIENT_PROFILE)
	public ResponseEntity<?> updatePatietProfile(@RequestBody @Validated(OnUpdate.class) PatientProfileRequest request) {
		boolean updated = profileService.updatePatientProfile(request);
		String message = updated ? IResponseConstants.PROFILE_UPDATE_SUCCESSFUL : IResponseConstants.PROFILE_UPDATE_FAILED;
		HttpStatus status = updated ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}

	/**
	 * Update profile of doctor based on data given in input request body.
	 * 
	 * @param request contains updated data of doctor
	 * @return
	 */
	@PutMapping(IPathConstants.UPDATE_DOCTOR_PROFILE)
	public ResponseEntity<?> updateDoctorProfile(@RequestBody @Validated(OnUpdate.class) DoctorProfileRequest request) {
		boolean updated = profileService.updateDoctorProfile(request);
		String message = updated ? IResponseConstants.PROFILE_UPDATE_SUCCESSFUL : IResponseConstants.PROFILE_UPDATE_FAILED;
		HttpStatus status = updated ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}
	
	/**
	 * Upload given image file and update profile picture of user based on provided role.
	 * 
	 * @param file new profile picture
	 * @param role user's role
	 * @return
	 */
	@PutMapping(IPathConstants.UPLOAD_PROFILE_PIC)
	public ResponseEntity<?> uploadProfilePicture(@RequestParam(IRequestConstants.FILE) MultipartFile file, @PathVariable(IRequestConstants.ROLE) String role) {
		boolean uploaded = profileService.uploadProflePic(file, role);
		String message = uploaded ? IResponseConstants.PROFILE_PICTURE_UPDATE_SUCCESSFUL : IResponseConstants.PROFILE_PICTURE_UPDATE_FAILED;
		HttpStatus status = uploaded ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}
	
	/**
	 * Retrieve name of profile picture for particular user based on role.
	 * 
	 * @param role user's role
	 * @return
	 */
	@GetMapping(IPathConstants.GET_PROFILE_PICTURE_NAME)
	public ResponseEntity<?> getProfilePicName(@PathVariable(IRequestConstants.ROLE) String role) {
		return CommonUtil.prepareResponseWithContent(profileService.getProfilePicName(role), HttpStatus.OK);
	}
	
	/**
	 * Update password of user based on role.
	 * 
	 * @param passwordRequest contains new password
	 * @param role user's role
	 * @return
	 */
	@PatchMapping(IPathConstants.UPDATE_PASSWORD_PATH)
	public ResponseEntity<?> updatePassword(@RequestBody PasswordRequest passwordRequest, @PathVariable(IRequestConstants.ROLE) String role) {
		boolean updated = profileService.updatePassword(passwordRequest, role);
		String message = updated ? IResponseConstants.PASSWORD_UPDATE_SUCCESSFUL : IResponseConstants.PASSWORD_UPDATE_FAILED;
		HttpStatus status = updated ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}
	
	/**
	 * Retrieve all sub-profiles of a patient.
	 * 
	 * @param id id of patient for which sub-profiles requested
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_PATIENT_SUB_PROFILES_PATH)
	public ResponseEntity<?> getPatientSubProfiles(@RequestParam(IRequestConstants.ID) Integer id) {
		return CommonUtil.prepareResponseWithContent(profileService.getPatientSubProfiles(id), HttpStatus.OK);
	}
	
	/**
	 * Post a leave request with input parameters like date and reason.
	 * 
	 * @param role user's role who post leave (currently doctor only)
	 * @param request contains data to post leave like date and reason
	 * @return
	 */
	@PostMapping(IPathConstants.APPLY_LEAVE_PATH)
	public ResponseEntity<?> applyForLeave(@PathVariable(IRequestConstants.ROLE) String role, @RequestBody @Validated LeaveRequest request) {
		boolean applied = profileService.applyForLeave(role, request);
		String message = applied ? IResponseConstants.LEAVE_APPLY_SUCCESSFUL : IResponseConstants.LEAVE_APPLY_FAILED;
		HttpStatus status = applied ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}
	
	/**
	 * Retrieve leaves with pagination based on given parameters.
	 * User can filter out leaves based on input parameters date and status, however parameters are optional.
	 * 
	 * @param role user's role who post leave (currently doctor only)
	 * @param from date range starting
	 * @param to date rang ending
	 * @param status view leaves of particular status
	 * @param pageable pagination details
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_LEAVE_PATH)
	public ResponseEntity<?> searchLeaves(@PathVariable(IRequestConstants.ROLE) String role, @RequestParam(name = IRequestConstants.FROM, required = false) LocalDate from, 
			@RequestParam(name = IRequestConstants.TO, required = false) LocalDate to, @RequestParam(name = IRequestConstants.STATUS, required = false) LeaveStatus status,
			Pageable pageable) {
		
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.FROM, from);
		requestMap.put(IRequestConstants.TO, to);
		requestMap.put(IRequestConstants.STATUS, status);
		pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		return CommonUtil.prepareResponseWithContent(profileService.searchLeaves(requestMap, role, pageable), HttpStatus.OK);
	}
	
	/**
	 * Update profile of doctor based on data given in input request body.
	 * 
	 * @param request contains updated data of admin
	 * @return
	 */
	@PatchMapping(IPathConstants.UPDATE_ADMIN_NAME)
	public ResponseEntity<?> updateAdminName(@RequestBody Map<String, Object> request) {
		String name = request.get(IRequestConstants.NAME) == null ? null : (String) request.get(IRequestConstants.NAME);
		String message;
		HttpStatus status;
		if (name == null || name.isBlank()) {
			message = IResponseConstants.PROVIDE_NAME;
			status = HttpStatus.BAD_REQUEST;
		} else {
			boolean updated = profileService.updateAdminName(name);
			message = updated ? IResponseConstants.NAME_UPDATE_SUCCESSFUL : IResponseConstants.NAME_UPDATE_FAIL;
			status = updated ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;			
		}
		return CommonUtil.prepareResponseWithMessage(message, status);
	}
}
