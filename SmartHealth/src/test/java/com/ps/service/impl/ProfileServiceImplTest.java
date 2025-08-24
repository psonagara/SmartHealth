package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.ps.config.props.PathProperties;
import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
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
import com.ps.util.JwtUtil;
import com.ps.util.ProfileUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * This class contains unit test methods for testing
 * methods of {@link ProfileServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {
	
	@InjectMocks
	private ProfileServiceImpl profileService;
	
	@Mock
	private PatientRepository patientRepository;
	
	private MockedStatic<JwtUtil> jwtUtilMock;
	
	@Mock
	private PatientMapper patientMapper;

	@Mock
	private DoctorRepository doctorRepository;
	
	@Mock
	private DoctorMapper doctorMapper;
	
	private MockedStatic<ProfileUtil> profileUtilMock;
	
	@Mock
	private PathProperties pathProperties;
	
	@Mock
	private AdminRepository adminRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private SubProfileRepository subProfileRepository;
	
	@Mock
	private DoctorLeaveRepository leaveRepository;
	
	@Mock
	private HolidayRepository holidayRepository;
	
	@Mock
	private DoctorLeaveMapper doctorLeaveMapper;
	
	@Mock
	private AdminMapper adminMapper;
	
	@BeforeEach
	void setUp() throws Exception {
		jwtUtilMock = Mockito.mockStatic(JwtUtil.class);
		profileUtilMock = Mockito.mockStatic(ProfileUtil.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
	}

	@AfterEach
	void tearDown() throws Exception {
		jwtUtilMock.close();
		profileUtilMock.close();
	}

	/**
	 * Test {@code getPatientProfile} method of {@link ProfileServiceImpl}.
	 * Success scenario is tested.
	 */
	@Test
	void testGetPatientProfileSuccess() {
		
		String email = TestDataUtil.getPatientEmail();
		Patient patient = TestDataUtil.getPatient();
		PatientProfileResponse patientProfileResponse = TestConverterUtil.toPatientProfileResponse(patient);
		
		mockJwt(IPatientConstants.PATIENT_ROLE, email);
		when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
		when(patientMapper.toPatientProfileResponse(patient)).thenReturn(patientProfileResponse);
		
		assertEquals(patientProfileResponse, profileService.getPatientProfile());
	}
	
	/**
	 * Test {@code getPatientProfile} method of {@link ProfileServiceImpl}.
	 * Exception scenario tested where patient email id is not registered.
	 */
	@Test
	void testGetPatientProfilePatientNotFound() {
		String email = TestDataUtil.getPatientEmail();
		
		mockJwt(IPatientConstants.PATIENT_ROLE, email);
		when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		PatientException patientException = assertThrows(PatientException.class, ()-> {
			profileService.getPatientProfile();
		});
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
	}
	
	/**
	 * Test {@code getPatientProfile} method of {@link ProfileServiceImpl}.
	 * Exception scenario tested where logined user is not patient.
	 */
	@Test
	void testGetPatientProfileInvalidRole() {
		
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IDoctorConstants.DOCTOR_ROLE);
		
		PatientException patientException = assertThrows(PatientException.class, ()-> {
			profileService.getPatientProfile();
		});
		assertEquals(IExceptionConstants.INVALID_PATIEN_TOKEN_ROLE, patientException.getMessage());
	}
	
	/**
	 * Test {@code getDoctorProfile} method of {@link ProfileServiceImpl}.
	 * Success scenario is tested.
	 */
	@Test
	void testGetDoctorProfileSuccess() {
		
		String email = TestDataUtil.getDoctorEmail();
		Doctor doctor = TestDataUtil.getDoctor();
		DoctorProfileResponse doctorProfileResponse = TestConverterUtil.toDoctorProfileResponse(doctor);
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
		when(doctorMapper.toDoctorProfileResponse(doctor)).thenReturn(doctorProfileResponse);
		
		assertEquals(doctorProfileResponse, profileService.getDoctorProfile());
	}
	
	/**
	 * Test {@code getDoctorProfile} method of {@link ProfileServiceImpl}.
	 * Exception scenario tested where doctor email id is not registered.
	 */
	@Test
	void testGetDoctorProfileDoctorNotFound() {
		String email = TestDataUtil.getDoctorEmail();
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		DoctorException doctorException = assertThrows(DoctorException.class, ()-> {
			profileService.getDoctorProfile();
		});
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
	}
	
	/**
	 * Test {@code getDoctorProfile} method of {@link ProfileServiceImpl}.
	 * Exception scenario tested where logined user is not doctor.
	 */
	@Test
	void testGetDoctorProfileInvalidRole() {
		
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IPatientConstants.PATIENT_ROLE);
		
		DoctorException doctorException = assertThrows(DoctorException.class, ()-> {
			profileService.getDoctorProfile();
		});
		assertEquals(IExceptionConstants.INVALID_DOCTOR_TOKEN_ROLE, doctorException.getMessage());
	}
	

	/**
	 * Test {@code updatePatientProfile} method of {@link ProfileServiceImpl}.
	 * Cover cases for successful method execution with sub case 
	 * 1) Profile Update Successful
	 * 2) Profile Update Failed
	 * 
	 */
	@Test
	void testUpdatePatientProfile() {
		
		String email = TestDataUtil.getPatientEmail();
		Patient patient = TestDataUtil.getPatient();
		PatientProfileRequest patientProfileRequest = new PatientProfileRequest();
		
		mockJwt(IPatientConstants.PATIENT_ROLE, email);
		when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
		doNothing().when(patientMapper).updatePatientFromRequest(patientProfileRequest, patient);
		when(patientRepository.save(patient)).thenReturn(patient);
		
		// Case when profile update successful
		assertTrue(profileService.updatePatientProfile(patientProfileRequest));
		
		// Case when profile update failed
		when(patientRepository.save(patient)).thenReturn(null);
		assertFalse(profileService.updatePatientProfile(patientProfileRequest));
	}

	/**
	 * Test {@code updatePatientProfile} method of {@link ProfileServiceImpl}.
	 * Case when patient not found for given email.
	 */
	@Test
	void testUpdatePatientProfilePatientNotFound() {
		
		String email = TestDataUtil.getPatientEmail();
		PatientProfileRequest patientProfileRequest = new PatientProfileRequest();
		
		mockJwt(IPatientConstants.PATIENT_ROLE, email);
		when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		PatientException patientException = assertThrows(PatientException.class, ()-> {
			profileService.updatePatientProfile(patientProfileRequest);
		});
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
	}

	/**
	 * Test {@code updatePatientProfile} method of {@link ProfileServiceImpl}.
	 * Exception scenario tested where logined user is not patient.
	 */
	@Test
	void testUpdatePatientProfileInvalidRole() {
		
		PatientProfileRequest patientProfileRequest = new PatientProfileRequest();
		
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IDoctorConstants.DOCTOR_ROLE);
		
		PatientException patientException = assertThrows(PatientException.class, ()-> {
			profileService.updatePatientProfile(patientProfileRequest);
		});
		assertEquals(IExceptionConstants.INVALID_PATIEN_TOKEN_ROLE, patientException.getMessage());
	}
	
	/**
	 * Test {@code updateDoctorProfile} method of {@link ProfileServiceImpl}.
	 * Cover cases for successful method execution with sub case 
	 * 1) Profile Update Successful
	 * 2) Profile Update Failed
	 * 
	 */
	@Test
	void testUpdateDoctorProfile() {
		
		String email = TestDataUtil.getDoctorEmail();
		Doctor doctor = TestDataUtil.getDoctor();
		DoctorProfileRequest doctorProfileRequest = new DoctorProfileRequest();
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
		doNothing().when(doctorMapper).updateDoctorFromRequest(doctorProfileRequest, doctor);
		when(doctorRepository.save(doctor)).thenReturn(doctor);

		// Case when profile update successful
		assertTrue(profileService.updateDoctorProfile(doctorProfileRequest));
		
		// Case when profile update failed
		when(doctorRepository.save(doctor)).thenReturn(null);
		assertFalse(profileService.updateDoctorProfile(doctorProfileRequest));
	}

	/**
	 * Test {@code updateDoctorProfile} method of {@link ProfileServiceImpl}.
	 * Case when doctor not found for given email.
	 */
	@Test
	void testUpdateDoctorProfileDoctorNotFound() {
		
		String email = TestDataUtil.getDoctorEmail();
		DoctorProfileRequest doctorProfileRequest = new DoctorProfileRequest();
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		DoctorException doctorException = assertThrows(DoctorException.class, ()-> {
			profileService.updateDoctorProfile(doctorProfileRequest);
		});
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
	}

	/**
	 * Test {@code updateDoctorProfile} method of {@link ProfileServiceImpl}.
	 * Exception scenario tested where logined user is not doctor.
	 */
	@Test
	void testUpdateDoctorProfileInvalidRole() {
		
		DoctorProfileRequest doctorProfileRequest = new DoctorProfileRequest();
		
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IPatientConstants.PATIENT_ROLE);
		
		DoctorException doctorException = assertThrows(DoctorException.class, ()-> {
			profileService.updateDoctorProfile(doctorProfileRequest);
		});
		assertEquals(IExceptionConstants.INVALID_DOCTOR_TOKEN_ROLE, doctorException.getMessage());
	}
	
	/**
	 * Test {@code uploadProflePic} method of {@link ProfileServiceImpl}.
	 * Cover case when patient upload profile picture.
	 */
	@Test
	void testUploadProflePicPatient() {
		Integer patientId = 1;
		String imagePath = "/images/patient/";
		String fileName = "1_ProfilePic_user.jpg";
		String errorMsg = "Something Went Wrong";
		String email = TestDataUtil.getPatientEmail();
		MultipartFile mockFile = mock(MultipartFile.class);

		mockJwt(IPatientConstants.PATIENT_ROLE, email);
		profileUtilMock.when(() -> ProfileUtil.validateImageFile(mockFile)).thenAnswer(inv -> null);
		profileUtilMock.when(() -> ProfileUtil.getFileName(patientId, imagePath, mockFile)).thenReturn(fileName);
		when(pathProperties.getPatientImagePath()).thenReturn(imagePath);
		when(patientRepository.findIdByEmail(email)).thenReturn(Optional.of(patientId));
		when(patientRepository.updateprofilePicPath(fileName, email)).thenReturn(1);
		
		// Profile Picture name updated successfully
		assertTrue(profileService.uploadProflePic(mockFile, IPatientConstants.PATIENT_ROLE));

		// Profile Picture name not updated
		when(patientRepository.updateprofilePicPath(fileName, email)).thenReturn(0);
		assertFalse(profileService.uploadProflePic(mockFile, IPatientConstants.PATIENT_ROLE));

		// When IOException occur during file upload
		profileUtilMock.when(() -> ProfileUtil.getFileName(patientId, imagePath, mockFile)).thenThrow(new IOException(errorMsg));
		ProfileException profileException = assertThrows(ProfileException.class, ()-> {
			profileService.uploadProflePic(mockFile, IPatientConstants.PATIENT_ROLE);
		});
		assertEquals(IExceptionConstants.PROFILE_PIC_UPLOAD_FAIL, profileException.getMessage());
		
		// When patient not found
		when(patientRepository.findIdByEmail(email)).thenReturn(Optional.empty());
		PatientException patientException = assertThrows(PatientException.class, ()-> {
			profileService.uploadProflePic(mockFile, IPatientConstants.PATIENT_ROLE);
		});
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
		
	}

	/**
	 * Test {@code uploadProflePic} method of {@link ProfileServiceImpl}.
	 * Cover case when doctor upload profile picture.
	 */
	@Test
	void testUploadProflePicDoctor() {
		Integer doctorId = 1;
		String imagePath = "/images/doctor/";
		String fileName = "1_ProfilePic_user.jpg";
		String errorMsg = "Something Went Wrong";
		String email = TestDataUtil.getDoctorEmail();
		MultipartFile mockFile = mock(MultipartFile.class);
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		profileUtilMock.when(() -> ProfileUtil.validateImageFile(mockFile)).thenAnswer(inv -> null);
		profileUtilMock.when(() -> ProfileUtil.getFileName(doctorId, imagePath, mockFile)).thenReturn(fileName);
		when(pathProperties.getDoctorImagePath()).thenReturn(imagePath);
		when(doctorRepository.findIdByEmail(email)).thenReturn(Optional.of(doctorId));
		when(doctorRepository.updateprofilePicPath(fileName, email)).thenReturn(1);
		
		// Profile Picture name updated successfully
		assertTrue(profileService.uploadProflePic(mockFile, IDoctorConstants.DOCTOR_ROLE));
		
		// Profile Picture name not updated
		when(doctorRepository.updateprofilePicPath(fileName, email)).thenReturn(0);
		assertFalse(profileService.uploadProflePic(mockFile, IDoctorConstants.DOCTOR_ROLE));
		
		// When IOException occur during file upload
		profileUtilMock.when(() -> ProfileUtil.getFileName(doctorId, imagePath, mockFile)).thenThrow(new IOException(errorMsg));
		ProfileException profileException = assertThrows(ProfileException.class, ()-> {
			profileService.uploadProflePic(mockFile, IDoctorConstants.DOCTOR_ROLE);
		});
		assertEquals(IExceptionConstants.PROFILE_PIC_UPLOAD_FAIL, profileException.getMessage());
		
		// When doctor not found
		when(doctorRepository.findIdByEmail(email)).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, ()-> {
			profileService.uploadProflePic(mockFile, IDoctorConstants.DOCTOR_ROLE);
		});
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
		
	}

	/**
	 * Test {@code uploadProflePic} method of {@link ProfileServiceImpl}.
	 * Cover case when admin upload profile picture.
	 */
	@Test
	void testUploadProflePicAdmin() {
		Integer adminId = 1;
		String imagePath = "/images/admin/";
		String fileName = "1_ProfilePic_user.jpg";
		String errorMsg = "Something Went Wrong";
		String email = TestDataUtil.getAdminEmail();
		MultipartFile mockFile = mock(MultipartFile.class);
		
		mockJwt(IAdminConstants.ADMIN_ROLE, email);
		profileUtilMock.when(() -> ProfileUtil.validateImageFile(mockFile)).thenAnswer(inv -> null);
		profileUtilMock.when(() -> ProfileUtil.getFileName(adminId, imagePath, mockFile)).thenReturn(fileName);
		when(pathProperties.getAdminImagePath()).thenReturn(imagePath);
		when(adminRepository.findIdByEmail(email)).thenReturn(Optional.of(adminId));
		when(adminRepository.updateprofilePicPath(fileName, email)).thenReturn(1);
		
		// Profile Picture name updated successfully
		assertTrue(profileService.uploadProflePic(mockFile, IAdminConstants.ADMIN_ROLE));
		
		// Profile Picture name not updated
		when(adminRepository.updateprofilePicPath(fileName, email)).thenReturn(0);
		assertFalse(profileService.uploadProflePic(mockFile, IAdminConstants.ADMIN_ROLE));
		
		// When IOException occur during file upload
		profileUtilMock.when(() -> ProfileUtil.getFileName(adminId, imagePath, mockFile)).thenThrow(new IOException(errorMsg));
		ProfileException profileException = assertThrows(ProfileException.class, ()-> {
			profileService.uploadProflePic(mockFile, IAdminConstants.ADMIN_ROLE);
		});
		assertEquals(IExceptionConstants.PROFILE_PIC_UPLOAD_FAIL, profileException.getMessage());
		
		// When admin not found
		when(adminRepository.findIdByEmail(email)).thenReturn(Optional.empty());
		AdminException adminException = assertThrows(AdminException.class, ()-> {
			profileService.uploadProflePic(mockFile, IAdminConstants.ADMIN_ROLE);
		});
		assertEquals(IExceptionConstants.ADMIN_NOT_FOUND, adminException.getMessage());
		
	}
	
	/**
	 * Test {@code uploadProflePic} method of {@link ProfileServiceImpl}.
	 * Case when invalid role or invalid logined is there.
	 */
	@Test
    void testUploadProfilePicRoleMismatch() {
        MultipartFile mockFile = mock(MultipartFile.class);
        
        jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn("OTHER_ROLE");
        profileUtilMock.when(() -> ProfileUtil.validateImageFile(mockFile)).thenAnswer(inv -> null);

        ProfileException ex = assertThrows(ProfileException.class, () ->
            profileService.uploadProflePic(mockFile, IPatientConstants.PATIENT_ROLE)
        );
        assertEquals(IExceptionConstants.SESSION_MISMATCHED, ex.getMessage());
    }

	/**
	 * Test {@code uploadProflePic} method of {@link ProfileServiceImpl}.
	 * Default case in switch where other role is logined.
	 */
	@Test
	void testUploadProfilePicDefault() {
		MultipartFile mockFile = mock(MultipartFile.class);
		
		mockJwt("OTHER_ROLE", "other@mail.com");
		profileUtilMock.when(() -> ProfileUtil.validateImageFile(mockFile)).thenAnswer(inv -> null);

		ProfileException ex = assertThrows(ProfileException.class, () ->
			profileService.uploadProflePic(mockFile, "OTHER_ROLE")
		);
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, ex.getMessage());
	}
	
	/**
	 * Test {@code getProfilePicName} method of {@link ProfileServiceImpl}.
	 * Case when patient is logined.
	 */
	@Test
	void testGetProfilePicNamePatient() {
		String email = TestDataUtil.getPatientEmail();
		String role = IPatientConstants.PATIENT_ROLE;
		String fileName = "1_ProfilePic_user.jpg";
		String defaultImage = "defaultPic.jpg";
		
		mockJwt(IPatientConstants.PATIENT_ROLE, email);		
		when(patientRepository.getProfilePicName(email)).thenReturn(fileName);
		when(pathProperties.getDefaultProfilePicName()).thenReturn(defaultImage);
		// Success scenario
		assertEquals(fileName, profileService.getProfilePicName(role));

		// Case when default profile picture returned
		when(patientRepository.getProfilePicName(email)).thenReturn(null);
		assertEquals(defaultImage, profileService.getProfilePicName(role));

		// Second case of default profile picture returned
		when(patientRepository.getProfilePicName(email)).thenReturn(" ");
		assertEquals(defaultImage, profileService.getProfilePicName(role));
	}
	
	/**
	 * Test {@code getProfilePicName} method of {@link ProfileServiceImpl}.
	 * Case when doctor is logined.
	 */
	@Test
	void testGetProfilePicNameDoctor() {
		String email = TestDataUtil.getDoctorEmail();
		String role = IDoctorConstants.DOCTOR_ROLE;
		String fileName = "1_ProfilePic_user.jpg";
		String defaultImage = "defaultPic.jpg";
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);		
		when(doctorRepository.getProfilePicName(email)).thenReturn(fileName);
		when(pathProperties.getDefaultProfilePicName()).thenReturn(defaultImage);
		// Success scenario
		assertEquals(fileName, profileService.getProfilePicName(role));
		
		// Case when default profile picture returned
		when(doctorRepository.getProfilePicName(email)).thenReturn(null);
		assertEquals(defaultImage, profileService.getProfilePicName(role));
		
		// Second case of default profile picture returned
		when(doctorRepository.getProfilePicName(email)).thenReturn(" ");
		assertEquals(defaultImage, profileService.getProfilePicName(role));
	}

	/**
	 * Test {@code getProfilePicName} method of {@link ProfileServiceImpl}.
	 * Case when admin is logined.
	 */
	@Test
	void testGetProfilePicNameAdmin() {
		String email = TestDataUtil.getAdminEmail();
		String role = IAdminConstants.ADMIN_ROLE;
		String fileName = "1_ProfilePic_user.jpg";
		String defaultImage = "defaultPic.jpg";
		
		mockJwt(IAdminConstants.ADMIN_ROLE, email);		
		when(adminRepository.getProfilePicName(email)).thenReturn(fileName);
		when(pathProperties.getDefaultProfilePicName()).thenReturn(defaultImage);
		// Success scenario
		assertEquals(fileName, profileService.getProfilePicName(role));
		
		// Case when default profile picture returned
		when(adminRepository.getProfilePicName(email)).thenReturn(null);
		assertEquals(defaultImage, profileService.getProfilePicName(role));
		
		// Second case of default profile picture returned
		when(adminRepository.getProfilePicName(email)).thenReturn(" ");
		assertEquals(defaultImage, profileService.getProfilePicName(role));
	}
	
	/**
	 * Test {@code getProfilePicName} method of {@link ProfileServiceImpl}.
	 * Default case in switch where other role is logined.
	 */
	@Test
	void testGetProfilePicNameDefault() {
		mockJwt("OTHER_ROLE", "other@mail.com");

		ProfileException ex = assertThrows(ProfileException.class, () ->
			profileService.getProfilePicName("OTHER_ROLE")
		);
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, ex.getMessage());
	}
	
	/**
	 * Test {@code getProfilePicName} method of {@link ProfileServiceImpl}.
	 * Case when logined user is not same as input role parameter.
	 */
	@Test
	void testGetProfilePicNameInvalidRole() {
		mockJwt(IPatientConstants.PATIENT_ROLE, TestDataUtil.getPatientEmail());
		
		ProfileException ex = assertThrows(ProfileException.class, () ->
			profileService.getProfilePicName(IDoctorConstants.DOCTOR_ROLE)
		);
		assertEquals(IExceptionConstants.SESSION_MISMATCHED, ex.getMessage());
	}

	/**
	 * Test {@code updatePassword} method of {@link ProfileServiceImpl}.
	 * Case when logined user is patient.
	 */
	@Test
	void testUpdatePasswordPatient() {
		String role = IPatientConstants.PATIENT_ROLE;
		String email = TestDataUtil.getPatientEmail();
		String encodedPassword = "$2a$10$d6o/wJABjSHVJa3At9Dhpe2x6x5P/skQg2HgFWw2MnUNKlOqt0jCK";
		PasswordRequest passwordRequest = TestDataUtil.getPasswordRequest();

		mockJwt(IPatientConstants.PATIENT_ROLE, email);
		when(passwordEncoder.encode(passwordRequest.getPassword())).thenReturn(encodedPassword);
		when(patientRepository.updatePassword(encodedPassword, email)).thenReturn(1);
		// Success scenario
		assertTrue(profileService.updatePassword(passwordRequest, role));

		// Password updating failed scenario
		when(patientRepository.updatePassword(encodedPassword, email)).thenReturn(0);
		assertFalse(profileService.updatePassword(passwordRequest, role));
	}
	
	/**
	 * Test {@code updatePassword} method of {@link ProfileServiceImpl}.
	 * Case when logined user is doctor.
	 */
	@Test
	void testUpdatePasswordDoctor() {
		String role = IDoctorConstants.DOCTOR_ROLE;
		String email = TestDataUtil.getDoctorEmail();
		String encodedPassword = "$2a$10$qba/yfe5RMNm7/.c2uKdFul9QiZIBEB7HugUQXH6nKLVMW.iU2bFm";
		PasswordRequest passwordRequest = TestDataUtil.getPasswordRequest();
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		when(passwordEncoder.encode(passwordRequest.getPassword())).thenReturn(encodedPassword);
		when(doctorRepository.updatePassword(encodedPassword, email)).thenReturn(1);
		// Success scenario
		assertTrue(profileService.updatePassword(passwordRequest, role));
		
		// Password updating failed scenario
		when(doctorRepository.updatePassword(encodedPassword, email)).thenReturn(0);
		assertFalse(profileService.updatePassword(passwordRequest, role));
	}
	
	/**
	 * Test {@code updatePassword} method of {@link ProfileServiceImpl}.
	 * Case when logined user is admin.
	 */
	@Test
	void testUpdatePasswordAdmin() {
		String role = IAdminConstants.ADMIN_ROLE;
		String email = TestDataUtil.getAdminEmail();
		String encodedPassword = "$2a$10$C.refJDMVi2i4vE.Ds19r.Iw4T6pYXaAPm9K5V/V/RD8SBhdvQReK";
		PasswordRequest passwordRequest = TestDataUtil.getPasswordRequest();
		
		mockJwt(IAdminConstants.ADMIN_ROLE, email);
		when(passwordEncoder.encode(passwordRequest.getPassword())).thenReturn(encodedPassword);
		when(adminRepository.updatePassword(encodedPassword, email)).thenReturn(1);
		// Success scenario
		assertTrue(profileService.updatePassword(passwordRequest, role));
		
		// Password updating failed scenario
		when(adminRepository.updatePassword(encodedPassword, email)).thenReturn(0);
		assertFalse(profileService.updatePassword(passwordRequest, role));
	}
	
	/**
	 * Test {@code updatePassword} method of {@link ProfileServiceImpl}.
	 * Failure scenarios.
	 */
	@Test
	void testUpdatePasswordFailure() {
		PasswordRequest passwordRequest = TestDataUtil.getPasswordRequest();
		
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IPatientConstants.PATIENT_ROLE);
		ProfileException profileException = assertThrows(ProfileException.class, () ->
			profileService.updatePassword(passwordRequest, IAdminConstants.ADMIN_ROLE)
		);
		// Logined role is not matched with input paramter role
		assertEquals(IExceptionConstants.SESSION_MISMATCHED, profileException.getMessage());
		
		// Password validation case 1
		profileException = assertThrows(ProfileException.class, () ->
			profileService.updatePassword(null, IPatientConstants.PATIENT_ROLE)
		);
		assertEquals(IExceptionConstants.PASSWORD_VALIDATION, profileException.getMessage());

		// Password validation case 2
		passwordRequest.setPassword(null);
		profileException = assertThrows(ProfileException.class, () ->
			profileService.updatePassword(passwordRequest, IPatientConstants.PATIENT_ROLE)
		);
		assertEquals(IExceptionConstants.PASSWORD_VALIDATION, profileException.getMessage());

		// Password validation case 3
		passwordRequest.setPassword(" ");
		profileException = assertThrows(ProfileException.class, () ->
			profileService.updatePassword(passwordRequest, IPatientConstants.PATIENT_ROLE)
		);
		assertEquals(IExceptionConstants.PASSWORD_VALIDATION, profileException.getMessage());

		// Password validation case 3
		passwordRequest.setPassword("1234");
		profileException = assertThrows(ProfileException.class, () ->
			profileService.updatePassword(passwordRequest, IPatientConstants.PATIENT_ROLE)
        );
		assertEquals(IExceptionConstants.PASSWORD_VALIDATION, profileException.getMessage());
	}
	
	/**
	 * Test {@code updatePassword} method of {@link ProfileServiceImpl}.
	 * Default case in switch where other role is logined.
	 */
	@Test
	void testUpdatePasswordDefault() {
		mockJwt("OTHER_ROLE", "other@mail.com");

		ProfileException ex = assertThrows(ProfileException.class, () ->
			profileService.updatePassword(TestDataUtil.getPasswordRequest(), "OTHER_ROLE")
		);
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, ex.getMessage());
	}

	/**
	 * Test {@code getPatientSubProfiles} method of {@link ProfileServiceImpl}.
	 * Success scenario.
	 */
	@Test
	void testGetPatientSubProfiles() {
		Integer id = 1;
		List<SubProfile> subProfiles = List.of(TestDataUtil.getSubProfile());
		
		when(subProfileRepository.findByPatientId(id)).thenReturn(subProfiles);
		
		List<SubProfileDTO> actualResult = profileService.getPatientSubProfiles(id);
		assertEquals(subProfiles.get(0).getId(), actualResult.get(0).getId());
		assertEquals(subProfiles.get(0).getName(), actualResult.get(0).getName());
		assertEquals(subProfiles.get(0).getPhone(), actualResult.get(0).getPhone());
		assertEquals(subProfiles.get(0).getRelation().getId(), actualResult.get(0).getRelation().getId());
		assertEquals(subProfiles.get(0).getRelation().getName(), actualResult.get(0).getRelation().getName());
	}

	/**
	 * Test {@code getPatientSubProfiles} method of {@link ProfileServiceImpl}.
	 * Case when no sub-profiles are associated with given patient id.
	 */
	@Test
	void testGetPatientSubProfilesFail() {
		Integer id = 1;
		when(subProfileRepository.findByPatientId(id)).thenReturn(new ArrayList<>());
		
		ProfileException profileException = assertThrows(ProfileException.class, () ->
			profileService.getPatientSubProfiles(id)
		);
		assertEquals(IExceptionConstants.SUB_PROFILES_NOT_FOUND, profileException.getMessage());
		
	}

	/**
	 * Test {@code applyForLeave} method of {@link ProfileServiceImpl}.
	 */
	@Test
	void testApplyForLeave() {
		Integer days = 3;
		String role = IDoctorConstants.DOCTOR_ROLE;
		String email = TestDataUtil.getDoctorEmail();
		Doctor doctor = TestDataUtil.getDoctor();
		LeaveRequest leaveRequest = TestDataUtil.getLeaveRequest();
		DoctorLeave doctorLeave = TestConverterUtil.toDoctorLeave(leaveRequest, doctor, days);
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		when(leaveRepository.hasOverlappingSlot(email, leaveRequest.getFrom(), leaveRequest.getTo())).thenReturn(false);
		when(leaveRepository.hasOverlappingSlot(email, leaveRequest.getFrom(), leaveRequest.getTo())).thenReturn(false);
		when(holidayRepository.findAll()).thenReturn(TestDataUtil.getHolidays());
		when(doctorRepository.findByEmail(email)).thenReturn(Optional.of(doctor));
		when(doctorLeaveMapper.toDoctorLeave(leaveRequest, doctor, days)).thenReturn(doctorLeave);
		when(leaveRepository.save(doctorLeave)).thenReturn(doctorLeave);
		
		// Success scenario
		assertTrue(profileService.applyForLeave(role, leaveRequest));
		
		// Case when failed to save leave
		when(leaveRepository.save(doctorLeave)).thenReturn(null);
		assertFalse(profileService.applyForLeave(role, leaveRequest));

		// Case when doctor is not found in DB
		when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, () ->
			profileService.applyForLeave(role, leaveRequest)
		);
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());

		// Case when leave is already booked on given day(s)
		when(leaveRepository.hasOverlappingSlot(email, leaveRequest.getFrom(), leaveRequest.getTo())).thenReturn(true);
		doctorException = assertThrows(DoctorException.class, () ->
			profileService.applyForLeave(role, leaveRequest)
		);
		assertEquals(IExceptionConstants.LEAVE_BOOKING_OVERLAP, doctorException.getMessage());

		// Case when invalid role is provided
		ProfileException profileException = assertThrows(ProfileException.class, () ->
			profileService.applyForLeave(IPatientConstants.PATIENT_ROLE, leaveRequest)
		);
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, profileException.getMessage());

		// Second Case of invalid role is passed
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IPatientConstants.PATIENT_ROLE);
		profileException = assertThrows(ProfileException.class, () ->
			profileService.applyForLeave(IDoctorConstants.DOCTOR_ROLE, leaveRequest)
		);
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, profileException.getMessage());
	}

	/**
	 * Test {@code searchLeaves} method of {@link ProfileServiceImpl}.
	 */
	@Test
	void testSearchLeaves() {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.FROM, null);
		requestMap.put(IRequestConstants.TO, null);
		requestMap.put(IRequestConstants.STATUS, null);
		String role = IDoctorConstants.DOCTOR_ROLE;
		String email = TestDataUtil.getDoctorEmail();
		DoctorLeave doctorLeave = TestDataUtil.getDoctorLeave();
		Pageable pageable = PageRequest.of(0, 10);
		Page<DoctorLeave> pages = new PageImpl<>(List.of(doctorLeave), pageable, 1);
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, email);
		when(leaveRepository.searchLeaves(email, (LocalDate) requestMap.get(IRequestConstants.FROM), (LocalDate) requestMap.get(IRequestConstants.TO), (LeaveStatus) requestMap.get(IRequestConstants.STATUS), pageable)).thenReturn(pages);
		when(doctorLeaveMapper.toLeaveResponse(doctorLeave)).thenReturn(TestConverterUtil.toLeaveResponse(doctorLeave));
		
		Map<String,Object> searchLeaves = profileService.searchLeaves(requestMap, role, pageable);
		assertNotNull(searchLeaves.get(IResponseConstants.DATA));
		@SuppressWarnings("unchecked")
		List<LeaveResponse> response = (List<LeaveResponse>) searchLeaves.get(IResponseConstants.DATA);
		assertEquals(doctorLeave.getId(), response.get(0).getId());
		assertEquals(doctorLeave.getFrom(), response.get(0).getFrom());
		assertEquals(doctorLeave.getTo(), response.get(0).getTo());
		assertEquals(doctorLeave.getStatus(), response.get(0).getStatus());
		
		// Case when invalid role is passed
		ProfileException profileException = assertThrows(ProfileException.class, () ->
			profileService.searchLeaves(requestMap, IPatientConstants.PATIENT_ROLE, pageable)
	    );
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, profileException.getMessage());

		// Second Case of invalid role is passed
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IPatientConstants.PATIENT_ROLE);
		profileException = assertThrows(ProfileException.class, () ->
			profileService.searchLeaves(requestMap, IDoctorConstants.DOCTOR_ROLE, pageable)
		);
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, profileException.getMessage());
	}
	
	/**
	 * Test {@code getAdminProfile} method of {@link ProfileServiceImpl}.
	 */
	@Test
	void testGetAdminProfile() {
		
		String email = TestDataUtil.getAdminEmail();
		Admin admin = TestDataUtil.getAdmin();
		AdminProfileResponse adminProfileResponse = TestConverterUtil.toAdminProfileResponse(admin);
		
		mockJwt(IAdminConstants.ADMIN_ROLE, email);
		when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
		when(adminMapper.toAdminProfileResponse(admin)).thenReturn(adminProfileResponse);
		// Success scenario
		assertEquals(adminProfileResponse, profileService.getAdminProfile());

		// Case when admin not found in DB
		when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
		AdminException adminException = assertThrows(AdminException.class, ()-> {
			profileService.getAdminProfile();
		});
		assertEquals(IExceptionConstants.ADMIN_NOT_FOUND, adminException.getMessage());

		// When admin is not logined
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IDoctorConstants.DOCTOR_ROLE);
		adminException = assertThrows(AdminException.class, ()-> {
			profileService.getAdminProfile();
		});
		assertEquals(IExceptionConstants.INVALID_ADMIN_TOKEN_ROLE, adminException.getMessage());
	}
	
	/**
	 * Test {@code updateAdminName} method of {@link ProfileServiceImpl}.
	 */
	@Test
	void testUpdateAdminName() {
		String name = "New Name";
		String email = TestDataUtil.getAdminEmail();
		
		mockJwt(IAdminConstants.ADMIN_ROLE, email);
		when(adminRepository.updateName(name, email)).thenReturn(1);
		// Success scenario
		assertTrue(profileService.updateAdminName(name));
		
		// when update name failed
		when(adminRepository.updateName(name, email)).thenReturn(0);
		assertFalse(profileService.updateAdminName(name));
		
		// When admin is not logined
		jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(IDoctorConstants.DOCTOR_ROLE);
		AdminException adminException = assertThrows(AdminException.class, ()-> {
			profileService.updateAdminName(name);
		});
		assertEquals(IExceptionConstants.INVALID_ADMIN_TOKEN_ROLE, adminException.getMessage());
	}
	
	private void mockJwt(String role, String email) {
	    jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(role);
	    jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
	}
}
