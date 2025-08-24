package com.ps.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ps.config.props.PathProperties;
import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.dto.request.AdminProfileRequest;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.LoginResponse;
import com.ps.entity.Admin;
import com.ps.entity.Doctor;
import com.ps.entity.Patient;
import com.ps.exception.AdminException;
import com.ps.exception.DoctorException;
import com.ps.exception.PatientException;
import com.ps.mapper.AdminMapper;
import com.ps.mapper.DoctorMapper;
import com.ps.mapper.PatientMapper;
import com.ps.repo.AdminRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.PatientRepository;
import com.ps.service.IAuthService;
import com.ps.service.IAvailabilityService;
import com.ps.util.JwtUtil;

import jakarta.transaction.Transactional;

/**
 * Implementation of the authentication service interface.
 * Handles registration, login, and related operations for patients, doctors, and admins.
 */
@Service
public class AuthServiceImpl implements IAuthService {

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private IAvailabilityService availabilityService;

	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private PatientMapper patientMapper;
	
	@Autowired
	private DoctorMapper doctorMapper;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private PathProperties pathProperties;

	/**
     * Registers a new patient based on the provided request data.
     *
     * @param request The validated PatientProfileRequest containing patient details.
     * @return true if registration succeeds, false otherwise.
     * @throws PatientException If a patient with the same email or phone already exists.
     */
	@Override
	public boolean registerPatient(PatientProfileRequest request) {
		List<Patient> existingPatients = patientRepository.findByEmailOrPhone(request.getEmail(), request.getPhone());
		if (!existingPatients.isEmpty())
			throw new PatientException(IExceptionConstants.PATIENT_ALREADY_EXIST, HttpStatus.CONFLICT);
		
		Patient patient = patientMapper.toPatient(request);
		patient.setPassword(passwordEncoder.encode(request.getPassword()));
		patient.setRoles(Set.of(IPatientConstants.PATIENT_ROLE));
		patient.setProfilePicPath(pathProperties.getDefaultProfilePicName());
		patient = patientRepository.save(patient);
		return patient != null;
	}

	/**
     * Authenticates a patient and returns a login response with a JWT token.
     *
     * @param request The validated LoginRequest containing user credentials.
     * @return LoginResponse with token and role.
     * @throws PatientException If patient is not found, inactive, or password is incorrect.
     */
	@Override
	public LoginResponse loginPatient(LoginRequest request) {
		String user = request.getUser().trim();
		List<Patient> patients = patientRepository.findByEmailOrPhone(user, user);
		if (patients.isEmpty())
			throw new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND);
		if (patients.size() > 1)
			throw new PatientException(IExceptionConstants.MULTIPLE_PATIENT_FOUND + user, HttpStatus.INTERNAL_SERVER_ERROR);			

		Patient patient = patients.get(0);
		if (!patient.getIsActive())
			throw new PatientException(IExceptionConstants.INACTIVE_PROFILE, HttpStatus.LOCKED);
		if (!passwordEncoder.matches(request.getPassword(), patient.getPassword()))
			throw new PatientException(IExceptionConstants.INCORRECT_PASSWORD, HttpStatus.UNAUTHORIZED);

