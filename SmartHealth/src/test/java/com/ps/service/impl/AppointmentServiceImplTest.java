package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.RelationDTO;
import com.ps.dto.SubProfileDTO;
import com.ps.dto.request.AppointmentRequest;
import com.ps.dto.request.DSRequest;
import com.ps.dto.response.AIResponse;
import com.ps.dto.response.AVResponse;
import com.ps.dto.response.ApDResponse;
import com.ps.dto.response.DSResponse;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.Patient;
import com.ps.entity.SubProfile;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.SlotStatus;
import com.ps.exception.AppointmentException;
import com.ps.exception.AvailabilityException;
import com.ps.exception.DoctorException;
import com.ps.exception.PatientException;
import com.ps.exception.ProfileException;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.PatientRepository;
import com.ps.repo.SubProfileRepository;
import com.ps.service.IDoctorService;
import com.ps.util.JwtUtil;
import com.ps.util.TestDataUtil;

/**
 * This class contains unit test methods for testing
 * methods of {@link AppointmentServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {
	
	@InjectMocks
	private AppointmentServiceImpl appointmentService;
	
	@Mock
	private AvailabilityRepository availabilityRepository;

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private SubProfileRepository subProfileRepository;
	
	@Mock
	private IDoctorService doctorService;
	
	private MockedStatic<JwtUtil> jwtUtilMock;

	@BeforeEach
	void setUp() throws Exception {
		jwtUtilMock = mockStatic(JwtUtil.class);
	}

	@AfterEach
	void tearDown() throws Exception {
		jwtUtilMock.close();
	}

	/**
	 * Test {@link AppointmentServiceImpl#viewDetails(Map)}.
	 * 
	 */
	@Test
	void testViewDetails() {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.DOCTOR_ID, 1);
		requestMap.put(IRequestConstants.SLOT_ID, 1);
		Patient patient = TestDataUtil.getPatient();
		Availability availability = TestDataUtil.getAvailability();
		
		when(availabilityRepository.fetchByIdAndStatusIn((Integer) requestMap.get(IRequestConstants.SLOT_ID))).thenReturn(Optional.of(availability));
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(TestDataUtil.getPatientEmail());
		when(patientRepository.findByEmail(JwtUtil.getEmailFromToken())).thenReturn(Optional.of(patient));
		
		ApDResponse viewDetails = appointmentService.viewDetails(requestMap);
		assertEquals(availability.getId(), viewDetails.getSlot().getId());
		assertEquals(availability.getDate(), viewDetails.getSlot().getDate());
		assertEquals(availability.getDoctor().getId(), viewDetails.getDoctor().getId());
		assertEquals(availability.getDoctor().getName(), viewDetails.getDoctor().getName());
		assertEquals(patient.getId(), viewDetails.getPatient().getId());
		assertEquals(patient.getName(), viewDetails.getPatient().getName());
		
		// case when patient is not found
		when(patientRepository.findByEmail(JwtUtil.getEmailFromToken())).thenReturn(Optional.empty());
		PatientException patientException = assertThrows(PatientException.class, ()-> appointmentService.viewDetails(requestMap));
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
		
		// case when doctor id from request mismatched with doctor id from slot details
		requestMap.put(IRequestConstants.DOCTOR_ID, 2);
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()-> appointmentService.viewDetails(requestMap));
		assertEquals(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, availabilityException.getMessage());

		// case when slots is not found with given id from request
		requestMap.put(IRequestConstants.SLOT_ID, 2);
		when(availabilityRepository.fetchByIdAndStatusIn((Integer) requestMap.get(IRequestConstants.SLOT_ID))).thenReturn(Optional.empty());
		availabilityException = assertThrows(AvailabilityException.class, ()-> appointmentService.viewDetails(requestMap));
		assertEquals(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, availabilityException.getMessage());
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#bookAppointment(AppointmentRequest)}.
	 * 
	 */
	@Test
	void testBookAppointment() {
		LocalDate today = LocalDate.now();
		Patient patient = TestDataUtil.getPatient();
		Doctor doctor = TestDataUtil.getDoctor();
		SubProfile subProfile = TestDataUtil.getSubProfile();
		AppointmentRequest request = getAppointmentRequest();
		Availability availability = TestDataUtil.getAvailability();
		Appointment appointment = TestDataUtil.getAppointment();
		
		when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.of(patient));
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(TestDataUtil.getPatientEmail());
		when(availabilityRepository.fetchByIdAndStatusIn(request.getSlotId())).thenReturn(Optional.of(availability));
		when(doctorRepository.findById(request.getDoctorId())).thenReturn(Optional.of(doctor));
		// Case when booking date is in past
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.CANT_BOOK_PAST_SLOTS, availabilityException.getMessage());

		availability.setDate(today.plusDays(2));
		when(subProfileRepository.findById(request.getSubProfile().getId())).thenReturn(Optional.of(subProfile));
		when(subProfileRepository.save((SubProfile) any())).thenReturn(subProfile);
		when(appointmentRepository.save((Appointment) any())).thenReturn(appointment);
		when(availabilityRepository.save((Availability) any())).thenReturn(availability);
		// 1. Case when appointment booked successfully for given sub profile
		assertTrue(appointmentService.bookAppointment(request));
		
		subProfile.getPatient().setId(2);
		// 2. Case when patient id from sub profile doesn't match with id of patient (who) booking appointment
		PatientException patientException = assertThrows(PatientException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.SUB_PROFILE_MISMATHCED, patientException.getMessage());
		
		// 3. Case when sub profile not found for given id from request
		when(subProfileRepository.findById(request.getSubProfile().getId())).thenReturn(Optional.empty());
		ProfileException profileException = assertThrows(ProfileException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.SUB_PROFILE_NOT_FOUND, profileException.getMessage());
		
		// 4. Case when sub profile id not provided in request (new sub profile)
		request.getSubProfile().setId(null);
		assertTrue(appointmentService.bookAppointment(request));
		
		// 5. Case when sub profile flag is off (patient booking for self)
		request.setIsSubProfile(false);
		assertTrue(appointmentService.bookAppointment(request));
		
		// 6. Case when failed to save availability
		when(availabilityRepository.save((Availability) any())).thenReturn(null);
		assertFalse(appointmentService.bookAppointment(request));

		// 7. Case when failed to save both availability and appointment
		when(appointmentRepository.save((Appointment) any())).thenReturn(null);
		assertFalse(appointmentService.bookAppointment(request));
		
		// 8. Case when patient booking for today's slot but time is in past
		availability.setDate(today);
		availability.setEndTime(LocalTime.now().minusMinutes(2));
		availabilityException = assertThrows(AvailabilityException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.CANT_BOOK_PAST_SLOTS, availabilityException.getMessage());

		// 9. Case when patient booking for today's slot booking time is not in past
		availability.setDate(today);
		availability.setEndTime(LocalTime.now().plusMinutes(20));
		assertFalse(appointmentService.bookAppointment(request));
		
		// 10. Case when doctor id from slots mismatched with id of doctor from request
		doctor.setId(2);
		availabilityException = assertThrows(AvailabilityException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, availabilityException.getMessage());

		// 11. Case when doctor is not found for given id
		when(doctorRepository.findById(request.getDoctorId())).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
		
		// 12. Case when availability not found for given id from request
		when(availabilityRepository.fetchByIdAndStatusIn(request.getSlotId())).thenReturn(Optional.empty());
		availabilityException = assertThrows(AvailabilityException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, availabilityException.getMessage());
		
		// 13. Case when patient mismatched
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn("otherpatient@shc.com");
		patientException = assertThrows(PatientException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.SESSION_MISMATCHED, patientException.getMessage());
		
		// 14. Case when patient not found for given id from request
		when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.empty());
		patientException = assertThrows(PatientException.class, ()->appointmentService.bookAppointment(request));
		assertEquals(IExceptionConstants.PATIENT_NOT_FOUND, patientException.getMessage());
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#viewAllAppointments(Map, Pageable)}.
	 * Cases when patient is logined
	 */
	@Test
	void testViewAllAppointmentsPatient() {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.NAME, "s");
		Pageable pageable = PageRequest.of(0, 10);
		String email = TestDataUtil.getPatientEmail();
		String role = IPatientConstants.PATIENT_ROLE;
		List<Appointment> appointments = List.of(TestDataUtil.getAppointment());
		Page<Appointment> pages = new PageImpl<>(appointments, pageable, appointments.size());

		mockJwt(role, email);
		when(appointmentRepository.filterAppointmentsForPatient(email, (String) requestMap.get(IRequestConstants.NAME), (LocalDate) requestMap.get(IRequestConstants.DATE), (AppointmentStatus) requestMap.get(IRequestConstants.STATUS), pageable)).thenReturn(pages);

		// success scenario
		Map<String,Object> viewAllAppointments = appointmentService.viewAllAppointments(requestMap, pageable);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> appointmentList = (List<Map<String, Object>>) viewAllAppointments.get(IResponseConstants.DATA);
		Map<String, Object> appointmentDataMap = appointmentList.get(0);
		assertEquals(appointments.get(0).getId(), appointmentDataMap.get(IResponseConstants.ID));
		assertEquals(appointments.get(0).getAvailability().getDoctor().getName(), appointmentDataMap.get(IResponseConstants.DOCTOR_NAME));
		
		// case when no appointments found
		pages = new PageImpl<>(List.of());
		when(appointmentRepository.filterAppointmentsForPatient(email, (String) requestMap.get(IRequestConstants.NAME), (LocalDate) requestMap.get(IRequestConstants.DATE), (AppointmentStatus) requestMap.get(IRequestConstants.STATUS), pageable)).thenReturn(pages);
		AppointmentException appointmentException = assertThrows(AppointmentException.class, ()-> appointmentService.viewAllAppointments(requestMap, pageable));
		assertEquals(IExceptionConstants.NO_APPOINTMENTS_FOUND, appointmentException.getMessage());
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#viewAllAppointments(Map, Pageable)}.
	 * Cases when doctor is logined
	 */
	@Test
	void testViewAllAppointmentsDoctor() {
		Map<String, Object> requestMap = new HashMap<>();
		Pageable pageable = PageRequest.of(0, 10);
		String email = TestDataUtil.getDoctorEmail();
		String role = IDoctorConstants.DOCTOR_ROLE;
		List<Appointment> appointments = List.of(TestDataUtil.getAppointment());
		Page<Appointment> pages = new PageImpl<>(appointments, pageable, appointments.size());

		mockJwt(role, email);
		when(appointmentRepository.filterAppointemtnsForDoctor(email, (String) requestMap.get(IRequestConstants.NAME), (LocalDate) requestMap.get(IRequestConstants.DATE), (AppointmentStatus) requestMap.get(IRequestConstants.STATUS), (Integer) requestMap.get(IRequestConstants.SLOT_ID), pageable)).thenReturn(pages);

		// success scenario
		Map<String,Object> viewAllAppointments = appointmentService.viewAllAppointments(requestMap, pageable);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> appointmentList = (List<Map<String, Object>>) viewAllAppointments.get(IResponseConstants.DATA);
		Map<String, Object> appointmentDataMap = appointmentList.get(0);
		assertEquals(appointments.get(0).getId(), appointmentDataMap.get(IResponseConstants.ID));
		assertEquals(appointments.get(0).getPatient().getName(), appointmentDataMap.get(IResponseConstants.PATIENT_NAME));
		
		// case when no appointments found
		pages = new PageImpl<>(List.of());
		when(appointmentRepository.filterAppointemtnsForDoctor(email, (String) requestMap.get(IRequestConstants.NAME), (LocalDate) requestMap.get(IRequestConstants.DATE), (AppointmentStatus) requestMap.get(IRequestConstants.STATUS), (Integer) requestMap.get(IRequestConstants.SLOT_ID), pageable)).thenReturn(pages);
		AppointmentException appointmentException = assertThrows(AppointmentException.class, ()-> appointmentService.viewAllAppointments(requestMap, pageable));
		assertEquals(IExceptionConstants.NO_APPOINTMENTS_FOUND, appointmentException.getMessage());
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#viewAllAppointments(Map, Pageable)}.
	 * Cases when other role is logined
	 */
	@Test
	void testViewAllAppointmentsOther() {
		Map<String, Object> requestMap = new HashMap<>();
		Pageable pageable = PageRequest.of(0, 10);
		
		mockJwt("OTHER_ROLE", "other@shc.com");
		
		ProfileException profileException = assertThrows(ProfileException.class, ()-> appointmentService.viewAllAppointments(requestMap, pageable));
		assertEquals(IExceptionConstants.PROVIDE_VALID_ROLE, profileException.getMessage());
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#changeAppointmentStatus(Integer, AppointmentStatus, boolean)}.
	 * 
	 */
	@Test
	void testChangeAppointmentStatus() {
		Integer id = 1;
		Appointment appointment = TestDataUtil.getAppointment();
		AppointmentStatus newStatus = AppointmentStatus.APPROVED;
		
		mockJwt(IDoctorConstants.DOCTOR_ROLE, TestDataUtil.getDoctorEmail());
		when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
		when(appointmentRepository.updateStatusByAppId((AppointmentStatus) any(), eq(id), (LocalDateTime) any())).thenReturn(1);
		// successful scenario
		assertEquals(IResponseConstants.APPOINTMENT_APPROVED, appointmentService.changeAppointmentStatus(id, newStatus, false));

		// case when method called by admin
		assertEquals(IResponseConstants.APPOINTMENT_APPROVED, appointmentService.changeAppointmentStatus(id, newStatus, true));
		
		// status to update is D_CANCELLED
		newStatus = AppointmentStatus.D_CANCELLED;
		when(availabilityRepository.updateStatusById((SlotStatus) any(), anyInt(), (LocalDateTime) any())).thenReturn(1);
		assertEquals(IResponseConstants.APPOINTMENT_CANCELLED, appointmentService.changeAppointmentStatus(id, newStatus, false));

		// status to update is P_CANCELLED but doctor can't, so getting exception
		AppointmentException appointmentException = assertThrows(AppointmentException.class, ()-> appointmentService.changeAppointmentStatus(id, AppointmentStatus.P_CANCELLED, false));
		assertEquals(IExceptionConstants.INVALID_STATUS_IN_CHANGE_REQUEST, appointmentException.getMessage());

		// case when failed to update appointment status
		when(appointmentRepository.updateStatusByAppId((AppointmentStatus) any(), eq(id), (LocalDateTime) any())).thenReturn(0);
		appointmentException = assertThrows(AppointmentException.class, ()-> appointmentService.changeAppointmentStatus(id, AppointmentStatus.APPROVED, false));
		assertEquals(IExceptionConstants.APPOINTMENT_UPDATE_STATUS_FAIL, appointmentException.getMessage());
		
		// Case when status transition is not valid. e.g.: from BOOKED TO COMPLETED
		newStatus = AppointmentStatus.COMPLETED;
		assertEquals(String.format(IExceptionConstants.APPOINTMENT_STATUS_CHANGE_FAIL, newStatus, appointment.getStatus()), appointmentService.changeAppointmentStatus(id, newStatus, false));
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#changeAppointmentStatus(Integer, AppointmentStatus, boolean)}.
	 * When patient request status change request
	 * 
	 */
	@Test
	void testChangeAppointmentStatusPatient() {
		Integer id = 1;
		Appointment appointment = TestDataUtil.getAppointment();
		AppointmentStatus newStatus = AppointmentStatus.P_CANCELLED;
		
		mockJwt(IPatientConstants.PATIENT_ROLE, TestDataUtil.getPatientEmail());
		when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
		when(appointmentRepository.updateStatusByAppId((AppointmentStatus) any(), eq(id), (LocalDateTime) any())).thenReturn(1);
		when(availabilityRepository.updateStatusById((SlotStatus) any(), anyInt(), (LocalDateTime) any())).thenReturn(1);
		
		assertEquals(IResponseConstants.APPOINTMENT_CANCELLED, appointmentService.changeAppointmentStatus(id, newStatus, false));

		when(appointmentRepository.findById(id)).thenReturn(Optional.empty());
		AppointmentException appointmentException = assertThrows(AppointmentException.class, ()-> appointmentService.changeAppointmentStatus(id, newStatus, false));
		assertEquals(IExceptionConstants.APPOINTMENT_NOT_FOUND, appointmentException.getMessage());
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#viewAppointmentDetails(Integer)}.
	 * 
	 */
	@Test
	void testViewAppointmentDetails() {
		Integer id = 1;
		Appointment appointment = TestDataUtil.getAppointment();
		String email = TestDataUtil.getPatientEmail();
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
		when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
		
		Map<String,Object> viewAppointmentDetails = appointmentService.viewAppointmentDetails(id);
		AIResponse aiResponse = (AIResponse) viewAppointmentDetails.get(IResponseConstants.APPOINTMENT_INFO);
		DSResponse dsResponse = (DSResponse) viewAppointmentDetails.get(IResponseConstants.DOCTOR_INFO);
		assertEquals(appointment.getPatient().getId(), aiResponse.getPatient().getId());
		assertEquals(appointment.getStatus(), aiResponse.getStatus());
		assertEquals(appointment.getAvailability().getDoctor().getId(), dsResponse.getId());
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn("other@shc.com");
		PatientException patientException = assertThrows(PatientException.class, ()-> appointmentService.viewAppointmentDetails(id));
		assertEquals(IExceptionConstants.DO_NOT_ACCESS_TO_APPOINTMENT, patientException.getMessage());

		when(appointmentRepository.findById(id)).thenReturn(Optional.empty());
		AppointmentException appointmentException = assertThrows(AppointmentException.class, ()-> appointmentService.viewAppointmentDetails(id));
		assertEquals(IExceptionConstants.APPOINTMENT_NOT_FOUND, appointmentException.getMessage());
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#searchDoctorsToBookAppointment(DSRequest, Pageable)}.
	 * 
	 */
	@Test
	void testSearchDoctorsToBookAppointment() {
		DSRequest dsRequest = new DSRequest();
		Pageable pageable = PageRequest.of(0, 10);
		Map<String, Object> resultMap = new HashMap<>();
 		
		when(doctorService.searchDoctor(dsRequest, pageable)).thenReturn(resultMap);
		assertEquals(resultMap, appointmentService.searchDoctorsToBookAppointment(dsRequest, pageable));
	}
	
	/**
	 * Test {@link AppointmentServiceImpl#viewSlotsToBookAppointment(Map)}.
	 * 
	 */
	@Test
	void testViewSlotsToBookAppointment() {
		Map<String, Object> requestMap = new HashMap<>();
		Map<LocalDate, List<AVResponse>> resultMap = new HashMap<>();
		
		when(doctorService.viewSlots(requestMap)).thenReturn(resultMap);
		assertEquals(resultMap, appointmentService.viewSlotsToBookAppointment(requestMap));
	}
	
	private void mockJwt(String role, String email) {
	    jwtUtilMock.when(JwtUtil::getPrimaryRoleFromToken).thenReturn(role);
	    jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
	}

	private AppointmentRequest getAppointmentRequest() { 
		RelationDTO relation = new RelationDTO();
		relation.setId(1);
		relation.setName("Brother");
		
		SubProfileDTO subProfile = new SubProfileDTO();
		subProfile.setId(1);
		subProfile.setName("Joh Rob");
		subProfile.setPhone("1230456789");
		subProfile.setRelation(relation);
		
		AppointmentRequest appointmentRequest = new AppointmentRequest();
		appointmentRequest.setDoctorId(1);
		appointmentRequest.setIsSubProfile(true);
		appointmentRequest.setPatientId(1);
		appointmentRequest.setSlotId(1);
		appointmentRequest.setSubProfile(subProfile);
		return appointmentRequest;
	}
}
