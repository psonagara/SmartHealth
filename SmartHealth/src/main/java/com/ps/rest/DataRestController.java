package com.ps.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constants.IPathConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.service.IDataService;

/**
 * This rest controller is used to retrieve open resources.
 * Like list of degrees, departments etc.
 */
@RestController
@RequestMapping(IPathConstants.DATA_PATH)
public class DataRestController {

	@Autowired
	private IDataService dataService;
	
	/**
	 * Retrieve list of degrees
	 * 
	 * @return
	 */
	@GetMapping(IPathConstants.DOCTOR_DEGREE)
	public ResponseEntity<?> getDoctorDegrees() {
		return ResponseEntity.ok(dataService.getDoctorDegrees());
	}
	
	/**
	 * Retrieve list of departments
	 * 
	 * @return
	 */
	@GetMapping(IPathConstants.DOCTOR_DEPARTMENT)
	public ResponseEntity<?> getDoctorDepartment() {
		return ResponseEntity.ok(dataService.getDoctorDepartment());
	}
	
	/**
	 * Retrieve list of specializations
	 * 
	 * @return
	 */
	@GetMapping(IPathConstants.DOCTOR_SPECIALIZATION)
	public ResponseEntity<?> getDoctorSpecializations() {
		return ResponseEntity.ok(dataService.getDoctorSpecializations());
	}

	/**
	 * Retrieve list of relations
	 * 
	 * @return
	 */
	@GetMapping(IPathConstants.PATIENT_RELATION_PROFILE)
	public ResponseEntity<?> getPatientRelations() {
		return ResponseEntity.ok(dataService.getPatientRelations());
	}
	
	/**
	 * Retrieve resource by it's name and for provided role.
	 * 
	 * @return
	 */
	@GetMapping(IPathConstants.PICTURE_BY_FILE_NAME)
	public ResponseEntity<?> getPictureByName(
			@PathVariable(IRequestConstants.FILE_NAME) String fileName,
			@RequestParam(name = IRequestConstants.ROLE, required = false) String role) {
		if (fileName.isBlank() || fileName.equals("null")) {
			return ResponseEntity.badRequest().body(IResponseConstants.FILE_NAME_CONSTRAINTS);
		}
		return ResponseEntity.ok(dataService.getPictureByName(fileName, role));
	}
	
	/**
	 * View list of holidays with pagination.
	 * 
	 * @param pageable pagination details
	 * @return
	 */
	@GetMapping(IPathConstants.VIEW_HOLIDAYS)
	public ResponseEntity<?> viewHolidays(@PageableDefault(page = 0,size = 10) Pageable pageable) {
		return ResponseEntity.ok(dataService.viewHolidays(pageable));
	}
}
