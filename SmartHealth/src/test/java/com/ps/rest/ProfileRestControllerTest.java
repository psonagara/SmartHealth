package com.ps.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.SubProfileDTO;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LeaveRequest;
import com.ps.dto.request.PasswordRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.AdminProfileResponse;
import com.ps.dto.response.ApiResponse;
import com.ps.dto.response.DoctorProfileResponse;
import com.ps.dto.response.PatientProfileResponse;
import com.ps.entity.DoctorLeave;
import com.ps.service.IProfileService;
import com.ps.util.JwtUtil;
import com.ps.util.ProfileUtil;
import com.ps.util.RestTestUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link ProfileRestController}.
 * <p>
 * This class verifies the behavior of profile related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(ProfileRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileRestControllerTest {

	@MockBean
	private IProfileService profileService;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtUtil jwtUtil;

	/**
	 * Test case for GET /profile/view/{role}.
	 * <p>
	 * Scenario: Valid request to fetch profile data by patient.  
	 * Expectation: Returns HTTP 200 (OK) with the profile data of patient.
	 */
	@Test
	void testGetProfilePatient() throws Exception {
		String role = IPatientConstants.PATIENT_ROLE;
		PatientProfileResponse response = TestConverterUtil.toPatientProfileResponse(TestDataUtil.getPatient());

		when(profileService.getPatientProfile()).thenReturn(response);

		MvcResult mvcResult = mockMvc.perform(get("/profile/view/{role}", role)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String json = mvcResult.getResponse().getContentAsString();
		ApiResponse<PatientProfileResponse> actualResponse = RestTestUtil.toObjectFromJson(json, new TypeReference<ApiResponse<PatientProfileResponse>>() {});
		assertEquals(response, actualResponse.getContent());
	}

	/**
	 * Test case for GET /profile/view/{role}.
	 * <p>
	 * Scenario: Valid request to fetch profile data by doctor.  
	 * Expectation: Returns HTTP 200 (OK) with the profile data of doctor.
	 */
	@Test
	void testGetProfileDoctor() throws Exception {
		String role = IDoctorConstants.DOCTOR_ROLE;
		DoctorProfileResponse response = TestConverterUtil.toDoctorProfileResponse(TestDataUtil.getDoctor());

		when(profileService.getDoctorProfile()).thenReturn(response);

		MvcResult mvcResult = mockMvc.perform(get("/profile/view/{role}", role)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String json = mvcResult.getResponse().getContentAsString();
		ApiResponse<DoctorProfileResponse> actualResponse = RestTestUtil.toObjectFromJson(json, new TypeReference<ApiResponse<DoctorProfileResponse>>() {});
		assertEquals(response, actualResponse.getContent());
	}

	/**
	 * Test case for GET /profile/view/{role}.
	 * <p>
	 * Scenario: Valid request to fetch profile data by admin.  
	 * Expectation: Returns HTTP 200 (OK) with the profile data of admin.
	 */
	@Test
	void testGetProfileAdmin() throws Exception {
		String role = IAdminConstants.ADMIN_ROLE;
		AdminProfileResponse response = TestConverterUtil.toAdminProfileResponse(TestDataUtil.getAdmin());

		when(profileService.getAdminProfile()).thenReturn(response);

		MvcResult mvcResult = mockMvc.perform(get("/profile/view/{role}", role)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String json = mvcResult.getResponse().getContentAsString();
		ApiResponse<AdminProfileResponse> actualResponse = RestTestUtil.toObjectFromJson(json, new TypeReference<ApiResponse<AdminProfileResponse>>() {});
		assertEquals(response, actualResponse.getContent());
	}

	/**
	 * Test case for GET /profile/view/{role}.
	 * <p>
	 * Scenario: Invalid request due to invalid input role.  
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with the message.
	 */
	@Test
	void testGetProfileInvalidRequest() throws Exception {
		AdminProfileResponse response = TestConverterUtil.toAdminProfileResponse(TestDataUtil.getAdmin());

		when(profileService.getAdminProfile()).thenReturn(response);
		// case when empty or blank role is provided
		mockMvc.perform(get("/profile/view/{role}", " ")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROVIDE_ROLE_FOR_PROFILE));

		// case when invalid role provided
		mockMvc.perform(get("/profile/view/{role}", "other")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IResponseConstants.INVALID_ROLE));
	}


	/**
	 * Test case for PUT /profile/update/patient.
	 * <p>
	 * Scenario: Valid request to update profile data by patient.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testUpdatePatietProfile() throws Exception {
		PatientProfileRequest request = getPatientProfileRequest();
		
		when(profileService.updatePatientProfile(request)).thenReturn(true);

		mockMvc.perform(put("/profile/update/patient")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROFILE_UPDATE_SUCCESSFUL));
	}

	/**
	 * Test case for PUT /profile/update/patient.
	 * <p>
	 * Scenario: Valid request to update profile data by patient but updation failed.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testUpdatePatietProfileFail() throws Exception {
		PatientProfileRequest request = getPatientProfileRequest();
		
		when(profileService.updatePatientProfile(request)).thenReturn(false);
		
		mockMvc.perform(put("/profile/update/patient")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROFILE_UPDATE_FAILED));
	}
	

	/**
	 * Test case for PUT /profile/update/doctor.
	 * <p>
	 * Scenario: Valid request to update profile data by doctor.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testUpdateDoctorProfile() throws Exception {
		DoctorProfileRequest request = getDoctorProfileRequest();
		
		when(profileService.updateDoctorProfile(request)).thenReturn(true);
		
		mockMvc.perform(put("/profile/update/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROFILE_UPDATE_SUCCESSFUL));
	}
	
	/**
	 * Test case for PUT /profile/update/doctor.
	 * <p>
	 * Scenario: Valid request to update profile data by doctor but updation failed.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testUpdateDoctorProfileFail() throws Exception {
		DoctorProfileRequest request = getDoctorProfileRequest();
		
		when(profileService.updateDoctorProfile(request)).thenReturn(false);
		
		mockMvc.perform(put("/profile/update/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROFILE_UPDATE_FAILED));
	}
	
	/**
	 * Test case for PUT /profile/upload/picture/{role}.
	 * <p>
	 * Scenario: Valid request to upload profile picture.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testUploadProfilePicture() throws Exception {
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "Consider this as image bytes".getBytes());
		String role = IPatientConstants.PATIENT_ROLE;
		
		when(profileService.uploadProflePic(mockMultipartFile, role)).thenReturn(true);
		
		mockMvc.perform(multipart(HttpMethod.PUT, "/profile/upload/picture/{role}", role)
				.file(mockMultipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROFILE_PICTURE_UPDATE_SUCCESSFUL));
	}

	/**
	 * Test case for PUT /profile/upload/picture/{role}.
	 * <p>
	 * Scenario: Valid request to upload profile picture but failed to upload.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testUploadProfilePictureFail() throws Exception {
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "Consider this as image bytes".getBytes());
		String role = IPatientConstants.PATIENT_ROLE;
		
		when(profileService.uploadProflePic(mockMultipartFile, role)).thenReturn(false);
		
		mockMvc.perform(multipart(HttpMethod.PUT, "/profile/upload/picture/{role}", role)
				.file(mockMultipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROFILE_PICTURE_UPDATE_FAILED));
	}

	/**
	 * Test case for GET /profile/picture/name/{role}.
	 * <p>
	 * Scenario: Valid request to get profile picture name.  
	 * Expectation: Returns HTTP 200 (OK) with the name of image.
	 */
	@Test
	void testGetProfilePicName() throws Exception {
		String role = IPatientConstants.PATIENT_ROLE;
		String profilePicName = "1_ProfilePic_user.jpg";
		
		when(profileService.getProfilePicName(role)).thenReturn(profilePicName);
		
		mockMvc.perform(get("/profile/picture/name/{role}", role)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").value(profilePicName));
	}

	/**
	 * Test case for PATCH /profile/update/password/{role}.
	 * <p>
	 * Scenario: Valid request to update password.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testUpdatePassword() throws Exception {
		String role = IPatientConstants.PATIENT_ROLE;
		PasswordRequest passwordRequest = TestDataUtil.getPasswordRequest();
		
		when(profileService.updatePassword(any(PasswordRequest.class), eq(role))).thenReturn(true);
		
		mockMvc.perform(patch("/profile/update/password/{role}", role)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(passwordRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PASSWORD_UPDATE_SUCCESSFUL));
	}

	/**
	 * Test case for PATCH /profile/update/password/{role}.
	 * <p>
	 * Scenario: Valid request to update password but failed to update password.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testUpdatePasswordFail() throws Exception {
		String role = IPatientConstants.PATIENT_ROLE;
		PasswordRequest passwordRequest = TestDataUtil.getPasswordRequest();
		
		when(profileService.updatePassword(any(PasswordRequest.class), eq(role))).thenReturn(false);
		
		mockMvc.perform(patch("/profile/update/password/{role}", role)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(passwordRequest)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PASSWORD_UPDATE_FAILED));
	}
	
	/**
	 * Test case for GET /profile/view/sub-profiles/patient.
	 * <p>
	 * Scenario: Valid request to get all sub-profiles of patient.  
	 * Expectation: Returns HTTP 200 (OK) with the list of sub profile data.
	 */
	@Test
	void testGetPatientSubProfiles() throws Exception {
		Integer id = 1;
		List<SubProfileDTO> subProfileDTOs = List.of(ProfileUtil.prepareSubProfileDTO(TestDataUtil.getSubProfile()));

		when(profileService.getPatientSubProfiles(id)).thenReturn(subProfileDTOs);

		MvcResult mvcResult = mockMvc.perform(get("/profile/view/sub-profiles/patient")
				.param("id", String.valueOf(id))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		String json = mvcResult.getResponse().getContentAsString();
		ApiResponse<List<SubProfileDTO>> actualResponse = RestTestUtil.toObjectFromJson(json, new TypeReference<ApiResponse<List<SubProfileDTO>>>(){});
		assertEquals(subProfileDTOs, actualResponse.getContent());
	}

	/**
	 * Test case for POST /profile/apply/leave/{role}.
	 * <p>
	 * Scenario: Valid request to apply for leave.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testApplyForLeave() throws Exception {
		String role = IDoctorConstants.DOCTOR_ROLE;
		LeaveRequest request = getLeaveRequest();
		
		when(profileService.applyForLeave(eq(role), any(LeaveRequest.class))).thenReturn(true);

		mockMvc.perform(post("/profile/apply/leave/{role}", role)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.LEAVE_APPLY_SUCCESSFUL));
	}

	/**
	 * Test case for POST /profile/apply/leave/{role}.
	 * <p>
	 * Scenario: Valid request to apply for leave but failed to apply.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testApplyForLeaveFail() throws Exception {
		String role = IDoctorConstants.DOCTOR_ROLE;
		LeaveRequest request = getLeaveRequest();
		
		when(profileService.applyForLeave(eq(role), any(LeaveRequest.class))).thenReturn(false);
		
		mockMvc.perform(post("/profile/apply/leave/{role}", role)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.LEAVE_APPLY_FAILED));
	}
	
	/**
	 * Test case for GET /profile/view/leave/{role}.
	 * <p>
	 * Scenario: Valid request to view leaves.  
	 * Expectation: Returns HTTP 200 (OK) with the list of leaves data with pagination.
	 */
	@Test
	void testSearchLeaves() throws Exception {
		String role = IDoctorConstants.DOCTOR_ROLE;
		DoctorLeave leave = TestDataUtil.getDoctorLeave();
		Map<String, Object> responseMap = RestTestUtil.prepareResponseMap(List.of(TestConverterUtil.toLeaveResponse(leave)));

		when(profileService.searchLeaves(anyMap(), eq(role), (Pageable) any())).thenReturn(responseMap);

		mockMvc.perform(get("/profile/view/leave/{role}", role)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value(leave.getId()));
	}
	
	/**
	 * Test case for PATCH /profile/update/admin/name.
	 * <p>
	 * Scenario: Valid request to update name by admin.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testUpdateAdminName() throws Exception {
		Map<String, Object> request = new HashMap<>();
		request.put(IRequestConstants.NAME, "Admin Admin");
		
		when(profileService.updateAdminName(anyString())).thenReturn(true);
		
		mockMvc.perform(patch("/profile/update/admin/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.NAME_UPDATE_SUCCESSFUL));
	}

	/**
	 * Test case for PATCH /profile/update/admin/name.
	 * <p>
	 * Scenario: Valid request to update name by admin but failed to update name.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testUpdateAdminNameFail() throws Exception {
		Map<String, Object> request = new HashMap<>();
		request.put(IRequestConstants.NAME, "Admin Admin");
		
		when(profileService.updateAdminName(anyString())).thenReturn(false);
		
		mockMvc.perform(patch("/profile/update/admin/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.NAME_UPDATE_FAIL));
	}
	
	/**
	 * Test case for PATCH /profile/update/admin/name.
	 * <p>
	 * Scenario: Invalid request to update admin name.  
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with the message.
	 */
	@Test
	void testUpdateAdminNameInvalidRequest() throws Exception {
		Map<String, Object> request = new HashMap<>();
		
		when(profileService.updateAdminName(anyString())).thenReturn(false);
		
		//case when name is not provide in request body
		mockMvc.perform(patch("/profile/update/admin/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROVIDE_NAME));
		
		// case when name is empty
		request.put(IRequestConstants.NAME, "  ");
		mockMvc.perform(patch("/profile/update/admin/name")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROVIDE_NAME));
	}
	
	private LeaveRequest getLeaveRequest() { 
		LocalDate date = LocalDate.now().plusDays(1);
		if (date.getDayOfWeek()==DayOfWeek.SUNDAY) {
			date = date.plusDays(1);
		}
		
		LeaveRequest leaveRequest = new LeaveRequest();
		leaveRequest.setFrom(date);
		leaveRequest.setReason("Some Reason");
		return leaveRequest;
	}
	
	private DoctorProfileRequest getDoctorProfileRequest() { 
		DoctorProfileRequest doctor = new DoctorProfileRequest();
		doctor.setAddress("JMN");
		doctor.setDob(LocalDate.of(1996, 8, 12));
		doctor.setGender("Female");
		doctor.setName("Sh Do");
		doctor.setPassword("12345");
		doctor.setPhone("0123456789");
		doctor.setRegistrationNumber("INS1025638");
		doctor.setYearOfExperience(5);
		return doctor;
	}

	private PatientProfileRequest getPatientProfileRequest() {
		PatientProfileRequest patient = new PatientProfileRequest();
		patient.setDob(LocalDate.of(2000, 02, 10));
		patient.setEmail("patient@shc.com");
		patient.setGender("Male");
		patient.setHeight(175.0);
		patient.setName("Sm Ca");
		patient.setPassword("12345");
		patient.setPhone("9876543210");
		patient.setWeight(70);
		return patient;
	}
}
