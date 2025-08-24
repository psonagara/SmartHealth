package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
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

import com.ps.constants.IAdminConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.request.ADRequest;
import com.ps.dto.request.AGRequest;
import com.ps.dto.request.AVRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.dto.response.AVResponse;
import com.ps.entity.AGPreference;
import com.ps.entity.Appointment;
import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.entity.SlotInput;
import com.ps.enu.AGMode;
import com.ps.enu.AppointmentStatus;
import com.ps.enu.SlotStatus;
import com.ps.exception.AppointmentException;
import com.ps.exception.AvailabilityException;
import com.ps.exception.DoctorException;
import com.ps.mapper.AGPreferenceMapper;
import com.ps.mapper.AvailabilityMapper;
import com.ps.repo.AGPreferenceRepository;
import com.ps.repo.AppointmentRepository;
import com.ps.repo.AvailabilityRepository;
import com.ps.repo.DoctorLeaveRepository;
import com.ps.repo.DoctorRepository;
import com.ps.repo.HolidayRepository;
import com.ps.repo.SlotInputRepository;
import com.ps.util.JwtUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * This class contains unit test methods for testing
 * methods of {@link AvailabilityServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {
	
	@InjectMocks
	private AvailabilityServiceImpl availabilityService;
	
	@Mock
	private AvailabilityRepository availabilityRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private HolidayRepository holidayRepository;

	@Mock
	private AGPreferenceRepository agPreferenceRepository;

	@Mock
	private SlotInputRepository slotInputRepository;

	@Mock
	private AppointmentRepository appointmentRepository;
	
	@Mock
	private AGPreferenceMapper agMapper;
	
	@Mock
	private DoctorLeaveRepository leaveRepository;
	
	@Mock
	private AvailabilityMapper availabilityMapper;
	
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
	 * Test {@link AvailabilityServiceImpl#generateAvailabilitySlots(AGRequest)}.
	 * 
	 */
	@Test
	void testGenerateAvailabilitySlots() {
		AGRequest request = new AGRequest();
		request.setMode(AGMode.AUTO);
		Doctor doctor = TestDataUtil.getDoctor();
		String email = TestDataUtil.getDoctorEmail();
		AvailabilityServiceImpl spyService = spy(availabilityService);
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
		when(doctorRepository.findByEmail(JwtUtil.getEmailFromToken())).thenReturn(Optional.of(doctor));
		doReturn(true).when(spyService).setDefaultAGPreference(doctor);
		// case when mode is AUTO
		spyService.generateAvailabilitySlots(request);
		verify(spyService, times(1)).setDefaultAGPreference(doctor);
		
		request.setMode(AGMode.CUSTOM_ONE_TIME);
		doNothing().when(spyService).generateAvailabilitySlots(request, doctor);
		//case when mode is CUSTOM_ONE_TIME
		spyService.generateAvailabilitySlots(request);
		verify(spyService, times(1)).generateAvailabilitySlots(request, doctor);
		
		//case when mode is CUSTOM_CONTINUOUS
		request.setMode(AGMode.CUSTOM_CONTINUOUS);
		spyService.generateAvailabilitySlots(request);

		// case when mode is MANUAL, generation of slots with given date and time
		request.setMode(AGMode.MANUAL);
		request.setManualSlots(TestDataUtil.getAsRequests());
		spyService.generateAvailabilitySlots(request);

		// case when mode is MANUAL, just save preference to MANUAL
		request.setManualSlots(null);
		AGPreference agPreference = TestDataUtil.getAGPreference();
		agPreference.setDoctor(TestDataUtil.getDoctor());
		
		when(agPreferenceRepository.findById(doctor.getId())).thenReturn(Optional.of(agPreference));
		doNothing().when(agMapper).updateAGPreferenceFromRequest(request, agPreference);
		when(agPreferenceRepository.save(any(AGPreference.class))).thenReturn(agPreference);
		spyService.generateAvailabilitySlots(request);

		// previous scenario plus if failed to save availability generation preference
		when(agPreferenceRepository.save(any(AGPreference.class))).thenReturn(null);
		spyService.generateAvailabilitySlots(request);

		// in case of availability generation preference not found
		when(agPreferenceRepository.findById(doctor.getId())).thenReturn(Optional.empty());
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()-> spyService.generateAvailabilitySlots(request));
		assertEquals(IExceptionConstants.PREFERENCE_NOT_FOUND + agPreference.getDoctor().getId(), availabilityException.getMessage());
		
		// Case when mode is not fall under any switch case
		request.setMode(AGMode.SCHEDULED);
		availabilityException = assertThrows(AvailabilityException.class, ()-> spyService.generateAvailabilitySlots(request));
		assertEquals(IExceptionConstants.INVALID_MODE, availabilityException.getMessage());
		
		// in case of doctor not found
		when(doctorRepository.findByEmail(JwtUtil.getEmailFromToken())).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, ()-> spyService.generateAvailabilitySlots(request));
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#generateAvailabilitySlots(AGRequest, Doctor)}.
	 * Case when mode is MANUAL
	 */
	@Test
	void testGenerateAvailabilitySlotsManual() {
		AGRequest agRequest = new AGRequest();
		agRequest.setMode(AGMode.MANUAL);
		agRequest.setManualSlots(TestDataUtil.getAsRequests());
		Doctor doctor = TestDataUtil.getDoctor();
		
		when(availabilityRepository.existsByDoctorAndDateAndStartTimeAndEndTime(eq(doctor), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
		when(availabilityRepository.hasOverlappingSlot(eq(doctor.getId()), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
		when(availabilityRepository.save(any(Availability.class))).thenReturn(TestDataUtil.getAvailability());
		// successful creation of slot
		availabilityService.generateAvailabilitySlots(agRequest, doctor);
		verify(availabilityRepository, times(1)).save(any(Availability.class));
		
		// case when given have overlapping with existing one
		when(availabilityRepository.hasOverlappingSlot(eq(doctor.getId()), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
		availabilityService.generateAvailabilitySlots(agRequest, doctor);

		// case when given have overlapping with existing one as exact date and time for given doctor		
		when(availabilityRepository.existsByDoctorAndDateAndStartTimeAndEndTime(eq(doctor), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);
		availabilityService.generateAvailabilitySlots(agRequest, doctor);

		// case when slot generation request is for past time
		agRequest.getManualSlots().get(0).setDate(LocalDate.now().minusDays(2));
		availabilityService.generateAvailabilitySlots(agRequest, doctor);
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#generateAvailabilitySlots(AGRequest, Doctor)}.
	 * Testing for modes other than MANUAL
	 * 
	 */
	@Test
	void testGenerateAvailabilitySlotsO() {
		AGRequest agRequest = TestDataUtil.getAgRequest();
		Doctor doctor = TestDataUtil.getDoctor();
		AGPreference agPreference = TestDataUtil.getAGPreference();
		List<SlotInput> slotInputs = TestDataUtil.getSlotInput();
		LocalDate holiday = null;
		LocalDate leaveDay = null;
		for (LocalDate date = agRequest.getStartDate(); !date.isAfter(agRequest.getEndDate()); date = date.plusDays(1)) {
			if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
				if (holiday != null && leaveDay == null) {
					leaveDay = date;
				} else if (holiday == null) {
					holiday = date;
				} else {
					break;
				}
			}
		}
		
		when(availabilityRepository.existsByDoctorAndDateAndStartTimeAndEndTime(eq(doctor), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
		when(availabilityRepository.hasOverlappingSlot(eq(doctor.getId()), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
		when(availabilityRepository.save(any(Availability.class))).thenReturn(TestDataUtil.getAvailability());
		when(agPreferenceRepository.findById(doctor.getId())).thenReturn(Optional.of(agPreference));
		doNothing().when(agMapper).updateAGPreferenceFromRequest(any(AGRequest.class), eq(agPreference));
		when(agPreferenceRepository.save(any(AGPreference.class))).thenReturn(agPreference);
		when(slotInputRepository.findByStartTimeAndEndTimeAndGapInMinutes(any(LocalTime.class), any(LocalTime.class), anyInt())).thenAnswer(invocation -> {
			LocalTime start = invocation.getArgument(0);
			LocalTime end = invocation.getArgument(1);
			int gapInMinutes = invocation.getArgument(2);
			
			return slotInputs.stream().filter(s -> s.getStartTime().equals(start) && s.getEndTime().equals(end) && s.getGapInMinutes().equals(gapInMinutes)).findFirst();
		});
		when(holidayRepository.existsByHolidayDate(holiday)).thenReturn(true);
		when(leaveRepository.existsByDoctorAndDay(doctor.getEmail(), leaveDay)).thenReturn(true);
		// Execution of with mode CUSTOM_ONE_TIME
		availabilityService.generateAvailabilitySlots(agRequest, doctor);
		
		// case when slot input is not present in DB then saving it
		when(slotInputRepository.findByStartTimeAndEndTimeAndGapInMinutes(any(LocalTime.class), any(LocalTime.class), anyInt())).thenReturn(Optional.empty());
		when(slotInputRepository.save(any(SlotInput.class))).thenAnswer(inv -> inv.getArgument(0));
		availabilityService.generateAvailabilitySlots(agRequest, doctor);
		
		// Execution with mode CUSTOM_CONTINUOUS
		agRequest.setMode(AGMode.CUSTOM_CONTINUOUS);
		agRequest.setDaysAhead(1);
		availabilityService.generateAvailabilitySlots(agRequest, doctor);

		// Execution with mode SCHEDULED
		agRequest.setMode(AGMode.SCHEDULED);
		availabilityService.generateAvailabilitySlots(agRequest, doctor);

		// Execution with mode CUSTOM_ONE_TIME
		agRequest.setMode(AGMode.CUSTOM_ONE_TIME);
		agRequest.setSlotInputs(List.of());
		availabilityService.generateAvailabilitySlots(agRequest, doctor);
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#activateAGPreference()}.
	 * 
	 */
	@Test
	void testActivateAGPreference() {
		Doctor doctor = TestDataUtil.getDoctor();
		String email = TestDataUtil.getDoctorEmail();
		AGPreference agPreference = TestDataUtil.getAGPreference();
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
		when(doctorRepository.findByEmail(JwtUtil.getEmailFromToken())).thenReturn(Optional.of(doctor));
		when(agPreferenceRepository.findById(doctor.getId())).thenReturn(Optional.of(agPreference));
		when(agPreferenceRepository.save(agPreference)).thenReturn(agPreference);
		// success scenario
		assertTrue(availabilityService.activateAGPreference());
		
		// Failed to save preference
		when(agPreferenceRepository.save(agPreference)).thenReturn(null);
		assertFalse(availabilityService.activateAGPreference());

		// Preference not found for given doctor id
		when(agPreferenceRepository.findById(doctor.getId())).thenReturn(Optional.empty());
		DoctorException doctorException = assertThrows(DoctorException.class, () -> availabilityService.activateAGPreference());
		assertEquals(IExceptionConstants.AG_PREFERENCE_NOT_FOUND, doctorException.getMessage());

		// When doctor's profile is incomplete
		doctor.setProfileComplete(false);
		doctorException = assertThrows(DoctorException.class, () -> availabilityService.activateAGPreference());
		assertEquals(IExceptionConstants.INCOMPLETE_DOCTOR_PROFILE, doctorException.getMessage());
		
		// doctor not found case
		when(doctorRepository.findByEmail(JwtUtil.getEmailFromToken())).thenReturn(Optional.empty());
		doctorException = assertThrows(DoctorException.class, () -> availabilityService.activateAGPreference());
		assertEquals(IExceptionConstants.DOCTOR_NOT_FOUND, doctorException.getMessage());
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#setDefaultAGPreference(Doctor)}.
	 * 
	 */
	@Test
	void testSetDefaultAGPreference() {
		Doctor doctor = TestDataUtil.getDoctor();
		AGPreference agPreference = TestDataUtil.getAGPreference();
		
		when(agPreferenceRepository.findById(doctor.getId())).thenReturn(Optional.of(agPreference));
		when(slotInputRepository.findByStartTimeAndEndTimeAndGapInMinutes(any(LocalTime.class), any(LocalTime.class), anyInt())).thenReturn(Optional.empty());
		when(slotInputRepository.save(any(SlotInput.class))).thenAnswer(inv -> inv.getArgument(0));
		when(agPreferenceRepository.save(any(AGPreference.class))).thenReturn(agPreference);
		// successfully saved preference
		assertTrue(availabilityService.setDefaultAGPreference(doctor));

		// case when lastGeneratedOn is null
		agPreference.setLastGeneratedOn(null);
		assertTrue(availabilityService.setDefaultAGPreference(doctor));

		// case when lastGeneratedOn is in future
		agPreference.setLastGeneratedOn(LocalDate.now().plusDays(2));
		assertTrue(availabilityService.setDefaultAGPreference(doctor));

		// case when preference in not found
		when(agPreferenceRepository.findById(doctor.getId())).thenReturn(Optional.empty());
		assertTrue(availabilityService.setDefaultAGPreference(doctor));

		// failed to save preference
		when(agPreferenceRepository.save(any(AGPreference.class))).thenReturn(null);
		assertFalse(availabilityService.setDefaultAGPreference(doctor));
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#getAGPreference()}.
	 * 
	 */
	@Test
	void testGetAGPreference() {
		String email = TestDataUtil.getDoctorEmail();
		AGPreference agPreference = TestDataUtil.getAGPreference();
		AGPreferenceResponse agPreferenceResponse = TestConverterUtil.toAGPreferenceResponse(agPreference);
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
		when(agPreferenceRepository.findByDoctorEmail(email)).thenReturn(Optional.of(agPreference));
		when(agMapper.toResponse(agPreference)).thenReturn(agPreferenceResponse);
		// success scenario
		assertEquals(agPreferenceResponse, availabilityService.getAGPreference());

		// case when preference not found
		when(agPreferenceRepository.findByDoctorEmail(email)).thenReturn(Optional.empty());
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()-> availabilityService.getAGPreference());
		assertEquals(IExceptionConstants.PREFERENCE_NOT_FOUND + email, availabilityException.getMessage());
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#getAvailabilityData(AVRequest, Pageable)}.
	 * 
	 */
	@Test
	void testGetAvailabilityData() {
		AVRequest request = new AVRequest();
		Pageable pageable = PageRequest.of(0, 10);
		List<Availability> slots = List.of(TestDataUtil.getAvailability());
		AVResponse avResponse = TestConverterUtil.toAVResponse(slots.get(0));
		Page<Availability> pages = new PageImpl<>(slots, pageable, slots.size());
		
		when(availabilityRepository.fetchAvailabilitySlotsByDateRange(JwtUtil.getEmailFromToken(), request.getFrom(), request.getTo(), pageable)).thenReturn(pages);
		when(availabilityMapper.toResponse(any(Availability.class))).thenReturn(avResponse);
		// success scenario
		Map<String,Object> availabilityData = availabilityService.getAvailabilityData(request, pageable);
		@SuppressWarnings("unchecked")
		List<AVResponse> responseList = (List<AVResponse>) availabilityData.get(IResponseConstants.DATA);
		assertEquals(avResponse, responseList.get(0));
		
		// case when no slot found
		pages = new PageImpl<>(List.of());
		when(availabilityRepository.fetchAvailabilitySlotsByDateRange(JwtUtil.getEmailFromToken(), request.getFrom(), request.getTo(), pageable)).thenReturn(pages);
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()-> availabilityService.getAvailabilityData(request, pageable));
		assertEquals(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, availabilityException.getMessage());
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#deleteAvailabilitySlot(Integer)}.
	 * 
	 */
	@Test
	void testDeleteAvailabilitySlot() {
		Integer id = 1;
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(TestDataUtil.getDoctorEmail());
		when(availabilityRepository.deleteByIdAndEmailAndStatus(id, JwtUtil.getEmailFromToken(), List.of(SlotStatus.AVAILABLE))).thenReturn(1);
		// slot deleted
		assertEquals(1, availabilityService.deleteAvailabilitySlot(id));

		when(availabilityRepository.deleteByIdAndEmailAndStatus(id, JwtUtil.getEmailFromToken(), List.of(SlotStatus.AVAILABLE))).thenReturn(0);
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()->availabilityService.deleteAvailabilitySlot(id));
		assertEquals(IExceptionConstants.NOT_ABLE_TO_DELETE_SLOT, availabilityException.getMessage());
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#bulkDeleteAvailabilitySlots(ADRequest)}.
	 * 
	 */
	@Test
	void testBulkDeleteAvailabilitySlots() {
		ADRequest request = new ADRequest();
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(TestDataUtil.getDoctorEmail());
		when(availabilityRepository.deleteByDateAndTimeRange(JwtUtil.getEmailFromToken(), request.getStartDate(), request.getEndDate(), request.getStartTime(), request.getEndTime(), List.of(SlotStatus.AVAILABLE))).thenReturn(5);
		
		availabilityService.bulkDeleteAvailabilitySlots(request);
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#viewSlotDetails(Integer, Integer, boolean)}.
	 * 
	 */
	@Test
	void testViewSlotDetails() {
		Integer id = 1, appointmentId = 1;
		String email = TestDataUtil.getDoctorEmail();
		Appointment appointment = TestDataUtil.getAppointment();
		Availability availability = TestDataUtil.getAvailability();
		AVResponse avResponse = TestConverterUtil.toAVResponse(availability);
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(email);
		when(availabilityRepository.findById(id)).thenReturn(Optional.of(availability));
		when(availabilityMapper.toResponse(availability)).thenReturn(avResponse);
		
		// case when slot details fetched successfully
		ADResponse adResponse = availabilityService.viewSlotDetails(id, appointmentId, false);
		assertEquals(avResponse, adResponse.getAvResponse());
		
		// case when at least one appointment is booked
		when(appointmentRepository.fetchByAvailabilityIdAndAppointmentId(id, appointmentId)).thenReturn(List.of(appointment));
		adResponse = availabilityService.viewSlotDetails(id, appointmentId, false);
		assertEquals(appointment.getPatient().getId(), adResponse.getAppointmentInfo().get(0).getPatient().getId());
		assertEquals(appointment.getCreatedAt(), adResponse.getAppointmentInfo().get(0).getBookingTime());
		assertEquals(appointment.getStatus(), adResponse.getAppointmentInfo().get(0).getStatus());

		// case when admin is accessing
		when(appointmentRepository.fetchByAvailabilityIdAndAppointmentId(id, appointmentId)).thenReturn(List.of());
		adResponse = availabilityService.viewSlotDetails(id, appointmentId, true);
		Map<String,Object> additionalInfo = adResponse.getAdditionalInfo();
		assertEquals(availability.getDoctor().getId(), additionalInfo.get(IAdminConstants.DOCTOR_ID));
		assertEquals(availability.getDoctor().getName(), additionalInfo.get(IAdminConstants.DOCTOR_NAME));
		assertEquals(availability.getDoctor().getEmail(), additionalInfo.get(IAdminConstants.DOCTOR_EMAIL));
		assertEquals(availability.getDoctor().getPhone(), additionalInfo.get(IAdminConstants.DOCTOR_PHONE));
		
		// case when doctor doesn't matched with slot
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn("other@shc.com");
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()-> availabilityService.viewSlotDetails(id, appointmentId, false));
		assertEquals(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, availabilityException.getMessage());
		
		// case when slot not found for given id
		when(availabilityRepository.findById(id)).thenReturn(Optional.empty());
		availabilityException = assertThrows(AvailabilityException.class, ()-> availabilityService.viewSlotDetails(id, appointmentId, false));
		assertEquals(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, availabilityException.getMessage());
	}
	
	/**
	 * Test {@link AvailabilityServiceImpl#changeAvailabilityAppointmentStatus(Map, boolean)}.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	void testChangeAvailabilityAppointmentStatus() {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put(IRequestConstants.STATUS, AppointmentStatus.APPROVED);
		requestMap.put(IRequestConstants.SLOT_ID, 1);
		Availability availability = TestDataUtil.getAvailability();
		
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn(TestDataUtil.getDoctorEmail());
		when(availabilityRepository.findById(anyInt())).thenReturn(Optional.of(availability));
		when(appointmentRepository.updateStatusByAvailabilityId(any(AppointmentStatus.class), anyInt(), any(LocalDateTime.class), (List<AppointmentStatus>) any())).thenReturn(1);
		// status changed to APPROVED successfully
		assertEquals(IResponseConstants.APPOINTMENT_APPROVED, availabilityService.changeAvailabilityAppointmentStatus(requestMap, false));

		// status changed to APPROVED successfully when access by admin
		assertEquals(IResponseConstants.APPOINTMENT_APPROVED, availabilityService.changeAvailabilityAppointmentStatus(requestMap, true));
		
		// status changed to D_CANCELLED successfully
		requestMap.put(IRequestConstants.STATUS, AppointmentStatus.D_CANCELLED);
		when(availabilityRepository.updateStatusById(eq(SlotStatus.CANCELLED), anyInt(), any(LocalDateTime.class))).thenReturn(1);
		assertEquals(IResponseConstants.APPOINTMENT_CANCELLED, availabilityService.changeAvailabilityAppointmentStatus(requestMap, false));

		// case when updating status failed
		Appointment appointment = TestDataUtil.getAppointment();
		when(appointmentRepository.updateStatusByAvailabilityId(any(AppointmentStatus.class), anyInt(), any(LocalDateTime.class), (List<AppointmentStatus>) any())).thenReturn(0);
		when(appointmentRepository.findTopByAvailabilityIdOrderByIdDesc(anyInt())).thenReturn(Optional.of(appointment));
		assertEquals(IResponseConstants.APPOINTMENT_CANCELLED_FAIL + appointment.getStatus(), availabilityService.changeAvailabilityAppointmentStatus(requestMap, false));
		
		// case when updating status failed and no appointment found for given slot
		when(appointmentRepository.findTopByAvailabilityIdOrderByIdDesc(anyInt())).thenReturn(Optional.empty());
		AppointmentException appointmentException = assertThrows(AppointmentException.class, ()-> availabilityService.changeAvailabilityAppointmentStatus(requestMap, false));
		assertEquals(IExceptionConstants.NO_APPOINTMENT_FOR_SLOT, appointmentException.getMessage());
		
		// when doctor and slot mismatched
		jwtUtilMock.when(JwtUtil::getEmailFromToken).thenReturn("other@shc.com");
		AvailabilityException availabilityException = assertThrows(AvailabilityException.class, ()-> availabilityService.changeAvailabilityAppointmentStatus(requestMap, false));
		assertEquals(IExceptionConstants.DOCTOR_SLOTS_MISMATCHED, availabilityException.getMessage());

		when(availabilityRepository.findById(anyInt())).thenReturn(Optional.empty());
		availabilityException = assertThrows(AvailabilityException.class, ()-> availabilityService.changeAvailabilityAppointmentStatus(requestMap, false));
		assertEquals(IExceptionConstants.AVAILABILITY_SLOTS_NOT_FOUND, availabilityException.getMessage());
	}
}
