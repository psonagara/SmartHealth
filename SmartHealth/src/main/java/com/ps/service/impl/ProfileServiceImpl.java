package com.ps.service.impl;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ps.config.props.PathProperties;
import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.dto.SubProfileDTO;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LeaveRequest;
import com.ps.dto.request.PasswordRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.AdminProfileResponse;
import com.ps.dto.response.DoctorProfileResponse;
import com.ps.dto.response.LeaveResponse;
import com.ps.dto.response.PatientProfileResponse;
import com.ps.entity.Admin;
import com.ps.entity.Doctor;
import com.ps.entity.DoctorLeave;
import com.ps.entity.Holiday;
import com.ps.entity.Patient;
import com.ps.entity.SubProfile;
import com.ps.enu.LeaveStatus;
import com.ps.exception.AdminException;
import com.ps.exception.DoctorException;
import com.ps.exception.PatientException;
import com.ps.exception.ProfileException;
import com.ps.mapper.AdminMapper;
import com.ps.mapper.DoctorLeaveMapper;
import com.ps.mapper.DoctorMapper;
import com.ps.mapper.PatientMapper;
import com.ps.repo.AdminRepository;
import com.ps.repo.DoctorLeaveRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.HolidayRepository;
import com.ps.repo.PatientRepository;
import com.ps.repo.SubProfileRepository;
import com.ps.service.IProfileService;
import com.ps.util.CommonUtil;
import com.ps.util.JwtUtil;
import com.ps.util.ProfileUtil;

import jakarta.transaction.Transactional;

/**
 * Service implementation for managing user profiles across different roles
 * (Patient, Doctor, and Admin). This includes viewing and updating profiles,
 * managing profile pictures, changing passwords, handling sub-profiles,
 * applying for leaves, and retrieving leave information.
 * <p>
 * This service enforces role-based access using JWT tokens and throws
 * role-specific exceptions when access is unauthorized.
 * </p>
 *
 * <p><b>Roles supported:</b></p>
 * <ul>
 *     <li>{@link IPatientConstants#PATIENT_ROLE}</li>
 *     <li>{@link IDoctorConstants#DOCTOR_ROLE}</li>
 *     <li>{@link IAdminConstants#ADMIN_ROLE}</li>
 * </ul>
 *
 * <p>
 * Security checks are performed based on the primary role and email extracted
 * from the JWT token via {@link JwtUtil}.
 * </p>
 */
