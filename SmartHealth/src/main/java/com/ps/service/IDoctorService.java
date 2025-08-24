package com.ps.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ps.dto.request.DSRequest;
import com.ps.dto.response.AVResponse;

public interface IDoctorService {
	
	Map<String, Object> searchDoctor(DSRequest dsRequest, Pageable pageable);
	Map<LocalDate, List<AVResponse>> viewSlots(Map<String, Object> requestMap);
	Map<String, Object> viewDashboard();
}
