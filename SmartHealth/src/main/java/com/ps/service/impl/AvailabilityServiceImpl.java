package com.ps.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IRequestConstants;
import com.ps.dto.request.ADRequest;
import com.ps.dto.request.AGRequest;
import com.ps.dto.request.ASRequest;
import com.ps.dto.request.AVRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.dto.response.AIResponse;
import com.ps.dto.response.AVResponse;
import com.ps.entity.AGPreference;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.SlotInput;
import com.ps.enu.AGMode;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.SlotStatus;
import com.ps.exception.AppointmentException;
import com.ps.exception.AvailabilityException;
import com.ps.exception.DoctorException;
import com.ps.mapper.AGPreferenceMapper;
import com.ps.mapper.AvailabilityMapper;
import com.ps.repo.AGPreferenceRepository;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorLeaveRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.HolidayRepository;
import com.ps.repo.SlotInputRepository;
import com.ps.service.IAvailabilityService;
import com.ps.util.AppointmentUtil;
import com.ps.util.AvailabilityUtil;
import com.ps.util.CommonUtil;
import com.ps.util.JwtUtil;

import jakarta.transaction.Transactional;

/**
 * Service implementation for managing doctor availability in the Smarthealth application.
 * <p>
 * This service handles:
 * <ul>
 *   <li>Generating availability slots (single or recurring)</li>
 *   <li>Viewing availability with filters and pagination</li>
 *   <li>Bulk deletion of slots</li>
 *   <li>Automatic slot generation through scheduled tasks</li>
 *   <li>Checking for existing slots to prevent overlaps</li>
 * </ul>
 * <p>
 * It ensures that only authorized doctors can manage their availability,
 * and validates that slot timings do not conflict with existing entries.
 */
@Service
public class AvailabilityServiceImpl implements IAvailabilityService {

	private static final Logger LOG = LoggerFactory.getLogger(AvailabilityServiceImpl.class);

	@Autowired
	private AvailabilityRepository availabilityRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private AGPreferenceRepository agPreferenceRepository;

	@Autowired
	private SlotInputRepository slotInputRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private AGPreferenceMapper agMapper;
	
	@Autowired
	private DoctorLeaveRepository leaveRepository;
	
	@Autowired
	private AvailabilityMapper availabilityMapper;