		return new LoginResponse(jwtUtil.generateToken(patient.getEmail(), patient.getRoles()), IPatientConstants.PATIENT_ROLE);
	}

	/**
     * Registers a new doctor based on the provided request data.
     *
     * @param request The validated DoctorProfileRequest containing doctor details.
     * @return true if registration succeeds, false otherwise.
     * @throws DoctorException If a doctor with the same email or phone already exists.
     */
	@Override
	@Transactional
	public boolean registerDoctor(DoctorProfileRequest request) {
		List<Doctor> existingDoctor = doctorRepository.findByEmailOrPhone(request.getEmail(), request.getPhone());
		if (!existingDoctor.isEmpty())
			throw new DoctorException(IExceptionConstants.DOCTOR_ALREADY_EXIST, HttpStatus.CONFLICT);
		
		Doctor doctor = doctorMapper.toDoctor(request);
		doctor.setPassword(passwordEncoder.encode(request.getPassword()));
		doctor.setRoles(Set.of(IDoctorConstants.DOCTOR_ROLE));
		doctor.setProfilePicPath(pathProperties.getDefaultProfilePicName());
		doctor = doctorRepository.save(doctor);
		if (doctor != null) {
			return availabilityService.setDefaultAGPreference(doctor);
		}
		return false;
	}

	/**
     * Authenticates a doctor and returns a login response with a JWT token.
     *
     * @param request The validated LoginRequest containing user credentials.
     * @return LoginResponse with token and role.
     * @throws DoctorException If doctor is not found, inactive, or password is incorrect.
     */
	@Override
	public LoginResponse loginDoctor(LoginRequest request) {
		String user = request.getUser().trim();
		List<Doctor> doctors = doctorRepository.findByEmailOrPhone(user, user);
		if (doctors.isEmpty())
			throw new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND);
		if (doctors.size() > 1)		
			throw new DoctorException(IExceptionConstants.MULTIPLE_DOCTOR_FOUND + user, HttpStatus.NOT_FOUND);
		
		Doctor doctor = doctors.get(0);
		if (!doctor.getIsActive())
			throw new DoctorException(IExceptionConstants.INACTIVE_PROFILE, HttpStatus.LOCKED);
		if (!passwordEncoder.matches(request.getPassword(), doctor.getPassword()))
			throw new DoctorException(IExceptionConstants.INCORRECT_PASSWORD, HttpStatus.UNAUTHORIZED);
		
		return new LoginResponse(jwtUtil.generateToken(doctor.getEmail(), doctor.getRoles()), IDoctorConstants.DOCTOR_ROLE);
	}

	/**
     * Registers a new admin based on the provided request data.
     *
     * @param request The validated AdminProfileRequest containing admin details.
     * @return true if registration succeeds, false otherwise.
     * @throws AdminException If an admin with the same email or phone already exists.
     */
	@Override
	public boolean registerAdmin(AdminProfileRequest request) {
		List<Admin> existingAdmins = adminRepository.findByEmailOrPhone(request.getEmail(), request.getPhone());
		if (!existingAdmins.isEmpty())
			throw new AdminException(IExceptionConstants.ADMIN_ALREADY_EXIST, HttpStatus.CONFLICT);
		
		Admin admin = adminMapper.toAdmin(request);
		admin.setPassword(passwordEncoder.encode(request.getPassword()));
		admin.setRoles(Set.of(IAdminConstants.ADMIN_ROLE));
		admin.setProfilePicPath(pathProperties.getDefaultProfilePicName());
		admin = adminRepository.save(admin);
		return admin != null;
	}

	/**
     * Authenticates an admin and returns a login response with a JWT token.
     *
     * @param request The validated LoginRequest containing user credentials.
     * @return LoginResponse with token and role.
     * @throws AdminException If admin is not found, inactive, or password is incorrect.
     */
	@Override
	public LoginResponse loginAdmin(LoginRequest request) {
		String user = request.getUser().trim();
		List<Admin> list = adminRepository.findByEmailOrPhone(user, user);
		if (list.isEmpty())
			throw new AdminException(IExceptionConstants.ADMIN_NOT_FOUND, HttpStatus.NOT_FOUND);
		if (list.size() > 1)
			throw new AdminException(IExceptionConstants.MULTIPLE_ADMIN_FOUND + user, HttpStatus.CONFLICT);

		Admin admin = list.get(0);
		if (!admin.getIsActive())
			throw new AdminException(IExceptionConstants.INACTIVE_PROFILE, HttpStatus.LOCKED);
		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword()))
			throw new AdminException(IExceptionConstants.INCORRECT_PASSWORD, HttpStatus.UNAUTHORIZED);	
		
		return new LoginResponse(jwtUtil.generateToken(admin.getEmail(), admin.getRoles()), IAdminConstants.ADMIN_ROLE);
	}
}
