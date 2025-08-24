package com.ps.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constants.IPathConstants;
import com.ps.service.IDoctorService;
import com.ps.util.CommonUtil;

/**
 * REST controller for managing APIs related to doctor information and scheduling.
 * <p>
 * This controller provides endpoints for:
 * <ul>
 *   <li>Searching doctors with filtering and pagination</li>
 *   <li>Viewing available slots for a specific doctor</li>
 *   <li>Retrieving dashboard data for the logged-in doctor</li>
 * </ul>
 * All responses are wrapped in a standard application response format.
 */
@RestController
@RequestMapping(IPathConstants.DOCTOR_PATH)
public class DoctorRestController {
	
	@Autowired
	private IDoctorService doctorService;
	
	 /**
     * Retrieves the dashboard data for the currently authenticated doctor.
     * <p>
     * Includes appointment statistics, today's schedule, notifications, and
     * recent performance trends.
     *
     * @return {@link ResponseEntity} containing the dashboard data
     */
	@GetMapping(IPathConstants.DASHBOARD)
	public ResponseEntity<?> viewDashboard() {
		return CommonUtil.prepareResponseWithContent(doctorService.viewDashboard(), HttpStatus.OK);
	}
}
