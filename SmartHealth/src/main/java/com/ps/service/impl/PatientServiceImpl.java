package com.ps.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ps.constants.IPatientConstants;
import com.ps.constants.IResponseConstants;
import com.ps.entity.Appointment;
import com.ps.enu.AppointmentStatus;
import com.ps.repo.AppointmentRepository;
import com.ps.service.IPatientService;
import com.ps.util.AppointmentUtil;
import com.ps.util.JwtUtil;

/**
 * Service implementation for managing patient-related operations.
 * <p>
 * This class provides business logic for retrieving patient dashboard
 * data, including appointment statistics, upcoming appointments,
 * and notifications.
 */
@Service
public class PatientServiceImpl implements IPatientService {
	
	@Autowired
	private AppointmentRepository appointmentRepository;

	/**
     * Retrieves the dashboard data for the currently authenticated patient.
     * <p>
     * The dashboard includes:
     * <ul>
     *   <li>Statistics for upcoming, approved, completed, and cancelled appointments</li>
     *   <li>A list of upcoming appointments from today onwards</li>
     *   <li>An empty notifications list (placeholder for future use)</li>
     * </ul>
     * The patient is identified using the email extracted from the
     * current JWT token.
     *
     * @return a map containing dashboard data structured with statistics,
     *         upcoming appointments, and notifications
     */
	@Override
	public Map<String, Object> viewDashboard() {
		String email = JwtUtil.getEmailFromToken();
		LocalDate today = LocalDate.now();
		
		Map<String, Object> stats = new HashMap<>();
		stats.put(IResponseConstants.UPCOMING_APPOINTMENTS, appointmentRepository.countByPatientEmailAndAvailabilityDateGreaterThanEqual(email, today));
		stats.put(IResponseConstants.APPROVED_APPOINTMENTS, appointmentRepository.countByPatientEmailAndStatus(email, AppointmentStatus.APPROVED));
		stats.put(IResponseConstants.COMPLETED_APPOINTMENTS, appointmentRepository.countByPatientEmailAndStatus(email, AppointmentStatus.COMPLETED));
		stats.put(IResponseConstants.CANCELLED_APPOINTMENTS, appointmentRepository.countByPatientEmailAndStatusIn(email, List.of(AppointmentStatus.D_CANCELLED, AppointmentStatus.P_CANCELLED)));
		
		List<Appointment> appointments = appointmentRepository.findByPatientEmailAndAvailabilityDateGreaterThanEqual(email, today);
		List<Map<String, Object>> upcomingAppointments = appointments.stream().map(a -> AppointmentUtil.prepareViewAllAppointmentsResponse(a, IPatientConstants.PATIENT_ROLE)).collect(Collectors.toList());
		
		Map<String, Object> response = new HashMap<>();
		response.put(IResponseConstants.STATS, stats);
		response.put(IResponseConstants.UPCOMING_APPOINTMENTS, upcomingAppointments);
		response.put(IResponseConstants.NOTIFICATIONS, new ArrayList<>());
		return response;
	}

}
