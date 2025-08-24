package com.ps.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ps.config.props.SlotsProperties;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IExceptionConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IRequestConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.HolidayDTO;
import com.ps.dto.SubProfileDTO;
import com.ps.dto.request.ADSRequest;
import com.ps.dto.request.APSRequest;
import com.ps.dto.response.ADResponse;
import com.ps.dto.response.ADSResponse;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.dto.response.APSResponse;
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
import com.ps.service.IAdminService;
import com.ps.service.IAppointmentService;
import com.ps.service.IAvailabilityService;
import com.ps.util.AdminUtil;
import com.ps.util.CommonUtil;
import com.ps.util.ProfileUtil;

import jakarta.transaction.Transactional;

/**
 * Implementation of the admin service interface.
 * Provides data and manage slots, appointments, users, leaves, holidays.
 * 
 */
@Service
public class AdminServiceImpl implements IAdminService {
	
	private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private SubProfileRepository subProfileRepository;
	
	@Autowired
	private AvailabilityRepository availabilityRepository;
	
	@Autowired
	private IAvailabilityService availabilityService;
	
	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private IAppointmentService appointmentService;
	
	@Autowired
	private DoctorLeaveRepository leaveRepository;
	
	@Autowired
	private SlotsProperties slotsProperties;
	
	@Autowired
	private HolidayRepository holidayRepository;
	
	@Autowired
	private DoctorMapper doctorMapper;
	
	@Autowired
	private AGPreferenceMapper agMapper;
	
	@Autowired
	private PatientMapper patientMapper;
	
	@Autowired
	private HolidayMapper holidayMapper;
	
	/**
	 * Retrieve filtered list of doctors. Filter works based on input parameters and all
	 * parameters are optional. Null values are handled at repository level, if no parameter(s) are 
	 * passed then all records returns as pages and response prepare with pagination details (like totalPages, currentPage etc.)
	 * 
	 */
	@Override
	public Map<String, Object> searchDoctors(ADSRequest adsRequest, Pageable pageable) {
		Page<Doctor> pages = doctorRepository.searchDoctors(adsRequest.getId(), adsRequest.getName(), adsRequest.getEmail(), adsRequest.getPhone(), 
				adsRequest.getGender(), adsRequest.getDegree(), adsRequest.getSpecialization(), adsRequest.getDepartment(), 
				adsRequest.getYearOfExperience(), adsRequest.getRegistrationNumber(), adsRequest.getProfileComplete(), 
				adsRequest.getIsActive(), pageable);
		List<ADSResponse> response = pages.getContent().stream().map(AdminUtil::prepareADSResponseForSeachDoctors).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(response, pages);
	}

	/**
	 * Retrieve filtered list of patients. Filter works based on input parameters and all 
	 * parameters are optional. Null values are handled at repository level, if no parameter(s) are
	 * passed then all records returns as pages and reponse prepare with pagination details (like totalPages, currentPage etc.)
	 * 
	 */
	@Override
	public Map<String, Object> searchPatients(APSRequest apsRequest, Pageable pageable) {
		Page<Patient> pages = patientRepository.searchPatients(apsRequest.getId(), apsRequest.getName(), apsRequest.getEmail(), apsRequest.getPhone(), apsRequest.getGender(), apsRequest.getProfileComplete(), apsRequest.getIsActive(), pageable);
		List<APSResponse> response = pages.getContent().stream().map(AdminUtil::prepareAPSResponseForSeachPatients).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(response, pages);
	}
	
