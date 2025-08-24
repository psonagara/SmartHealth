package com.ps.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ps.constants.IDoctorConstants;
import com.ps.constants.IResponseConstants;
import com.ps.constants.IValidationConstants;
import com.ps.dto.request.AppointmentRequest;
import com.ps.dto.request.DSRequest;
import com.ps.dto.response.AIResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.ApDResponse;
import com.ps.dto.response.DSResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.Patient;
import com.ps.enu.AppointmentStatus;
import com.ps.service.IAppointmentService;
import com.ps.util.AppointmentUtil;
import com.ps.util.AvailabilityUtil;
import com.ps.util.JwtUtil;
import com.ps.util.RestTestUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link AppointmentRestController}.
 * <p>
 * This class verifies the behavior of Appointment related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(AppointmentRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IAppointmentService appointmentService;
	
	@MockBean
	private JwtUtil jwtUtil;

	/**
	 * Test case for GET /appointment/view/details.
	 * <p>
	 * Scenario: Valid request to fetch details to book an appointment by patient.  
	 * Expectation: Returns HTTP 200 (OK) with the details required to book an appointment.
	 */
	@Test
	void testViewDetails() throws Exception {
		Doctor doctor = TestDataUtil.getDoctor();
		Availability availability = TestDataUtil.getAvailability();
		Patient patient = TestDataUtil.getPatient();
		ApDResponse apDResponse = AppointmentUtil.prepareApDResponse(doctor, availability, patient);
		
		when(appointmentService.viewDetails(anyMap())).thenReturn(apDResponse);
		
		mockMvc.perform(get("/appointment/view/details?doctorId=1&id=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.doctor.id").value(doctor.getId()))
		.andExpect(jsonPath("$.content.doctor.name").value(doctor.getName()))
		.andExpect(jsonPath("$.content.patient.id").value(patient.getId()))
		.andExpect(jsonPath("$.content.patient.name").value(patient.getName()))
		.andExpect(jsonPath("$.content.slot.id").value(availability.getId()))
		.andExpect(jsonPath("$.content.slot.date").value(availability.getDate().toString()));
	}
	
	/**
	 * Test case for POST /appointment/book.
	 * <p>
	 * Scenario: Valid request to fetch details to book an appointment by patient.  
	 * Expectation: Returns HTTP 200 (OK) with the details required to book an appointment.
	 */
	@Test
	void testBookAppointment() throws Exception {
		AppointmentRequest request = prepareAppointmentRequest();
		
		when(appointmentService.bookAppointment(request)).thenReturn(true);
		
		mockMvc.perform(post("/appointment/book")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.APPOINTMENT_BOOKED_SUCCESSFUL));
	}
	
	/**
	 * Test case for POST /appointment/book.
	 * <p>
	 * Scenario: Failed to book appointment.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testBookAppointmentFail() throws Exception {
		AppointmentRequest request = prepareAppointmentRequest();
		
		when(appointmentService.bookAppointment(request)).thenReturn(false);
		
		mockMvc.perform(post("/appointment/book")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.APPOINTMENT_BOOKED_FAIL));
	}
	
	/**
	 * Test case for GET /appointment/view/all.
	 * <p>
	 * Scenario: Valid request to fetch list of appointments with pagination.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testViewAllAppointments() throws Exception {
		Appointment appointment = TestDataUtil.getAppointment();
		Map<String, Object> responseMap = RestTestUtil.prepareResponseMap(List.of(AppointmentUtil.prepareViewAllAppointmentsResponse(appointment, IDoctorConstants.DOCTOR_ROLE)));
		
		when(appointmentService.viewAllAppointments(anyMap(), any(Pageable.class))).thenReturn(responseMap);
		
		mockMvc.perform(get("/appointment/view/all")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value(appointment.getId()))
		.andExpect(jsonPath("$.content.data[0].patientName").value(appointment.getPatient().getName()));
	}
	
	/**
	 * Test case for PATCH /appointment/change/status/{appointmentId}.
	 * <p>
	 * Scenario: Valid request to change status of appointments.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testChangeAppointmentStatus() throws Exception {
		Integer appointmentId = 1;
		AppointmentStatus status = AppointmentStatus.APPROVED;
		
		when(appointmentService.changeAppointmentStatus(appointmentId, status, false)).thenReturn(IResponseConstants.APPOINTMENT_APPROVED);
		
		mockMvc.perform(patch("/appointment/change/status/" + appointmentId + "?status=" + status)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.APPOINTMENT_APPROVED));
	}

	/**
	 * Test case for GET /appointment/view/details/{appointmentId}.
	 * <p>
	 * Scenario: Valid request to view details of an appointment.  
	 * Expectation: Returns HTTP 200 (OK) with details of appointment.
	 */
	@Test
	void testViewAppointmentDetails() throws Exception {
		Integer appointmentId = 1;
		Appointment appointment = TestDataUtil.getAppointment();
		Map<String,Object> responseMap = prepareAppointmentDetailsMap(appointment);
		
		when(appointmentService.viewAppointmentDetails(appointmentId)).thenReturn(responseMap);
		
		mockMvc.perform(get("/appointment/view/details/" + appointmentId)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.appointmentInfo.patient.id").value(appointment.getPatient().getId()))
		.andExpect(jsonPath("$.content.appointmentInfo.status").value(appointment.getStatus().toString()))
		.andExpect(jsonPath("$.content.doctorInfo.id").value(appointment.getAvailability().getDoctor().getId()))
		.andExpect(jsonPath("$.content.doctorInfo.name").value(appointment.getAvailability().getDoctor().getName()));
	}

	/**
	 * Test case for POST /appointment/search/doctor.
	 * <p>
	 * Scenario: Valid request to search doctors based on given search parameters.  
	 * Expectation: Returns HTTP 200 (OK) with list of doctors.
	 */
	@Test
	void testSearchDoctorsToBookAppointment() throws Exception {
		DSRequest request = new DSRequest();
		Doctor doctor = TestDataUtil.getDoctor();
		Map<String,Object> responseMap = RestTestUtil.prepareResponseMap(List.of(AvailabilityUtil.prepareDSResponse(doctor)));
		
		when(appointmentService.searchDoctorsToBookAppointment(eq(request), (Pageable) any())).thenReturn(responseMap);
		
		mockMvc.perform(post("/appointment/search/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value(doctor.getId()))
		.andExpect(jsonPath("$.content.data[0].name").value(doctor.getName()));

		// Case when date is provided to search doctor
		request.setDate(LocalDate.now());
		mockMvc.perform(post("/appointment/search/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value(doctor.getId()))
		.andExpect(jsonPath("$.content.data[0].name").value(doctor.getName()));
	}

	/**
	 * Test case for POST /appointment/search/doctor.
	 * <p>
	 * Scenario: Invalid request with date in past.
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with message.
	 */
	@Test
	void testSearchDoctorsToBookAppointmentInvalidRequest() throws Exception {
		DSRequest request = new DSRequest();
		request.setDate(LocalDate.of(2025, 8, 10));
		
		mockMvc.perform(post("/appointment/search/doctor")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value(IValidationConstants.SEARCH_DATE_CONSTRAINT));
	}
	
	/**
	 * Test case for GET /appointment/slots.
	 * <p>
	 * Scenario: Valid request to view slots of a doctor to book appointment.  
	 * Expectation: Returns HTTP 200 (OK) with list of available slots.
	 */
	@Test
	void testViewSlotsToBookAppointment() throws Exception {
		Availability availability = TestDataUtil.getAvailability();
		Map<LocalDate, List<AVResponse>> responseMap = Map.of(availability.getDate(), List.of(AvailabilityUtil.prepareAVResponse(availability)));
		
		when(appointmentService.viewSlotsToBookAppointment(anyMap())).thenReturn(responseMap);
		
		mockMvc.perform(get("/appointment/slots?doctorId=1")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.2025-07-20[0].id").value(availability.getId()))
		.andExpect(jsonPath("$.content.2025-07-20[0].startTime").value(availability.getStartTime().format(DateTimeFormatter.ISO_LOCAL_TIME).toString()))
		.andExpect(jsonPath("$.content.2025-07-20[0].endTime").value(availability.getEndTime().format(DateTimeFormatter.ISO_LOCAL_TIME).toString()));
	}
	
	private Map<String, Object> prepareAppointmentDetailsMap(Appointment appointment) {
		AIResponse aiResponse = AvailabilityUtil.prepareAIResponse(appointment);
		DSResponse dsResponse = AvailabilityUtil.prepareDSResponse(appointment.getAvailability().getDoctor());
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(IResponseConstants.APPOINTMENT_INFO, aiResponse);
		responseMap.put(IResponseConstants.DOCTOR_INFO, dsResponse);
		return responseMap;
	}
	
	private AppointmentRequest prepareAppointmentRequest() { 
		AppointmentRequest request = new AppointmentRequest();
		request.setDoctorId(1);
		request.setIsSubProfile(false);
		request.setPatientId(1);
		request.setSlotId(1);
		return request;
	}
}
