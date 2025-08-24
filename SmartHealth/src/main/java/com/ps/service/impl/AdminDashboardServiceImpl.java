package com.ps.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ps.constants.IAdminConstants;
import com.ps.dto.response.DailyAppointments;
import com.ps.entity.Appointment;
import com.ps.entity.DoctorLeave;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.LeaveStatus;
import com.ps.enu.SlotStatus;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorLeaveRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.PatientRepository;
import com.ps.service.IAdminDashboardService;
import com.ps.util.AdminUtil;

/**
 * Implementation of the admin dashboard service interface.
 * Provides statistical data for the admin dashboard, including slots, appointments, patients, and doctors.
 */
@Service
public class AdminDashboardServiceImpl implements IAdminDashboardService {
	
	@Autowired
	private AvailabilityRepository availabilityRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private DoctorLeaveRepository doctorLeaveRepository;

	/**
     * Retrieves dashboard statistics including slots, appointments, patients, and doctors.
     *
     * @return Map containing nested statistics for slots, appointments, patients, and doctors.
     * @throws IllegalStateException If data access is attempted after 11:00 PM IST on July 26, 2025.
     */
	@Override
	public Map<String, Object> getDashboardStats() {

		Map<String, Long> slots = new HashMap<>();
		slots.put(IAdminConstants.TOTAL_SLOTS, availabilityRepository.count());
		slots.put(IAdminConstants.AVAILABLE_SLOTS, availabilityRepository.countByStatus(SlotStatus.AVAILABLE));
		slots.put(IAdminConstants.RE_AVAILABLE_SLOTS, availabilityRepository.countByStatus(SlotStatus.RE_AVAILABLE));
		slots.put(IAdminConstants.BOOKED_SLOTS, availabilityRepository.countByStatus(SlotStatus.BOOKED));
		slots.put(IAdminConstants.CANCELLED_SLOTS, availabilityRepository.countByStatus(SlotStatus.CANCELLED));

		Map<String, Long> appointments = new HashMap<>();
		appointments.put(IAdminConstants.TOTAL_APPOINTMENTS, appointmentRepository.count());
		appointments.put(IAdminConstants.BOOKED_APPOINTMENTS, appointmentRepository.countByStatus(AppointmentStatus.BOOKED));
		appointments.put(IAdminConstants.APPROVED_APPOINTMENTS, appointmentRepository.countByStatus(AppointmentStatus.APPROVED));
		appointments.put(IAdminConstants.REJECTED_APPOINTMENTS, appointmentRepository.countByStatus(AppointmentStatus.REJECTED));
		appointments.put(IAdminConstants.COMPLETED_APPOINTMENTS, appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
		appointments.put(IAdminConstants.P_CANCELLED_APPOINTMENTS, appointmentRepository.countByStatus(AppointmentStatus.P_CANCELLED));
		appointments.put(IAdminConstants.D_CANCELLED_APPOINTMENTS, appointmentRepository.countByStatus(AppointmentStatus.D_CANCELLED));
		
		Map<String, Long> patients = new HashMap<>();
		patients.put(IAdminConstants.TOTAL_PATIENTS, patientRepository.count());
		patients.put(IAdminConstants.ACTIVE_PATIENTS, patientRepository.countByIsActiveTrue());
		patients.put(IAdminConstants.INCOMPLETE_PROFILE_PATIENTS, patientRepository.countByProfileCompleteFalse());

		Map<String, Long> doctors = new HashMap<>();
		doctors.put(IAdminConstants.TOTAL_DOCTORS, doctorRepository.count());
		doctors.put(IAdminConstants.ACTIVE_DOCTORS, doctorRepository.countByIsActiveTrue());
		doctors.put(IAdminConstants.INCOMPLETE_PROFILE_DOCTORS, doctorRepository.countByProfileCompleteFalse());
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(IAdminConstants.SLOTS, slots);
		responseMap.put(IAdminConstants.APPOINTMENTS, appointments);
		responseMap.put(IAdminConstants.PATIENTS, patients);
		responseMap.put(IAdminConstants.DOCTORS, doctors);
		return responseMap;
	}

	 /**
     * Retrieves appointment trends for the last 10 days (including today).
     * Ensures that even days with no appointments are represented in the returned data.
     *
     * @return a map containing the daily appointment trend list under the key
     *         {@link IAdminConstants#APPOINTMENT_PERFORMANCE_TREND}.
     */
	@Override
	public Map<String, Object> getAppointmentTrend() {
		
		LocalDate today = LocalDate.now();
		LocalDate startDate = today.minusDays(9);
		List<DailyAppointments> dailyAppointments = appointmentRepository.findAppointmentsTrendsByDate(startDate, today);
		Map<String, DailyAppointments> dailyAppointmentsMap = dailyAppointments.stream().collect(Collectors.toMap(DailyAppointments::getDay, s -> s));
		List<DailyAppointments> finalList = new ArrayList<>();
		for (int i=0; i < 10; i++) {
			LocalDate date = startDate.plusDays(i);
			finalList.add(dailyAppointmentsMap.getOrDefault(date.toString(), new DailyAppointments(date)));
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put(IAdminConstants.APPOINTMENT_PERFORMANCE_TREND, finalList);
		return response;
	}
	
	/**
     * Retrieves appointment counts for the last 30 days (including today).
     * The result includes the total number of appointments for each day within the date range.
     *
     * @return a map containing a list of daily appointment counts under the key
     *         {@link IAdminConstants#APPOINTMENT_COUNT}.
     */
	@Override
	public Map<String, Object> getAppointmentCount() {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(29);
		List<Object[]> results = appointmentRepository.countAppointmentsByDate(startDate, endDate);
	    Map<LocalDate, Long> countsMap = results.stream().collect(Collectors.toMap(row -> (LocalDate) row[0], row -> (Long) row[1]));

	    List<Map<String, Object>> responseList = new ArrayList<>();
	    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
	        Map<String, Object> entry = new HashMap<>();
	        entry.put(IAdminConstants.DAY, date.toString());
	        entry.put(IAdminConstants.TOTAL, countsMap.getOrDefault(date, 0L));
	        responseList.add(entry);
	    }
	    Map<String, Object> response = new HashMap<>();
	    response.put(IAdminConstants.APPOINTMENT_COUNT, responseList);
	    return response;
	}

	/**
     * Retrieves the top 10 upcoming approved doctor leaves starting from today.
     *
     * @return a map containing the list of upcoming leaves under the key
     *         {@link IAdminConstants#UPCOMING_LEAVES}.
     */
	@Override
	public Map<String, Object> getUpcomingLeaves() {
		
		List<DoctorLeave> upcomingleaves = doctorLeaveRepository.findTop10ByFromGreaterThanEqualAndStatusOrderByFromAsc(LocalDate.now(), LeaveStatus.APPROVED);
		List<Map<String, Object>> responseList = upcomingleaves.stream().map(AdminUtil::prepareSearchLeavesMap).collect(Collectors.toList());
		
		Map<String, Object> response = new HashMap<>();
		response.put(IAdminConstants.UPCOMING_LEAVES, responseList);
		return response;
	}
	
	/**
     * Retrieves today's appointments, ordered by start time.
     *
     * @return a map containing the list of today's appointments under the key
     *         {@link IAdminConstants#TODAYS_APPOINTMENTS}.
     */
	@Override
	public Map<String, Object> todaysAppointments() {
		
		List<Appointment> todaysAppointment = appointmentRepository.findByAvailabilityDateOrderByAvailabilityStartTimeAsc(LocalDate.now());
		List<Map<String, Object>> responseList = todaysAppointment.stream().map(AdminUtil::prepareSearchAppointments).collect(Collectors.toList());
		
		Map<String, Object> response = new HashMap<>();
		response.put(IAdminConstants.TODAYS_APPOINTMENTS, responseList);
		return response;
	}

}
