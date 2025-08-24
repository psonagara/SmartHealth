package com.ps.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constants.IPathConstants;
import com.ps.service.IPatientService;
import com.ps.util.CommonUtil;

/**
 * REST controller for handling patient-related API requests.
 * <p>
 * This controller provides endpoints for retrieving patient-specific data,
 * such as the dashboard view containing appointment statistics,
 * upcoming appointments, and notifications.
 */
@RestController
@RequestMapping(IPathConstants.PATIENT_PATH)
public class PatientRestController {
	
	@Autowired
	private IPatientService patientService;

	/**
     * Retrieves the dashboard data for the currently authenticated patient.
     * <p>
     * The dashboard includes:
     * <ul>
     *   <li>Appointment statistics (upcoming, approved, completed, cancelled)</li>
     *   <li>List of upcoming appointments</li>
     *   <li>Notifications (currently empty)</li>
     * </ul>
     * This endpoint requires authentication via JWT and identifies
     * the patient based on the token.
     *
     * @return a {@link ResponseEntity} containing the dashboard data and an HTTP status of 200 (OK)
     */
	@GetMapping(IPathConstants.DASHBOARD)
	public ResponseEntity<?> viewDashboard() {
		return CommonUtil.prepareResponseWithContent(patientService.viewDashboard(), HttpStatus.OK);
	}
}
