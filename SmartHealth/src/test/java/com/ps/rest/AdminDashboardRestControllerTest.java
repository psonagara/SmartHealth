package com.ps.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ps.constants.IAdminConstants;
import com.ps.dto.response.DailyAppointments;
import com.ps.entity.Appointment;
import com.ps.entity.DoctorLeave;
import com.ps.service.IAdminDashboardService;
import com.ps.util.AdminUtil;
import com.ps.util.JwtUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link AdminDashboardRestController}.
 * <p>
 * This class verifies the behavior of Admin dashboard related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(AdminDashboardRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminDashboardRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IAdminDashboardService adminDashboardService;

	@MockBean
	private JwtUtil jwtUtil;

	/**
	 * Test case for POST /admin/dashboard/stats.
	 * <p>
	 * Scenario: Valid request to fetch stats for admin dashboard.  
	 * Expectation: Returns HTTP 200 (OK) with the stats related to slots, appointments and users.
	 */
	@Test
	void testGetDashboardStats() throws Exception {
		Map<String, Object> responseMap = prepareDashboardStatsMap();

		when(adminDashboardService.getDashboardStats()).thenReturn(responseMap);

		mockMvc.perform(get("/admin/dashboard/stats")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.slots.totalSlots").value("250"))
		.andExpect(jsonPath("$.content.appointments.totalAppointments").value("180"))
		.andExpect(jsonPath("$.content.patients.totalPatients").value("8"))
		.andExpect(jsonPath("$.content.doctors.totalDoctors").value("12"));
	}

	/**
	 * Test case for POST /admin/dashboard/appointmentTrend.
	 * <p>
	 * Scenario: Valid request to fetch appointment trend for admin dashboard.  
	 * Expectation: Returns HTTP 200 (OK) with the data to display as appointment trend.
	 */
	@Test
	void testGetAppointmentTrend() throws Exception {
		Map<String, Object> responseMap = prepareAppointmentTrendMap();

		when(adminDashboardService.getAppointmentTrend()).thenReturn(responseMap);

		mockMvc.perform(get("/admin/dashboard/appointmentTrend")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.appointmentPerformanceTrend[0].day").value(LocalDate.of(2025, 8, 18).toString()))
		.andExpect(jsonPath("$.content.appointmentPerformanceTrend[0].booked").value("1"))
		.andExpect(jsonPath("$.content.appointmentPerformanceTrend[0].approved").value("2"))
		.andExpect(jsonPath("$.content.appointmentPerformanceTrend[0].completed").value("3"))
		.andExpect(jsonPath("$.content.appointmentPerformanceTrend[0].cancelled").value("4"));

	}
	
	/**
	 * Test case for POST /admin/dashboard/appointmentCount.
	 * <p>
	 * Scenario: Valid request to fetch appointment count for admin dashboard.  
	 * Expectation: Returns HTTP 200 (OK) with the data to display as appointment count.
	 */
	@Test
	void testGetAppointmentCount() throws Exception {
		Map<String, Object> responseMap = prepareAppointmentCountMap();
		
		when(adminDashboardService.getAppointmentCount()).thenReturn(responseMap);
		
		mockMvc.perform(get("/admin/dashboard/appointmentCount")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.appointmentCount[0].day").value("2025-08-18"))
		.andExpect(jsonPath("$.content.appointmentCount[0].total").value("10"));
	}
	
	/**
	 * Test case for POST /admin/dashboard/upcoming-leaves.
	 * <p>
	 * Scenario: Valid request to fetch upcoming leaves for admin dashboard.  
	 * Expectation: Returns HTTP 200 (OK) with the list of upcoming leaves.
	 */
	@Test
	void testGetUpcomingLeaves() throws Exception {
		DoctorLeave doctorLeave = TestDataUtil.getDoctorLeave();
		List<Map<String, Object>> responseList = List.of(AdminUtil.prepareSearchLeavesMap(doctorLeave));
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(IAdminConstants.UPCOMING_LEAVES, responseList);
		
		when(adminDashboardService.getUpcomingLeaves()).thenReturn(responseMap);
		
		mockMvc.perform(get("/admin/dashboard/upcoming-leaves")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.upcomingLeaves[0].doctorInfo.id").value(doctorLeave.getDoctor().getId()))
		.andExpect(jsonPath("$.content.upcomingLeaves[0].doctorInfo.name").value(doctorLeave.getDoctor().getName()))
		.andExpect(jsonPath("$.content.upcomingLeaves[0].leaveInfo.id").value(doctorLeave.getId()));
	}
	

	/**
	 * Test case for POST /admin/dashboard/todays-appointments.
	 * <p>
	 * Scenario: Valid request to fetch today's appointments for admin dashboard.  
	 * Expectation: Returns HTTP 200 (OK) with the list of appointments of today.
	 */
	@Test
	void testGetTodaysAppointments() throws Exception {
		Appointment appointment = TestDataUtil.getAppointment();
		List<Map<String, Object>> responseList = List.of(AdminUtil.prepareSearchAppointments(appointment));
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(IAdminConstants.TODAYS_APPOINTMENTS, responseList);

		when(adminDashboardService.todaysAppointments()).thenReturn(responseMap);

		mockMvc.perform(get("/admin/dashboard/todays-appointments")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.todaysAppointments[0].id").value(appointment.getId()))
		.andExpect(jsonPath("$.content.todaysAppointments[0].slotId").value(appointment.getAvailability().getId()));
	}

	private Map<String, Object> prepareAppointmentCountMap() {
		List<Map<String, Object>> responseList = List.of(
				Map.of(IAdminConstants.DAY, "2025-08-18", IAdminConstants.TOTAL, 10));
		Map<String, Object> response = new HashMap<>();
		response.put(IAdminConstants.APPOINTMENT_COUNT, responseList);
		return response;
	}

	private Map<String, Object> prepareAppointmentTrendMap() {
		List<DailyAppointments> dailyAppointments = List.of(
				new DailyAppointments(LocalDate.of(2025, 8, 18), 1L, 2L, 3L, 4L),
				new DailyAppointments(LocalDate.of(2025, 8, 19), 3L, 2L, 1L, 4L),
				new DailyAppointments(LocalDate.of(2025, 8, 20), 4L, 1L, 4L, 3L)
				);
		Map<String, Object> response = new HashMap<>();
		response.put(IAdminConstants.APPOINTMENT_PERFORMANCE_TREND, dailyAppointments);
		return response;
	}

	private Map<String, Object> prepareDashboardStatsMap() {
		Map<String, Long> slots = new HashMap<>();
		slots.put(IAdminConstants.TOTAL_SLOTS, 250L);

		Map<String, Long> appointments = new HashMap<>();
		appointments.put(IAdminConstants.TOTAL_APPOINTMENTS, 180L);

		Map<String, Long> patients = new HashMap<>();
		patients.put(IAdminConstants.TOTAL_PATIENTS, 8L);

		Map<String, Long> doctors = new HashMap<>();
		doctors.put(IAdminConstants.TOTAL_DOCTORS, 12L);

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(IAdminConstants.SLOTS, slots);
		responseMap.put(IAdminConstants.APPOINTMENTS, appointments);
		responseMap.put(IAdminConstants.PATIENTS, patients);
		responseMap.put(IAdminConstants.DOCTORS, doctors);
		return responseMap;
	}

}
