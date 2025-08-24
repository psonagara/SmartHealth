package com.ps.rest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constants.IPathConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.constants.IValidationConstants;
import com.ps.dto.request.AppointmentRequest;
import com.ps.dto.request.DSRequest;
import com.ps.enu.AppointmentStatus;
import com.ps.service.IAppointmentService;
import com.ps.util.CommonUtil;

import jakarta.validation.Valid;

/**
 * This rest controller is used to manage APIs related to appointments.
 * 
 */
@RestController
@RequestMapping(IPathConstants.APPOINTMENT)
public class AppointmentRestController {
	
	@Autowired
	private IAppointmentService appointmentService;

	/**
	 * Retrieve information required during booking of an appointment, 
	 * like doctor information and slot information.
	 * 
	 * @param doctorId id of doctor with whom booking appointment
	 * @param slotId id of slot for which booking appointment
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_DETAILS_PATH)
	public ResponseEntity<?> viewDetails(@RequestParam(IRequestConstants.DOCTOR_ID) Integer doctorId, @RequestParam(IRequestConstants.ID) Integer slotId) {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.DOCTOR_ID, doctorId);
		requestMap.put(IRequestConstants.SLOT_ID, slotId);
		return CommonUtil.prepareResponseWithContent(appointmentService.viewDetails(requestMap), HttpStatus.OK);
	}
	
	/**
	 * API call which handles appointment booking. 
	 * 
	 * @param request contains information required for booking of appointment.
	 * @return
	 */
	@PostMapping(IPathConstants.BOOK_APPOINTMENT_PATH)
	public ResponseEntity<?> bookAppointment(@RequestBody @Validated AppointmentRequest request) {
		boolean booked = appointmentService.bookAppointment(request);
		String message = booked ? IResponseConstants.APPOINTMENT_BOOKED_SUCCESSFUL : IResponseConstants.APPOINTMENT_BOOKED_FAIL;
		HttpStatus status = booked ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}
	
	/**
	 * Retrieve appointments with pagination based on role and email id of user.
	 * User can filter out based on several parameters, however all parameters are optional.
	 * 
	 * @param name name of patient or doctor based on role
	 * @param date view appointments or a particular date
	 * @param status view appointments based on given status
	 * @param slotId filter appointments based on slot id
	 * @param pageable pagination details
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_ALL_APPOINTMENTS)
	public ResponseEntity<?> viewAllAppointments(@RequestParam(name = IRequestConstants.NAME, required = false) String name, 
												 @RequestParam(name = IRequestConstants.DATE, required = false) LocalDate date, 
												 @RequestParam(name = IRequestConstants.STATUS, required = false) AppointmentStatus status,
												 @RequestParam(name = IRequestConstants.SLOT_ID, required = false) Integer slotId, 
												 @PageableDefault(page = 0, size = 10) Pageable pageable) {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.NAME, name);
		requestMap.put(IRequestConstants.DATE, date);
		requestMap.put(IRequestConstants.STATUS, status);
		requestMap.put(IRequestConstants.SLOT_ID, slotId);
		pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		return CommonUtil.prepareResponseWithContent(appointmentService.viewAllAppointments(requestMap, pageable), HttpStatus.OK);
	}
	
	/**
	 * Change status of appointment to given new status based on id of appointment.
	 * 
	 * @param id id of appointment for which status change requested
	 * @param status new status
	 * @return
	 */
	@PatchMapping(IPathConstants.CHANGE_APPOINTMENT_STATUS)
	public ResponseEntity<?> changeAppointmentStatus(@PathVariable(IRequestConstants.APPOINTMENT_ID) Integer id, @RequestParam(IRequestConstants.STATUS) AppointmentStatus status) {
		return CommonUtil.prepareResponseWithMessage(appointmentService.changeAppointmentStatus(id, status, false), HttpStatus.OK);
	}
	
	/**
	 * View details of particular appointment based on given appointment id by patient.
	 * 
	 * @param id unique number to identify appointment
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_APPOINTMENT_DETAILS)
	public ResponseEntity<?> viewAppointmentDetails(@PathVariable(IRequestConstants.APPOINTMENT_ID) Integer id) {
		return CommonUtil.prepareResponseWithContent(appointmentService.viewAppointmentDetails(id), HttpStatus.OK);
	}
	
	 /**
     * Searches for doctors based on request parameters and returns the results
     * with pagination.
     * <p>
     * If no request body is provided, a default empty {@link DSRequest} is used.
     * Dates in the past are not allowed for the search and will return a
     * {@code BAD_REQUEST} response.
     *
     * @param request  the search parameters (optional); may include name, department, specialization, degree, and date
     * @param pageable pagination details; defaults to first page with 10 items
     * @return {@link ResponseEntity} containing the paginated list of matching doctors or an error message
     */
	@PostMapping(IPathConstants.SEARCH_DOCTOR_PATH)
	public ResponseEntity<?> searchDoctorsToBookAppointment(@RequestBody(required = false) @Valid DSRequest request, 
										   @PageableDefault(page = 0, size = 10) Pageable pageable) {
		
		request = Objects.requireNonNullElse(request, new DSRequest());
		if (request.getDate() != null && request.getDate().isBefore(LocalDate.now())) {
			return CommonUtil.prepareResponseWithMessage(IValidationConstants.SEARCH_DATE_CONSTRAINT, HttpStatus.BAD_REQUEST);
		}
		pageable = PageRequest.of(pageable.getPageNumber(), 10);
		return CommonUtil.prepareResponseWithContent(appointmentService.searchDoctorsToBookAppointment(request, pageable), HttpStatus.OK);
	}
	
	 /**
     * Retrieves available appointment slots for a specific doctor.
     * <p>
     * Optionally, a date can be provided to view slots for that particular day;
     * if omitted, slots from the current date onward are returned.
     *
     * @param id   the unique identifier of the doctor
     * @param date the date for which slots are requested (optional)
     * @return {@link ResponseEntity} containing a map of dates to slot details
     */
	@GetMapping(IPathConstants.SLOTS)
	public ResponseEntity<?> viewSlotsToBookAppointment(@RequestParam(IRequestConstants.DOCTOR_ID) Integer id, @RequestParam(name =IRequestConstants.DATE, required = false) LocalDate date) {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.DOCTOR_ID, id);
		requestMap.put(IRequestConstants.DATE, date);
		return CommonUtil.prepareResponseWithContent(appointmentService.viewSlotsToBookAppointment(requestMap), HttpStatus.OK);
	}
}
