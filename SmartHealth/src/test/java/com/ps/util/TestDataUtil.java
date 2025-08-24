package com.ps.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IPatientConstants;
import com.ps.dto.request.AGRequest;
import com.ps.dto.request.ASRequest;
import com.ps.dto.request.LeaveRequest;
import com.ps.dto.request.PasswordRequest;
import com.ps.dto.response.DailyAppointments;
import com.ps.entity.AGPreference;
import com.ps.entity.Admin;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Doctor;
import com.ps.entity.DoctorLeave;
import com.ps.entity.Holiday;
import com.ps.entity.Patient;
import com.ps.entity.Relation;
import com.ps.entity.SlotInput;
import com.ps.entity.Specialization;
import com.ps.entity.SubProfile;
import com.ps.enu.AGMode;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.LeaveStatus;
import com.ps.enu.SlotStatus;
import com.ps.repo.DoctorRepository.DoctorIdNameProjection;
import com.ps.repo.PatientRepository.PatientIdNameProjection;

public interface TestDataUtil {

	public static String getPatientEmail() {
		return "patient@shc.com";
	}
	
	public static String getDoctorEmail() {
		return "doctor@shc.com";
	}
	
	public static String getAdminEmail() {
		return "admin@shc.com";
	}
	
	public static Patient getPatient() {
		Patient patient = new Patient();
		patient.setCreationTime(LocalDateTime.of(2025, 7, 10, 10, 02, 00));
		patient.setDob(LocalDate.of(2000, 02, 10));
		patient.setEmail(getPatientEmail());
		patient.setGender("Male");
		patient.setHeight(175.0);
		patient.setId(1);
		patient.setIsActive(true);
		patient.setName("Sm Ca");
		patient.setPassword("$2a$10$d6o/wJABjSHVJa3At9Dhpe2x6x5P/skQg2HgFWw2MnUNKlOqt0jCK");
		patient.setPhone("9876543210");
		patient.setProfileComplete(true);
		patient.setProfilePicPath("1_ProfilePic_user.jpg");
		patient.setRoles(Set.of(IPatientConstants.PATIENT_ROLE));
		patient.setUpdationTime(LocalDateTime.of(2025, 7, 11, 14, 00, 00));
		patient.setWeight(70);
		return patient;
	}
	
	public static Doctor getDoctor() { 
		Doctor doctor = new Doctor();
		doctor.setAddress("JMN");
		doctor.setAgPreference(getAGPreference());
		doctor.setCreationTime(LocalDateTime.of(2025, 7, 8, 15, 10, 00));
		doctor.setDegrees(getDegrees());
		doctor.setDepartments(getDepartment());
		doctor.setDob(LocalDate.of(1996, 8, 12));
		doctor.setEmail(getDoctorEmail());
		doctor.setGender("Female");
		doctor.setId(1);
		doctor.setIsActive(true);
		doctor.setName("Sh Do");
		doctor.setPassword("$2a$10$qba/yfe5RMNm7/.c2uKdFul9QiZIBEB7HugUQXH6nKLVMW.iU2bFm");
		doctor.setPhone("0123456789");
		doctor.setProfileComplete(true);
		doctor.setProfilePicPath("1_ProfilePic_user.jpg");
		doctor.setRegistrationNumber("INS1025638");
		doctor.setRoles(Set.of(IDoctorConstants.DOCTOR_ROLE));
		doctor.setSpecializations(getSpecialization());
		doctor.setUpdationTime(LocalDateTime.of(2025, 7, 9, 9, 00, 00));
		doctor.setYearOfExperience(5);
		return doctor;
	}
	
	public static Admin getAdmin() {
		Admin admin = new Admin();
		admin.setEmail(getAdminEmail());
		admin.setId(1);
		admin.setIsActive(true);
		admin.setName("Admin");
		admin.setPassword("$2a$10$C.refJDMVi2i4vE.Ds19r.Iw4T6pYXaAPm9K5V/V/RD8SBhdvQReK");
		admin.setPhone("1472583690");
		admin.setProfilePicPath("");
		admin.setRoles(Set.of(IAdminConstants.ADMIN_ROLE));
		return admin;
	}
	
