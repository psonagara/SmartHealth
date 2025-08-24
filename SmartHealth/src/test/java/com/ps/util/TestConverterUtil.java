package com.ps.util;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ps.dto.HolidayDTO;
import com.ps.dto.request.AdminProfileRequest;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LeaveRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.ADSResponse;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.dto.response.APSResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.AdminProfileResponse;
import com.ps.dto.response.DoctorProfileResponse;
import com.ps.dto.response.LeaveResponse;
import com.ps.dto.response.PatientProfileResponse;
import com.ps.entity.AGPreference;
import com.ps.entity.Admin;
import com.ps.entity.Availability;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Doctor;
import com.ps.entity.DoctorLeave;
import com.ps.entity.Holiday;
import com.ps.entity.Patient;
import com.ps.entity.SlotInput;
import com.ps.entity.Specialization;

public interface TestConverterUtil {

	public static PatientProfileResponse toPatientProfileResponse(Patient patient) {
		if (patient == null) {
			return null;
		}

		PatientProfileResponse patientProfileResponse = new PatientProfileResponse();
		patientProfileResponse.setDob(patient.getDob());
		patientProfileResponse.setEmail(patient.getEmail());
		patientProfileResponse.setGender(patient.getGender());
		patientProfileResponse.setHeight(patient.getHeight());
		patientProfileResponse.setId(patient.getId());
		patientProfileResponse.setName(patient.getName());
		patientProfileResponse.setPhone(patient.getPhone());
		patientProfileResponse.setProfilePicPath(patient.getProfilePicPath());
		patientProfileResponse.setWeight(patient.getWeight());
		return patientProfileResponse;
	}


	public static DoctorProfileResponse toDoctorProfileResponse(Doctor doctor) {
		if (doctor == null) {
			return null;
		}

		DoctorProfileResponse doctorProfileResponse = new DoctorProfileResponse();
		doctorProfileResponse.setId(doctor.getId());
		doctorProfileResponse.setName(doctor.getName());
		doctorProfileResponse.setEmail(doctor.getEmail());
		doctorProfileResponse.setPhone(doctor.getPhone());
		doctorProfileResponse.setDob(doctor.getDob());
		doctorProfileResponse.setGender(doctor.getGender());
		List<Degree> list = doctor.getDegrees();
		if (list != null) {
			doctorProfileResponse.setDegrees(new ArrayList<Degree>( list ));
		}
		List<Department> list1 = doctor.getDepartments();
		if (list1 != null) {
			doctorProfileResponse.setDepartments(new ArrayList<Department>(list1));
		}
		List<Specialization> list2 = doctor.getSpecializations();
		if (list2 != null) {
			doctorProfileResponse.setSpecializations(new ArrayList<Specialization>(list2));
		}
		doctorProfileResponse.setYearOfExperience(doctor.getYearOfExperience());
		doctorProfileResponse.setProfilePicPath(doctor.getProfilePicPath());
		doctorProfileResponse.setAddress(doctor.getAddress());
		doctorProfileResponse.setRegistrationNumber(doctor.getRegistrationNumber());
		return doctorProfileResponse;
	}
	
    public static DoctorLeave toDoctorLeave(LeaveRequest request, Doctor doctor, Integer days) {
        if (request == null && doctor == null && days == null) {
            return null;
        }

        DoctorLeave doctorLeave = new DoctorLeave();
        if (request != null) {
            doctorLeave.setFrom(request.getFrom());
            doctorLeave.setTo(request.getTo());
            doctorLeave.setReason(request.getReason());
        }
        doctorLeave.setDoctor(doctor);
        doctorLeave.setDays(days);
        return doctorLeave;
    }
    
    public static LeaveResponse toLeaveResponse(DoctorLeave leave) {
        if (leave == null) {
            return null;
        }

        LeaveResponse leaveResponse = new LeaveResponse();
        leaveResponse.setId(leave.getId());
        leaveResponse.setFrom(leave.getFrom());
        leaveResponse.setTo(leave.getTo());
        leaveResponse.setStatus(leave.getStatus());
        leaveResponse.setDays(leave.getDays());
        leaveResponse.setReason(leave.getReason());
        leaveResponse.setCreationTime(leave.getCreationTime());
        leaveResponse.setUpdationTime(leave.getUpdationTime());
        return leaveResponse;
    }
    
    public static AdminProfileResponse toAdminProfileResponse(Admin admin) {
        if (admin == null) {
            return null;
        }

        AdminProfileResponse adminProfileResponse = new AdminProfileResponse();
        adminProfileResponse.setId(admin.getId());
        adminProfileResponse.setName(admin.getName());
        adminProfileResponse.setEmail(admin.getEmail());
        adminProfileResponse.setPhone(admin.getPhone());
        adminProfileResponse.setProfilePicPath(admin.getProfilePicPath());
        return adminProfileResponse;
    }
    
    public static HolidayDTO toHolidayDto(Holiday holiday) {
        if (holiday == null) {
            return null;
        }

        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setId(holiday.getId());
        holidayDTO.setHolidayDate(holiday.getHolidayDate());
        holidayDTO.setReason(holiday.getReason());
        holidayDTO.setCreationTime(holiday.getCreationTime());
        return holidayDTO;
    }
    
    public static Patient toPatient(PatientProfileRequest request) {
        if (request == null) {
            return null;
        }

        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setDob(request.getDob());
        patient.setGender(request.getGender());
        patient.setHeight(request.getHeight());
        patient.setWeight(request.getWeight());
        return patient;
    }
    
