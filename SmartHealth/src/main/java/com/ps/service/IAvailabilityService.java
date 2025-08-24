package com.ps.service;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ps.dto.request.ADRequest;
import com.ps.dto.request.AGRequest;
import com.ps.dto.request.AVRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.entity.Doctor;

public interface IAvailabilityService {
	
	void generateAvailabilitySlots(AGRequest request);
	boolean activateAGPreference();
	boolean setDefaultAGPreference(Doctor doctor);
	void generateAvailabilitySlots(AGRequest request, Doctor doctor);
	AGPreferenceResponse getAGPreference();
	Map<String, Object>  getAvailabilityData(AVRequest request, Pageable pageable);
	int deleteAvailabilitySlot(Integer id);
	int bulkDeleteAvailabilitySlots(ADRequest request);
	ADResponse viewSlotDetails(Integer id, Integer appointmentId, boolean isAdmin);
	String changeAvailabilityAppointmentStatus(Map<String, Object> requestMap, boolean isAdmin);
}