	public static AGPreference getAGPreference() { 
		AGPreference agPreference = new AGPreference();
		agPreference.setCreatedAt(LocalDateTime.of(2025, 7, 8, 15, 10, 00));
		agPreference.setId(1);
		agPreference.setLastGeneratedOn(LocalDate.of(2025, 7, 8));
		agPreference.setSlotInputs(getSlotInput());
		agPreference.setStartDate(LocalDate.of(2025, 7, 8));
		return agPreference;
	}
	
	public static List<SlotInput> getSlotInput() {
		return new ArrayList<>(List.of(new SlotInput(LocalTime.of(9, 0), LocalTime.of(13, 0), 30),
                					   new SlotInput(LocalTime.of(14, 0), LocalTime.of(18, 0), 30)));
	}
	
	public static List<Degree> getDegrees() {
		
		List<Degree> degrees = new ArrayList<>();
		Degree degree1 = new Degree();
		degree1.setId(1);
		degree1.setName("MBBS");

		Degree degree2 = new Degree();
		degree2.setId(2);
		degree2.setName("MD");
		
		degrees.add(degree1);
		degrees.add(degree2);
		return degrees;
	}

	public static List<Department> getDepartment() {
		List<Department> departments = new ArrayList<>();
		Department department = new Department();
		department.setId(1);
		department.setName("Cardiology");
		
		departments.add(department);
		return departments;
	}

	public static List<Specialization> getSpecialization() {
		List<Specialization> specializations = new ArrayList<>();
		Specialization specialization = new Specialization();
		specialization.setId(1);
		specialization.setName("Cardiologist");
		
		specializations.add(specialization);
		return specializations;
	}
	
	public static PasswordRequest getPasswordRequest() { 
		PasswordRequest passwordRequest = new PasswordRequest();
		passwordRequest.setPassword("12345");
		return passwordRequest;
	}
	
	public static SubProfile getSubProfile() {
		SubProfile subProfile = new SubProfile();
		subProfile.setId(1);
		subProfile.setName("Joh Rob");
		subProfile.setPatient(getPatient());
		subProfile.setPhone("1230456789");
		subProfile.setRelation(getRelation());
		return subProfile;
	}
	
	public static Relation getRelation() {
		Relation relation = new Relation();
		relation.setId(1);
		relation.setName("Brother");
		return relation;
	}
	
	public static LeaveRequest getLeaveRequest() { 
		LeaveRequest leaveRequest = new LeaveRequest();
		leaveRequest.setFrom(LocalDate.of(2025, 8, 7));
		leaveRequest.setReason("Some Reason");
		leaveRequest.setTo(LocalDate.of(2025, 8, 11));
		return leaveRequest;
	}
	
	public static List<Holiday> getHolidays() {
		Holiday holiday = new Holiday();
		holiday.setCreationTime(LocalDateTime.of(2025, 7, 1, 12, 0, 0));
		holiday.setHolidayDate(LocalDate.of(2025, 8, 9));
		holiday.setId(1);
		holiday.setReason("RakshaBandhan");

		Holiday holiday2 = new Holiday();
		holiday2.setCreationTime(LocalDateTime.of(2025, 7, 1, 12, 1, 0));
		holiday2.setHolidayDate(LocalDate.of(2025, 8, 15));
		holiday2.setId(2);
		holiday2.setReason("IndependenceDay");
		
		return List.of(holiday, holiday2);
	}
	
