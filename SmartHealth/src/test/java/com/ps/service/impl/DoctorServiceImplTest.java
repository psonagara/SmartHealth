package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.request.DSRequest;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.DSResponse;
import com.ps.dto.response.DailyAppointments;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.enu.AppointmentStatus;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorRepository;
import com.ps.util.JwtUtil;
import com.ps.util.TestDataUtil;

/**
 * This class contains unit test methods for testing
 * methods of {@link DoctorServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

	@InjectMocks
	private DoctorServiceImpl doctorService;
	
	@Mock
	private DoctorRepository doctorRepository;
	
	@Mock
	private AvailabilityRepository availabilityRepository;
	
	@Mock
	private AppointmentRepository appointmentRepository;
	
	/**
	 * Test {@code searchDoctor} method of {@link DoctorServiceImpl}.
	 */
	@Test
	void testSearchDoctor() {
		DSRequest dsRequest = new DSRequest();
		Pageable pageable = PageRequest.of(0, 10);
		List<Doctor> doctors = List.of(TestDataUtil.getDoctor());
		Page<Doctor> pages = new PageImpl<>(doctors, pageable, 1L);
		
		when(doctorRepository.searchDoctorsWithSlots(dsRequest.getName(), dsRequest.getDepartment(), dsRequest.getSpecialization(), 
				dsRequest.getDegree(), dsRequest.getDate(), LocalDate.now(), pageable)).thenReturn(pages);
		
		Map<String,Object> searchDoctor = doctorService.searchDoctor(dsRequest, pageable);
		assertNotNull(searchDoctor);
		@SuppressWarnings("unchecked")
		List<DSResponse> dsResponse = (List<DSResponse>) searchDoctor.get(IResponseConstants.DATA);
		assertNotNull(dsResponse);
		assertEquals(doctors.get(0).getId(), dsResponse.get(0).getId());
		assertEquals(doctors.get(0).getName(), dsResponse.get(0).getName());
	}
	
	/**
	 * Test {@code viewSlots} method of {@link DoctorServiceImpl}.
	 */
	@Test
	void testViewSlots() {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.DOCTOR_ID, null);
		requestMap.put(IRequestConstants.DATE, null);
		List<Availability> slots = List.of(TestDataUtil.getAvailability());
		
		when(availabilityRepository.viewSlots(eq((Integer) requestMap.get(IRequestConstants.DOCTOR_ID)), eq((LocalDate) requestMap.get(IRequestConstants.DATE)),
																	 eq(LocalDate.now()), any())).thenReturn(slots);
		
		Map<LocalDate,List<AVResponse>> viewSlots = doctorService.viewSlots(requestMap);
		assertNotNull(viewSlots);
		List<AVResponse> list = viewSlots.get(slots.get(0).getDate());
		assertNotNull(list);
		assertEquals(slots.get(0).getId(), list.get(0).getId());
		assertEquals(slots.get(0).getStartTime(), list.get(0).getStartTime());
		assertEquals(slots.get(0).getEndTime(), list.get(0).getEndTime());
	}
	
	/**
	 * Test {@code viewDashboard} method of {@link DoctorServiceImpl}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testViewDashboard() { 
		MockedStatic<JwtUtil> jwtUtilMock = Mockito.mockStatic(JwtUtil.class);
		
		Long todaysAppointments = 4L;
		Long upcomingAppointments = 20L;
		Long pendingApproval = 10L;
		Long cancelledToday = 1L;
		LocalDate today = LocalDate.now();
		String email = TestDataUtil.getDoctorEmail();
		List<Appointment> appointments = List.of(TestDataUtil.getAppointment());
		List<DailyAppointments> dailyAppointments = TestDataUtil.getDailyAppointments();
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
		when(appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDate(email, today)).thenReturn(todaysAppointments);
		when(appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDateGreaterThanEqual(email, today)).thenReturn(upcomingAppointments);
		when(appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDateGreaterThanEqualAndStatus(email, today, AppointmentStatus.BOOKED)).thenReturn(pendingApproval);
		when(appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDateAndStatusIn(email, today, List.of(AppointmentStatus.D_CANCELLED, AppointmentStatus.P_CANCELLED))).thenReturn(cancelledToday);
		when(appointmentRepository.findByAvailabilityDoctorEmailAndAvailabilityDate(email, today)).thenReturn(appointments);
		when(appointmentRepository.findAppointmentsTrendsByDate(email, today.minusDays(4), today)).thenReturn(dailyAppointments);
		
		Map<String,Object> viewDashboard = doctorService.viewDashboard();
		assertNotNull(viewDashboard);
		Map<String,Object> stats = (Map<String,Object>) viewDashboard.get(IResponseConstants.STATS);
		assertNotNull(stats);
		assertEquals(todaysAppointments, stats.get(IResponseConstants.TODAYS_APPOINTMENTS));
		assertEquals(upcomingAppointments, stats.get(IResponseConstants.UPCOMING_APPOINTMENTS));
		assertEquals(pendingApproval, stats.get(IResponseConstants.PENDING_APPROVALS));
		assertEquals(cancelledToday, stats.get(IResponseConstants.CANCELLATIONS_TODAY));

		List<Map<String, Object>> todaysAppointmentsList = (List<Map<String, Object>>) viewDashboard.get(IResponseConstants.TODAYS_SCHEDULE);
		assertNotNull(todaysAppointmentsList);
		Map<String, Object> appointmentData = todaysAppointmentsList.get(0);
		assertNotNull(appointmentData);
		assertEquals(appointments.get(0).getId(), appointmentData.get(IResponseConstants.ID));
		assertEquals(appointments.get(0).getAvailability().getId(), appointmentData.get(IResponseConstants.SLOT_ID));
		assertEquals(appointments.get(0).getAvailability().getDate(), appointmentData.get(IResponseConstants.DATE));
		
		Map<String, Object> permormance = (Map<String, Object>) viewDashboard.get(IResponseConstants.PERFORMANCE);
		assertNotNull(permormance);
		List<DailyAppointments> actualDailyAppointments = (List<DailyAppointments>) permormance.get(IResponseConstants.DAILY_APPOINTMENTS);
		assertNotNull(actualDailyAppointments);
		assertEquals(dailyAppointments.get(0).getBooked(), actualDailyAppointments.get(0).getBooked());
		assertEquals(dailyAppointments.get(0).getApproved(), actualDailyAppointments.get(0).getApproved());
		assertEquals(0, actualDailyAppointments.get(1).getBooked());
		assertEquals(0, actualDailyAppointments.get(1).getApproved());
		assertEquals(0, actualDailyAppointments.get(1).getCancelled());
		assertEquals(0, actualDailyAppointments.get(1).getCompleted());

		jwtUtilMock.close();
	}

}