    public static Doctor toDoctor(DoctorProfileRequest request) {
        if (request == null) {
            return null;
        }

        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setDob(request.getDob());
        doctor.setGender(request.getGender());
        List<Degree> list = request.getDegrees();
        if (list != null) {
            doctor.setDegrees(new ArrayList<Degree>(list));
        }
        List<Specialization> list1 = request.getSpecializations();
        if (list1 != null) {
            doctor.setSpecializations(new ArrayList<Specialization>(list1));
        }
        List<Department> list2 = request.getDepartments();
        if (list2 != null) {
            doctor.setDepartments(new ArrayList<Department>(list2));
        }
        doctor.setYearOfExperience(request.getYearOfExperience());
        doctor.setAddress(request.getAddress());
        doctor.setRegistrationNumber(request.getRegistrationNumber());
        return doctor;
    }
    
    public static Admin toAdmin(AdminProfileRequest request) {
        if (request == null) {
            return null;
        }

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
        return admin;
    }
    
    public static AGPreferenceResponse toAGPreferenceResponse(AGPreference preference) {
        if (preference == null) {
            return null;
        }

        AGPreferenceResponse aGPreferenceResponse = new AGPreferenceResponse();
        aGPreferenceResponse.setId(preference.getId());
        aGPreferenceResponse.setMode(preference.getMode());
        aGPreferenceResponse.setDaysAhead(preference.getDaysAhead());
        List<SlotInput> list = preference.getSlotInputs();
        if (list != null) {
            aGPreferenceResponse.setSlotInputs(new ArrayList<SlotInput>(list));
        }
        aGPreferenceResponse.setStartDate(preference.getStartDate());
        aGPreferenceResponse.setEndDate(preference.getEndDate());
        aGPreferenceResponse.setSkipHoliday(preference.getSkipHoliday());
        return aGPreferenceResponse;
    }
    
    public static ADSResponse toADSResponse(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        ADSResponse aDSResponse = new ADSResponse();
        aDSResponse.setId(doctor.getId());
        aDSResponse.setName(doctor.getName());
        aDSResponse.setEmail(doctor.getEmail());
        aDSResponse.setPhone(doctor.getPhone());
        if (doctor.getDob() != null) {
            aDSResponse.setDob(DateTimeFormatter.ISO_LOCAL_DATE.format(doctor.getDob()));
        }
        aDSResponse.setGender(doctor.getGender());
        List<Degree> list = doctor.getDegrees();
        if (list != null) {
            aDSResponse.setDegrees(new ArrayList<Degree>(list));
        }
        List<Specialization> list1 = doctor.getSpecializations();
        if (list1 != null) {
            aDSResponse.setSpecializations(new ArrayList<Specialization>(list1));
        }
        List<Department> list2 = doctor.getDepartments();
        if (list2 != null) {
            aDSResponse.setDepartments(new ArrayList<Department>(list2));
        }
        aDSResponse.setYearOfExperience(doctor.getYearOfExperience());
        aDSResponse.setProfilePicPath(doctor.getProfilePicPath());
        aDSResponse.setAddress(doctor.getAddress());
        aDSResponse.setRegistrationNumber(doctor.getRegistrationNumber());
        aDSResponse.setProfileComplete(doctor.getProfileComplete());
        aDSResponse.setIsActive(doctor.getIsActive());
        aDSResponse.setCreationTime(doctor.getCreationTime());
        aDSResponse.setUpdationTime(doctor.getUpdationTime());
        return aDSResponse;
    }
    
    public static APSResponse toAPSResponse(Patient patient) {
        if (patient == null) {
            return null;
        }

        APSResponse aPSResponse = new APSResponse();
        aPSResponse.setId(patient.getId());
        aPSResponse.setName(patient.getName());
        aPSResponse.setEmail(patient.getEmail());
        aPSResponse.setPhone(patient.getPhone());
        if (patient.getDob() != null) {
            aPSResponse.setDob(DateTimeFormatter.ISO_LOCAL_DATE.format(patient.getDob()));
        }
        aPSResponse.setGender(patient.getGender());
        aPSResponse.setHeight(patient.getHeight());
        aPSResponse.setWeight(patient.getWeight());
        aPSResponse.setProfilePicPath(patient.getProfilePicPath());
        aPSResponse.setProfileComplete(patient.getProfileComplete());
        aPSResponse.setIsActive(patient.getIsActive());
        aPSResponse.setCreationTime(patient.getCreationTime());
        aPSResponse.setUpdationTime(patient.getUpdationTime());
        return aPSResponse;
    }
    
    public static Holiday toHoliday(HolidayDTO dto) {
        if (dto == null) {
            return null;
        }

        Holiday holiday = new Holiday();
        holiday.setId(dto.getId());
        holiday.setHolidayDate(dto.getHolidayDate());
        holiday.setReason(dto.getReason());
        holiday.setCreationTime(dto.getCreationTime());
        return holiday;
    }
    
    public static AVResponse toAVResponse(Availability availability) {
        if (availability == null) {
            return null;
        }

        AVResponse aVResponse = new AVResponse();
        aVResponse.setId(availability.getId());
        aVResponse.setDate(availability.getDate());
        aVResponse.setStartTime(availability.getStartTime());
        aVResponse.setEndTime(availability.getEndTime());
        aVResponse.setStatus(availability.getStatus());
        aVResponse.setMode(availability.getMode());
        return aVResponse;
    }
}
