package com.ps.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.ps.dto.SubProfileDTO;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LeaveRequest;
import com.ps.dto.request.PasswordRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.AdminProfileResponse;
import com.ps.dto.response.DoctorProfileResponse;
import com.ps.dto.response.PatientProfileResponse;

public interface IProfileService {

	PatientProfileResponse getPatientProfile();
	DoctorProfileResponse getDoctorProfile();
	boolean updatePatientProfile(PatientProfileRequest request);
	boolean updateDoctorProfile(DoctorProfileRequest request);
	boolean uploadProflePic(MultipartFile file, String role);
	String getProfilePicName(String role);
	boolean updatePassword(PasswordRequest request, String role);
	List<SubProfileDTO> getPatientSubProfiles(Integer id);
	boolean applyForLeave(String role, LeaveRequest request);
	Map<String, Object> searchLeaves(Map<String, Object> requestMap, String role, Pageable pageable);
	AdminProfileResponse getAdminProfile();
	boolean updateAdminName(String name);
}
