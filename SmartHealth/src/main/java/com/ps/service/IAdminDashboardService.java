package com.ps.service;

import java.util.Map;

public interface IAdminDashboardService {

	Map<String, Object> getDashboardStats();
	Map<String, Object> getAppointmentTrend();
	Map<String, Object> getAppointmentCount();
	Map<String, Object> getUpcomingLeaves();
	Map<String, Object> todaysAppointments();
}