	public static DoctorLeave getDoctorLeave() {
		DoctorLeave doctorLeave = new DoctorLeave();
		doctorLeave.setCreationTime(LocalDateTime.of(2025, 8, 1, 15, 12, 0));
		doctorLeave.setDays(3);
		doctorLeave.setDoctor(getDoctor());
		doctorLeave.setFrom(LocalDate.of(2025, 8, 7));
		doctorLeave.setId(1);
		doctorLeave.setReason("Some Reason");
		doctorLeave.setStatus(LeaveStatus.BOOKED);
		doctorLeave.setTo(LocalDate.of(2025, 8, 11));
		doctorLeave.setUpdationTime(LocalDateTime.of(2025, 8, 2, 9, 45, 0));
		return doctorLeave;
	}
	
	public static Appointment getAppointment() { 
		Appointment appointment = new Appointment();
		appointment.setAvailability(getAvailability());
		appointment.setCreatedAt(LocalDateTime.of(2025, 7, 12, 10, 10, 0));
		appointment.setId(1);
		appointment.setNote("");
		appointment.setPatient(getPatient());
		appointment.setStatus(AppointmentStatus.BOOKED);
		appointment.setSubProfile(getSubProfile());
		appointment.setUpdatedAt(LocalDateTime.of(2025, 7, 14, 9, 12, 0));
		return appointment;
	}
	
	public static Availability getAvailability() {
		Availability availability = new Availability();
		availability.setCreatedAt(LocalDateTime.of(2025, 7, 11, 10, 10, 0));
		availability.setDate(LocalDate.of(2025, 7, 20));
		availability.setDoctor(getDoctor());
		availability.setEndTime(LocalTime.of(10, 0));
		availability.setId(1);
		availability.setMode(AGMode.AUTO);
		availability.setStartTime(LocalTime.of(9, 30));
		availability.setStatus(SlotStatus.BOOKED);
		availability.setUpdatedAt(LocalDateTime.of(2025, 7, 14, 9, 12, 0));
		return availability;
	}
	
	public static List<DailyAppointments> getDailyAppointments() {
		LocalDate today = LocalDate.now();
		return List.of(
					new DailyAppointments(today.minusDays(4), 1L, 2L, 3L, 4L),
					new DailyAppointments(today.minusDays(2), 3L, 2L, 1L, 4L),
					new DailyAppointments(today.minusDays(1), 4L, 1L, 4L, 3L)
				);
	}
	
	public static List<DoctorIdNameProjection> getDoctorIdNameProjections() { 
		List<DoctorIdNameProjection> doctorIdNameProjections = new ArrayList<>();
		DoctorIdNameProjection doctorIdNameProjection = new DoctorIdNameProjection() {
			@Override
			public String getName() {
				return "Sh Do";
			}
			@Override
			public Integer getId() {
				return 1;
			}
		};
		doctorIdNameProjections.add(doctorIdNameProjection);
		return doctorIdNameProjections;
	}

	public static List<PatientIdNameProjection> getPatientIdNameProjections() { 
		List<PatientIdNameProjection> patientIdNameProjections = new ArrayList<>();
		PatientIdNameProjection patientIdNameProjection = new PatientIdNameProjection() {
			@Override
			public String getName() {
				return "Sm Ca";
			}
			@Override
			public Integer getId() {
				return 1;
			}
		};
		patientIdNameProjections.add(patientIdNameProjection);
		return patientIdNameProjections;
	}
	
	public static AGRequest getAgRequest() {
		LocalDate today = LocalDate.now();
		AGRequest agRequest = new AGRequest();
		agRequest.setEndDate(today.plusDays(8));
		agRequest.setMode(AGMode.CUSTOM_ONE_TIME);
		agRequest.setSlotInputs(TestDataUtil.getSlotInput());
		agRequest.setStartDate(today);
		return agRequest;
	}	
	
	public static List<ASRequest> getAsRequests() {
		ASRequest asRequest = new ASRequest();
		asRequest.setDate(LocalDate.now().plusDays(2));
		asRequest.setFrom(LocalTime.of(9, 0));
		asRequest.setTo(LocalTime.of(9, 30));
		return List.of(asRequest);
	}
}
