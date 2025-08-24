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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPathConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.HolidayDTO;
import com.ps.dto.request.ADSRequest;
import com.ps.dto.request.APSRequest;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.LeaveStatus;
import com.ps.enu.SlotStatus;
import com.ps.service.IAdminService;
import com.ps.util.CommonUtil;

import jakarta.validation.Valid;

/**
 * This rest controller is used to manage APIs related to admin operations.
 * Like viewing and managing users, slots, appointments, leaves and holidays.
 */
@RestController
@RequestMapping(IPathConstants.ADMIN_PATH)
public class AdminRestController {

	@Autowired
	private IAdminService adminService;

	/**
	 * Retrieves a list of doctors based on optional filter parameters for admin viewing.
	 * All parameters are optional, and the method filters results accordingly.
	 * 
	 * @param dsRequest containing fields to filter list of doctor
	 * @param pageable pagination details
	 * @return
	 */
	@PostMapping(IPathConstants.SEARCH_DOCTOR_PATH)
	public ResponseEntity<?> searchDoctors(@RequestBody(required = false) @Valid ADSRequest dsRequest, 
			@PageableDefault(page = 0, size = 10) Pageable pageable) {

		pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		return CommonUtil.prepareResponseWithContent(adminService.searchDoctors(dsRequest, pageable), HttpStatus.OK);
	}

	/**
	 * Retrieves a list of patients based on optional filter parameters for admin viewing.
	 * All parameters are optional, and the method filters results accordingly.
	 * 
	 * @param apsRequest containing fields to filter list of patient
	 * @param pageable pagination details
	 * @return
	 */
	@PostMapping(IPathConstants.SEARCH_PATIENT_PATH)
	public ResponseEntity<?> searchPatients(@RequestBody(required = false) APSRequest apsRequest, 
			@PageableDefault(page = 0, size = 10) Pageable pageable) {

		pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		return CommonUtil.prepareResponseWithContent(adminService.searchPatients(apsRequest, pageable), HttpStatus.OK);
	}

	/**
	 * Retrieve details of particular user based on given role and unique id for admin viewing.
	 * Both role and id are mandatory parameters to view detail of a user by admin.
	 * 
	 * @param role to identify user as patient or doctor
	 * @param id unique identification number of user
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_PROFILE)
	public ResponseEntity<?> viewProfile(@PathVariable(IRequestConstants.ROLE) String role, @RequestParam(IRequestConstants.ID) Integer id) {
		switch (role) {
			case IDoctorConstants.DOCTOR_ROLE:
				return CommonUtil.prepareResponseWithContent(adminService.viewDoctorProfile(id), HttpStatus.OK);
				
			case IPatientConstants.PATIENT_ROLE:
				return CommonUtil.prepareResponseWithContent(adminService.viewPatientProfile(id), HttpStatus.OK);
	
			default:
				return CommonUtil.prepareResponseWithMessage(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Toggle activation status of particular user based on given role and unique id by admin.
	 * If user is active then deactivate and vice versa. Both role and id are mandatory parameters to toggle activation status.
	 * 
	 * @param role to identify user as patient or doctor
	 * @param id unique identification number of user
	 * @return
	 */
	@PatchMapping(IPathConstants.TOGGLE_STATUS_PATH)
	public ResponseEntity<?> toggleStatus(@PathVariable(IRequestConstants.ROLE) String role, @RequestParam(IRequestConstants.ID) Integer id) {
		adminService.toggleStatus(id, role);
		return CommonUtil.prepareResponseWithMessage(IResponseConstants.USER_ACTIVATION_TOGGLED, HttpStatus.OK);
	}

