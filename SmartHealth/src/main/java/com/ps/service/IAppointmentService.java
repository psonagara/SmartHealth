package com.ps.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ps.dto.request.AppointmentRequest;
import com.ps.dto.request.DSRequest;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.ApDResponse;
import com.ps.enu.AppointmentStatus;

public interface IAppointmentService {

	ApDResponse viewDetails(Map<String, Object> requestMap);
	boolean bookAppointment(AppointmentRequest request);
	Map<String, Object> viewAllAppointments(Map<String, Object> requestMap, Pageable pageable);
	String changeAppointmentStatus(Integer id, AppointmentStatus status, boolean isAdmin);
	Map<String, Object> viewAppointmentDetails(Integer id);
	Map<String, Object> searchDoctorsToBookAppointment(DSRequest dsRequest, Pageable pageable);
	Map<LocalDate, List<AVResponse>> viewSlotsToBookAppointment(Map<String, Object> requestMap);
}
