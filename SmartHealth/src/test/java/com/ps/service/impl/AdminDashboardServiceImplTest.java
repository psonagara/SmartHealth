package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ps.constants.IAdminConstants;
import com.ps.dto.response.DailyAppointments;
import com.ps.dto.response.LeaveResponse;
import com.ps.entity.Appointment;
import com.ps.entity.DoctorLeave;
import com.ps.enu.LeaveStatus;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorLeaveRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.DoctorRepository.DoctorIdNameProjection;
import com.ps.repo.PatientRepository;
import com.ps.util.TestDataUtil;

/**
 * This test class contains unit test case for
 * methods of {@link AdminDashboardServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {
	
	@InjectMocks
	private AdminDashboardServiceImpl adminDashboardService;
	
	@Mock
	private AvailabilityRepository availabilityRepository;
	
	@Mock
	private AppointmentRepository appointmentRepository;
	
	@Mock
	private PatientRepository patientRepository;
	
	@Mock
	private DoctorRepository doctorRepository;
	
	@Mock
	private DoctorLeaveRepository doctorLeaveRepository;

	/**
	 * Test {@link AdminDashboardServiceImpl#getDashboardStats()}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testGetDashboardStats() {
		Long totalSlots = 150L;
		Long totalAppointments = 108L;
		Long totalPatients = 11L;
		Long totalDoctors = 18L;
		
		when(availabilityRepository.count()).thenReturn(totalSlots);
		when(appointmentRepository.count()).thenReturn(totalAppointments);
		when(patientRepository.count()).thenReturn(totalPatients);
		when(doctorRepository.count()).thenReturn(totalDoctors);
		
		Map<String,Object> dashboardStats = adminDashboardService.getDashboardStats();
		Map<String, Long> slots = (Map<String, Long>) dashboardStats.get(IAdminConstants.SLOTS);
		assertEquals(totalSlots, slots.get(IAdminConstants.TOTAL_SLOTS));
		
		Map<String, Long> appointments = (Map<String, Long>) dashboardStats.get(IAdminConstants.APPOINTMENTS);
		assertEquals(totalAppointments, appointments.get(IAdminConstants.TOTAL_APPOINTMENTS));

		Map<String, Long> patients = (Map<String, Long>) dashboardStats.get(IAdminConstants.PATIENTS);
		assertEquals(totalPatients, patients.get(IAdminConstants.TOTAL_PATIENTS));

		Map<String, Long> doctors = (Map<String, Long>) dashboardStats.get(IAdminConstants.DOCTORS);
		assertEquals(totalDoctors, doctors.get(IAdminConstants.TOTAL_DOCTORS));
	}
	
	/**
	 * Test {@link AdminDashboardServiceImpl#getAppointmentTrend()}.
	 */
	@Test
	void testGetAppointmentTrend() {
		LocalDate today = LocalDate.now();
		LocalDate startDate = today.minusDays(9);
		List<DailyAppointments> dailyAppointments = TestDataUtil.getDailyAppointments();
		
		when(appointmentRepository.findAppointmentsTrendsByDate(startDate, today)).thenReturn(dailyAppointments);
		
		Map<String,Object> appointmentTrend = adminDashboardService.getAppointmentTrend();
		@SuppressWarnings("unchecked")
		List<DailyAppointments> actualDailyAppointments = (List<DailyAppointments>) appointmentTrend.get(IAdminConstants.APPOINTMENT_PERFORMANCE_TREND);
		assertEquals(10, actualDailyAppointments.size());
		assertEquals(0, actualDailyAppointments.get(0).getBooked());
		assertEquals(0, actualDailyAppointments.get(0).getApproved());
		assertEquals(0, actualDailyAppointments.get(0).getCancelled());
		assertEquals(0, actualDailyAppointments.get(0).getCompleted());
		assertEquals(dailyAppointments.get(2).getBooked(), actualDailyAppointments.get(8).getBooked());
		assertEquals(dailyAppointments.get(2).getApproved(), actualDailyAppointments.get(8).getApproved());
	}
	
	/**
	 * Test {@link AdminDashboardServiceImpl#getAppointmentCount()}.
	 */
	@Test
	void testGetAppointmentCount() { 
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(29);
		List<Object[]> appointmentCountList = prepareAppointmentCountList();
		
		when(appointmentRepository.countAppointmentsByDate(startDate, endDate)).thenReturn(appointmentCountList);
		
		Map<String,Object> appointmentCount = adminDashboardService.getAppointmentCount();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> actualList= (List<Map<String, Object>>) appointmentCount.get(IAdminConstants.APPOINTMENT_COUNT);
		assertEquals(30, actualList.size());
		Map<String, Object> todaysCount = actualList.get(29);
		assertEquals(appointmentCountList.get(0)[0].toString(), todaysCount.get(IAdminConstants.DAY));
		assertEquals(appointmentCountList.get(0)[1], todaysCount.get(IAdminConstants.TOTAL));
		assertEquals(startDate.toString(), actualList.get(0).get(IAdminConstants.DAY));
		assertEquals(0L, actualList.get(0).get(IAdminConstants.TOTAL));
	}
	
	/**
	 * Test {@link AdminDashboardServiceImpl#getUpcomingLeaves()}.
	 */
	@Test
	void testGetUpcomingLeaves() {
		DoctorLeave doctorLeave = TestDataUtil.getDoctorLeave();
		doctorLeave.setStatus(LeaveStatus.APPROVED);
		List<DoctorLeave> doctorLeaves = List.of(doctorLeave);
		
		when(doctorLeaveRepository.findTop10ByFromGreaterThanEqualAndStatusOrderByFromAsc(LocalDate.now(), LeaveStatus.APPROVED)).thenReturn(doctorLeaves);
		
		Map<String,Object> upcomingLeaves = adminDashboardService.getUpcomingLeaves();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> actualList = (List<Map<String, Object>>) upcomingLeaves.get(IAdminConstants.UPCOMING_LEAVES);
		Map<String, Object> updcomingLeaveMap = actualList.get(0);
		LeaveResponse leaveResponse = (LeaveResponse) updcomingLeaveMap.get(IAdminConstants.LEAVE_INFO);
		DoctorIdNameProjection doctorIdName = (DoctorIdNameProjection) updcomingLeaveMap.get(IAdminConstants.DOCTOR_INFO);
		
		assertEquals(doctorLeave.getId(), leaveResponse.getId());
		assertEquals(doctorLeave.getStatus(), leaveResponse.getStatus());
		assertEquals(doctorLeave.getDoctor().getId(), doctorIdName.getId());
		assertEquals(doctorLeave.getDoctor().getName(), doctorIdName.getName());
	}
	
	/**
	 * Test {@link AdminDashboardServiceImpl#todaysAppointments()}.
	 */
	@Test
	void testTodaysAppointments() {
		Appointment appointment = TestDataUtil.getAppointment();
		List<Appointment> appointments = List.of(appointment);
		
		when(appointmentRepository.findByAvailabilityDateOrderByAvailabilityStartTimeAsc(LocalDate.now())).thenReturn(appointments);
		
		Map<String,Object> todaysAppointments = adminDashboardService.todaysAppointments();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> response = (List<Map<String, Object>>) todaysAppointments.get(IAdminConstants.TODAYS_APPOINTMENTS);
		Map<String, Object> resultMap = response.get(0);
		
		assertEquals(appointment.getId(), resultMap.get(IAdminConstants.ID));		
		assertEquals(appointment.getAvailability().getId(), resultMap.get(IAdminConstants.SLOT_ID));
		assertEquals(appointment.getAvailability().getDoctor().getName(), resultMap.get(IAdminConstants.DOCTOR_NAME));
		assertEquals(appointment.getPatient().getName(), resultMap.get(IAdminConstants.PATIENT_NAME));
		assertEquals(appointment.getAvailability().getDate(), resultMap.get(IAdminConstants.DATE));
		assertEquals(appointment.getAvailability().getStartTime(), resultMap.get(IAdminConstants.FROM));
		assertEquals(appointment.getAvailability().getEndTime(), resultMap.get(IAdminConstants.TO));
		assertEquals(appointment.getStatus(), resultMap.get(IAdminConstants.STATUS));
		assertEquals(appointment.getCreatedAt(), resultMap.get(IAdminConstants.BOOKED_ON));
	}
	
	private List<Object[]> prepareAppointmentCountList() {
		Object[] todaysCount = new Object[2];
		todaysCount[0] = LocalDate.now();
		todaysCount[1] = 36L;

		List<Object[]> appointmentCountList = new ArrayList<>();
		appointmentCountList.add(todaysCount);
		return appointmentCountList;
	}
}
