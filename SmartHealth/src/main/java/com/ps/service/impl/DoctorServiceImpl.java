package com.ps.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ps.constants.IDoctorConstants;
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
import com.ps.service.IDoctorService;
import com.ps.util.AppointmentUtil;
import com.ps.util.AvailabilityUtil;
import com.ps.util.CommonUtil;
import com.ps.util.JwtUtil;

/**
 * Service implementation for doctor-related operations in the Smarthealth application.
 * <p>
 * Provides methods for searching doctors based on criteria, viewing available
 * appointment slots, and retrieving doctor-specific dashboard statistics.
 */
@Service
public class DoctorServiceImpl implements IDoctorService {

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private AvailabilityRepository availabilityRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	/**
     * Searches for doctors based on the provided search parameters.
     * <p>
     * The method supports filtering by name, department, specialization, degree, and date.
     * The results also include available slots for each doctor.
     *
     * @param dsRequest the search parameters encapsulated in a {@link DSRequest} object
     * @param pageable  pagination and sorting information
     * @return a map containing a list of {@link DSResponse} objects and pagination metadata
     */
	@Override
	public Map<String, Object> searchDoctor(DSRequest dsRequest, Pageable pageable) {
		Page<Doctor> doctors = doctorRepository.searchDoctorsWithSlots(dsRequest.getName(), dsRequest.getDepartment(), dsRequest.getSpecialization(), 
				dsRequest.getDegree(), dsRequest.getDate(), LocalDate.now(), pageable);
		List<DSResponse> responseList = doctors.getContent()
				.stream()
				.map(doctor -> AvailabilityUtil.prepareDSResponse(doctor))
				.collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(responseList, doctors);
	}

	 /**
     * Retrieves available appointment slots for a given doctor, grouped by date.
     * <p>
     * The slots are filtered to exclude past dates/times.
     *
     * @param requestMap a map containing:
     *                   <ul>
     *                     <li>{@link IRequestConstants#DOCTOR_ID} - the doctor's ID</li>
     *                     <li>{@link IRequestConstants#DATE} - the date from which to view slots</li>
     *                   </ul>
     * @return a sorted map where each key is a date and the value is a list of {@link AVResponse} slot details
     */
	@Override
	public Map<LocalDate, List<AVResponse>> viewSlots(Map<String, Object> requestMap) {
		List<Availability> slots = availabilityRepository.viewSlots((Integer) requestMap.get(IRequestConstants.DOCTOR_ID), (LocalDate) requestMap.get(IRequestConstants.DATE),
																	 LocalDate.now(), LocalTime.now());
		Map<LocalDate, List<AVResponse>> responseMap = slots.stream()
				.collect(Collectors.groupingBy(
						Availability::getDate,
						TreeMap::new,
						Collectors.mapping(AvailabilityUtil::prepareAVResponse, Collectors.toList())));
		return responseMap;
	}

	/**
     * Retrieves dashboard statistics and data for the currently authenticated doctor.
     * <p>
     * Includes:
     * <ul>
     *   <li>Today's appointment count</li>
     *   <li>Upcoming appointments</li>
     *   <li>Pending approvals</li>
     *   <li>Cancellations today</li>
     *   <li>Today's appointment schedule</li>
     *   <li>Performance trends over the last 5 days</li>
     * </ul>
     *
     * @return a map containing dashboard statistics, today's schedule, notifications, and performance data
     */
	@Override
	public Map<String, Object> viewDashboard() {
		String email = JwtUtil.getEmailFromToken();
		LocalDate today = LocalDate.now();
		
		Map<String, Object> stats = new HashMap<>();
		stats.put(IResponseConstants.TODAYS_APPOINTMENTS, appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDate(email, today));
		stats.put(IResponseConstants.UPCOMING_APPOINTMENTS, appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDateGreaterThanEqual(email, today));
		stats.put(IResponseConstants.PENDING_APPROVALS, appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDateGreaterThanEqualAndStatus(email, today, AppointmentStatus.BOOKED));
		stats.put(IResponseConstants.CANCELLATIONS_TODAY, appointmentRepository.countByAvailabilityDoctorEmailAndAvailabilityDateAndStatusIn(email, today, List.of(AppointmentStatus.D_CANCELLED, AppointmentStatus.P_CANCELLED)));
		
		List<Appointment> appointments = appointmentRepository.findByAvailabilityDoctorEmailAndAvailabilityDate(email, today);
		List<Map<String, Object>> todaysAppointments = appointments.stream().map(a -> AppointmentUtil.prepareViewAllAppointmentsResponse(a, IDoctorConstants.DOCTOR_ROLE)).collect(Collectors.toList());
		
		LocalDate startDate = today.minusDays(4);
		Map<String, Object> permormance = new HashMap<>();
		List<DailyAppointments> dailyAppointments = appointmentRepository.findAppointmentsTrendsByDate(email, startDate, today);
		Map<String, DailyAppointments> dailyAppointmentsMap = dailyAppointments.stream().collect(Collectors.toMap(DailyAppointments::getDay, s -> s));
		List<DailyAppointments> finalList = new ArrayList<>();
		for (int i=0; i < 5; i++) {
			LocalDate date = startDate.plusDays(i);
			finalList.add(dailyAppointmentsMap.getOrDefault(date.toString(), new DailyAppointments(date)));
		}
		permormance.put(IResponseConstants.DAILY_APPOINTMENTS, finalList);
		
		Map<String, Object> response = new HashMap<>();
		response.put(IResponseConstants.STATS, stats);
		response.put(IResponseConstants.TODAYS_SCHEDULE, todaysAppointments);
		response.put(IResponseConstants.NOTIFICATIONS, new ArrayList<>());
		response.put(IResponseConstants.PERFORMANCE, permormance);
		return response;
	}

}
