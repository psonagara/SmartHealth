package com.ps.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.SubProfileDTO;
import com.ps.dto.request.AppointmentRequest;
import com.ps.dto.request.DSRequest;
import com.ps.dto.response.AIResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.ApDResponse;
import com.ps.dto.response.DSResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.Patient;
import com.ps.entity.SubProfile;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.SlotStatus;
import com.ps.exception.AppointmentException;
import com.ps.exception.AvailabilityException;
import com.ps.exception.DoctorException;
import com.ps.exception.PatientException;
import com.ps.exception.ProfileException;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.PatientRepository;
import com.ps.repo.SubProfileRepository;
import com.ps.service.IAppointmentService;
import com.ps.service.IDoctorService;
import com.ps.util.AppointmentUtil;
import com.ps.util.AvailabilityUtil;
import com.ps.util.CommonUtil;
import com.ps.util.JwtUtil;

import jakarta.transaction.Transactional;

/**
 * Implementation of the {@link IAppointmentService} interface for managing appointment operations.
 * <p>
 * This service handles booking, viewing, and updating appointments, as well as retrieving detailed
 * information about appointments for both patients and doctors. It also includes validation to
 * ensure correct role-based access and appointment status transitions.
 * </p>
 */
@Service
public class AppointmentServiceImpl implements IAppointmentService {

	@Autowired
	private AvailabilityRepository availabilityRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private SubProfileRepository subProfileRepository;
	
	@Autowired
	private IDoctorService doctorService;

	/**
     * Retrieves detailed information about a specific slot, including slot details,
     * doctor information and patient information.
     *
     * @param requestMap a map containing {@code SLOT_ID} and {@code DOCTOR_ID} to identify the slot and doctor
     * @return an {@link ApDResponse} containing aggregated slot, doctor, and patient data
     * @throws AvailabilityException if the slot does not exist or belongs to a different doctor
     * @throws PatientException if the patient is not found in the session
     */
	@Override
	public ApDResponse viewDetails(Map<String, Object> requestMap) {
		Availability availability = availabilityRepository.fetchByIdAndStatusIn((Integer) requestMap.get(IRequestConstants.SLOT_ID)).orElseThrow(() -> new AvailabilityException(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, HttpStatus.BAD_REQUEST));
		Doctor doctor = availability.getDoctor();
		if (!doctor.getId().equals((Integer) requestMap.get(IRequestConstants.DOCTOR_ID)))
			throw new AvailabilityException(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, HttpStatus.BAD_REQUEST);

		Patient patient = patientRepository.findByEmail(JwtUtil.getEmailFromToken()).orElseThrow(() -> new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
		return AppointmentUtil.prepareApDResponse(doctor, availability, patient);
	}

	/**
     * Books an appointment for a patient (or their sub-profile) with a specific doctor
     * for a given slot.
     * <p>
     * Validates that the slot is available, the booking is not in the past, and
     * the patient is authorized to book.
     * </p>
     *
     * @param request an {@link AppointmentRequest} containing slot, doctor, patient, and optional sub-profile details
     * @return {@code true} if the appointment is successfully booked, {@code false} otherwise
     * @throws PatientException if the session does not match the booking request
     * @throws AvailabilityException if the slot is not found, belongs to another doctor, or is in the past
     * @throws ProfileException if sub-profile data is invalid or mismatched
     */
	@Override
	@Transactional
	public boolean bookAppointment(AppointmentRequest request) {
		Patient patient = patientRepository.findById(request.getPatientId()).orElseThrow(() -> new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
		if (!patient.getEmail().equals(JwtUtil.getEmailFromToken()))
			throw new PatientException(IExceptionConstants.SESSION_MISMATCHED, HttpStatus.UNAUTHORIZED);
		
		Availability availability = availabilityRepository.fetchByIdAndStatusIn(request.getSlotId()).orElseThrow(() -> new AvailabilityException(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, HttpStatus.BAD_REQUEST));
		Doctor doctor = doctorRepository.findById(request.getDoctorId()).orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.BAD_REQUEST));
		if (!availability.getDoctor().getId().equals(doctor.getId()))
			throw new AvailabilityException(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, HttpStatus.BAD_REQUEST);
		
		LocalDate date = availability.getDate();
		LocalDate today = LocalDate.now();
		if (date.isBefore(today) || (date.isEqual(today) && availability.getEndTime().isBefore(LocalTime.now()))) {
			throw new AvailabilityException(IExceptionConstants.CANT_BOOK_PAST_SLOTS, HttpStatus.BAD_REQUEST);
		} 
		
		availability.setStatus(SlotStatus.BOOKED);
		SubProfile subProfile = null;
		if (request.getIsSubProfile()) {
			SubProfileDTO subProfileDTO = request.getSubProfile();
			Integer id = subProfileDTO.getId();
			if (id != null) {
				subProfile = subProfileRepository.findById(id).orElseThrow(() -> new ProfileException(IExceptionConstants.SUB_PROFILE_NOT_FOUND, HttpStatus.BAD_REQUEST));
				if (!subProfile.getPatient().getId().equals(patient.getId())) {
					throw new PatientException(IExceptionConstants.SUB_PROFILE_MISMATHCED, HttpStatus.UNAUTHORIZED);
				}
			}
			subProfile = subProfileRepository.save(AppointmentUtil.prepareSubProfile(subProfileDTO, patient));
		}
		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setAvailability(availability);
		appointment.setSubProfile(subProfile);
		appointment.setNote(request.getNote());
		return appointmentRepository.save(appointment) != null && availabilityRepository.save(availability) != null;
	}

