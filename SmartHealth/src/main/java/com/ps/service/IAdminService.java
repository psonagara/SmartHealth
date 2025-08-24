package com.ps.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.ps.dto.HolidayDTO;
import com.ps.dto.request.ADSRequest;
import com.ps.dto.request.APSRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.ADSResponse;
import com.ps.dto.response.APSResponse;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.LeaveStatus;
import com.ps.repo.DoctorRepository.DoctorIdNameProjection;
import com.ps.repo.PatientRepository.PatientIdNameProjection;

public interface IAdminService {

	Map<String, Object> searchDoctors(ADSRequest adsRequest, Pageable pageable);
	Map<String, Object> searchPatients(APSRequest apsRequest, Pageable pageable);
	ADSResponse viewDoctorProfile(Integer id);
	APSResponse viewPatientProfile(Integer id);
	int toggleStatus(Integer id, String role);
	List<DoctorIdNameProjection> getDoctorsList();
	List<PatientIdNameProjection> getPatientList();
	Map<String, Object> searchAvailabilitySlots(Map<String, Object> requestMap, Pageable pageable);
	int deleteAvailabilitySlot(Integer id);
	ADResponse viewSlotDetails(Integer id, Integer appointmentId);
	String changeAvailabilityAppointmentStatus(Map<String, Object> requestMap);
	Map<String, Object> searchAppointments(Map<String, Object> requestMap, Pageable pageable);
	String changeAppointmentStatus(Integer id, AppointmentStatus newStatus);
	Map<String, Object> searchLeaves(Map<String, Object> requestMap, Pageable pageable);
	String changeLeaveStatus(Integer id, String role, LeaveStatus status);
	boolean addHoliday(HolidayDTO holidayDTO);
	void deleteHoliday(Integer id);
}
