package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.ps.service.IAvailabilityService;
import com.ps.util.JwtUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * This test class contains unit test case for
 * methods of {@link AuthServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	@Mock
	private PatientRepository patientRepository;
	
	@Mock
	private PatientMapper patientMapper;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private PathProperties pathProperties;
	
	@Mock
	private JwtUtil jwtUtil;
	
	@Mock
	private DoctorRepository doctorRepository;
	
	@Mock
	private DoctorMapper doctorMapper;
	
	@Mock
	private IAvailabilityService availabilityService;
	
	@Mock
	private AdminRepository adminRepository;
	
	@Mock
	private AdminMapper adminMapper;
	
	/**
	 * Test {@link AuthServiceImpl#registerPatient(PatientProfileRequest)}.
	 */
	@Test
	void testRegisterPatient() {
		String defaultImage = "defaultPic.jpg";
		Patient patient = TestDataUtil.getPatient();
		PatientProfileRequest request = getPatientProfileRequest();
		
		when(patientRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(new ArrayList<>());
		when(patientMapper.toPatient(request)).thenReturn(TestConverterUtil.toPatient(request));
		when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$d6o/wJABjSHVJa3At9Dhpe2x6x5P/skQg2HgFWw2MnUNKlOqt0jCK");
		when(pathProperties.getDefaultProfilePicName()).thenReturn(defaultImage);
		when(patientRepository.save((Patient) any())).thenReturn(patient);
		// Successfully registered
		assertTrue(authService.registerPatient(request));

		// Failed to register
		when(patientRepository.save((Patient) any())).thenReturn(null);
		assertFalse(authService.registerPatient(request));
		
		// Patient is already exist with given email or phone or both
		when(patientRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(List.of(patient));
		PatientException patientException = assertThrows(PatientException.class, () -> {
			authService.registerPatient(request);
		});
		assertEquals(IExceptionConstants.PATIENT_ALREADY_EXIST, patientException.getMessage());
	}
	
	/**
	 * Test {@link AuthServiceImpl#loginPatient(LoginRequest)}.
	 */
	@Test
	void testLoginPatient() {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbWFuLnNoYXJtYUBnbWFpbC5jb20iLCJSb2xlIjpbInBhdGllbnQiXSwiaWF0IjoxNzU1MDc2MzQxLCJleHAiOjE3NTUxMjY3NDF9.D5x2RsKzDJl4bSsQkueLuho-C1Pcj9myEjUPIr1rC4WrezXmVkykCJ3qfAhkivlji8aAt8ah9lD3nl4mBZ-cJA";
		Patient patient = TestDataUtil.getPatient();
		List<Patient> patients = List.of(patient);
		String user = TestDataUtil.getPatientEmail();
		LoginRequest request = new LoginRequest();
		request.setUser(user);
		request.setPassword("12345");
		
		when(patientRepository.findByEmailOrPhone(user, user)).thenReturn(patients);
		when(passwordEncoder.matches(request.getPassword(), patient.getPassword())).thenReturn(true);
		when(jwtUtil.generateToken(patient.getEmail(), patient.getRoles())).thenReturn(token);
		
		// Successful login
		LoginResponse loginResponse = authService.loginPatient(request);
		assertEquals(token, loginResponse.getToken());
		assertEquals(IPatientConstants.PATIENT_ROLE, loginResponse.getRole());

		// When input password is not correct
		when(passwordEncoder.matches(request.getPassword(), patient.getPassword())).thenReturn(false);
		PatientException patientException = assertThrows(PatientException.class, () -> {
			authService.loginPatient(request);
		});
		assertEquals(IExceptionConstants.INCORRECT_PASSWORD, patientException.getMessage());

		// When patient is inactive
		patients.get(0).setIsActive(false);
		patientException = assertThrows(PatientException.class, () -> {
			authService.loginPatient(request);
		});
		assertEquals(IExceptionConstants.INACTIVE_PROFILE, patientException.getMessage());
		
		// exception scenario where if due to data corruption or due to data migration etc. multiple record found for given user
		when(patientRepository.findByEmailOrPhone(user, user)).thenReturn(List.of(patient, patient));
		patientException = assertThrows(PatientException.class, () -> {
			authService.loginPatient(request);
		});
		assertEquals(IExceptionConstants.MULTIPLE_PATIENT_FOUND + user, patientException.getMessage());

		// scenario where no patient found for given user
		when(patientRepository.findByEmailOrPhone(user, user)).thenReturn(new ArrayList<>());
		patientException = assertThrows(PatientException.class, () -> {
			authService.loginPatient(request);
		});
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
	}
	
	/**
	 * Test {@link AuthServiceImpl#registerDoctor(DoctorProfileRequest)}.
	 */
	@Test
	void testRegisterDoctor() {
		String defaultImage = "defaultPic.jpg";
		Doctor doctor = TestDataUtil.getDoctor();
		DoctorProfileRequest request = getDoctorProfileRequest();
		
		when(doctorRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(new ArrayList<>());
		when(doctorMapper.toDoctor(request)).thenReturn(TestConverterUtil.toDoctor(request));
		when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$qba/yfe5RMNm7/.c2uKdFul9QiZIBEB7HugUQXH6nKLVMW.iU2bFm");
		when(pathProperties.getDefaultProfilePicName()).thenReturn(defaultImage);
		when(doctorRepository.save((Doctor) any())).thenReturn(doctor);
		when(availabilityService.setDefaultAGPreference((Doctor) any())).thenReturn(true);
		// Successfully registered
		assertTrue(authService.registerDoctor(request));
		
		// Successfully registered but failed to save default availability generation preference
		when(availabilityService.setDefaultAGPreference((Doctor) any())).thenReturn(false);
		assertFalse(authService.registerDoctor(request));

		// Failed to register doctor
		when(doctorRepository.save((Doctor) any())).thenReturn(null);
		assertFalse(authService.registerDoctor(request));
		
		// Doctor is already exist with given email or phone or both
		when(doctorRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(List.of(doctor));
		DoctorException doctorException = assertThrows(DoctorException.class, () -> {
			authService.registerDoctor(request);
		});
		assertEquals(IExceptionConstants.DOCTOR_ALREADY_EXIST, doctorException.getMessage());
	}
	
	/**
	 * Test {@link AuthServiceImpl#loginDoctor(LoginRequest)}.
	 */
	@Test
	void testLoginDoctor() {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbmphbGkuc2hhcm1hQHNtYXJ0aGVhbHRoLmluIiwiUm9sZSI6WyJkb2N0b3IiXSwiaWF0IjoxNzU1MDc5OTYxLCJleHAiOjE3NTUxMzAzNjF9.jBebKFzje9ZcbMNFcQxIa8f9iONQnn-61JNI3l4BeqvMwy1aaBXuQv_5YRYS_d41w6VeyMsiKIphHdVaqmctCg";
		Doctor doctor = TestDataUtil.getDoctor();
		List<Doctor> doctors = List.of(doctor);
		String user = TestDataUtil.getDoctorEmail();
		LoginRequest request = new LoginRequest();
		request.setUser(user);
		request.setPassword("12345");
		
		when(doctorRepository.findByEmailOrPhone(user, user)).thenReturn(doctors);
		when(passwordEncoder.matches(request.getPassword(), doctor.getPassword())).thenReturn(true);
		when(jwtUtil.generateToken(doctor.getEmail(), doctor.getRoles())).thenReturn(token);
		
		// Successful login
		LoginResponse loginResponse = authService.loginDoctor(request);
		assertEquals(token, loginResponse.getToken());
		assertEquals(IDoctorConstants.DOCTOR_ROLE, loginResponse.getRole());

		// When input password is not correct
		when(passwordEncoder.matches(request.getPassword(), doctor.getPassword())).thenReturn(false);
		DoctorException doctorException = assertThrows(DoctorException.class, () -> {
			authService.loginDoctor(request);
		});
		assertEquals(IExceptionConstants.INCORRECT_PASSWORD, doctorException.getMessage());

		// When doctor is inactive
		doctors.get(0).setIsActive(false);
		doctorException = assertThrows(DoctorException.class, () -> {
			authService.loginDoctor(request);
		});
		assertEquals(IExceptionConstants.INACTIVE_PROFILE, doctorException.getMessage());
		
		// exception scenario where if due to data corruption or due to data migration etc. multiple record found for given user
		when(doctorRepository.findByEmailOrPhone(user, user)).thenReturn(List.of(doctor, doctor));
		doctorException = assertThrows(DoctorException.class, () -> {
			authService.loginDoctor(request);
		});
		assertEquals(IExceptionConstants.MULTIPLE_DOCTOR_FOUND + user, doctorException.getMessage());

		// scenario where no doctor found for given user
		when(doctorRepository.findByEmailOrPhone(user, user)).thenReturn(new ArrayList<>());
		doctorException = assertThrows(DoctorException.class, () -> {
			authService.loginDoctor(request);
		});
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
	}
	
	/**
	 * Test {@link AuthServiceImpl#registerAdmin(AdminProfileRequest)}.
	 */
	@Test
	void restRegisterAdmin() {
		String defaultImage = "defaultPic.jpg";
		Admin admin = TestDataUtil.getAdmin();
		AdminProfileRequest request = getAdminProfileRequest();
		
		when(adminRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(new ArrayList<>());
		when(adminMapper.toAdmin(request)).thenReturn(TestConverterUtil.toAdmin(request));
		when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$C.refJDMVi2i4vE.Ds19r.Iw4T6pYXaAPm9K5V/V/RD8SBhdvQReK");
		when(pathProperties.getDefaultProfilePicName()).thenReturn(defaultImage);
		when(adminRepository.save((Admin) any())).thenReturn(admin);
		// Successfully registered
		assertTrue(authService.registerAdmin(request));

		// Failed to register admin
		when(adminRepository.save((Admin) any())).thenReturn(null);
		assertFalse(authService.registerAdmin(request));
		
		// Admin is already exist with given email or phone or both
		when(adminRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())).thenReturn(List.of(admin));
		AdminException adminException = assertThrows(AdminException.class, () -> {
			authService.registerAdmin(request);
		});
		assertEquals(IExceptionConstants.ADMIN_ALREADY_EXIST, adminException.getMessage());
	}
	
	/**
	 * Test {@link AuthServiceImpl#loginAdmin(LoginRequest)}.
	 */
	@Test
	void testLoginAdmin() {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBzbWFydGhlYWx0aC5pbiIsIlJvbGUiOlsiYWRtaW4iXSwiaWF0IjoxNzU1MDgwNjQ0LCJleHAiOjE3NTUxMzEwNDR9.-OriDvl6hlVASmKqij-Of3yXlaZCa3uZOfJGfm7MSxSDV0Bjvlq5N3Goa0gnREVGwgUem8JjNJ2kQ-UPJvKuvg";
		Admin admin = TestDataUtil.getAdmin();
		List<Admin> admins = List.of(admin);
		String user = TestDataUtil.getAdminEmail();
		LoginRequest request = new LoginRequest();
		request.setUser(user);
		request.setPassword("12345");
		
		when(adminRepository.findByEmailOrPhone(user, user)).thenReturn(admins);
		when(passwordEncoder.matches(request.getPassword(), admin.getPassword())).thenReturn(true);
		when(jwtUtil.generateToken(admin.getEmail(), admin.getRoles())).thenReturn(token);
		
		// Successful login
		LoginResponse loginResponse = authService.loginAdmin(request);
		assertEquals(token, loginResponse.getToken());
		assertEquals(IAdminConstants.ADMIN_ROLE, loginResponse.getRole());

		// When input password is not correct
		when(passwordEncoder.matches(request.getPassword(), admin.getPassword())).thenReturn(false);
		AdminException adminException = assertThrows(AdminException.class, () -> {
			authService.loginAdmin(request);
		});
		assertEquals(IExceptionConstants.INCORRECT_PASSWORD, adminException.getMessage());

		// When admin is inactive
		admins.get(0).setIsActive(false);
		adminException = assertThrows(AdminException.class, () -> {
			authService.loginAdmin(request);
		});
		assertEquals(IExceptionConstants.INACTIVE_PROFILE, adminException.getMessage());
		
		// exception scenario where if due to data corruption or due to data migration etc. multiple record found for given user
		when(adminRepository.findByEmailOrPhone(user, user)).thenReturn(List.of(admin, admin));
		adminException = assertThrows(AdminException.class, () -> {
			authService.loginAdmin(request);
		});
		assertEquals(IExceptionConstants.MULTIPLE_ADMIN_FOUND + user, adminException.getMessage());

		// scenario where no admin found for given user
		when(adminRepository.findByEmailOrPhone(user, user)).thenReturn(new ArrayList<>());
		adminException = assertThrows(AdminException.class, () -> {
			authService.loginAdmin(request);
		});
		assertEquals(IExceptionConstants.ADMIN_NOT_FOUND, adminException.getMessage());
	}
	
	private PatientProfileRequest getPatientProfileRequest() { 
		PatientProfileRequest patientProfileRequest = new PatientProfileRequest();
		patientProfileRequest.setEmail(TestDataUtil.getPatientEmail());
		patientProfileRequest.setPhone("9876543210");
		patientProfileRequest.setName("Sm Ca");
		patientProfileRequest.setPassword("12345");
		return patientProfileRequest;
	}
	
	private DoctorProfileRequest getDoctorProfileRequest() {
		DoctorProfileRequest doctorProfileRequest = new DoctorProfileRequest();
		doctorProfileRequest.setEmail(TestDataUtil.getDoctorEmail());
		doctorProfileRequest.setPhone("0123456789");
		doctorProfileRequest.setName("Sh Do");
		doctorProfileRequest.setPassword("12345");
		return doctorProfileRequest;
	}
	
	private AdminProfileRequest getAdminProfileRequest() {
		AdminProfileRequest adminProfileRequest = new AdminProfileRequest();
		adminProfileRequest.setEmail(TestDataUtil.getAdminEmail());
		adminProfileRequest.setPhone("1472583690");
		adminProfileRequest.setName("Admin");
		adminProfileRequest.setPassword("12345");
		return adminProfileRequest;
	}

}