	/**
     * Retrieves a paginated list of appointments based on the current user's role (doctor or patient)
     * and the provided filters such as name, date, slot ID, and appointment status.
     *
     * @param requestMap filter parameters for searching appointments
     * @param pageable   pagination and sorting information
     * @return a map containing the filtered appointments and pagination details
     * @throws AppointmentException if no appointments are found
     * @throws ProfileException if the user role is invalid
     */
	@Override
	public Map<String, Object> viewAllAppointments(Map<String, Object> requestMap, Pageable pageable) {
		String email = JwtUtil.getEmailFromToken();
		String role = JwtUtil.getPrimaryRoleFromToken();
		String name = requestMap.get(IRequestConstants.NAME) != null ? (String) requestMap.get(IRequestConstants.NAME) : null;
		Integer slotId = (Integer) requestMap.get(IRequestConstants.SLOT_ID);
		LocalDate date = (LocalDate) requestMap.get(IRequestConstants.DATE);
		AppointmentStatus status = (AppointmentStatus) requestMap.get(IRequestConstants.STATUS);

		Page<Appointment> pages;
		switch (role) {
			case IPatientConstants.PATIENT_ROLE:
				pages = appointmentRepository.filterAppointmentsForPatient(email, name, date, status, pageable);
				break;
				
			case IDoctorConstants.DOCTOR_ROLE:
				pages = appointmentRepository.filterAppointemtnsForDoctor(email, name, date, status, slotId, pageable);
				break;
	
			default:
				throw new ProfileException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
		}

		List<Appointment> appointments = pages.getContent();
		if (appointments.isEmpty()) {
			throw new AppointmentException(IExceptionConstants.NO_APPOINTMENTS_FOUND, HttpStatus.NO_CONTENT);
		}
		List<Map<String, Object>> result = appointments.stream().map(a -> AppointmentUtil.prepareViewAllAppointmentsResponse(a, role)).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(result, pages);
	}

