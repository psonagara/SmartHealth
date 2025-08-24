package com.ps.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.response.ADSResponse;
import com.ps.dto.response.APSResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.LeaveResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.DoctorLeave;
import com.ps.entity.Patient;
import com.ps.enu.LeaveStatus;
import com.ps.exception.DoctorException;
import com.ps.repo.DoctorRepository.DoctorIdNameProjection;

public interface AdminUtil {

	public static ADSResponse prepareADSResponseForSeachDoctors(Doctor doctor) { 
		ADSResponse adsResponse = new ADSResponse();
		adsResponse.setId(doctor.getId());
		adsResponse.setName(doctor.getName());
		adsResponse.setEmail(doctor.getEmail());
		adsResponse.setPhone(doctor.getPhone());
		adsResponse.setIsActive(doctor.getIsActive());
		adsResponse.setProfileComplete(doctor.getProfileComplete());
		return adsResponse;
	}

	public static APSResponse prepareAPSResponseForSeachPatients(Patient patient) { 
		APSResponse apsResponse = new APSResponse();
		apsResponse.setId(patient.getId());
		apsResponse.setName(patient.getName());
		apsResponse.setEmail(patient.getEmail());
		apsResponse.setPhone(patient.getPhone());
		apsResponse.setIsActive(patient.getIsActive());
		apsResponse.setProfileComplete(patient.getProfileComplete());
		return apsResponse;
	}

	public static Map<String, Object> prepareSearchAvailabilitySlots(Availability availability) {
		Map<String, Object> responseMap = new HashMap<>();
		AVResponse avResponse = new AVResponse();
		avResponse.setDate(availability.getDate());
		avResponse.setEndTime(availability.getEndTime());
		avResponse.setId(availability.getId());
		avResponse.setMode(availability.getMode());
		avResponse.setStartTime(availability.getStartTime());
		avResponse.setStatus(availability.getStatus());
		responseMap.put(IAdminConstants.SLOT, avResponse);
		responseMap.put(IAdminConstants.DOCTOR, new DoctorIdNameProjection() {
			public String getName() { return availability.getDoctor().getName(); }
			public Integer getId() { return availability.getDoctor().getId(); }
		});
		return responseMap;
	}

	public static Map<String, Object> prepareSearchAppointments(Appointment appointment) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put(IAdminConstants.ID, appointment.getId());		
		resultMap.put(IAdminConstants.SLOT_ID, appointment.getAvailability().getId());
		resultMap.put(IAdminConstants.DOCTOR_NAME, appointment.getAvailability().getDoctor().getName());
		resultMap.put(IAdminConstants.PATIENT_NAME, appointment.getPatient().getName());
		resultMap.put(IAdminConstants.DATE, appointment.getAvailability().getDate());
		resultMap.put(IAdminConstants.FROM, appointment.getAvailability().getStartTime());
		resultMap.put(IAdminConstants.TO, appointment.getAvailability().getEndTime());
		resultMap.put(IAdminConstants.STATUS, appointment.getStatus());
		resultMap.put(IAdminConstants.BOOKED_ON, appointment.getCreatedAt());
		return resultMap;
	}

	public static Map<String, Object> prepareSearchLeavesMap(DoctorLeave doctorLeave) {
		Map<String, Object> resultMap = new HashMap<>();
		LeaveResponse leaveResponse = new LeaveResponse();
		BeanUtils.copyProperties(doctorLeave, leaveResponse);
		resultMap.put(IAdminConstants.LEAVE_INFO, leaveResponse);
		resultMap.put(IAdminConstants.DOCTOR_INFO, new DoctorIdNameProjection() {
			public String getName() {return doctorLeave.getDoctor().getName();}
			public Integer getId() {return doctorLeave.getDoctor().getId();}
		});
		return resultMap;
	}
	
	public static boolean isLeaveStatusTransitionValid(LeaveStatus current, LeaveStatus target, String role) {
		if (role.equals(IDoctorConstants.DOCTOR_ROLE)) {
			if (!(target == LeaveStatus.APPROVED || target == LeaveStatus.REJECTED)) {
				throw new DoctorException(IExceptionConstants.INVALID_STATUS_IN_CHANGE_REQUEST, HttpStatus.BAD_REQUEST);
			}
			return switch (target) {
				case APPROVED -> current == LeaveStatus.BOOKED;
				case REJECTED -> current == LeaveStatus.BOOKED;
				default -> false;
			};
		}
		return false;
	}
	
	public static String getSuccessMessageForLeaveStausChange(LeaveStatus status) {
	    return switch (status) {
	        case APPROVED -> IResponseConstants.LEAVE_APPROVED;
	        case REJECTED -> IResponseConstants.LEAVE_REJECTED;
	        default -> IResponseConstants.LEAVE_STATUS_CHANGED;
	    };
	}
}
