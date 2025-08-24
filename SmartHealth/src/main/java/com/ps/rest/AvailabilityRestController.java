package com.ps.rest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ps.dto.request.ADRequest;
import com.ps.dto.request.AGRequest;
import com.ps.dto.request.AVRequest;
import com.ps.enu.AppointmentStatus;
import com.ps.service.IAvailabilityService;
import com.ps.util.CommonUtil;

/**
 * This rest controller is used to manage APIs related to 
 * Availability slots and it's management like status change, deletion etc.
 */
@RestController
@RequestMapping(IPathConstants.AVAILABILITY_PATH)
public class AvailabilityRestController {

	@Autowired
	private IAvailabilityService availabilityService;

	/**
	 * Create new slots or/and save slots generation preferences based on 
	 * mode of creation.
	 * 
	 * @param request contains data to generate slots or/and save preference
	 * @return
	 */
	@SuppressWarnings("incomplete-switch")
	@PostMapping(IPathConstants.GENERATE_SLOTS_PATH)
	public ResponseEntity<?> createAvailabilitySlots(@RequestBody @Validated AGRequest request) {		
		availabilityService.generateAvailabilitySlots(request);
		
		String message = "";
		switch (request.getMode()) {
			case AUTO:
				message = IResponseConstants.AVAILABILITY_PREFERENCE_AUTO;
				break;
				
			case MANUAL:
				message = (request.getManualSlots() == null || request.getManualSlots().isEmpty())
				          ? IResponseConstants.AVAILABILITY_PREFERENCE_MANUAL : IResponseConstants.AVAILABILITY_GENERATION_MANUAL;
				break;
				
			case CUSTOM_ONE_TIME:
				message = IResponseConstants.AVAILABILITY_GENERATION_CUSTOM_ONE_TIME;
				break;
				
			case CUSTOM_CONTINUOUS:
				message = IResponseConstants.AVAILABILITY_GENERATION_CUSTOM_CONTINUOUS;
				break;
		}
		return CommonUtil.prepareResponseWithMessage(message, HttpStatus.OK);
	}

	/**
	 * API used to activate availability slots generation process
	 * 
	 * @return
	 */
	@PatchMapping(IPathConstants.ACTIVATE_AVAILABILITY_GENERATION_PATH)
	public ResponseEntity<?> activateAvailabilityGeneration() {
		boolean activated = availabilityService.activateAGPreference();
		String message = activated ? IResponseConstants.AVAILABILITY_GENERATION_ACTIVATED : IResponseConstants.AVAILABILITY_GENERATION_ACTIVATE_FAIL;
		HttpStatus status = activated ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}

	/**
	 * Retrieve information about Availability Generation Preference
	 * for particular doctor based on email.
	 * 
	 * @return
	 */
	@GetMapping(IPathConstants.GET_AG_PREFERENCE)
	public ResponseEntity<?> getAGPreference() {
		return CommonUtil.prepareResponseWithContent(availabilityService.getAGPreference(), HttpStatus.OK);
	}

	/**
	 * View availability slots based on given input parameters for filter(optional)
	 * with pagination.
	 * 
	 * @param from date range starting
	 * @param to date range ending
	 * @param pageable pagination details
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_SLOTS_PATH)
	public ResponseEntity<?> getAvailabilityData(@RequestParam(required = false, name = IRequestConstants.FROM) LocalDate from, 
			@RequestParam(required = false, name = IRequestConstants.TO) LocalDate to,
			@PageableDefault(page=0, size=10) Pageable pageable) {
		if (from != null && to != null && from.isAfter(to)) {
			return ResponseEntity.badRequest().body(IValidationConstants.TO_DATE_CONSTRAINTS);
		}
		AVRequest request = new AVRequest(from, to);
		pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		return CommonUtil.prepareResponseWithContent(availabilityService.getAvailabilityData(request, pageable), HttpStatus.OK);
	}

	/**
	 * Delete particular slot based on given id
	 * 
	 * @param id id of slot to be deleted
	 * @return
	 */
	@DeleteMapping(IPathConstants.DELETE_SLOTS_PATH)
	public ResponseEntity<?> deleteAvailabilitySlot(@PathVariable(IRequestConstants.ID) Integer id) {
		int deleted = availabilityService.deleteAvailabilitySlot(id);
		return CommonUtil.prepareResponseWithMessage(deleted + IResponseConstants.AVAILABILITY_DELETION_DONE, HttpStatus.OK);
	}

	/**
	 * Delete all available slots based on given date and time range.
	 * Date range is mandatory and timer range is optional to delete slots.
	 * 
	 * @param request contains data like date and time range
	 * @return
	 */
	@PostMapping(IPathConstants.BULK_DELETE_SLOTS_PATH)
	public ResponseEntity<?> bulkDeleteAvailabilitySlot(@RequestBody @Validated ADRequest request) {
		int deleted = availabilityService.bulkDeleteAvailabilitySlots(request);
		return CommonUtil.prepareResponseWithMessage(deleted + IResponseConstants.AVAILABILITY_DELETION_DONE, HttpStatus.OK);
	}

	/**
	 * Retrieve details of particular slot and all associated appointments based on given id of slot.
	 * Appointment id is optional, if provided then slot details will be retrieved with particular appointment(not all).
	 * 
	 * @param id unique identification number of slot
	 * @param appointmentId unique identification number of appointment
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_SLOT_DETAILS)
	public ResponseEntity<?> viewSlotDetails(@RequestParam(IRequestConstants.ID) Integer id, @RequestParam(name = IRequestConstants.APPOINTMENT_ID, required = false) Integer appointmentId) {
		return CommonUtil.prepareResponseWithContent(availabilityService.viewSlotDetails(id, appointmentId, false), HttpStatus.OK);
	}

	/**
	 * Retrieve latest appointment booked with given slot id and change its status as per 
	 * new requested status.
	 * 
	 * @param slotId unique identification number of slot
	 * @param status new status
	 * @return
	 */
	@PatchMapping(IPathConstants.CHANGE_AVAILABILITY_APPOINTMENT_STATUS)
	public ResponseEntity<?> changeAvailabilityAppointmentStatus(@RequestParam(IRequestConstants.SLOT_ID) Integer slotId, @RequestParam(IRequestConstants.STATUS) AppointmentStatus status) {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.SLOT_ID, slotId);
		requestMap.put(IRequestConstants.STATUS, status);
		String message = availabilityService.changeAvailabilityAppointmentStatus(requestMap, false);
		return CommonUtil.prepareResponseWithMessage(message, HttpStatus.OK);
	}
}
