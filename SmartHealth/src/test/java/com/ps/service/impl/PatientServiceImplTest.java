package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ps.constants.IPatientConstants;
import com.ps.constants.IResponseConstants;
import com.ps.entity.Appointment;
import com.ps.enu.AppointmentStatus;
import com.ps.repo.AppointmentRepository;
import com.ps.util.JwtUtil;
import com.ps.util.TestDataUtil;

/**
 * This test class contains unit test case for
 * methods of {@link PatientServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {
	
	@InjectMocks
	private PatientServiceImpl patientService;
	
	@Mock
	private AppointmentRepository appointmentRepository;
	
	private MockedStatic<JwtUtil> jwtUtilMock;

	@BeforeEach
	void setUp() throws Exception {
		jwtUtilMock = Mockito.mockStatic(JwtUtil.class);
	}

	@AfterEach
	void tearDown() throws Exception {
		jwtUtilMock.close();
	}

	/**
	 * Test {@code viewDashboard} method of {@link PatientServiceImpl}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testViewDashboard() {
		Long upcomingAppointments = 1L;
		Long approvedAppointments = 7L;
		Long completedAppointments = 10L;
		Long cancelledAppointments = 3L;
		String email = TestDataUtil.getPatientEmail();
		LocalDate today = LocalDate.now();
		List<Appointment> appointments = List.of(TestDataUtil.getAppointment());
		
		mockJwt(IPatientConstants.PATIENT_ROLE, email);
		when(appointmentRepository.countByPatientEmailAndAvailabilityDateGreaterThanEqual(email, today)).thenReturn(upcomingAppointments);
		when(appointmentRepository.countByPatientEmailAndStatus(email, AppointmentStatus.APPROVED)).thenReturn(approvedAppointments);
		when(appointmentRepository.countByPatientEmailAndStatus(email, AppointmentStatus.COMPLETED)).thenReturn(completedAppointments);
		when(appointmentRepository.countByPatientEmailAndStatusIn(email, List.of(AppointmentStatus.D_CANCELLED, AppointmentStatus.P_CANCELLED))).thenReturn(cancelledAppointments);
		when(appointmentRepository.findByPatientEmailAndAvailabilityDateGreaterThanEqual(email, today)).thenReturn(appointments);
		
		Map<String,Object> viewDashboard = patientService.viewDashboard();
		assertNotNull(viewDashboard.get(IResponseConstants.STATS));
		Map<String, Object> stats = (Map<String, Object>) viewDashboard.get(IResponseConstants.STATS);
		assertEquals(upcomingAppointments, stats.get(IResponseConstants.UPCOMING_APPOINTMENTS));
		assertEquals(approvedAppointments, stats.get(IResponseConstants.APPROVED_APPOINTMENTS));
		assertEquals(completedAppointments, stats.get(IResponseConstants.COMPLETED_APPOINTMENTS));
		assertEquals(cancelledAppointments, stats.get(IResponseConstants.CANCELLED_APPOINTMENTS));
		
		assertNotNull(viewDashboard.get(IResponseConstants.UPCOMING_APPOINTMENTS));
		List<Map<String, Object>> upcomingAppointmentsList = (List<Map<String, Object>>) viewDashboard.get(IResponseConstants.UPCOMING_APPOINTMENTS);
		assertNotNull(upcomingAppointmentsList.get(0));
		Map<String, Object> appointmentData = (Map<String, Object>) upcomingAppointmentsList.get(0);
		assertEquals(appointments.get(0).getId(), appointmentData.get(IResponseConstants.ID));
		assertEquals(appointments.get(0).getAvailability().getDate(), appointmentData.get(IResponseConstants.DATE));
		assertEquals(appointments.get(0).getAvailability().getDoctor().getName(), appointmentData.get(IResponseConstants.DOCTOR_NAME));
		assertEquals(appointments.get(0).getStatus(), appointmentData.get(IResponseConstants.STATUS));
		assertEquals(appointments.get(0).getAvailability().getStartTime(), appointmentData.get(IResponseConstants.FROM));
		assertEquals(appointments.get(0).getAvailability().getEndTime(), appointmentData.get(IResponseConstants.TO));
	}

	private void mockJwt(String role, String email) {
	    jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(role);
	    jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
	}
}
