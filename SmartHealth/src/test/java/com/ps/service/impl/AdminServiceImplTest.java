package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ps.config.props.SlotsProperties;
import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.HolidayDTO;
import com.ps.dto.request.ADSRequest;
import com.ps.dto.request.APSRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.ADSResponse;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.dto.response.APSResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.LeaveResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.DoctorLeave;
import com.ps.entity.Holiday;
import com.ps.entity.Patient;
import com.ps.entity.SubProfile;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.LeaveStatus;
import com.ps.enu.SlotStatus;
import com.ps.exception.AdminException;
import com.ps.exception.AppointmentException;
import com.ps.exception.AvailabilityException;
import com.ps.exception.DoctorException;
import com.ps.exception.PatientException;
import com.ps.mapper.AGPreferenceMapper;
import com.ps.mapper.DoctorMapper;
import com.ps.mapper.HolidayMapper;
import com.ps.mapper.PatientMapper;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorLeaveRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.DoctorRepository.DoctorIdNameProjection;
import com.ps.repo.HolidayRepository;
import com.ps.repo.PatientRepository;
import com.ps.repo.PatientRepository.PatientIdNameProjection;
import com.ps.repo.SubProfileRepository;
import com.ps.service.IAppointmentService;
import com.ps.service.IAvailabilityService;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * This test class contains unit test case for
 * methods of {@link AdminServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {
	
	@InjectMocks
	private AdminServiceImpl adminService;
	
	@Mock
	private DoctorRepository doctorRepository;
	
	@Mock
	private PatientRepository patientRepository;
	
	@Mock
	private SubProfileRepository subProfileRepository;
	
	@Mock
	private AvailabilityRepository availabilityRepository;
	
	@Mock
	private IAvailabilityService availabilityService;
	
	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private IAppointmentService appointmentService;
	
	@Mock
	private DoctorLeaveRepository leaveRepository;
	
	@Mock
	private SlotsProperties slotsProperties;
	
	@Mock
	private HolidayRepository holidayRepository;
	
	@Mock
	private DoctorMapper doctorMapper;
	
	@Mock
	private AGPreferenceMapper agMapper;
	
	@Mock
	private PatientMapper patientMapper;
	
	@Mock
	private HolidayMapper holidayMapper;

	/**
	 * Test {@link AdminServiceImpl#searchDoctors(ADSRequest, Pageable)}.
	 * 
	 */
	@Test
	void testSearchDoctors() {
		ADSRequest adsRequest = new ADSRequest();
		Pageable pageable = PageRequest.of(0, 10);
		List<Doctor> doctors = List.of(TestDataUtil.getDoctor());
		Page<Doctor> pages = new PageImpl<>(doctors, pageable, doctors.size());
		
		when(doctorRepository.searchDoctors(adsRequest.getId(), adsRequest.getName(), adsRequest.getEmail(), adsRequest.getPhone(), 
				adsRequest.getGender(), adsRequest.getDegree(), adsRequest.getSpecialization(), adsRequest.getDepartment(), 
				adsRequest.getYearOfExperience(), adsRequest.getRegistrationNumber(), adsRequest.getProfileComplete(), 
				adsRequest.getIsActive(), pageable)).thenReturn(pages);
		
		Map<String,Object> searchDoctors = adminService.searchDoctors(adsRequest, pageable);
		@SuppressWarnings("unchecked")
		List<ADSResponse> response = (List<ADSResponse>) searchDoctors.get(IResponseConstants.DATA);
		assertEquals(doctors.get(0).getId(), response.get(0).getId());
		assertEquals(doctors.get(0).getEmail(), response.get(0).getEmail());
		assertEquals(doctors.get(0).getName(), response.get(0).getName());
		assertEquals(doctors.get(0).getPhone(), response.get(0).getPhone());
		assertEquals(doctors.get(0).getIsActive(), response.get(0).getIsActive());
		assertEquals(doctors.get(0).getProfileComplete(), response.get(0).getProfileComplete());
	}
	
	/**
	 * Test {@link AdminServiceImpl#searchPatients(APSRequest, Pageable)}.
	 * 
	 */
	@Test
	void testSearchPatients() {
		APSRequest apsRequest = new APSRequest();
		Pageable pageable = PageRequest.of(0, 10);
		List<Patient> patients = List.of(TestDataUtil.getPatient());
		Page<Patient> pages = new PageImpl<>(patients, pageable, patients.size());
		
		when(patientRepository.searchPatients(apsRequest.getId(), apsRequest.getName(), apsRequest.getEmail(), apsRequest.getPhone(), apsRequest.getGender(), apsRequest.getProfileComplete(), apsRequest.getIsActive(), pageable)).thenReturn(pages);
		
		Map<String,Object> searchPatients = adminService.searchPatients(apsRequest, pageable);
		@SuppressWarnings("unchecked")
		List<APSResponse> response = (List<APSResponse>) searchPatients.get(IResponseConstants.DATA);
		assertEquals(patients.get(0).getId(), response.get(0).getId());
		assertEquals(patients.get(0).getName(), response.get(0).getName());
		assertEquals(patients.get(0).getEmail(), response.get(0).getEmail());
		assertEquals(patients.get(0).getPhone(), response.get(0).getPhone());
		assertEquals(patients.get(0).getIsActive(), response.get(0).getIsActive());
		assertEquals(patients.get(0).getProfileComplete(), response.get(0).getProfileComplete());
	}
	
	/**
	 * Test {@link AdminServiceImpl#viewDoctorProfile(Integer)}.
	 * 
	 */
	@Test
	void testViewDoctorProfile() {
		Integer id = 1;
		Doctor doctor = TestDataUtil.getDoctor();
		AGPreferenceResponse agPreferenceResponse = TestConverterUtil.toAGPreferenceResponse(doctor.getAgPreference());
		ADSResponse adsResponse = TestConverterUtil.toADSResponse(doctor);
		
		when(doctorRepository.findById(id)).thenReturn(Optional.of(doctor));
		when(agMapper.toResponse(doctor.getAgPreference())).thenReturn(agPreferenceResponse);
		when(doctorMapper.toADSResponse(doctor)).thenReturn(adsResponse);
		
		// Success scenario
		ADSResponse actualadsResponse = adminService.viewDoctorProfile(id);
		assertEquals(adsResponse, actualadsResponse);
		assertEquals(agPreferenceResponse, actualadsResponse.getAgPreferenceResponse());

		// Case when doctor not found for given id
		when(doctorRepository.findById(id)).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, ()->{
			adminService.viewDoctorProfile(id);
		});
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
	}

	/**
	 * Test {@link AdminServiceImpl#viewPatientProfile(Integer)}.
	 * 
	 */
	@Test
	void testViewPatientProfile() {
		Integer id = 1;
		Patient patient = TestDataUtil.getPatient();
		APSResponse apsResponse = TestConverterUtil.toAPSResponse(patient);
		List<SubProfile> subprofiles = List.of(TestDataUtil.getSubProfile());
		
		when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
		when(patientMapper.toAPSResponse(patient)).thenReturn(apsResponse);
		when(subProfileRepository.findByPatientId(id)).thenReturn(subprofiles);
		
		// Success scenario
		APSResponse actualapsResponse = adminService.viewPatientProfile(id);
		assertEquals(apsResponse, actualapsResponse);
		assertEquals(subprofiles.get(0).getId(), actualapsResponse.getSubProfiles().get(0).getId());
		
		// Case when patient not found for given id
		when(patientRepository.findById(id)).thenReturn(Optional.empty());
		PatientException patientException = assertThrows(PatientException.class, ()->{
			adminService.viewPatientProfile(id);
		});
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
	}
	
	/**
	 * Test {@link AdminServiceImpl#viewPatientProfile(Integer)}.
	 * Case when there is no sub profiles linked with given patient
	 */
	@Test
	void testViewPatientProfileNoSubProfile() {
		Integer id = 1;
		Patient patient = TestDataUtil.getPatient();
		APSResponse apsResponse = TestConverterUtil.toAPSResponse(patient);
		
		when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
		when(patientMapper.toAPSResponse(patient)).thenReturn(apsResponse);
		when(subProfileRepository.findByPatientId(id)).thenReturn(new ArrayList<>());
		
		APSResponse actualResponse = adminService.viewPatientProfile(id);
		assertEquals(apsResponse, actualResponse);
		assertNull(actualResponse.getSubProfiles());
	}

	/**
	 * Test {@link AdminServiceImpl#toggleStatus(Integer, String)}.
	 * When provided role is doctor.
	 */
	@Test
	void testToggleStatusDoctor() {
		Integer id = 1;
		String role = IDoctorConstants.DOCTOR_ROLE;
		Doctor doctor = TestDataUtil.getDoctor();
		
		when(doctorRepository.findById(id)).thenReturn(Optional.of(doctor));
		when(doctorRepository.toggleStatus(eq(id), anyBoolean())).thenReturn(1);
		// activation status toggled when activation status is active
		assertEquals(1, adminService.toggleStatus(id, role));

		// activation status toggled when activation status is inactive 
		doctor.setIsActive(false);
		assertEquals(1, adminService.toggleStatus(id, role));
		
		// activation status toggle failed
		when(doctorRepository.toggleStatus(id, !doctor.getIsActive())).thenReturn(0);
		AdminException adminException = assertThrows(AdminException.class, () -> {
			adminService.toggleStatus(id, role);
		});
		assertEquals(IExceptionConstants.SOMETHING_WENT_WRONG, adminException.getMessage());

		// when doctor not found for given id
		when(doctorRepository.findById(id)).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, () -> {
			adminService.toggleStatus(id, role);
		});
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
	}

	/**
	 * Test {@link AdminServiceImpl#toggleStatus(Integer, String)}.
	 * When provided role is patient.
	 */
	@Test
	void testToggleStatusPatient() {
		Integer id = 1;
		String role = IPatientConstants.PATIENT_ROLE;
		Patient patient = TestDataUtil.getPatient();
		
		when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
		when(patientRepository.toggleStatus(eq(id), anyBoolean())).thenReturn(1);
		// activation status toggled when activation status is active
		assertEquals(1, adminService.toggleStatus(id, role));

		// activation status toggled when activation status is inactive 
		patient.setIsActive(false);
		assertEquals(1, adminService.toggleStatus(id, role));

		// activation status toggle failed
		when(patientRepository.toggleStatus(id, !patient.getIsActive())).thenReturn(0);
		AdminException adminException = assertThrows(AdminException.class, () -> {
			adminService.toggleStatus(id, role);
		});
		assertEquals(IExceptionConstants.SOMETHING_WENT_WRONG, adminException.getMessage());

		// when patient not found for given id
		when(patientRepository.findById(id)).thenReturn(Optional.empty());
		PatientException patientException = assertThrows(PatientException.class, () -> {
			adminService.toggleStatus(id, role);
		});
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
	}
	
	/**
	 * Test {@link AdminServiceImpl#toggleStatus(Integer, String)}.
	 * When provided role is not in switch case and falls under default.
	 */
	@Test
	void testToggleStatusDefault() {
		String role = "OTHER_ROLE";
		
		AdminException adminException = assertThrows(AdminException.class, ()->adminService.toggleStatus(1, role));
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, adminException.getMessage());
		
	}

	/**
	 * Test {@link AdminServiceImpl#getDoctorsList()}.
	 * 
	 */
	@Test
	void testGetDoctorsList() {
		List<DoctorIdNameProjection> doctorIdNameProjections = TestDataUtil.getDoctorIdNameProjections();
		
		when(doctorRepository.findAllProjectedBy()).thenReturn(doctorIdNameProjections);
		
		List<DoctorIdNameProjection> doctorsList = adminService.getDoctorsList();
		assertEquals(doctorIdNameProjections, doctorsList);
	}

	/**
	 * Test {@link AdminServiceImpl#getPatientList()}.
	 * 
	 */
	@Test
	void testGetPatientList() {
		List<PatientIdNameProjection> patientIdNameProjections = TestDataUtil.getPatientIdNameProjections();
		
		when(patientRepository.findAllProjectedBy()).thenReturn(patientIdNameProjections);
		
		List<PatientIdNameProjection> patientList = adminService.getPatientList();
		assertEquals(patientIdNameProjections, patientList);
	}
	
	/**
	 * Test {@link AdminServiceImpl#searchAvailabilitySlots(Map, Pageable)}.
	 * 
	 */
	@Test
	void testSearchAvailabilitySlots() { 
		Map<String, Object> requestMap = new HashMap<>();
		Pageable pageable = PageRequest.of(0, 10);
		List<Availability> slots = List.of(TestDataUtil.getAvailability());
		Page<Availability> pages = new PageImpl<>(slots, pageable, slots.size());
		
		when(availabilityRepository.searchAvailability((Integer) requestMap.get(IRequestConstants.DOCTOR_ID), (LocalDate) requestMap.get(IRequestConstants.DATE), (SlotStatus) requestMap.get(IRequestConstants.STATUS), pageable)).thenReturn(pages);
		
		Map<String,Object> searchAvailabilitySlots = adminService.searchAvailabilitySlots(requestMap, pageable);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> response = (List<Map<String, Object>>) searchAvailabilitySlots.get(IResponseConstants.DATA);
		Map<String, Object> responseMap = response.get(0);
		AVResponse avResponse = (AVResponse) responseMap.get(IAdminConstants.SLOT);
		DoctorIdNameProjection doctorIdNameProjection = (DoctorIdNameProjection) responseMap.get(IAdminConstants.DOCTOR);
		assertEquals(slots.get(0).getId(), avResponse.getId());
		assertEquals(slots.get(0).getDate(), avResponse.getDate());
		assertEquals(slots.get(0).getMode(), avResponse.getMode());
		assertEquals(slots.get(0).getDoctor().getId(), doctorIdNameProjection.getId());
		assertEquals(slots.get(0).getDoctor().getName(), doctorIdNameProjection.getName());
	}
	
	/**
	 * Test {@link AdminServiceImpl#deleteAvailabilitySlot(Integer)}.
	 * 
	 */
	@Test
	void testDeleteAvailabilitySlot() {
		Integer id = 1;
		
		// Successfully deleted
		when(availabilityRepository.deleteByIdAndStatus(id)).thenReturn(1);
		assertEquals(1, adminService.deleteAvailabilitySlot(id));
		
		// Somehow slot is not deleted, may be mismatched id or status
		when(availabilityRepository.deleteByIdAndStatus(id)).thenReturn(0);
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()->adminService.deleteAvailabilitySlot(id));
		assertEquals(IExceptionConstants.NOT_ABLE_TO_DELETE_SLOT, availabilityException.getMessage());
	}
	
	/**
	 * Test {@link AdminServiceImpl#viewSlotDetails(Integer, Integer)}.
	 * 
	 */
	@Test
	void testViewSlotDetails() {
		Integer id = 1;
		Integer appointmentId = 1;
		ADResponse adResponse = new ADResponse();
		
		when(availabilityService.viewSlotDetails(id, appointmentId, true)).thenReturn(adResponse);
		
		assertEquals(adResponse, adminService.viewSlotDetails(id, appointmentId));
	}
	
	/**
	 * Test {@link AdminServiceImpl#changeAvailabilityAppointmentStatus(Map)}.
	 * 
	 */
	@Test
	void testChangeAvailabilityAppointmentStatus() { 
		String message = IResponseConstants.APPOINTMENT_APPROVED;
		Map<String, Object> requestMap = new HashMap<>();
		
		when(availabilityService.changeAvailabilityAppointmentStatus(requestMap, true)).thenReturn(message);
		
		assertEquals(IResponseConstants.APPOINTMENT_APPROVED, adminService.changeAvailabilityAppointmentStatus(requestMap));
	}
	
	/**
	 * Test {@link AdminServiceImpl#searchAppointments(Map, Pageable)}.
	 * 
	 */
	@Test
	void testSearchAppointments() {
		Map<String, Object> requestMap = new HashMap<>();
		Pageable pageable = PageRequest.of(0, 10);
		List<Appointment> appointments = List.of(TestDataUtil.getAppointment());
		Page<Appointment> pages = new PageImpl<>(appointments, pageable, appointments.size());
		
		when(appointmentRepository.searchAppointments((Integer) requestMap.get(IRequestConstants.DOCTOR_ID), (Integer) requestMap.get(IRequestConstants.PATIENT_ID), (LocalDate) requestMap.get(IRequestConstants.DATE), (AppointmentStatus) requestMap.get(IRequestConstants.STATUS), pageable)).thenReturn(pages);
		
		Map<String,Object> searchAppointments = adminService.searchAppointments(requestMap, pageable);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> response = (List<Map<String, Object>>) searchAppointments.get(IResponseConstants.DATA);
		Map<String, Object> responseMap = response.get(0);
		assertEquals(appointments.get(0).getId(), responseMap.get(IResponseConstants.ID));
		assertEquals(appointments.get(0).getAvailability().getDoctor().getName(), responseMap.get(IAdminConstants.DOCTOR_NAME));
		assertEquals(appointments.get(0).getPatient().getName(), responseMap.get(IAdminConstants.PATIENT_NAME));
		assertEquals(appointments.get(0).getAvailability().getDate(), responseMap.get(IAdminConstants.DATE));
	}
	
	/**
	 * Test {@link AdminServiceImpl#changeAppointmentStatus(Integer, AppointmentStatus)}.
	 * 
	 */
	@Test
	void testChangeAppointmentStatus() {
		Integer id = 1;
		String message = IResponseConstants.APPOINTMENT_APPROVED;
		AppointmentStatus newStatus = AppointmentStatus.APPROVED;
		
		when(appointmentService.changeAppointmentStatus(id, newStatus, true)).thenReturn(message);
		
		assertEquals(message, adminService.changeAppointmentStatus(id, newStatus));
	}

	/**
	 * Test {@link AdminServiceImpl#searchLeaves(Map, Pageable)}.
	 * 
	 */
	@Test
	void testSearchLeaves() {
		Map<String, Object> requestMap = new HashMap<>();
		Pageable pageable = PageRequest.of(0, 10);
		List<DoctorLeave> doctorLeaves = List.of(TestDataUtil.getDoctorLeave());
		Page<DoctorLeave> pages = new PageImpl<>(doctorLeaves, pageable, doctorLeaves.size());
		
		when(leaveRepository.searchLeavesByAdmin((Integer) requestMap.get(IRequestConstants.DOCTOR_ID), (LocalDate) requestMap.get(IRequestConstants.FROM), (LocalDate) requestMap.get(IRequestConstants.TO), (LeaveStatus) requestMap.get(IRequestConstants.STATUS), pageable)).thenReturn(pages);
		
		Map<String,Object> searchLeaves = adminService.searchLeaves(requestMap, pageable);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> response = (List<Map<String, Object>>) searchLeaves.get(IResponseConstants.DATA);
		Map<String, Object> responseMap= response.get(0);
		LeaveResponse leaveResponse = (LeaveResponse) responseMap.get(IAdminConstants.LEAVE_INFO);
		DoctorIdNameProjection doctorIdNameProjection = (DoctorIdNameProjection) responseMap.get(IAdminConstants.DOCTOR_INFO);
		assertEquals(doctorLeaves.get(0).getId(), leaveResponse.getId());
		assertEquals(doctorLeaves.get(0).getStatus(), leaveResponse.getStatus());
		assertEquals(doctorLeaves.get(0).getDoctor().getId(), doctorIdNameProjection.getId());
		assertEquals(doctorLeaves.get(0).getDoctor().getName(), doctorIdNameProjection.getName());
	}
	
	/**
	 * Test {@link AdminServiceImpl#changeLeaveStatus(Integer, String, LeaveStatus)}.
	 * 
	 */
	@Test
	void testChangeLeaveStatus() {
		Integer id = 1;
		String role = IDoctorConstants.DOCTOR_ROLE;
		LeaveStatus status = LeaveStatus.APPROVED;
		DoctorLeave doctorLeave = TestDataUtil.getDoctorLeave();
		
		when(leaveRepository.findById(id)).thenReturn(Optional.of(doctorLeave));
		when(leaveRepository.changeLeaveStatus(eq(id), (LocalDateTime) any(), (LeaveStatus) any())).thenReturn(1);
		when(slotsProperties.getMaximumGenerationDays()).thenReturn(5);
		when(availabilityRepository.findByDoctorIdAndDateBetween(eq(doctorLeave.getDoctor().getId()), (LocalDate) any(), (LocalDate) any())).thenReturn(new ArrayList<>());
		// successfully updating leave status
		assertEquals(IResponseConstants.LEAVE_APPROVED, adminService.changeLeaveStatus(id, role, status));

		// when status is other than APPROVED, currently only possible value is REJECTED
		assertEquals(IResponseConstants.LEAVE_REJECTED ,adminService.changeLeaveStatus(id, role, LeaveStatus.REJECTED));

		// case when updating status failed
		when(leaveRepository.changeLeaveStatus(eq(id), (LocalDateTime) any(), (LeaveStatus) any())).thenReturn(0);
		AppointmentException appointmentException = assertThrows(AppointmentException.class, ()->adminService.changeLeaveStatus(id, role, status));
		assertEquals(IResponseConstants.LEAVE_STATUS_UPDATE_FAILED, appointmentException.getMessage());
		
		// case when status transition is not correct, example: from REJECTED to APPROVED
		doctorLeave.setStatus(LeaveStatus.REJECTED);
		assertEquals("Not able to change status to " + status + " as current status is: " + doctorLeave.getStatus(), adminService.changeLeaveStatus(id, role, status));
		
		// case when leave is not found with given id
		when(leaveRepository.findById(id)).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, ()->adminService.changeLeaveStatus(id, role, status));
		assertEquals(IExceptionConstants.LEAVE_NOT_FOUND, doctorException.getMessage());
		
		// case when provided role is other than doctor
		AdminException adminException = assertThrows(AdminException.class, ()-> adminService.changeLeaveStatus(id, IPatientConstants.PATIENT_ROLE, status));
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, adminException.getMessage());
	}
	
	/**
	 * Test {@link AdminServiceImpl#changeLeaveStatus(Integer, String, LeaveStatus)}.
	 * This test method covers different scenarios of private method {@code deleteSlotOrCancelAppointment}
	 * which get call from {@link AdminServiceImpl#changeLeaveStatus(Integer, String, LeaveStatus)}.
	 * 
	 */
	@Test
	void testChangeLeaveStatusHandleSlots() {
		Integer id = 1;
		LocalDate today = LocalDate.now();
		String role = IDoctorConstants.DOCTOR_ROLE;
		LeaveStatus status = LeaveStatus.APPROVED;
		DoctorLeave doctorLeave = TestDataUtil.getDoctorLeave();
		doctorLeave.setFrom(today.plusDays(7));
		doctorLeave.setTo(today.plusDays(10));
		
		when(leaveRepository.findById(id)).thenReturn(Optional.of(doctorLeave));
		when(leaveRepository.changeLeaveStatus(eq(id), (LocalDateTime) any(), (LeaveStatus) any())).thenReturn(1);
		when(slotsProperties.getMaximumGenerationDays()).thenReturn(5);
		// Case when leave booking dates doesn't falls in maximum generation dates
		assertEquals(IResponseConstants.LEAVE_APPROVED, adminService.changeLeaveStatus(id, role, status));

		doctorLeave.setFrom(today);
		doctorLeave.setTo(today.plusDays(6));
		List<Availability> slots = List.of(TestDataUtil.getAvailability());

		when(availabilityRepository.findByDoctorIdAndDateBetween(eq(doctorLeave.getDoctor().getId()), (LocalDate) any(), (LocalDate) any())).thenReturn(slots);
		when(availabilityRepository.updateStatusByIds(eq(SlotStatus.CANCELLED), anyList(), (LocalDateTime) any())).thenReturn(1);
		when(appointmentRepository.updateStatusByAvailabilityIds(eq(AppointmentStatus.D_CANCELLED), anyList(), (LocalDateTime) any(), eq(List.of(AppointmentStatus.BOOKED, AppointmentStatus.APPROVED)))).thenReturn(1);
		// case when from date is within maximum generation date limit and to date is out of that limit (greater)
		assertEquals(IResponseConstants.LEAVE_APPROVED, adminService.changeLeaveStatus(id, role, status));
		
		slots.get(0).setStatus(SlotStatus.AVAILABLE);
		doNothing().when(availabilityRepository).deleteAll(anyList());
		// case when slot(s) status is AVAILABLE
		assertEquals(IResponseConstants.LEAVE_APPROVED, adminService.changeLeaveStatus(id, role, status));

		slots.get(0).setStatus(SlotStatus.RE_AVAILABLE);
		// case when slot(s) status is RE_AVAILABLE
		assertEquals(IResponseConstants.LEAVE_APPROVED, adminService.changeLeaveStatus(id, role, status));

		slots.get(0).setStatus(SlotStatus.CANCELLED);
		// case when slot(s) status is CANCELLED
		assertEquals(IResponseConstants.LEAVE_APPROVED, adminService.changeLeaveStatus(id, role, status));
	}
	
	/**
	 * Test {@link AdminServiceImpl#addHoliday(HolidayDTO)}.
	 * 
	 */
	@Test
	void testAddHoliday() {
		HolidayDTO holidayDTO = getHolidayDTO();
		Holiday holiday = TestConverterUtil.toHoliday(holidayDTO);
		
		when(holidayMapper.toHoliday(holidayDTO)).thenReturn(TestConverterUtil.toHoliday(holidayDTO));
		when(holidayRepository.save(holiday)).thenReturn(TestDataUtil.getHolidays().get(0));
		// Success scenario
		assertTrue(adminService.addHoliday(holidayDTO));

		// saving holiday failed
		when(holidayRepository.save(holiday)).thenReturn(null);
		assertFalse(adminService.addHoliday(holidayDTO));
		
		// when given date is conflict with sunday
		holidayDTO.setHolidayDate(LocalDate.of(2025, 8, 10));
		AdminException adminException = assertThrows(AdminException.class, ()->adminService.addHoliday(holidayDTO));
		assertEquals(IExceptionConstants.HOLIDAY_SUNDAY_CONFLICT, adminException.getMessage());
	}
	
	/**
	 * Test {@link AdminServiceImpl#deleteHoliday(Integer)}.
	 * 
	 */
	@Test
	void testDeleteHoliday() {
		Integer id = 1;
		doNothing().when(holidayRepository).deleteById(id);
		
		adminService.deleteHoliday(id);
		verify(holidayRepository, times(1)).deleteById(id);
	}
	
	private HolidayDTO getHolidayDTO() {
		HolidayDTO holidayDTO = new HolidayDTO();
		holidayDTO.setHolidayDate(LocalDate.of(2025, 8, 9));
		holidayDTO.setReason("RakshaBandhan");
		return holidayDTO;
	}
}
