package com.ps.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.request.AdminProfileRequest;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.LoginResponse;
import com.ps.service.IAuthService;
import com.ps.util.JwtUtil;
import com.ps.util.RestTestUtil;

/**
 * Unit tests for {@link AuthRestController}.
 * <p>
 * This class verifies the behavior of authentication related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(AuthRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IAuthService authService;
	
	@MockBean
	private JwtUtil jwtUtil;

	/**
	 * Test case for POST /auth/register/patient.
	 * <p>
	 * Scenario: Valid request to register patient.  
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testRegisterPatient() throws Exception {
		PatientProfileRequest request = getPatientProfileRequest();
		
		when(authService.registerPatient(request)).thenReturn(true);
		
		mockMvc.perform(post("/auth/register/patient")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PATIENT_REGISTER_SUCCESSFUL));
	}

	/**
	 * Test case for POST /auth/register/patient.
	 * <p>
	 * Scenario: Failed to register patient.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with message.
	 */
	@Test
	void testRegisterPatientFail() throws Exception {
		PatientProfileRequest request = getPatientProfileRequest();
		
		when(authService.registerPatient(request)).thenReturn(false);
		
		mockMvc.perform(post("/auth/register/patient")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PATIENT_REGISTER_FAILED));
	}
	
	/**
	 * Test case for POST /auth/login/patient.
	 * <p>
	 * Scenario: Valid request to login patient.  
	 * Expectation: Returns HTTP 200 (OK) with token and role and message in response.
	 */
	@Test
	void testLoginPatient() throws Exception {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbWFuLnNoYXJtYUBnbWFpbC5jb20iLCJSb2xlIjpbInBhdGllbnQiXSwiaWF0IjoxNzU1MDc2MzQxLCJleHAiOjE3NTUxMjY3NDF9.D5x2RsKzDJl4bSsQkueLuho-C1Pcj9myEjUPIr1rC4WrezXmVkykCJ3qfAhkivlji8aAt8ah9lD3nl4mBZ-cJA";
		LoginRequest request = new LoginRequest();
		request.setPassword("12345");
		request.setUser("patient@shc.com");
		LoginResponse loginResponse = new LoginResponse(token, IPatientConstants.PATIENT_ROLE);
		
		when(authService.loginPatient(request)).thenReturn(loginResponse);
		
		mockMvc.perform(post("/auth/login/patient")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.token").value(token))
		.andExpect(jsonPath("$.content.role").value(IPatientConstants.PATIENT_ROLE))
		.andExpect(jsonPath("$.message").value(IResponseConstants.PATIENT_LOGIN_SUCCESSFUL));
	}

	/**
	 * Test case for POST /auth/register/doctor.
	 * <p>
	 * Scenario: Valid request to register doctor.  
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testRegisterDoctor() throws Exception {
		DoctorProfileRequest request = getDoctorProfileRequest();
		
		when(authService.registerDoctor(request)).thenReturn(true);
		
		mockMvc.perform(post("/auth/register/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.DOCTOR_REGISTER_SUCCESSFUL));
	}
	
	/**
	 * Test case for POST /auth/register/doctor.
	 * <p>
	 * Scenario: Failed to register doctor.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with message.
	 */
	@Test
	void testRegisterDoctorFail() throws Exception {
		DoctorProfileRequest request = getDoctorProfileRequest();
		
		when(authService.registerDoctor(request)).thenReturn(false);
		
		mockMvc.perform(post("/auth/register/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.DOCTOR_REGISTER_FAILED));
	}
	
	/**
	 * Test case for POST /auth/login/doctor.
	 * <p>
	 * Scenario: Valid request to login doctor.  
	 * Expectation: Returns HTTP 200 (OK) with token and role and message in response.
	 */
	@Test
	void testLoginDoctor() throws Exception {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbmphbGkuc2hhcm1hQHNtYXJ0aGVhbHRoLmluIiwiUm9sZSI6WyJkb2N0b3IiXSwiaWF0IjoxNzU1MDc5OTYxLCJleHAiOjE3NTUxMzAzNjF9.jBebKFzje9ZcbMNFcQxIa8f9iONQnn-61JNI3l4BeqvMwy1aaBXuQv_5YRYS_d41w6VeyMsiKIphHdVaqmctCg";
		LoginRequest request = new LoginRequest();
		request.setPassword("12345");
		request.setUser("doctor@shc.com");
		LoginResponse loginResponse = new LoginResponse(token, IDoctorConstants.DOCTOR_ROLE);
		
		when(authService.loginDoctor(request)).thenReturn(loginResponse);
		
		mockMvc.perform(post("/auth/login/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.token").value(token))
		.andExpect(jsonPath("$.content.role").value(IDoctorConstants.DOCTOR_ROLE))
		.andExpect(jsonPath("$.message").value(IResponseConstants.DOCTOR_LOGIN_SUCCESSFUL));
	}
	/**
	 * Test case for POST /auth/register/admin.
	 * <p>
	 * Scenario: Valid request to register admin.  
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testRegisterAdmin() throws Exception {
		AdminProfileRequest request = getAdminProfileRequest();
		
		when(authService.registerAdmin(request)).thenReturn(true);
		
		mockMvc.perform(post("/auth/register/admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.ADMIN_REGISTER_SUCCESSFUL));
	}
	
	/**
	 * Test case for POST /auth/register/admin.
	 * <p>
	 * Scenario: Failed to register admin.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with message.
	 */
	@Test
	void testRegisterAdminFail() throws Exception {
		AdminProfileRequest request = getAdminProfileRequest();
		
		when(authService.registerAdmin(request)).thenReturn(false);
		
		mockMvc.perform(post("/auth/register/admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.ADMIN_REGISTER_FAILED));
	}
	
	/**
	 * Test case for POST /auth/login/admin.
	 * <p>
	 * Scenario: Valid request to login admin.  
	 * Expectation: Returns HTTP 200 (OK) with token and role and message in response.
	 */
	@Test
	void testLoginAdmin() throws Exception {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBzbWFydGhlYWx0aC5pbiIsIlJvbGUiOlsiYWRtaW4iXSwiaWF0IjoxNzU1MDgwNjQ0LCJleHAiOjE3NTUxMzEwNDR9.-OriDvl6hlVASmKqij-Of3yXlaZCa3uZOfJGfm7MSxSDV0Bjvlq5N3Goa0gnREVGwgUem8JjNJ2kQ-UPJvKuvg";
		LoginRequest request = new LoginRequest();
		request.setPassword("12345");
		request.setUser("admin@shc.com");
		LoginResponse loginResponse = new LoginResponse(token, IAdminConstants.ADMIN_ROLE);
		
		when(authService.loginAdmin(request)).thenReturn(loginResponse);
		
		mockMvc.perform(post("/auth/login/admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.token").value(token))
		.andExpect(jsonPath("$.content.role").value(IAdminConstants.ADMIN_ROLE))
		.andExpect(jsonPath("$.message").value(IResponseConstants.ADMIN_LOGIN_SUCCESSFUL));
	}
	
	private AdminProfileRequest getAdminProfileRequest() {
		AdminProfileRequest request = new AdminProfileRequest();
		request.setEmail("admin@shc.com");
		request.setName("Admin");
		request.setPassword("12345");
		request.setPhone("1472583690");
		return request;
	}
	
	private DoctorProfileRequest getDoctorProfileRequest() {
		DoctorProfileRequest request = new DoctorProfileRequest();
		request.setEmail("doctor@shc.com");
		request.setName("Sh Do");
		request.setPhone("0123456789");
		request.setPassword("12345");
		return request;
	}
	
	private PatientProfileRequest getPatientProfileRequest() {
		PatientProfileRequest request = new PatientProfileRequest();
		request.setEmail("patient@shc.com");
		request.setName("Sm Ca");
		request.setPhone("9876543210");
		request.setPassword("12345");
		return request;
	}

}
