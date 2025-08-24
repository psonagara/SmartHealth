package com.ps.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ps.constants.IPatientConstants;
import com.ps.constants.IResponseConstants;
import com.ps.entity.Appointment;
import com.ps.service.IPatientService;
import com.ps.util.AppointmentUtil;
import com.ps.util.JwtUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link PatientRestController}.
 * <p>
 * This class verifies the behavior of patient related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(PatientRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IPatientService patientService;
	
	@MockBean
	private JwtUtil jwtUtil;

	/**
	 * Test case for GET /patient/dashboard.
	 * <p>
	 * Scenario: Valid request to fetch data to display on patients dashboard.  
	 * Expectation: Returns HTTP 200 (OK) with the data to display on patients dashboard.
	 */
	@Test
	void testViewDashboard() throws Exception {
		Appointment appointment = TestDataUtil.getAppointment();
		Map<String,Object> responseMap = prepareViewDashboardMap(appointment);
		
		when(patientService.viewDashboard()).thenReturn(responseMap);
		
		mockMvc.perform(get("/patient/dashboard")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.upcomingAppointments[0].id").value(appointment.getId()))
		.andExpect(jsonPath("$.content.stats.upcomingAppointments").value("2"));
	}
	
	private Map<String, Object> prepareViewDashboardMap(Appointment appointment) {
		Map<String, Object> stats = new HashMap<>();
		stats.put(IResponseConstants.UPCOMING_APPOINTMENTS, 2L);
		
		List<Map<String, Object>> upcomingAppointments = List.of(AppointmentUtil.prepareViewAllAppointmentsResponse(appointment, IPatientConstants.PATIENT_ROLE));		
		
		Map<String, Object> response = new HashMap<>();
		response.put(IResponseConstants.STATS, stats);
		response.put(IResponseConstants.UPCOMING_APPOINTMENTS, upcomingAppointments);
		response.put(IResponseConstants.NOTIFICATIONS, new ArrayList<>());
		return response;
	}
}