@Service
public class ProfileServiceImpl implements IProfileService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProfileServiceImpl.class);

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private PathProperties pathProperties;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SubProfileRepository subProfileRepository;
	
	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private DoctorLeaveRepository leaveRepository;
	
	@Autowired
	private HolidayRepository holidayRepository;
	
	@Autowired
	private PatientMapper patientMapper;
	
	@Autowired
	private DoctorMapper doctorMapper;
	
	@Autowired
	private DoctorLeaveMapper doctorLeaveMapper;
	
	@Autowired
	private AdminMapper adminMapper;

	/**
     * Retrieves the profile of the currently logged-in patient.
     *
     * @return a {@link PatientProfileResponse} containing patient profile details.
     * @throws PatientException if the user is not a patient or the patient is not found.
     */
	@Override
	public PatientProfileResponse getPatientProfile() {
		if (!JwtUtil.getPrimaryRoleFromToken().equals(IPatientConstants.PATIENT_ROLE))
			throw new PatientException(IExceptionConstants.INVALID_PATIEN_TOKEN_ROLE, HttpStatus.UNAUTHORIZED);
		
		Patient patient = patientRepository.findByEmail(JwtUtil.getEmailFromToken()).orElseThrow(() -> new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
		return patientMapper.toPatientProfileResponse(patient);
	}

	 /**
     * Retrieves the profile of the currently logged-in doctor.
     *
     * @return a {@link DoctorProfileResponse} containing doctor profile details.
     * @throws DoctorException if the user is not a doctor or the doctor is not found.
     */
	@Override
	public DoctorProfileResponse getDoctorProfile() {
		if (!JwtUtil.getPrimaryRoleFromToken().equals(IDoctorConstants.DOCTOR_ROLE))
			throw new DoctorException(IExceptionConstants.INVALID_DOCTOR_TOKEN_ROLE, HttpStatus.UNAUTHORIZED);
		
		Doctor doctor = doctorRepository.findByEmail(JwtUtil.getEmailFromToken()).orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.BAD_REQUEST));
		return doctorMapper.toDoctorProfileResponse(doctor);
	}

	 /**
     * Updates the profile of the currently logged-in patient.
     *
     * @param request the {@link PatientProfileRequest} containing updated patient details.
     * @return true if the update was successful, false otherwise.
     * @throws PatientException if the user is not a patient or the patient is not found.
     */
	@Override
	public boolean updatePatientProfile(PatientProfileRequest request) {
		if (!JwtUtil.getPrimaryRoleFromToken().equals(IPatientConstants.PATIENT_ROLE))
			throw new PatientException(IExceptionConstants.INVALID_PATIEN_TOKEN_ROLE, HttpStatus.UNAUTHORIZED);
		
		Patient patient = patientRepository.findByEmail(JwtUtil.getEmailFromToken()).orElseThrow(() -> new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND));
		patientMapper.updatePatientFromRequest(request, patient);
		return patientRepository.save(patient) != null;
	}

	 /**
     * Updates the profile of the currently logged-in doctor.
     *
     * @param request the {@link DoctorProfileRequest} containing updated doctor details.
     * @return true if the update was successful, false otherwise.
     * @throws DoctorException if the user is not a doctor or the doctor is not found.
     */
	@Override
	public boolean updateDoctorProfile(DoctorProfileRequest request) {
		if (!JwtUtil.getPrimaryRoleFromToken().equals(IDoctorConstants.DOCTOR_ROLE))
			throw new DoctorException(IExceptionConstants.INVALID_DOCTOR_TOKEN_ROLE, HttpStatus.UNAUTHORIZED);
		
		Doctor doctor = doctorRepository.findByEmail(JwtUtil.getEmailFromToken()).orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.BAD_REQUEST));
		doctorMapper.updateDoctorFromRequest(request, doctor);
		return doctorRepository.save(doctor) != null;
	}

	 /**
     * Uploads and saves the profile picture for the logged-in user of the specified role.
     *
     * @param file the profile picture file to be uploaded.
     * @param role the role of the user (Patient, Doctor, Admin).
     * @return true if the picture was successfully uploaded, false otherwise.
     * @throws ProfileException if validation fails or the role does not match the session.
     */
	@Override
	@Transactional
	public boolean uploadProflePic(MultipartFile file, String role) {
		ProfileUtil.validateImageFile(file);
		try {
			if (!JwtUtil.getPrimaryRoleFromToken().equals(role))
				throw new ProfileException(IExceptionConstants.SESSION_MISMATCHED, HttpStatus.BAD_REQUEST);

			int updated;
			String email = JwtUtil.getEmailFromToken();
			switch (role) {
				case IPatientConstants.PATIENT_ROLE:
					Integer patientId = patientRepository.findIdByEmail(email).orElseThrow(() -> new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND));
					String patientFileName = ProfileUtil.getFileName(patientId, pathProperties.getPatientImagePath(), file);
					updated = patientRepository.updateprofilePicPath(patientFileName, email);
					break;
					
				case IDoctorConstants.DOCTOR_ROLE:
					Integer doctorId = doctorRepository.findIdByEmail(email).orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));
					String doctorFileName = ProfileUtil.getFileName(doctorId, pathProperties.getDoctorImagePath(), file);
					updated = doctorRepository.updateprofilePicPath(doctorFileName, email);
					break;
					
				case IAdminConstants.ADMIN_ROLE:
					Integer adminId = adminRepository.findIdByEmail(email).orElseThrow(() -> new AdminException(IExceptionConstants.ADMIN_NOT_FOUND, HttpStatus.BAD_REQUEST));
					String adminFileName = ProfileUtil.getFileName(adminId, pathProperties.getAdminImagePath(), file);
					updated = adminRepository.updateprofilePicPath(adminFileName, email);
					break;
				default:
					throw new ProfileException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
			}
			return updated == 1;
		} catch (IOException exception) {
			LOG.error("IOException in ProfileServiceImpl.uploadProfilePic {}", exception.getMessage(), exception);
			throw new ProfileException(IExceptionConstants.PROFILE_PIC_UPLOAD_FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	 /**
     * Retrieves the profile picture file name for the logged-in user of the given role.
     *
     * @param role the role of the user (Patient, Doctor, Admin).
     * @return the file name of the profile picture, or the default picture if none exists.
     * @throws ProfileException if the role does not match the session.
     */
	@Override
	public String getProfilePicName(String role) {
		if (!JwtUtil.getPrimaryRoleFromToken().equals(role))
			throw new ProfileException(IExceptionConstants.SESSION_MISMATCHED, HttpStatus.BAD_REQUEST);
		
		String profilePicName;
		String email = JwtUtil.getEmailFromToken();
		switch (role) {
			case IPatientConstants.PATIENT_ROLE:
				profilePicName = patientRepository.getProfilePicName(email);
				break;
				
			case IDoctorConstants.DOCTOR_ROLE:
				profilePicName = doctorRepository.getProfilePicName(email);
				break;
				
			case IAdminConstants.ADMIN_ROLE:
				profilePicName = adminRepository.getProfilePicName(email);
				break;
				
			default:
				throw new ProfileException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
		}
		return (profilePicName == null || profilePicName.isBlank()) ? pathProperties.getDefaultProfilePicName() : profilePicName;
	}

	 /**
     * Updates the password for the logged-in user of the given role.
     *
     * @param request the {@link PasswordRequest} containing the new password.
     * @param role the role of the user (Patient, Doctor, Admin).
     * @return true if the password update was successful, false otherwise.
     * @throws ProfileException if the role does not match the session or validation fails.
     */
	@Override
	@Transactional
	public boolean updatePassword(PasswordRequest request, String role) {
		if (!JwtUtil.getPrimaryRoleFromToken().equals(role))
			throw new ProfileException(IExceptionConstants.SESSION_MISMATCHED, HttpStatus.BAD_REQUEST);
		if (request == null)
			throw new ProfileException(IExceptionConstants.PASSWORD_VALIDATION, HttpStatus.BAD_REQUEST);
		String password = request.getPassword();
		if (password == null || password.isBlank() || password.length() < 5)
			throw new ProfileException(IExceptionConstants.PASSWORD_VALIDATION, HttpStatus.BAD_REQUEST);
		
		int updated;
		String email = JwtUtil.getEmailFromToken();
		password = passwordEncoder.encode(password);
		switch (role) {
			case IPatientConstants.PATIENT_ROLE:
				updated = patientRepository.updatePassword(password, email);
				break;
				
			case IDoctorConstants.DOCTOR_ROLE:
				updated = doctorRepository.updatePassword(password, email);
				break;
				
			case IAdminConstants.ADMIN_ROLE:
				updated = adminRepository.updatePassword(password, email);
				break;
				
			default:
				throw new ProfileException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
		}
		return updated == 1;
	}

	/**
     * Retrieves sub-profiles associated with a patient.
     *
     * @param id the patient ID.
     * @return a list of {@link SubProfileDTO} objects representing the patient's sub-profiles.
     * @throws ProfileException if no sub-profiles are found.
     */
	@Override
	public List<SubProfileDTO> getPatientSubProfiles(Integer id) {
		List<SubProfile> subProfiles = subProfileRepository.findByPatientId(id);
		if (subProfiles.isEmpty())
			throw new ProfileException(IExceptionConstants.SUB_PROFILES_NOT_FOUND, HttpStatus.NO_CONTENT);
		
		List<SubProfileDTO> response = subProfiles.stream().map(ProfileUtil::prepareSubProfileDTO).collect(Collectors.toList());
		return response;
	}

	 /**
     * Applies for leave for a doctor, excluding Sundays and configured holidays.
     *
     * @param role the role of the user (must be Doctor).
     * @param request the {@link LeaveRequest} containing leave details.
     * @return true if the leave request was successfully saved, false otherwise.
     * @throws ProfileException if the role is invalid.
     * @throws DoctorException if overlapping leave dates are found or the doctor is not found.
     */
	@Override
	public boolean applyForLeave(String role, LeaveRequest request) {
	    if (!IDoctorConstants.DOCTOR_ROLE.equals(role) || !IDoctorConstants.DOCTOR_ROLE.equals(JwtUtil.getPrimaryRoleFromToken()))
	        throw new ProfileException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
	    
	    LocalDate from = request.getFrom();
	    LocalDate to = request.getTo();
	    String email = JwtUtil.getEmailFromToken();
	    if (leaveRepository.hasOverlappingSlot(email, from, to))
	        throw new DoctorException(IExceptionConstants.LEAVE_BOOKING_OVERLAP, HttpStatus.CONFLICT);
	    
	    // Retrieve Holidays date from DB
	    Set<LocalDate> holidays = holidayRepository.findAll()
	            .stream()
	            .map(Holiday::getHolidayDate)
	            .collect(Collectors.toSet());
	    // Exclude Sundays and Holiday From Applied Leave Days
	    int effectiveDays = (int) Stream.iterate(from, date -> !date.isAfter(to), date -> date.plusDays(1))
	    		.filter(date -> date.getDayOfWeek() != DayOfWeek.SUNDAY && !holidays.contains(date))
	    		.count();
	    Doctor doctor = doctorRepository.findByEmail(email).orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.BAD_REQUEST));
	    DoctorLeave leave = doctorLeaveMapper.toDoctorLeave(request, doctor, effectiveDays);
	    return leaveRepository.save(leave) != null;
	}

	/**
     * Searches for leaves for a doctor based on date range and status.
     *
     * @param requestMap a map containing search parameters: from date, to date, and leave status.
     * @param role the role of the user (must be Doctor).
     * @param pageable the {@link Pageable} object for pagination.
     * @return a map containing the search results and pagination details.
     * @throws ProfileException if the role is invalid.
     */
	@Override
	public Map<String, Object> searchLeaves(Map<String, Object> requestMap, String role, Pageable pageable) {
	    if (!IDoctorConstants.DOCTOR_ROLE.equals(role) || !IDoctorConstants.DOCTOR_ROLE.equals(JwtUtil.getPrimaryRoleFromToken()))
	        throw new ProfileException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
	    
	    String email = JwtUtil.getEmailFromToken();
	    Page<DoctorLeave> pages = leaveRepository.searchLeaves(email, (LocalDate) requestMap.get(IRequestConstants.FROM), (LocalDate) requestMap.get(IRequestConstants.TO), (LeaveStatus) requestMap.get(IRequestConstants.STATUS), pageable);
		List<LeaveResponse> response = pages.getContent().stream().map(doctorLeaveMapper::toLeaveResponse).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(response, pages);
	}

	/**
     * Retrieves the profile of the currently logged-in admin.
     *
     * @return an {@link AdminProfileResponse} containing admin profile details.
     * @throws AdminException if the user is not an admin or the admin is not found.
     */
	@Override
	public AdminProfileResponse getAdminProfile() {
		if (!JwtUtil.getPrimaryRoleFromToken().equals(IAdminConstants.ADMIN_ROLE))
			throw new AdminException(IExceptionConstants.INVALID_ADMIN_TOKEN_ROLE, HttpStatus.UNAUTHORIZED);
		
		Admin admin = adminRepository.findByEmail(JwtUtil.getEmailFromToken()).orElseThrow(() -> new AdminException(IExceptionConstants.ADMIN_NOT_FOUND, HttpStatus.BAD_REQUEST));
		return adminMapper.toAdminProfileResponse(admin);
	}

	 /**
     * Updates the name of the currently logged-in admin.
     *
     * @param name the new name of the admin.
     * @return true if the update was successful, false otherwise.
     * @throws AdminException if the user is not an admin.
     */
	@Override
	@Transactional
	public boolean updateAdminName(String name) {
		if (JwtUtil.getPrimaryRoleFromToken().equals(IAdminConstants.ADMIN_ROLE))		
			return adminRepository.updateName(name, JwtUtil.getEmailFromToken()) == 1;
		else
			throw new AdminException(IExceptionConstants.INVALID_ADMIN_TOKEN_ROLE, HttpStatus.UNAUTHORIZED);
	}
	
}