	/**
	 * Retrieve List of users info (id and name only) to display in filter dropdown.
	 * This list is retrieved and displayed to admin to filter slots and appointments.
	 * 
	 * @param role to identify user as patient or doctor
	 * @return
	 */
	@GetMapping(IPathConstants.USERS_LIST)
	public ResponseEntity<?> getUsersList(@PathVariable(IRequestConstants.ROLE) String role) {
		switch (role) {
			case IDoctorConstants.DOCTOR_ROLE:
				return CommonUtil.prepareResponseWithContent(adminService.getDoctorsList(), HttpStatus.OK);
				
			case IPatientConstants.PATIENT_ROLE:
				return CommonUtil.prepareResponseWithContent(adminService.getPatientList(), HttpStatus.OK);
	
			default:
				return CommonUtil.prepareResponseWithMessage(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Retrieves list of availability slots based on optional filter parameters for admin viewing.
	 * All parameters are optional, and the method filters results accordingly.
	 * 
	 * @param doctorId unique number to get slots based on doctor
	 * @param date view slots for particular date
	 * @param status fetch slots with given status
	 * @param pageable pagination details
	 * @return
	 */
	@GetMapping(IPathConstants.SEARCH_SLOTS_PATH)
	public ResponseEntity<?> searchAvailabilitySlots(@RequestParam(name = IRequestConstants.DOCTOR_ID, required = false) Integer doctorId,
			@RequestParam(name = IRequestConstants.DATE, required = false) LocalDate date, @RequestParam(name = IRequestConstants.STATUS, required=false) SlotStatus status,
			Pageable pageable) {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.DOCTOR_ID, doctorId);
		requestMap.put(IRequestConstants.DATE, date);
		requestMap.put(IRequestConstants.STATUS, status);
		return CommonUtil.prepareResponseWithContent(adminService.searchAvailabilitySlots(requestMap, pageable), HttpStatus.OK);
	}

	/**
	 * Delete particular slot by providing it's id by admin.
	 * 
	 * @param id id of slot to be deleted
	 * @return
	 */
	@DeleteMapping(IPathConstants.DELETE_SLOTS_PATH)
	public ResponseEntity<?> deleteAvailabilitySlot(@PathVariable(IRequestConstants.ID) Integer id) {
		int deleted = adminService.deleteAvailabilitySlot(id);
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
		return CommonUtil.prepareResponseWithContent(adminService.viewSlotDetails(id, appointmentId), HttpStatus.OK);
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
		String message = adminService.changeAvailabilityAppointmentStatus(requestMap);
		return CommonUtil.prepareResponseWithMessage(message, HttpStatus.OK);
	}

	/**
	 * Retrieves list of appointments based on optional filter parameters for admin viewing.
	 * All parameters are optional, and the method filters results accordingly.
	 * 
	 * @param doctorId unique number to filter appointments based on doctor
	 * @param patientId unique number to filter appointments based on patient 
	 * @param date view appointments for particular date
	 * @param status view appointments of given status
	 * @param pageable pagination details
	 * @return
	 */
	@GetMapping(IPathConstants.SEARCH_APPOINTMENTS_PATH)
	public ResponseEntity<?> searchAppointments(@RequestParam(name = IRequestConstants.DOCTOR_ID, required = false) Integer doctorId,
			@RequestParam(name = IRequestConstants.PATIENT_ID, required=false) Integer patientId, @RequestParam(name = IRequestConstants.DATE, required = false) LocalDate date, 
			@RequestParam(name = IRequestConstants.STATUS, required=false) AppointmentStatus status, @PageableDefault(page = 0,size = 10) Pageable pageable) {

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.DOCTOR_ID, doctorId);
		requestMap.put(IRequestConstants.PATIENT_ID, patientId);
		requestMap.put(IRequestConstants.DATE, date);
		requestMap.put(IRequestConstants.STATUS, status);
		pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		return CommonUtil.prepareResponseWithContent(adminService.searchAppointments(requestMap, pageable), HttpStatus.OK);
	}

	/**
	 * Change status of appointment for given appointment id to new status.
	 * 
	 * @param id unique number to identify appointment
	 * @param status new status
	 * @return
	 */
	@PatchMapping(IPathConstants.CHANGE_APPOINTMENT_STATUS)
	public ResponseEntity<?> changeAppointmentStatus(@PathVariable(IRequestConstants.APPOINTMENT_ID) Integer id, @RequestParam(IRequestConstants.STATUS) AppointmentStatus status) {
		return CommonUtil.prepareResponseWithMessage(adminService.changeAppointmentStatus(id, status), HttpStatus.OK);
	}

	/**
	 * Retrieves list of leaves based on optional filter parameters for admin viewing.
	 * All parameters are optional, and the method filters results accordingly.
	 * 
	 * @param doctorId retrieve leaves of particular doctor based on it's id
	 * @param from date range starting 
	 * @param to date range ending
	 * @param status view leaves of particular status
	 * @param pageable pagination details
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_LEAVE_PATH)
	public ResponseEntity<?> searchLeaves(@RequestParam(name = IRequestConstants.DOCTOR_ID, required = false) Integer doctorId, @RequestParam(name = IRequestConstants.FROM, required = false) LocalDate from, 
			@RequestParam(name = IRequestConstants.TO, required = false) LocalDate to, @RequestParam(name = IRequestConstants.STATUS, required = false) LeaveStatus status,
			@PageableDefault(page = 0,size = 10) Pageable pageable) {

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.DOCTOR_ID, doctorId);
		requestMap.put(IRequestConstants.FROM, from);
		requestMap.put(IRequestConstants.TO, to);
		requestMap.put(IRequestConstants.STATUS, status);
		pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		return CommonUtil.prepareResponseWithContent(adminService.searchLeaves(requestMap, pageable), HttpStatus.OK);
	}

	/**
	 * Change status of leave (approve or reject) by admin.
	 * 
	 * @param id unique number to identify leave
	 * @param role role of user for whom changing leave status (currently doctor only)
	 * @param status new status
	 * @return
	 */
	@PatchMapping(IPathConstants.CHANGE_LEAVE_STATUS)
	public ResponseEntity<?> changeLeaveStatus(@PathVariable(IRequestConstants.ID) Integer id, @PathVariable(IRequestConstants.ROLE) String role, @RequestParam(IRequestConstants.STATUS) LeaveStatus status) {
		return CommonUtil.prepareResponseWithMessage(adminService.changeLeaveStatus(id, role, status), HttpStatus.OK);
	}
	
	/**
	 * Add new holiday with given input parameters holidayDate and reason.
	 * 
	 * @param holidayDTO contains input parameters to add new holiday
	 * @return
	 */
	@PostMapping(IPathConstants.ADD_HOLIDAY)
	public ResponseEntity<?> addHoliday(@RequestBody @Valid HolidayDTO holidayDTO) {
		boolean added = adminService.addHoliday(holidayDTO);
		String message = added ? IResponseConstants.HOLIDAY_ADDED_SUCCESS : IResponseConstants.HOLIDAY_ADDED_FAIL;
		HttpStatus status = added ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
		return CommonUtil.prepareResponseWithMessage(message, status);
	}

	/**
	 * Delete particular holiday based on given id.
	 * 
	 * @param id id of holiday to be deleted
	 * @return
	 */
	@DeleteMapping(IPathConstants.DELETE_HOLIDAY)
	public ResponseEntity<?> deleteHoliday(@PathVariable(IRequestConstants.ID) Integer id) {
		adminService.deleteHoliday(id);
		return CommonUtil.prepareResponseWithMessage(IResponseConstants.HOLIDAY_DELETE_SUCCESS, HttpStatus.OK);
	}

}