	/**
	 * Retrieves doctor information from provided id. Also add doctor's availability generation
	 * preference data along with doctor information. Prepare response and send to controller level.
	 * 
	 */
	@Override
	public ADSResponse viewDoctorProfile(Integer id) {
		Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.BAD_REQUEST));;
		AGPreferenceResponse agPreferenceResponse = agMapper.toResponse(doctor.getAgPreference());
		ADSResponse adsResponse = doctorMapper.toADSResponse(doctor);
		adsResponse.setAgPreferenceResponse(agPreferenceResponse);
		return adsResponse;
	}

	/**
	 * Retrieves patient information from provided id. Also add patient's sub-profiles
	 * data along with patient information. Prepare response and send to controller level.
	 * 
	 */
	@Override
	public APSResponse viewPatientProfile(Integer id) {
		Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
		APSResponse apsResponse = patientMapper.toAPSResponse(patient);
		List<SubProfile> subProfiles = subProfileRepository.findByPatientId(id);
		if (!subProfiles.isEmpty()) {
			List<SubProfileDTO> subProfileDTOs = subProfiles.stream().map(ProfileUtil::prepareSubProfileDTO).collect(Collectors.toList());
			apsResponse.setSubProfiles(subProfileDTOs);
		}
		return apsResponse;
	}

	/**
	 * Toggle activation status of user. Based on user's role, retrieve its activation
	 * status using user's id. If status is activate then deactivate it and vice versa.
	 * 
	 */
	@Override
	@Transactional
	public int toggleStatus(Integer id, String role) {
		int toggled = 0;
		switch (role) {
			case IDoctorConstants.DOCTOR_ROLE:
				Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new DoctorException(IExceptionConstants.DOCTOR_NOT_FOUND, HttpStatus.BAD_REQUEST));
				toggled = doctorRepository.toggleStatus(id, !doctor.getIsActive());
				break;
				
			case IPatientConstants.PATIENT_ROLE:
				Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientException(IExceptionConstants.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
				toggled = patientRepository.toggleStatus(id, !patient.getIsActive());
				break;
	
			default:
				throw new AdminException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
		}
		
		if (toggled != 1) {
			throw new AdminException(IExceptionConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return toggled;
	}

	/**
	 * Retrieve id and name of all doctors.
	 * 
	 */
	@Override
	public List<DoctorIdNameProjection> getDoctorsList() {
		return doctorRepository.findAllProjectedBy();
	}

	/**
	 * Retrieve id and name of all patients.
	 * 
	 */
	@Override
	public List<PatientIdNameProjection> getPatientList() {
		return patientRepository.findAllProjectedBy();
	}
	
	/**
	 * Retrieves filtered availability slots. Filter based on given input parameters like
	 * id of doctor, date of slot etc. Null value of not passed parameters are handled at 
	 * repository level. Response is send to controller level with pagination details.
	 */
	@Override
	public Map<String, Object> searchAvailabilitySlots(Map<String, Object> requestMap, Pageable pageable) {
		Page<Availability> pages = availabilityRepository.searchAvailability((Integer) requestMap.get(IRequestConstants.DOCTOR_ID), (LocalDate) requestMap.get(IRequestConstants.DATE), (SlotStatus) requestMap.get(IRequestConstants.STATUS), pageable);
		List<Map<String, Object>> response = pages.getContent().stream().map(AdminUtil::prepareSearchAvailabilitySlots).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(response, pages);
	}

	/**
	 * Delete particular slot based on given id of slot.
	 * 
	 */
	@Override
	@Transactional
	public int deleteAvailabilitySlot(Integer id) {
		int deleted = availabilityRepository.deleteByIdAndStatus(id);
		if (deleted == 1) {
			return deleted;
		} else {
			throw new AvailabilityException(IExceptionConstants.NOT_ABLE_TO_DELETE_SLOT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieve slot information and all appointments information booked with slot.
	 * If appointmentId is also present then retrieve slot information and instead of
	 * all appointments retrieve information of particular appointment based on given appointmentId.
	 */
	@Override
	public ADResponse viewSlotDetails(Integer id, Integer appointmentId) {
		return availabilityService.viewSlotDetails(id, appointmentId, true);
	}

	/**
	 * Change status of latest appointment associated with slot of given id.
	 */
	@Override
	public String changeAvailabilityAppointmentStatus(Map<String, Object> requestMap) {
		return availabilityService.changeAvailabilityAppointmentStatus(requestMap, true);
	}

	/**
	 * Retrieves filtered appointments. Filter based on given input parameters like
	 * id of doctor, id of patient, date of appointment etc. Null value of not passed parameters are handled at 
	 * repository level. Response is send to controller level with pagination details.
	 */
	@Override
	public Map<String, Object> searchAppointments(Map<String, Object> requestMap, Pageable pageable) {
		Page<Appointment> pages = appointmentRepository.searchAppointments((Integer) requestMap.get(IRequestConstants.DOCTOR_ID), (Integer) requestMap.get(IRequestConstants.PATIENT_ID), (LocalDate) requestMap.get(IRequestConstants.DATE), (AppointmentStatus) requestMap.get(IRequestConstants.STATUS), pageable);
		List<Map<String, Object>> response = pages.getContent().stream().map(AdminUtil::prepareSearchAppointments).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(response, pages);
	}

	/**
	 * Change status of appointment to requested new status based on given appointment id.
	 * 
	 */
	@Override
	public String changeAppointmentStatus(Integer id, AppointmentStatus newStatus) {
		return appointmentService.changeAppointmentStatus(id, newStatus, true);
	}

	/**
	 * Retrieves filtered leaves. Filter based on given input parameters like
	 * id of doctor, date of leave etc. Null value of not passed parameters are handled at 
	 * repository level. Response is send to controller level with pagination details.
	 * 
	 */
	@Override
	public Map<String, Object> searchLeaves(Map<String, Object> requestMap, Pageable pageable) {
		Page<DoctorLeave> pages = leaveRepository.searchLeavesByAdmin((Integer) requestMap.get(IRequestConstants.DOCTOR_ID), (LocalDate) requestMap.get(IRequestConstants.FROM), (LocalDate) requestMap.get(IRequestConstants.TO), (LeaveStatus) requestMap.get(IRequestConstants.STATUS), pageable);
		List<Map<String, Object>> response = pages.getContent().stream().map(AdminUtil::prepareSearchLeavesMap).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(response, pages);
	}

	/**
	 * Approve or Reject leave. Based on role (currently doctor only) retrieve current leave status
	 * and based on it update it to requested new status.
	 * 
	 */
	@Override
	@Transactional
	public String changeLeaveStatus(Integer id, String role, LeaveStatus status) {
		if (role.equals(IDoctorConstants.DOCTOR_ROLE)) {			
			DoctorLeave leave = leaveRepository.findById(id).orElseThrow(() -> new DoctorException(IExceptionConstants.LEAVE_NOT_FOUND, HttpStatus.BAD_REQUEST));
			
			if (!AdminUtil.isLeaveStatusTransitionValid(leave.getStatus(), status, role)) {
				return "Not able to change status to " + status + " as current status is: " + leave.getStatus();
			}
			
			LocalDateTime now = LocalDateTime.now();
			int changed = leaveRepository.changeLeaveStatus(id, now, status);
			if (changed != 1) {
		        throw new AppointmentException(IResponseConstants.LEAVE_STATUS_UPDATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
		    }
			// If leave approved then delete available slots, cancel booked and re_available slots and cancel appointments of booked slot
			if (status == LeaveStatus.APPROVED) {
				deleteSlotOrCancelAppointment(leave, now);				
			}
			
			return AdminUtil.getSuccessMessageForLeaveStausChange(status);
		} else {
			throw new AdminException(IExceptionConstants.PROVIDE_VALID_ROLE, HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * This method is used when status of any leave request changed to {@code LeaveStatus.APPROVED}.
	 * Availability slots generation in advance is limited by some days (as defined in properties file).
	 * So, if {@code LeaveStatus.APPROVED} leave fall under that period where slots may be generated then
	 * check for slots and if slots are generated then check it status, if status is {@link SlotStatus.AVAILABLE} or
	 * {@link SlotStatus.RE_AVAILABLE} then cancel slot, if {@link SlotStatus.BOOKED} then cancel slot and also mark 
	 * associated appointment as cancelled by doctor.
	 * 
	 * @param leave
	 * @param now
	 */
	private void deleteSlotOrCancelAppointment(DoctorLeave leave, LocalDateTime now) {
		Integer doctorId = leave.getDoctor().getId();
		LocalDate from = leave.getFrom();
		LocalDate to = leave.getTo();
		LocalDate today = LocalDate.now();
		LocalDate limitDate = today.plusDays(slotsProperties.getMaximumGenerationDays());
		
		// As we are restricting slot generation for maximum defined days(for example 15) from current date
		// So if from date is after that date(e.g.: 15 days after today) after which no slots are generated then no need to modify/delete slots/appointments
		if (!from.isAfter(limitDate)) {
			if (to.isAfter(limitDate)) {
				to = limitDate;
			}
			if (from.isBefore(today)) {
				from = today;
			}

			List<Availability> slots = availabilityRepository.findByDoctorIdAndDateBetween(doctorId, from, to);
			if (!slots.isEmpty()) {
				List<Availability> slotsToDelete = new ArrayList<>();
		        List<Integer> slotsToCancel = new ArrayList<>();
		        List<Integer> appointmentsToCancel = new ArrayList<>();
		        
		        for (Availability slot : slots) {
		        	switch (slot.getStatus()) {
						case AVAILABLE:
							slotsToDelete.add(slot);
							break;
						case BOOKED:
							slotsToCancel.add(slot.getId());
							appointmentsToCancel.add(slot.getId());
							break;
						case RE_AVAILABLE:
							slotsToCancel.add(slot.getId());
							break;
						default:
							break;
					}
		        }
		        
		        if (!slotsToDelete.isEmpty()) {
		        	availabilityRepository.deleteAll(slotsToDelete);
		        }
		        if (!slotsToCancel.isEmpty()) {
		        	availabilityRepository.updateStatusByIds(SlotStatus.CANCELLED, slotsToCancel, now);
		        }
		        if (!appointmentsToCancel.isEmpty()) {
		        	appointmentRepository.updateStatusByAvailabilityIds(AppointmentStatus.D_CANCELLED, appointmentsToCancel, now, List.of(AppointmentStatus.BOOKED, AppointmentStatus.APPROVED));
		        }
			} else {
				LOG.info("No Slots found for doctorId: {} for date from: {} to: {}", doctorId, from, to);
			}
		} else {
			LOG.info("Applied Leave dates doesn't fall under maximum slot generation days");
		}
	}

	/**
	 * Add holiday to records based on given input parameters.
	 * 
	 */
	@Override
	public boolean addHoliday(HolidayDTO holidayDTO) {
		if (holidayDTO.getHolidayDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
			throw new AdminException(IExceptionConstants.HOLIDAY_SUNDAY_CONFLICT, HttpStatus.BAD_REQUEST);
		}
		Holiday holiday = holidayMapper.toHoliday(holidayDTO);
		return holidayRepository.save(holiday) != null;
	}

	/**
	 * Delete holiday based on given id of particular holiday.
	 * 
	 */
	@Override
	public void deleteHoliday(Integer id) {
		holidayRepository.deleteById(id);
	}
	
}