	/**
     * Changes the status of an appointment (e.g., approve, cancel, complete).
     * <p>
     * Ensures valid status transitions based on the role of the requester and optionally
     * allows admin-level overrides.
     * </p>
     *
     * @param id        the ID of the appointment
     * @param newStatus the new {@link AppointmentStatus} to set
     * @param isAdmin   {@code true} if the action is performed by an admin, bypassing some validations
     * @return a success message describing the status change
     * @throws AppointmentException if the appointment is not found or update fails
     * @throws ProfileException if the requester does not have access
     */
	@Override
	@Transactional
	public String changeAppointmentStatus(Integer id, AppointmentStatus newStatus, boolean isAdmin) {
		String role = JwtUtil.getPrimaryRoleFromToken();
		String email = JwtUtil.getEmailFromToken();
		Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentException(IExceptionConstants.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
		if (!isAdmin) {			
			AppointmentUtil.validateAccessForAppointment(role, appointment, email);
		}
		
		AppointmentStatus currentStatus = appointment.getStatus();
		if (!AppointmentUtil.isStatusTransitionValid(role, currentStatus, newStatus)) {
			return String.format(IExceptionConstants.APPOINTMENT_STATUS_CHANGE_FAIL, newStatus, currentStatus);
	    }
		
		int changed = appointmentRepository.updateStatusByAppId(newStatus, id, LocalDateTime.now());
	    if (changed != 1) {
	        throw new AppointmentException(IExceptionConstants.APPOINTMENT_UPDATE_STATUS_FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    updateAvailabilityStatusIfRequired(newStatus, appointment.getAvailability().getId());
		return AppointmentUtil.getSuccessMessageForStausChange(newStatus);
	}

	/**
     * Retrieves detailed information for a specific appointment, including appointment info
     * and doctor details.
     *
     * @param id the ID of the appointment
     * @return a map containing appointment and doctor information
     * @throws AppointmentException if the appointment is not found
     * @throws PatientException if the appointment does not belong to the current patient
     */
	@Override
	public Map<String, Object> viewAppointmentDetails(Integer id) {
		Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentException(IExceptionConstants.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
		if (!appointment.getPatient().getEmail().equals(JwtUtil.getEmailFromToken()))
			throw new PatientException(IExceptionConstants.DO_NOT_ACCESS_TO_APPOINTMENT, HttpStatus.UNAUTHORIZED);
		
		AIResponse aiResponse = AvailabilityUtil.prepareAIResponse(appointment);
		DSResponse dsResponse = AvailabilityUtil.prepareDSResponse(appointment.getAvailability().getDoctor());
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(IResponseConstants.APPOINTMENT_INFO, aiResponse);
		responseMap.put(IResponseConstants.DOCTOR_INFO, dsResponse);
		return responseMap;
	}
	
	/**
     * Updates the availability status of a slot if an appointment is cancelled.
     *
     * @param newStatus      the new {@link AppointmentStatus} of the appointment
     * @param availabilityId the ID of the availability slot
     */
	private void updateAvailabilityStatusIfRequired(AppointmentStatus newStatus, Integer availabilityId) {
	    LocalDateTime now = LocalDateTime.now();
	    if (newStatus == AppointmentStatus.P_CANCELLED)
	        availabilityRepository.updateStatusById(SlotStatus.RE_AVAILABLE, availabilityId, now);
	    else if (newStatus == AppointmentStatus.D_CANCELLED)
	        availabilityRepository.updateStatusById(SlotStatus.CANCELLED, availabilityId, now);
	}

	/**
	 * Searches for doctors based on the given search criteria in order to book an appointment.
	 * <p>
	 * Delegates the search operation to {@link IDoctorService#searchDoctor(DSRequest, Pageable)},
	 * which applies filtering, sorting, and pagination as per the provided parameters.
	 * </p>
	 *
	 * @param dsRequest the search criteria object containing filters such as specialization, location, etc.
	 * @param pageable  pagination and sorting information
	 * @return a {@link Map} containing search results and possibly additional metadata
	 *
	 * @see IDoctorService#searchDoctor(DSRequest, Pageable)
	 */
	@Override
	public Map<String, Object> searchDoctorsToBookAppointment(DSRequest dsRequest, Pageable pageable) {
		return doctorService.searchDoctor(dsRequest, pageable);
	}

	/**
	 * Retrieves available appointment slots for a doctor based on the provided request parameters.
	 * <p>
	 * Delegates the slot retrieval to {@link IDoctorService#viewSlots(Map)}, which processes the request
	 * to return a mapping of dates to lists of available slot details.
	 * </p>
	 *
	 * @param requestMap a map containing parameters required to view slots (e.g., doctor ID, date range, filters)
	 * @return a {@link Map} where keys are {@link LocalDate} objects representing dates, and values are
	 *         lists of {@link AVResponse} representing available slots on those dates
	 *
	 * @see IDoctorService#viewSlots(Map)
	 */
	@Override
	public Map<LocalDate, List<AVResponse>> viewSlotsToBookAppointment(Map<String, Object> requestMap) {
		return doctorService.viewSlots(requestMap);
	}
}
