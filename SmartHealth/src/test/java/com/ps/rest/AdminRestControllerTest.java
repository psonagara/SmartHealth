package com.ps.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.HolidayDTO;
import com.ps.dto.request.ADSRequest;
import com.ps.dto.request.APSRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.ADSResponse;
import com.ps.dto.response.AIResponse;
import com.ps.dto.response.APSResponse;
import com.ps.dto.response.AVResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.DoctorLeave;
import com.ps.entity.Patient;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.LeaveStatus;
import com.ps.exception.AdminException;
import com.ps.exception.AvailabilityException;
import com.ps.repo.DoctorRepository.DoctorIdNameProjection;
import com.ps.repo.PatientRepository.PatientIdNameProjection;
import com.ps.service.IAdminService;
import com.ps.util.AdminUtil;
import com.ps.util.AvailabilityUtil;
import com.ps.util.JwtUtil;
import com.ps.util.RestTestUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link AdminRestController}.
 * <p>
 * This class verifies the behavior of Admin-related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(AdminRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IAdminService adminService;
	
	@MockBean
	private JwtUtil jwtUtil;

	 /**
     * Test case for POST /admin/search/doctor.
     * <p>
     * Scenario: Valid doctor search request by admin.  
     * Expectation: Returns HTTP 200 (OK) with the list of doctors in the response body.
     */
	@Test
	void testSearchDoctors() throws Exception {
		ADSRequest dsRequest = new ADSRequest();
		Pageable pageable = PageRequest.of(0, 10);
		Doctor doctor = TestDataUtil.getDoctor();
		List<ADSResponse> adsResponses = List.of(AdminUtil.prepareADSResponseForSeachDoctors(doctor));
		Map<String, Object> resultMap = RestTestUtil.prepareResponseMap(adsResponses);
		
		when(adminService.searchDoctors(dsRequest, pageable)).thenReturn(resultMap);
		
		mockMvc.perform(post("/admin/search/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(dsRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value(doctor.getId()))
		.andExpect(jsonPath("$.content.data[0].name").value(doctor.getName()));
	}

	 /**
     * Test case for POST /admin/search/patient.
     * <p>
     * Scenario: Valid patient search request by admin.  
     * Expectation: Returns HTTP 200 (OK) with the list of patients in the response body.
     */
	@Test
	void testSearchPatients() throws Exception {
		APSRequest apsRequest = new APSRequest();
		Pageable pageable = PageRequest.of(0, 10);
		Patient patient = TestDataUtil.getPatient();
		List<APSResponse> apsResponses = List.of(AdminUtil.prepareAPSResponseForSeachPatients(patient));
		Map<String, Object> resultMap = RestTestUtil.prepareResponseMap(apsResponses);
		
		when(adminService.searchPatients(apsRequest, pageable)).thenReturn(resultMap);
		
		mockMvc.perform(post("/admin/search/patient")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(apsRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value(patient.getId()))
		.andExpect(jsonPath("$.content.data[0].name").value(patient.getName()));
	}

	 /**
     * Test case for GET /admin/view/{role}.
     * <p>
     * Scenario: Valid view doctor details request by admin. 
     * Expectation: Returns HTTP 200 (OK) with the details of doctor in the response body.
     */
	@Test
	void testViewProfileDoctor() throws Exception {
		Integer id = 1;
		Doctor doctor = TestDataUtil.getDoctor();
		ADSResponse adsResponse = TestConverterUtil.toADSResponse(doctor);
		
		when(adminService.viewDoctorProfile(id)).thenReturn(adsResponse);
		
		mockMvc.perform(get("/admin/view/doctor?id="+id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.id").value(doctor.getId()))
		.andExpect(jsonPath("$.content.name").value(doctor.getName()))
		.andExpect(jsonPath("$.content.email").value(doctor.getEmail()));
	}

	/**
	 * Test case for GET /admin/view/{role}.
	 * <p>
	 * Scenario: Valid view patient details request by admin. 
	 * Expectation: Returns HTTP 200 (OK) with the details of patient in the response body.
	 */
	@Test
	void testViewProfilePatient() throws Exception {
		Integer id = 1;
		Patient patient = TestDataUtil.getPatient();
		APSResponse apsResponse = TestConverterUtil.toAPSResponse(patient);
		
		when(adminService.viewPatientProfile(id)).thenReturn(apsResponse);
		
		mockMvc.perform(get("/admin/view/patient?id="+id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.id").value(patient.getId()))
		.andExpect(jsonPath("$.content.name").value(patient.getName()))
		.andExpect(jsonPath("$.content.email").value(patient.getEmail()));
	}

	/**
	 * Test case for GET /admin/view/{role}.
	 * <p>
	 * Scenario: Invalid role is provided.
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with the message.
	 */
	@Test
	void testViewProfileInvalidRole() throws Exception {
		mockMvc.perform(get("/admin/view/other?id=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IExceptionConstants.PROVIDE_VALID_ROLE));
	}

	/**
	 * Test case for PATCH /admin/toggle/status/{role}.
	 * <p>
	 * Scenario: Valid scenario for toggling activation status by admin. 
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testToggleStatus() throws Exception {
		Integer id = 1;
		String role = IDoctorConstants.DOCTOR_ROLE;
		// Passing either role is okay for testing
		when(adminService.toggleStatus(id, role)).thenReturn(1);
		
		mockMvc.perform(patch("/admin/toggle/status/doctor?id="+id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.USER_ACTIVATION_TOGGLED));
	}

	/**
	 * Test case for PATCH /admin/toggle/status/{role}.
	 * <p>
	 * Scenario: Invalid role is provided.
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with the message.
	 */
	@Test
	void testToggleStatusInvalidRole() throws Exception {
		Integer id = 1;
		String role = "other";
		// In case of invalid role exception thrown at service layer and 
		// error response prepared at controller advice
		when(adminService.toggleStatus(id, role)).thenThrow(new AdminException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST));
		
		mockMvc.perform(patch("/admin/toggle/status/" + role + "?id=" + id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IExceptionConstants.PROVIDE_VALID_ROLE));
	}

	/**
	 * Test case for GET /admin/{role}/list.
	 * <p>
	 * Scenario: Valid request to get list of doctors details(id and name).
	 * Expectation: Returns HTTP 200 (OK) with the list of details of all doctors.
	 */
	@Test
	void testGetUsersListDoctor() throws Exception {
		List<DoctorIdNameProjection> doctorList = TestDataUtil.getDoctorIdNameProjections();
		String role = IDoctorConstants.DOCTOR_ROLE;
		
		when(adminService.getDoctorsList()).thenReturn(doctorList);
		
		mockMvc.perform(get("/admin/" + role + "/list")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].id").value(doctorList.get(0).getId()))
		.andExpect(jsonPath("$.content[0].name").value(doctorList.get(0).getName()));
	}

	/**
	 * Test case for GET /admin/{role}/list.
	 * <p>
	 * Scenario: Valid request to get list of patients details(id and name).
	 * Expectation: Returns HTTP 200 (OK) with the list of details of all patients.
	 */
	@Test
	void testGetUsersListPatient() throws Exception {
		List<PatientIdNameProjection> patientList = TestDataUtil.getPatientIdNameProjections();
		String role = IPatientConstants.PATIENT_ROLE;
		
		when(adminService.getPatientList()).thenReturn(patientList);
		
		mockMvc.perform(get("/admin/" + role + "/list")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].id").value(patientList.get(0).getId()))
		.andExpect(jsonPath("$.content[0].name").value(patientList.get(0).getName()));
	}

	/**
	 * Test case for GET /admin/{role}/list.
	 * <p>
	 * Scenario: Invalid role is provided.
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with message.
	 */
	@Test
	void testGetUsersListInvalidRole() throws Exception {
		String role = "other";
		
		mockMvc.perform(get("/admin/" + role + "/list")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IExceptionConstants.PROVIDE_VALID_ROLE));
	}
	
	/**
	 * Test case for GET /admin/search/slots.
	 * <p>
	 * Scenario: Valid request to fetch availability slots based on given search parameters.
	 * Expectation: Returns HTTP 200 (OK) with list of slots and associated doctor details(name and id).
	 */
	@Test
	void testSearchAvailabilitySlots() throws Exception {
		Availability availability = TestDataUtil.getAvailability();
		Map<String,Object> searchAvailabilitySlots = RestTestUtil.prepareResponseMap(List.of(AdminUtil.prepareSearchAvailabilitySlots(availability)));
		
		when(adminService.searchAvailabilitySlots(anyMap(), any(Pageable.class))).thenReturn(searchAvailabilitySlots);
		
		mockMvc.perform(get("/admin/search/slots")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].doctor.id").value(availability.getDoctor().getId()))
		.andExpect(jsonPath("$.content.data[0].doctor.name").value(availability.getDoctor().getName()))
		.andExpect(jsonPath("$.content.data[0].slot.id").value(availability.getId()))
		.andExpect(jsonPath("$.content.data[0].slot.date").value(availability.getDate().toString()))
		.andExpect(jsonPath("$.content.data[0].slot.status").value(availability.getStatus().toString()));
	}
	
	/**
	 * Test case for DELETE /admin/delete/slots/{id}.
	 * <p>
	 * Scenario: Valid request to fetch availability slots based on given search parameters.
	 * Expectation: Returns HTTP 200 (OK) with list of slots and associated doctor details(name and id).
	 */
	@Test
	void testDeleteAvailabilitySlot() throws Exception {
		Integer id = 1;
		int deleted = 1;
		
		when(adminService.deleteAvailabilitySlot(id)).thenReturn(deleted);
		
		mockMvc.perform(delete("/admin/delete/slots/"+id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(deleted + IResponseConstants.AVAILABILITY_DELETION_DONE));
	}

	/**
	 * Test case for DELETE /admin/delete/slots/{id}.
	 * <p>
	 * Scenario: Invalid id or id of slot with status other than AVAILABLE or any failure in deletion.
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with message.
	 */
	@Test
	void testDeleteAvailabilitySlotFail() throws Exception {
		Integer id = 5;
		
		when(adminService.deleteAvailabilitySlot(id)).thenThrow(new AvailabilityException(IExceptionConstants.NOT_ABLE_TO_DELETE_SLOT, HttpStatus.INTERNAL_SERVER_ERROR));
		
		mockMvc.perform(delete("/admin/delete/slots/"+id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IExceptionConstants.NOT_ABLE_TO_DELETE_SLOT));
	}

	/**
	 * Test case for GET /admin/view/slots/details.
	 * <p>
	 * Scenario: Valid request to view details of slot by admin.
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testViewSlotDetails() throws Exception {
		Integer id = 1;
		Integer appointmentId = null;
		Availability availability = TestDataUtil.getAvailability();
		AVResponse avResponse = TestConverterUtil.toAVResponse(availability);
		Map<String, Object> doctorInfo = new HashMap<>();
		doctorInfo.put(IAdminConstants.DOCTOR_ID, availability.getDoctor().getId());
		ADResponse adResponse = new ADResponse();
		adResponse.setAvResponse(avResponse);
		adResponse.setAdditionalInfo(doctorInfo);
		
		when(adminService.viewSlotDetails(id, appointmentId)).thenReturn(adResponse);
		
		mockMvc.perform(get("/admin/view/slots/details?id="+id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.avResponse.id").value(availability.getId()))
		.andExpect(jsonPath("$.content.additionalInfo.doctorId").value(availability.getDoctor().getId()));

		// case when appointmentId is also provided
		appointmentId = 1;
		List<AIResponse> aiResponses = List.of(AvailabilityUtil.prepareAIResponse(TestDataUtil.getAppointment()));
		adResponse.setAppointmentInfo(aiResponses);
		
		when(adminService.viewSlotDetails(id, appointmentId)).thenReturn(adResponse);

		mockMvc.perform(get("/admin/view/slots/details?id="+id+"&appointmentId="+appointmentId)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.avResponse.id").value(availability.getId()))
		.andExpect(jsonPath("$.content.appointmentInfo[0].status").value(aiResponses.get(0).getStatus().toString()))
		.andExpect(jsonPath("$.content.additionalInfo.doctorId").value(availability.getDoctor().getId()))
		.andExpect(jsonPath("$.content.appointmentInfo[0].patient.id").value(aiResponses.get(0).getPatient().getId()));
	}
	
	/**
	 * Test case for GET /admin/change/appointment/status.
	 * <p>
	 * Scenario: Valid request to view details of slot by admin.
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testChangeAvailabilityAppointmentStatus() throws Exception {
		when(adminService.changeAvailabilityAppointmentStatus(anyMap())).thenReturn(IResponseConstants.APPOINTMENT_APPROVED);
		
		mockMvc.perform(patch("/admin/change/appointment/status?slotId=1&status=APPROVED")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.APPOINTMENT_APPROVED));
	}
	
	/**
	 * Test case for GET /admin/search/appointment.
	 * <p>
	 * Scenario: Valid request to view appointments based on given filter parameters by admin.
	 * Expectation: Returns HTTP 200 (OK) with list of appointments.
	 */
	@Test
	void testSearchAppointments() throws Exception {
		Appointment appointment = TestDataUtil.getAppointment();
		Map<String, Object> responseMap = RestTestUtil.prepareResponseMap(List.of(AdminUtil.prepareSearchAppointments(appointment)));
		
		when(adminService.searchAppointments(anyMap(), any(Pageable.class))).thenReturn(responseMap);
		
		mockMvc.perform(get("/admin/search/appointment")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value(appointment.getId()))
		.andExpect(jsonPath("$.content.data[0].slotId").value(appointment.getAvailability().getId()))
		.andExpect(jsonPath("$.content.data[0].doctorName").value(appointment.getAvailability().getDoctor().getName()))
		.andExpect(jsonPath("$.content.data[0].patientName").value(appointment.getPatient().getName()));
	}
	
	/**
	 * Test case for GET /admin/change/status/{appointmentId}.
	 * <p>
	 * Scenario: Valid request to change appointment status by admin.
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testChangeAppointmentStatus() throws Exception {
		Integer appointmentId = 1;
		AppointmentStatus status = AppointmentStatus.APPROVED;
		
		when(adminService.changeAppointmentStatus(appointmentId, status)).thenReturn(IResponseConstants.APPOINTMENT_APPROVED);
		
		mockMvc.perform(patch("/admin/change/status/"+appointmentId+"?status="+status)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.APPOINTMENT_APPROVED));
	}
	
	/**
	 * Test case for GET /admin/view/leave/{role}.
	 * <p>
	 * Scenario: Valid request to view leaves based on given filter parameters by admin.
	 * Expectation: Returns HTTP 200 (OK) with list of leaves.
	 */
	@Test
	void testSearchLeaves() throws Exception {
		String role = IDoctorConstants.DOCTOR_ROLE;
		DoctorLeave doctorLeave = TestDataUtil.getDoctorLeave();
		Map<String,Object> responseMap = RestTestUtil.prepareResponseMap(List.of(AdminUtil.prepareSearchLeavesMap(doctorLeave)));
		
		when(adminService.searchLeaves(anyMap(), any(Pageable.class))).thenReturn(responseMap);
		
		mockMvc.perform(get("/admin/view/leave/" + role)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].doctorInfo.id").value(doctorLeave.getDoctor().getId()))
		.andExpect(jsonPath("$.content.data[0].doctorInfo.name").value(doctorLeave.getDoctor().getName()))
		.andExpect(jsonPath("$.content.data[0].leaveInfo.id").value(doctorLeave.getId()))
		.andExpect(jsonPath("$.content.data[0].leaveInfo.status").value(doctorLeave.getStatus().toString()));
	}
	
	/**
	 * Test case for GET /admin/change/leave/status/{role}/{id}.
	 * <p>
	 * Scenario: Valid request to change leave status by admin.
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testChangeLeaveStatus() throws Exception {
		Integer id = 1;
		String role = IDoctorConstants.DOCTOR_ROLE;
		LeaveStatus status = LeaveStatus.APPROVED;
		
		when(adminService.changeLeaveStatus(id, role, status)).thenReturn(IResponseConstants.LEAVE_APPROVED);
		
		mockMvc.perform(patch("/admin/change/leave/status/"+role+"/"+id+"?status="+status)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.LEAVE_APPROVED));
	}
	
	/**
	 * Test case for GET /admin/add/holiday.
	 * <p>
	 * Scenario: Valid request to add Holiday by admin.
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testAddHoliday() throws Exception {
		HolidayDTO holidayDTO = new HolidayDTO();
		holidayDTO.setHolidayDate(LocalDate.of(2025, 8, 9));
		holidayDTO.setReason("RakshaBandhan");
		
		when(adminService.addHoliday(holidayDTO)).thenReturn(true);
		
		mockMvc.perform(post("/admin/add/holiday")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(holidayDTO)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.HOLIDAY_ADDED_SUCCESS));
	}
	
	/**
	 * Test case for GET /admin/add/holiday.
	 * <p>
	 * Scenario: InValid request to add Holiday by admin as given date fall on SUNDAY.
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with message.
	 */
	@Test
	void testAddHolidayFail() throws Exception {
		HolidayDTO holidayDTO = new HolidayDTO();
		holidayDTO.setHolidayDate(LocalDate.of(2025, 8, 10));
		holidayDTO.setReason("RakshaBandhan");
		
		when(adminService.addHoliday(holidayDTO)).thenThrow(new AdminException(IExceptionConstants.HOLIDAY_SUNDAY_CONFLICT, HttpStatus.BAD_REQUEST));
		
		mockMvc.perform(post("/admin/add/holiday")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(holidayDTO)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IExceptionConstants.HOLIDAY_SUNDAY_CONFLICT));
	}

	/**
	 * Test case for GET /admin/add/holiday.
	 * <p>
	 * Scenario: Valid request but failed to save holiday.
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with message.
	 */
	@Test
	void testAddHolidayFail2() throws Exception {
		HolidayDTO holidayDTO = new HolidayDTO();
		holidayDTO.setHolidayDate(LocalDate.of(2025, 8, 9));
		holidayDTO.setReason("RakshaBandhan");
		
		when(adminService.addHoliday(holidayDTO)).thenReturn(false);
		
		mockMvc.perform(post("/admin/add/holiday")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(holidayDTO)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.HOLIDAY_ADDED_FAIL));
	}
	
	/**
	 * Test case for GET /admin/delete/holiday/{id}.
	 * <p>
	 * Scenario: Valid request to add Holiday by admin.
	 * Expectation: Returns HTTP 200 (OK) with message.
	 */
	@Test
	void testDeleteHoliday() throws Exception {
		Integer id = 1;
		
		doNothing().when(adminService).deleteHoliday(id);
		
		mockMvc.perform(delete("/admin/delete/holiday/" + id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.HOLIDAY_DELETE_SUCCESS));
	}
}
