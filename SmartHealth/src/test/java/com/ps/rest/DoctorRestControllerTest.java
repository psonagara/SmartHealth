package com.ps.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import com.ps.constants.IDoctorConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.response.DailyAppointments;
import com.ps.entity.Appointment;
import com.ps.service.IDoctorService;
import com.ps.util.AppointmentUtil;
import com.ps.util.JwtUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link DoctorRestController}.
 * <p>
 * This class verifies the behavior of doctor related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(DoctorRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IDoctorService doctorService;
	
	@MockBean
	private JwtUtil jwtUtil;

	/**
	 * Test case for GET /doctor/dashboard.
	 * <p>
	 * Scenario: Valid request to fetch data to display on doctor's dashboard.  
	 * Expectation: Returns HTTP 200 (OK) with the data to display on doctor's dashboard.
	 */
	@Test
	void testViewDashboard() throws Exception {
		Appointment appointment = TestDataUtil.getAppointment();
		Map<String,Object> responseMap = prepareViewDashboardMap(appointment);
		
		when(doctorService.viewDashboard()).thenReturn(responseMap);
		
		mockMvc.perform(get("/doctor/dashboard")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.stats.todaysAppointments").value("6"))
		.andExpect(jsonPath("$.content.todaysSchedule[0].id").value(appointment.getId()))
		.andExpect(jsonPath("$.content.performance.dailyAppointments[0].day").value("2025-08-18"))
		.andExpect(jsonPath("$.content.performance.dailyAppointments[0].booked").value("1"))
		.andExpect(jsonPath("$.content.performance.dailyAppointments[0].approved").value("2"))
		.andExpect(jsonPath("$.content.performance.dailyAppointments[0].completed").value("3"))
		.andExpect(jsonPath("$.content.performance.dailyAppointments[0].cancelled").value("4"));
	}
	
	private Map<String, Object> prepareViewDashboardMap(Appointment appointment) {
		Map<String, Object> stats = new HashMap<>();
		stats.put(IResponseConstants.TODAYS_APPOINTMENTS, 6);
		
		List<Map<String, Object>> todaysAppointments = List.of(AppointmentUtil.prepareViewAllAppointmentsResponse(appointment, IDoctorConstants.DOCTOR_ROLE));

		List<DailyAppointments> dailyAppointments = List.of(
				new DailyAppointments(LocalDate.of(2025, 8, 18), 1L, 2L, 3L, 4L),
				new DailyAppointments(LocalDate.of(2025, 8, 19), 3L, 2L, 1L, 4L),
				new DailyAppointments(LocalDate.of(2025, 8, 20), 4L, 1L, 4L, 3L)
				);
		Map<String, Object> permormance = new HashMap<>();
		permormance.put(IResponseConstants.DAILY_APPOINTMENTS, dailyAppointments);
		
		Map<String, Object> response = new HashMap<>();
		response.put(IResponseConstants.STATS, stats);
		response.put(IResponseConstants.TODAYS_SCHEDULE, todaysAppointments);
		response.put(IResponseConstants.NOTIFICATIONS, new ArrayList<>());
		response.put(IResponseConstants.PERFORMANCE, permormance);
		return response;
	}
}
