package com.ps.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ps.config.props.SlotsProperties;
import com.ps.constants.IResponseConstants;
import com.ps.constants.IValidationConstants;
import com.ps.dto.request.ADRequest;
import com.ps.dto.request.AGRequest;
import com.ps.dto.request.AVRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.dto.response.AIResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.ApiResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.enu.AGMode;
import com.ps.enu.AppointmentStatus;
import com.ps.service.IAvailabilityService;
import com.ps.util.AvailabilityUtil;
import com.ps.util.JwtUtil;
import com.ps.util.RestTestUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link AvailabilityRestController}.
 * <p>
 * This class verifies the behavior of availability slot related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(AvailabilityRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AvailabilityRestControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IAvailabilityService availabilityService;
	
	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private SlotsProperties slotsProperties;

	/**
	 * Test case for POST /availability/generate/slots.
	 * <p>
	 * Scenario: Valid request to save preference or/and generate slots with different mode.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testCreateAvailabilitySlots() throws Exception {
		AGRequest request = TestDataUtil.getAgRequest();
		
		when(slotsProperties.getMaximumGenerationDays()).thenReturn(15);
		doNothing().when(availabilityService).generateAvailabilitySlots((AGRequest) any());
		
		// case when mode is CUSTOM_ONE_TIME
		mockMvc.perform(post("/availability/generate/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_GENERATION_CUSTOM_ONE_TIME));

		// case when mode is CUSTOM_CONTINUOUS
		request.setDaysAhead(3);
		request.setMode(AGMode.CUSTOM_CONTINUOUS);
		mockMvc.perform(post("/availability/generate/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_GENERATION_CUSTOM_CONTINUOUS));

		// case when mode is AUTO
		request = new AGRequest();
		request.setMode(AGMode.AUTO);
		mockMvc.perform(post("/availability/generate/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_PREFERENCE_AUTO));

		// case when mode is MANUAL, manual slots are not provided (basically in this case preference saved to MANUAL)
		request.setMode(AGMode.MANUAL);
		mockMvc.perform(post("/availability/generate/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_PREFERENCE_MANUAL));

		// case when mode is MANUAL, empty manual slots are provided (basically in this case preference saved to MANUAL)
		request.setManualSlots(List.of());
		mockMvc.perform(post("/availability/generate/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_PREFERENCE_MANUAL));

		// case when mode is MANUAL, manual slots are provided (in this case slots will be generated)
		request.setManualSlots(TestDataUtil.getAsRequests());
		mockMvc.perform(post("/availability/generate/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_GENERATION_MANUAL));
	}
	
	/**
	 * Test case for PATCH /availability/generate/activate.
	 * <p>
	 * Scenario: Valid request to save preference or/and generate slots with different mode.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testActivateAvailabilityGeneration() throws Exception {
		when(availabilityService.activateAGPreference()).thenReturn(true);
		
		mockMvc.perform(patch("/availability/generate/activate")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_GENERATION_ACTIVATED));
	}

	/**
	 * Test case for PATCH /availability/generate/activate.
	 * <p>
	 * Scenario: Valid request to save preference or/and generate slots with different mode but failed.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testActivateAvailabilityGenerationFail() throws Exception {
		when(availabilityService.activateAGPreference()).thenReturn(false);
		
		mockMvc.perform(patch("/availability/generate/activate")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message").value(IResponseConstants.AVAILABILITY_GENERATION_ACTIVATE_FAIL));
	}

	/**
	 * Test case for GET /availability/preference.
	 * <p>
	 * Scenario: Valid request to save preference or/and generate slots with different mode but failed.  
	 * Expectation: Returns HTTP 500 (INTERNAL_SERVER_ERROR) with the message.
	 */
	@Test
	void testGetAGPreference() throws Exception {
		AGPreferenceResponse agPreferenceResponse = TestConverterUtil.toAGPreferenceResponse(TestDataUtil.getAGPreference());

		when(availabilityService.getAGPreference()).thenReturn(agPreferenceResponse);
		
		MvcResult mvcResult = mockMvc.perform(get("/availability/preference")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn();
		String json = mvcResult.getResponse().getContentAsString();
		ApiResponse<AGPreferenceResponse> actualResponse = RestTestUtil.toObjectFromJson(json, new TypeReference<ApiResponse<AGPreferenceResponse>>() {});
		assertEquals(agPreferenceResponse, actualResponse.getContent());
	}
	
	/**
	 * Test case for GET /availability/view/slots.
	 * <p>
	 * Scenario: Valid request to view slots by doctor.  
	 * Expectation: Returns HTTP 200 (OK) with the list of availability slots.
	 */
	@Test
	void testGetAvailabilityData() throws Exception {
		Map<String,Object> responseMap = RestTestUtil.prepareResponseMap(List.of(TestConverterUtil.toAVResponse(TestDataUtil.getAvailability())));

		when(availabilityService.getAvailabilityData(any(AVRequest.class), (Pageable) any())).thenReturn(responseMap);

		mockMvc.perform(get("/availability/view/slots")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value("1"))
		.andExpect(jsonPath("$.content.data.length()").value("1"));

		// case when from date is provided
		mockMvc.perform(get("/availability/view/slots")
				.param("from", "2025-07-15")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value("1"))
		.andExpect(jsonPath("$.content.data.length()").value("1"));

		// case when from date and to date both are provided
		mockMvc.perform(get("/availability/view/slots")
				.param("from", "2025-07-15")
				.param("to", "2025-07-25")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.data[0].id").value("1"))
		.andExpect(jsonPath("$.content.data.length()").value("1"));
	}
	
	/**
	 * Test case for GET /availability/view/slots.
	 * <p>
	 * Scenario: Invalid request to view slots by doctor.  
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with the list of availability slots.
	 */
	@Test
	void testGetAvailabilityDataInvalidRequest() throws Exception {
		mockMvc.perform(get("/availability/view/slots")
				.param("from", "2025-07-15")
				.param("to", "2025-07-10")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(IValidationConstants.TO_DATE_CONSTRAINTS));
	}
	
	/**
	 * Test case for DELETE /availability/delete/slots/{id}.
	 * <p>
	 * Scenario: Valid request to delete slot by doctor.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testDeleteAvailabilitySlot() throws Exception {
		Integer id = 1, deleted = 1;
		
		when(availabilityService.deleteAvailabilitySlot(id)).thenReturn(deleted);

		mockMvc.perform(delete("/availability/delete/slots/{id}", id)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(deleted + IResponseConstants.AVAILABILITY_DELETION_DONE));
	}

	/**
	 * Test case for DELETE /availability/delete/slots.
	 * <p>
	 * Scenario: Valid request to delete slots by doctor.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testBulkDeleteAvailabilitySlot() throws Exception {
		Integer deleted = 1;
		ADRequest adRequest = new ADRequest();
		adRequest.setStartDate(LocalDate.of(2025, 7, 10));
		adRequest.setEndDate(LocalDate.of(2025, 7, 30));
		
		when(availabilityService.bulkDeleteAvailabilitySlots((ADRequest) any())).thenReturn(deleted);
		
		mockMvc.perform(post("/availability/delete/slots")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJsonString(adRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(deleted + IResponseConstants.AVAILABILITY_DELETION_DONE));
	}

	/**
	 * Test case for GET /availability/view/slots/details.
	 * <p>
	 * Scenario: Valid request to view details of slot.  
	 * Expectation: Returns HTTP 200 (OK) with the data of slot.
	 */
	@Test
	void testViewSlotDetails() throws Exception {
		Integer id = 1, appointmentId = 1;
		Availability availability = TestDataUtil.getAvailability();
		Appointment appointment = TestDataUtil.getAppointment();
		ADResponse adResponse = prepareAdResponse(availability, appointment);
		
		when(availabilityService.viewSlotDetails(id, appointmentId, false)).thenReturn(adResponse);
		
		mockMvc.perform(get("/availability/view/slots/details")
				.param("id", String.valueOf(id))
				.param("appointmentId", String.valueOf(appointmentId))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.avResponse.id").value(availability.getId()))
		.andExpect(jsonPath("$.content.avResponse.date").value(availability.getDate().toString()))
		.andExpect(jsonPath("$.content.appointmentInfo[0].patient.id").value(appointment.getPatient().getId()))
		.andExpect(jsonPath("$.content.appointmentInfo[0].bookingTime").value(appointment.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)))
		.andExpect(jsonPath("$.content.appointmentInfo[0].status").value(appointment.getStatus().toString()));
	}
	
	/**
	 * Test case for PATCH /availability/change/appointment/status.
	 * <p>
	 * Scenario: Valid request to change status of latest appointment linked with given slot.  
	 * Expectation: Returns HTTP 200 (OK) with the message.
	 */
	@Test
	void testChangeAvailabilityAppointmentStatus() throws Exception {
		Integer slotId = 1;
		AppointmentStatus status = AppointmentStatus.APPROVED;
		
		when(availabilityService.changeAvailabilityAppointmentStatus(anyMap(), eq(false))).thenReturn(IResponseConstants.APPOINTMENT_APPROVED);

		mockMvc.perform(patch("/availability/change/appointment/status")
				.param("slotId", String.valueOf(slotId))
				.param("status", String.valueOf(status))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.APPOINTMENT_APPROVED));
	}

	private ADResponse prepareAdResponse(Availability availability, Appointment appointment) {
		AVResponse avResponse = TestConverterUtil.toAVResponse(availability);
		List<AIResponse> aiResponses = List.of(AvailabilityUtil.prepareAIResponse(appointment));
		
		ADResponse adResponse = new ADResponse();
		adResponse.setAvResponse(avResponse);
		adResponse.setAppointmentInfo(aiResponses);
		return adResponse;
	}
}
