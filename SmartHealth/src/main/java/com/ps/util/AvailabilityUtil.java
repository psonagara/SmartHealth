package com.ps.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import com.ps.constants.IExceptionConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.request.AGRequest;
import com.ps.dto.response.AIResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.DSResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.SlotInput;
import com.ps.enu.AGMode;
import com.ps.enu.AppointmentStatus;
import com.ps.exception.AvailabilityException;

public interface AvailabilityUtil {

	public static Availability prepareAvailability(Doctor doctor, LocalDate date, LocalTime from, LocalTime to, AGMode mode) {
		Availability availability = new Availability();
		availability.setDoctor(doctor);
		availability.setDate(date);
		availability.setStartTime(from);
		availability.setEndTime(to);
		availability.setMode(mode);
		return availability;
	}
	
	public static AGRequest prepareAGRequest(int daysAhead, LocalDate startDate, List<SlotInput> slotInputs, AGMode mode, boolean skipHoliday) {
		AGRequest agRequest = new AGRequest();
		agRequest.setDaysAhead(daysAhead);
		agRequest.setStartDate(startDate);
		agRequest.setSlotInputs(slotInputs);
		agRequest.setMode(mode);
		agRequest.setSkipHoliday(skipHoliday);
		return agRequest;
	}
	
	public static DSResponse prepareDSResponse(Doctor doctor) {
		DSResponse dsResponse = new DSResponse();
		BeanUtils.copyProperties(doctor, dsResponse);
		dsResponse.setDegrees(doctor.getDegrees().stream().map(d -> d.getName()).collect(Collectors.toList()));
		dsResponse.setDepartments(doctor.getDepartments().stream().map(d -> d.getName()).collect(Collectors.toList()));
		dsResponse.setSpecializations(doctor.getSpecializations().stream().map(s -> s.getName()).collect(Collectors.toList()));
		return dsResponse;
	}
	
	public static AVResponse prepareAVResponse(Availability availability) {
		AVResponse avResponse = new AVResponse();
		avResponse.setId(availability.getId());
		avResponse.setStartTime(availability.getStartTime());
		avResponse.setEndTime(availability.getEndTime());
		return avResponse;
	}
	
	public static AIResponse prepareAIResponse(Appointment appointment) {
		AIResponse aiResponse = new AIResponse();
		aiResponse.setBookingTime(appointment.getCreatedAt());
		aiResponse.setUpdatedTime(appointment.getUpdatedAt());
		aiResponse.setStatus(appointment.getStatus());
		aiResponse.setNote(appointment.getNote());
		aiResponse.setPatient(AppointmentUtil.preparePPR(appointment.getPatient()));
		if (appointment.getSubProfile() != null) {
			aiResponse.setSubProfile(ProfileUtil.prepareSubProfileDTO(appointment.getSubProfile()));
		}
		return aiResponse;
	}
	
	public static String getFailureMessageForStatusChange(AppointmentStatus requestedStatus, AppointmentStatus current) {
		return switch (requestedStatus) {
	        case D_CANCELLED -> IResponseConstants.APPOINTMENT_CANCELLED_FAIL + current;
	        case APPROVED -> IResponseConstants.APPOINTMENT_APPROVED_FAIL + current;
	        case COMPLETED -> IResponseConstants.APPOINTMENT_COMPLETED_FAIL + current;
	        default -> IResponseConstants.APPOINTMENT_STATUS_CHANGE_FAIL + current;
		};
	}
	
	public static List<AppointmentStatus> getAllowedCurrentStatuses(AppointmentStatus status) {
		switch (status) {
			case APPROVED:
				return List.of(AppointmentStatus.BOOKED);
				
			case D_CANCELLED:
				return List.of(AppointmentStatus.BOOKED, AppointmentStatus.APPROVED);
	
			case COMPLETED:
				return List.of(AppointmentStatus.APPROVED);
	
			default:
				throw new AvailabilityException(IExceptionConstants.INVALID_STATUS_IN_CHANGE_REQUEST, HttpStatus.BAD_REQUEST);
		}
	}
}
