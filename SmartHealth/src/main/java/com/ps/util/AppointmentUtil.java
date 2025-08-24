package com.ps.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.SubProfileDTO;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.ApDResponse;
import com.ps.dto.response.PatientProfileResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.Patient;
import com.ps.entity.Relation;
import com.ps.entity.SubProfile;
import com.ps.enu.AppointmentStatus;
import com.ps.exception.AppointmentException;
import com.ps.exception.DoctorException;
import com.ps.exception.PatientException;
import com.ps.exception.ProfileException;

public interface AppointmentUtil {

	public static AVResponse prepareAVResponse(Availability availability) {
		AVResponse avResponse = new AVResponse();
		avResponse.setId(availability.getId());
		avResponse.setDate(availability.getDate());
		avResponse.setStartTime(availability.getStartTime());
		avResponse.setEndTime(availability.getEndTime());
		return avResponse;
	}
	
	public static PatientProfileResponse preparePPR(Patient patient) {
		PatientProfileResponse profileResponse = new PatientProfileResponse();
		profileResponse.setId(patient.getId());
		profileResponse.setName(patient.getName());
		profileResponse.setEmail(patient.getEmail());
		profileResponse.setPhone(patient.getPhone());
		return profileResponse;
	}
	
	public static SubProfile prepareSubProfile(SubProfileDTO dto, Patient patient) {
		SubProfile subProfile = new SubProfile();
		subProfile.setId(dto.getId());
		subProfile.setName(dto.getName());
		subProfile.setPhone(dto.getPhone());
		subProfile.setPatient(patient);
		Relation relation = new Relation();
		relation.setId(dto.getRelation().getId());
		relation.setName(dto.getRelation().getName());
		subProfile.setRelation(relation);
		return subProfile;
	}
	
	public static Map<String, Object> prepareViewAllAppointmentsResponse(Appointment appointment, String role) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put(IResponseConstants.ID, appointment.getId());
		if (role.equals(IPatientConstants.PATIENT_ROLE)) {
			resultMap.put(IResponseConstants.DOCTOR_NAME, appointment.getAvailability().getDoctor().getName());
		} else if (role.equals(IDoctorConstants.DOCTOR_ROLE)) {
			resultMap.put(IResponseConstants.PATIENT_NAME, appointment.getPatient().getName());
			resultMap.put(IResponseConstants.SLOT_ID, appointment.getAvailability().getId());
		}
		resultMap.put(IResponseConstants.DATE, appointment.getAvailability().getDate());
		resultMap.put(IResponseConstants.FROM, appointment.getAvailability().getStartTime());
		resultMap.put(IResponseConstants.TO, appointment.getAvailability().getEndTime());
		resultMap.put(IResponseConstants.STATUS, appointment.getStatus());
		return resultMap;
	}
	
	public static void validateAccessForAppointment(String role, Appointment appointment, String email) {
	    if (IPatientConstants.PATIENT_ROLE.equals(role)) {
	        if (!appointment.getPatient().getEmail().equals(email)) {
	            throw new PatientException(IExceptionConstants.DO_NOT_ACCESS_TO_APPOINTMENT, HttpStatus.UNAUTHORIZED);
	        }
	    } else if (IDoctorConstants.DOCTOR_ROLE.equals(role)) {
	        if (!appointment.getAvailability().getDoctor().getEmail().equals(email)) {
	            throw new DoctorException(IExceptionConstants.DO_NOT_ACCESS_TO_APPOINTMENT, HttpStatus.UNAUTHORIZED);
	        }
	    } else {
	        throw new ProfileException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
	    }
	}
	
	public static boolean isStatusTransitionValid(String role, AppointmentStatus current, AppointmentStatus target) {

	    if (IPatientConstants.PATIENT_ROLE.equals(role)) {
	    	if (target != AppointmentStatus.P_CANCELLED) {
	    		throw new AppointmentException(IExceptionConstants.INVALID_STATUS_IN_CHANGE_REQUEST, HttpStatus.BAD_REQUEST);
	    	}
	        return target == AppointmentStatus.P_CANCELLED &&
	               (current == AppointmentStatus.BOOKED || current == AppointmentStatus.APPROVED);
	    }

	    if (IDoctorConstants.DOCTOR_ROLE.equals(role)) {
	    	if (!(target == AppointmentStatus.APPROVED || target == AppointmentStatus.D_CANCELLED || target == AppointmentStatus.COMPLETED)) {
	    		throw new AppointmentException(IExceptionConstants.INVALID_STATUS_IN_CHANGE_REQUEST, HttpStatus.BAD_REQUEST);
	    	}
	        return switch (target) {
	            case APPROVED -> current == AppointmentStatus.BOOKED;
	            case D_CANCELLED -> current == AppointmentStatus.BOOKED || current == AppointmentStatus.APPROVED;
	            case COMPLETED -> current == AppointmentStatus.APPROVED;
	            default -> false;
	        };
	    }
	    if (IAdminConstants.ADMIN_ROLE.equals(role)) {
	    	if (!(target == AppointmentStatus.APPROVED || target == AppointmentStatus.D_CANCELLED || target == AppointmentStatus.COMPLETED || target == AppointmentStatus.P_CANCELLED)) {
	    		throw new AppointmentException(IExceptionConstants.INVALID_STATUS_IN_CHANGE_REQUEST, HttpStatus.BAD_REQUEST);
	    	}
	        return switch (target) {
	            case APPROVED -> current == AppointmentStatus.BOOKED;
	            case D_CANCELLED -> current == AppointmentStatus.BOOKED || current == AppointmentStatus.APPROVED;
	            case P_CANCELLED -> current == AppointmentStatus.BOOKED || current == AppointmentStatus.APPROVED;
	            case COMPLETED -> current == AppointmentStatus.APPROVED;
	            default -> false;
	        };
	    }
	    return false;
	}
	
	public static String getSuccessMessageForStausChange(AppointmentStatus status) {
	    return switch (status) {
	        case P_CANCELLED, D_CANCELLED -> IResponseConstants.APPOINTMENT_CANCELLED;
	        case APPROVED -> IResponseConstants.APPOINTMENT_APPROVED;
	        case COMPLETED -> IResponseConstants.APPOINTMENT_COMPLETED;
	        default -> IResponseConstants.APPOINTMENT_STATUS_CHANGE;
	    };
	}
	
	public static ApDResponse prepareApDResponse(Doctor doctor, Availability availability, Patient patient) {
		ApDResponse response = new ApDResponse();
		response.setDoctor(AvailabilityUtil.prepareDSResponse(doctor));
		response.setSlot(AppointmentUtil.prepareAVResponse(availability));
		response.setPatient(AppointmentUtil.preparePPR(patient));
		return response;
	}
}
