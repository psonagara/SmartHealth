package com.ps.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constants.IPathConstants;
import com.ps.service.IAdminDashboardService;
import com.ps.util.CommonUtil;

/**
 * REST Controller for admin dashboard-related APIs in the Smarthealth application.
 * Provides endpoints to retrieve dashboard statistics for administrative users.
 */
@RestController
@RequestMapping(IPathConstants.ADMIN_DASHBOARD_PATH)
public class AdminDashboardRestController {
	
	@Autowired
	private IAdminDashboardService adminDashboardService;

	/**
     * Retrieves dashboard statistics for the admin user.
     *
     * @return ResponseEntity containing a map of dashboard statistics.
     */
	@GetMapping(IPathConstants.STATS_PATH)
	public ResponseEntity<?> getDashboardStats() {
		return CommonUtil.prepareResponseWithContent(adminDashboardService.getDashboardStats(), HttpStatus.OK);
	}
	
	/**
     * Retrieves appointment performance trend for the last 10 days.
     *
     * @return {@link ResponseEntity} containing a list of daily appointment counts,
     *         ensuring that days with zero appointments are included in the trend data.
     */
	@GetMapping(IPathConstants.APPOINTMENT_TREND_PATH)
	public ResponseEntity<?> getAppointmentTrend() {
		return CommonUtil.prepareResponseWithContent(adminDashboardService.getAppointmentTrend(), HttpStatus.OK);
	}
	
	/**
     * Retrieves daily appointment counts for the last 30 days.
     *
     * @return {@link ResponseEntity} containing a list of date-wise appointment totals,
     *         including days with zero appointments.
     */
	@GetMapping(IPathConstants.APPOINTMENT_COUNT_PATH)
	public ResponseEntity<?> getAppointmentCount() {
		return CommonUtil.prepareResponseWithContent(adminDashboardService.getAppointmentCount(), HttpStatus.OK);
	}
	
	/**
     * Retrieves the top 10 upcoming approved doctor leaves starting from today.
     *
     * @return {@link ResponseEntity} containing the list of upcoming leaves with doctor details.
     */
	@GetMapping(IPathConstants.UPCOMING_LEAVES)
	public ResponseEntity<?> getUpcomingLeaves() {
		return CommonUtil.prepareResponseWithContent(adminDashboardService.getUpcomingLeaves(), HttpStatus.OK);
	}
	
	/**
     * Retrieves today's appointments ordered by start time.
     *
     * @return {@link ResponseEntity} containing the list of today's appointments
     *         along with patient and doctor details.
     */
	@GetMapping(IPathConstants.TODAYS_APPOINTMENTS)
	public ResponseEntity<?> getTodaysAppointments() {
		return CommonUtil.prepareResponseWithContent(adminDashboardService.todaysAppointments(), HttpStatus.OK);
	}
	
}