	/**
     * Generates availability slots for a doctor based on the provided request.
     * <p>
     * Validates:
     * <ul>
     *   <li>The doctor exists and is authorized</li>
     *   <li>The requested time range does not overlap existing slots</li>
     *   <li>Slot intervals are correctly calculated</li>
     * </ul>
     *
     * @param request an {@link AvailabilityRequest} containing date, time range, slot duration, and recurrence options
     * @return {@code true} if slots are successfully generated, {@code false} otherwise
     * @throws DoctorException if the doctor is not found
     * @throws AvailabilityException if there is an overlap or invalid request
     */
	@Override
	public void generateAvailabilitySlots(AGRequest request) {
		AGMode mode = request.getMode();
		Doctor doctor = doctorRepository.findByEmail(JwtUtil.getEmailFromToken())
				.orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));
		switch (mode) {
			case AUTO:
				setDefaultAGPreference(doctor);
				break;
	
			case CUSTOM_ONE_TIME:
			case CUSTOM_CONTINUOUS:
				generateAvailabilitySlots(request, doctor);
				break;
	
			case MANUAL:
				if (request.getManualSlots() == null) {
					setAGPreference(doctor, request);
				} else {
					generateAvailabilitySlots(request, doctor);
				}
				break;
	
			default:
				throw new AvailabilityException(IExceptionConstants.INVALID_MODE, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Generates availability slots for the specified doctor based on the provided auto-generation (AG) request.
	 * <p>
	 * Supports multiple generation modes:
	 * <ul>
	 *   <li>{@code MANUAL} – Creates availability slots from manually specified date/time ranges.</li>
	 *   <li>{@code CUSTOM_CONTINUOUS} / {@code SCHEDULED} – Creates slots starting from a given date, for a specified number of days ahead.</li>
	 * </ul>
	 * <p>
	 * For non-manual modes, the method:
	 * <ul>
	 *   <li>Skips days marked as holidays or approved leaves for the doctor.</li>
	 *   <li>Generates slots by splitting the day into intervals based on provided start time, end time, and gap duration.</li>
	 *   <li>Updates the last generated date if applicable.</li>
	 *   <li>Stores the doctor’s AG preferences for future automated slot generation.</li>
	 * </ul>
	 *
	 * @param request the {@link AGRequest} containing generation mode, date range, slot intervals, and gap durations
	 * @param doctor  the {@link Doctor} for whom availability slots will be generated
	 * @throws AvailabilityException if slot generation fails or results in conflicting entries
	 */
	@Override
	public void generateAvailabilitySlots(AGRequest request, Doctor doctor) {
		AGMode mode = request.getMode();
		if (mode == AGMode.MANUAL) {
			List<ASRequest> manualSlots = request.getManualSlots();
			for (ASRequest slot : manualSlots)
				generateAvailabilitySlots(slot.getDate(), slot.getFrom(), slot.getTo(), doctor, request.getMode());
		} else {
			LocalDate date = request.getStartDate();
			int daysAhead = (mode==AGMode.CUSTOM_CONTINUOUS || mode==AGMode.SCHEDULED) ? request.getDaysAhead() : (int) ChronoUnit.DAYS.between(date, request.getEndDate());
			
			for (int i=1; i < daysAhead+2; i++) {	
				boolean blockDay = isHolidayOrLeave(date, doctor);
				if (!blockDay) {
					for(SlotInput slot : request.getSlotInputs()) {
						LocalTime startTime = slot.getStartTime();
						LocalTime endTime = slot.getEndTime();
						int gap = slot.getGapInMinutes();
						while (startTime.plusMinutes(gap).compareTo(endTime) <= 0) {
							LocalTime slotEnd = startTime.plusMinutes(gap);
							generateAvailabilitySlots(date, startTime, slotEnd, doctor, request.getMode());
							startTime = slotEnd;
						}
					}
				}
				date = date.plusDays(1);
			}
			if (mode == AGMode.SCHEDULED || mode == AGMode.CUSTOM_CONTINUOUS) {
				request.setLastGeneratedOn(date.minusDays(1));
			}
			setAGPreference(doctor, request);
		}
	}

	/**
	 * Activates the auto-generation (AG) preference for the currently authenticated doctor.
	 * <p>
	 * Ensures that:
	 * <ul>
	 *   <li>The doctor exists and is authenticated.</li>
	 *   <li>The doctor’s profile is complete before activation.</li>
	 *   <li>The doctor has an existing AG preference record.</li>
	 * </ul>
	 * Once activated, the system will consider this doctor for scheduled slot generation.
	 *
	 * @return {@code true} if the preference was successfully activated, {@code false} otherwise
	 * @throws DoctorException if the doctor is not found, profile is incomplete, or AG preference is missing
	 */
	@Override
	public boolean activateAGPreference() {
		Doctor doctor = doctorRepository.findByEmail(JwtUtil.getEmailFromToken())
				.orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));
		if (!doctor.getProfileComplete())
			throw new DoctorException(IExceptionConstants.INCOMPLETE_DOCTOR_PROFILE, HttpStatus.EXPECTATION_FAILED);			

		AGPreference agPreference = agPreferenceRepository.findById(doctor.getId())
				.orElseThrow(() -> new DoctorException(IExceptionConstants.AG_PREFERENCE_NOT_FOUND, HttpStatus.NOT_FOUND));
		agPreference.setIsActive(true);
		return agPreferenceRepository.save(agPreference) != null;
	}

	/**
	 * Sets or updates the default auto-generation (AG) preference for the specified doctor.
	 * <p>
	 * The default configuration includes:
	 * <ul>
	 *   <li>Start date set to the current day.</li>
	 *   <li>Two daily time blocks:
	 *       <ul>
	 *         <li>Morning: 09:00–13:00, 30-minute slots</li>
	 *         <li>Afternoon: 14:00–18:00, 30-minute slots</li>
	 *       </ul>
	 *   </li>
	 *   <li>Preserves {@code lastGeneratedOn} if it is still in the future, otherwise resets it to today.</li>
	 *   <li>Ensures slot input configurations exist or creates them if missing.</li>
	 * </ul>
	 *
	 * @param doctor the {@link Doctor} for whom the default preference will be set
	 * @return {@code true} if the preference was successfully saved, {@code false} otherwise
	 */
	@Override
	public boolean setDefaultAGPreference(Doctor doctor) {
		LocalDate today = LocalDate.now();
		AGPreference preference = new AGPreference();
		preference.setStartDate(today);
		preference.setDoctor(doctor);
		
		Optional<AGPreference> optional = agPreferenceRepository.findById(doctor.getId());
		LocalDate lastGeneratedOn = null;
		if (optional.isPresent()) {
			preference.setId(optional.get().getId());
			lastGeneratedOn = optional.get().getLastGeneratedOn();
		}
		if (lastGeneratedOn == null || lastGeneratedOn.isBefore(today)) {
			preference.setLastGeneratedOn(today);
		} else {
			preference.setLastGeneratedOn(lastGeneratedOn);
		}
		
		List<SlotInput> slotInputs = new ArrayList<>(List.of(new SlotInput(LocalTime.of(9, 0), LocalTime.of(13, 0), 30),
				                                             new SlotInput(LocalTime.of(14, 0), LocalTime.of(18, 0), 30)));
		slotInputs = slotInputs.stream().map(this::findOrCreateSlotInputs).collect(Collectors.toCollection(ArrayList::new));
		preference.setSlotInputs(slotInputs);
		return agPreferenceRepository.save(preference) != null;
	}

	/**
	 * Generates a single availability slot for a given doctor on a specific date and time range.
	 * <p>
	 * The method performs the following checks before saving:
	 * <ul>
	 *   <li>Ensures the slot end time is in the future.</li>
	 *   <li>Ensures there is no existing slot with the same date/time range for the doctor.</li>
	 *   <li>Ensures the new slot does not overlap with any existing slot.</li>
	 * </ul>
	 * If validations pass, the slot is saved to the repository.
	 *
	 * @param date   the date of the availability slot
	 * @param from   the start time of the slot
	 * @param to     the end time of the slot
	 * @param doctor the doctor for whom the slot is being generated
	 * @param mode   the auto-generation mode ({@link AGMode})
	 */
	private void generateAvailabilitySlots(LocalDate date, LocalTime from, LocalTime to, Doctor doctor, AGMode mode) {
		LocalDateTime now = LocalDateTime.now();
		if (LocalDateTime.of(date, to).isAfter(now)) {
			boolean exists = availabilityRepository.existsByDoctorAndDateAndStartTimeAndEndTime(doctor, date, from, to);
			boolean overlap = true;
			if (!exists) {				
				overlap = availabilityRepository.hasOverlappingSlot(doctor.getId(), date, from, to);
			}
			if (!overlap) {
				availabilityRepository.save(AvailabilityUtil.prepareAvailability(doctor, date, from, to, mode));
			} else {
				LOG.info("Availability Slot Overlap For; doctorId: {}, date: {}, startTime: {}, endTIme: {}", doctor.getId(), date, from, to);
			}
		} else {
			LOG.info("Availability Slot generation request is for past; doctorId: {}, date: {}, startTime: {}, endTIme: {}", doctor.getId(), date, from, to);
		}
	}

	/**
	 * Updates and persists the auto-generation (AG) preference for a given doctor based on the provided request.
	 * <p>
	 * Behavior depends on the generation mode:
	 * <ul>
	 *   <li>If mode is {@code SCHEDULED}, only the {@code lastGeneratedOn} date is updated.</li>
	 *   <li>Otherwise, slot input configurations from the request are validated/created and 
	 *       other preference fields are updated via the mapper.</li>
	 * </ul>
	 *
	 * @param doctor  the doctor whose AG preference will be updated
	 * @param request the request containing updated AG configuration
	 * @return {@code true} if the preference was successfully saved, {@code false} otherwise
	 * @throws AvailabilityException if no existing preference record is found for the doctor
	 */
	private boolean setAGPreference(Doctor doctor, AGRequest request) {
		AGPreference preference = agPreferenceRepository.findById(doctor.getId())
				.orElseThrow(() -> new AvailabilityException(IExceptionConstants.PREFERENCE_NOT_FOUND + doctor.getId(), HttpStatus.INTERNAL_SERVER_ERROR));
		if (request.getMode() == AGMode.SCHEDULED) {
			preference.setLastGeneratedOn(request.getLastGeneratedOn());
		} else {
			List<SlotInput> slotInputs = request.getSlotInputs();
			if (slotInputs != null && slotInputs.size() > 0) {
				List<SlotInput> filtered = slotInputs.stream().map(slotInput -> findOrCreateSlotInputs(slotInput)).collect(Collectors.toCollection(ArrayList::new));
				request.setSlotInputs(filtered);
			}
			agMapper.updateAGPreferenceFromRequest(request, preference);
		}
		return agPreferenceRepository.save(preference) != null;
	}

	/**
	 * Retrieves an existing {@link SlotInput} entity matching the given time range and gap duration,
	 * or creates and saves a new one if it does not exist.
	 *
	 * @param slotInput the slot input configuration to find or create
	 * @return an existing or newly created {@link SlotInput} entity
	 */
	private SlotInput findOrCreateSlotInputs(SlotInput slotInput) {
		Optional<SlotInput> optional = slotInputRepository.findByStartTimeAndEndTimeAndGapInMinutes(slotInput.getStartTime(), slotInput.getEndTime(), slotInput.getGapInMinutes());
		if (optional.isPresent()) {
			return optional.get();
		}
		return slotInputRepository.save(slotInput);
	}

	/**
	 * Checks if a given date should be blocked for availability slot generation for a specific doctor.
	 * <p>
	 * A date is considered blocked if:
	 * <ul>
	 *   <li>It falls on a Sunday.</li>
	 *   <li>It exists in the holiday list.</li>
	 *   <li>The doctor has an approved leave for that date.</li>
	 * </ul>
	 *
	 * @param date   the date to check
	 * @param doctor the doctor for whom the date is being validated
	 * @return {@code true} if the date is a holiday, leave day, or Sunday; {@code false} otherwise
	 */
	private boolean isHolidayOrLeave(LocalDate date, Doctor doctor) {
		if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
			return true;
		}
		if (holidayRepository.existsByHolidayDate(date)) {
			return true;
		}
		if (leaveRepository.existsByDoctorAndDay(doctor.getEmail(), date)) {
			return true;
		}
		return false;
	}

	/**
	 * Retrieves the auto-generation (AG) preference for the currently authenticated doctor.
	 *
	 * @return the {@link AGPreferenceResponse} containing the doctor's AG configuration
	 * @throws AvailabilityException if no AG preference record is found for the doctor
	 */
	@Override
	public AGPreferenceResponse getAGPreference() {
		String email = JwtUtil.getEmailFromToken();
		AGPreference agPreference = agPreferenceRepository.findByDoctorEmail(email).orElseThrow(() -> new AvailabilityException(IExceptionConstants.PREFERENCE_NOT_FOUND + email, HttpStatus.NOT_FOUND));
		return agMapper.toResponse(agPreference);
	}

	/**
	 * Retrieves availability slot data for the currently authenticated doctor within the specified date range.
	 * <p>
	 * Results are paginated and returned as a map containing the list of availability responses
	 * and pagination metadata. Throws an exception if no slots are found.
	 *
	 * @param request  the date range and filter criteria for retrieving availability data
	 * @param pageable pagination information (page number, size, sort)
	 * @return a map containing the list of {@link AVResponse} objects and pagination details
	 * @throws AvailabilityException if no availability slots are found in the given date range
	 */
	@Override
	public Map<String, Object> getAvailabilityData(AVRequest request, Pageable pageable) {
		Page<Availability> pages = availabilityRepository.fetchAvailabilitySlotsByDateRange(JwtUtil.getEmailFromToken(), request.getFrom(), request.getTo(), pageable);
		List<Availability> availabilities = pages.getContent();
		if (availabilities.isEmpty())
			throw new AvailabilityException(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, HttpStatus.NO_CONTENT);
		
		List<AVResponse> responseList = availabilities.stream().map(availabilityMapper::toResponse).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(responseList, pages);
	}

	/**
	 * Deletes an availability slot by its ID for the currently authenticated doctor,
	 * provided that the slot is in the {@link SlotStatus#AVAILABLE} state.
	 *
	 * @param id the ID of the availability slot to delete
	 * @return {@code 1} if deletion is successful
	 * @throws AvailabilityException if the slot could not be deleted (e.g., wrong status or doctor mismatch)
	 */
	@Override
	@Transactional
	public int deleteAvailabilitySlot(Integer id) {
		int deleted = availabilityRepository.deleteByIdAndEmailAndStatus(id, JwtUtil.getEmailFromToken(), List.of(SlotStatus.AVAILABLE));
		if (deleted == 1) {
			return deleted;
		} else {
			throw new AvailabilityException(IExceptionConstants.NOT_ABLE_TO_DELETE_SLOT, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	 /**
     * Deletes multiple availability slots in bulk for the current doctor.
     * <p>
     * Only slots with status {@code AVAILABLE} can be deleted; booked or past slots are not removed.
     *
     * @param slotIds a list of availability slot IDs to delete
     * @return the count of deleted slots
     * @throws AvailabilityException if no slots are deleted (e.g., invalid IDs or unauthorized access)
     */
	@Override
	@Transactional
	public int bulkDeleteAvailabilitySlots(ADRequest request) {
		return availabilityRepository.deleteByDateAndTimeRange(JwtUtil.getEmailFromToken(), request.getStartDate(), request.getEndDate(), request.getStartTime(), request.getEndTime(), List.of(SlotStatus.AVAILABLE));
	}

	/**
	 * Retrieves detailed information about a specific availability slot, including
	 * appointment details if applicable.
	 * <p>
	 * If the requester is an admin, additional doctor information and slot creation/update
	 * timestamps are included. Non-admins can only view their own slots.
	 *
	 * @param id            the ID of the availability slot
	 * @param appointmentId optional appointment ID to filter appointment details
	 * @param isAdmin       whether the requester has admin privileges
	 * @return an {@link ADResponse} containing slot and (optionally) appointment details
	 * @throws AvailabilityException if the slot does not exist or if a non-admin tries to access another doctor's slot
	 */
	@Override
	public ADResponse viewSlotDetails(Integer id, Integer appointmentId, boolean isAdmin) {
		Availability availability = availabilityRepository.findById(id)
				.orElseThrow(() -> new AvailabilityException(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, HttpStatus.NO_CONTENT));
		if (!isAdmin && !availability.getDoctor().getEmail().equals(JwtUtil.getEmailFromToken())) {
			throw new AvailabilityException(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, HttpStatus.BAD_REQUEST);
		}
		
		ADResponse adResponse = new ADResponse();
		AVResponse avResponse = availabilityMapper.toResponse(availability);
		if (isAdmin) {
			avResponse.setCreated(availability.getCreatedAt());
			avResponse.setUpdated(availability.getUpdatedAt());
			Map<String, Object> doctorInfo = new HashMap<>();
			doctorInfo.put(IAdminConstants.DOCTOR_ID, availability.getDoctor().getId());
			doctorInfo.put(IAdminConstants.DOCTOR_NAME, availability.getDoctor().getName());
			doctorInfo.put(IAdminConstants.DOCTOR_EMAIL, availability.getDoctor().getEmail());
			doctorInfo.put(IAdminConstants.DOCTOR_PHONE, availability.getDoctor().getPhone());
			adResponse.setAdditionalInfo(doctorInfo);
		}
		adResponse.setAvResponse(avResponse);
		
		List<Appointment> appointments = appointmentRepository.fetchByAvailabilityIdAndAppointmentId(id, appointmentId);
		if (!appointments.isEmpty()) {			
			List<AIResponse> aiResponses = appointments.stream().map(AvailabilityUtil::prepareAIResponse).collect(Collectors.toList());
			adResponse.setAppointmentInfo(aiResponses);
		}
		return adResponse;
	}

	/**
	 * Changes the appointment status for all appointments associated with a given availability slot.
	 * <p>
	 * Non-admin users can only change statuses for their own slots. The update is allowed
	 * only if the appointment's current status is in the allowed list for the requested status.
	 * If the new status is {@link AppointmentStatus#D_CANCELLED}, the associated availability slot
	 * is also marked as {@link SlotStatus#CANCELLED}.
	 *
	 * @param requestMap a map containing the slot ID ({@code IRequestConstants.SLOT_ID})
	 *                   and the new appointment status ({@code IRequestConstants.STATUS})
	 * @param isAdmin    whether the requester has admin privileges
	 * @return a success or failure message describing the outcome of the status change
	 * @throws AvailabilityException if the slot does not exist or if a non-admin tries to change another doctor's slot
	 * @throws AppointmentException  if no appointment is found for the given slot
	 */
	@Override
	@Transactional
	public String changeAvailabilityAppointmentStatus(Map<String, Object> requestMap, boolean isAdmin) {
		AppointmentStatus requestedStatus = (AppointmentStatus) requestMap.get(IRequestConstants.STATUS);
		Integer slotId = (Integer) requestMap.get(IRequestConstants.SLOT_ID);
		
		Availability availability = availabilityRepository.findById(slotId).orElseThrow(() -> new AvailabilityException(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, HttpStatus.NO_CONTENT));
		if (!isAdmin && !availability.getDoctor().getEmail().equals(JwtUtil.getEmailFromToken()))
			throw new AvailabilityException(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, HttpStatus.BAD_REQUEST);
		
		List<AppointmentStatus> allowedCurrentStatuses = AvailabilityUtil.getAllowedCurrentStatuses(requestedStatus);
	    int result = appointmentRepository.updateStatusByAvailabilityId(requestedStatus, slotId, LocalDateTime.now(), allowedCurrentStatuses);
        if (result == 1) {
        	if (requestedStatus == AppointmentStatus.D_CANCELLED) {
        		availabilityRepository.updateStatusById(SlotStatus.CANCELLED, slotId, LocalDateTime.now());
        	}
        	return AppointmentUtil.getSuccessMessageForStausChange(requestedStatus);
        }
        Appointment appointment = appointmentRepository.findTopByAvailabilityIdOrderByIdDesc(slotId).orElseThrow(() -> new AppointmentException(IExceptionConstants.NO_APPOINTMENT_FOR_SLOT, HttpStatus.NO_CONTENT));
        return AvailabilityUtil.getFailureMessageForStatusChange(requestedStatus, appointment.getStatus());
	}
}
