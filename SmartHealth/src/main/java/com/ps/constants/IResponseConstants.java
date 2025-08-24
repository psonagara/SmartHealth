package com.ps.constants;

/**
 * Interface containing constants used as response message in APIs.
 */
public interface IResponseConstants {

	String PROFILE_UPDATE_SUCCESSFUL = "Profile Updated Successfully";
	String PROFILE_UPDATE_FAILED = "Profile Updated Failed";
	String PROFILE_PICTURE_UPDATE_SUCCESSFUL = "Profile Picture Upload Successful";
	String PROFILE_PICTURE_UPDATE_FAILED = "Profile Picture Upload Fail";
	String PASSWORD_UPDATE_SUCCESSFUL = "Password update successful";
	String PASSWORD_UPDATE_FAILED = "Password update fail";
	String PATIENT_REGISTER_SUCCESSFUL = "Patient Registration Successful";
	String PATIENT_REGISTER_FAILED = "Patient Registration Failed";
	String PATIENT_LOGIN_SUCCESSFUL = "Patient Login Successful";
	String DOCTOR_REGISTER_SUCCESSFUL = "Doctor Registration Successful";
	String DOCTOR_REGISTER_FAILED = "Doctor Registration Failed";
	String DOCTOR_LOGIN_SUCCESSFUL = "Doctor Login Successful";
	String INVALID_ROLE = "Invalid Role";
	String PROVIDE_ROLE_FOR_PROFILE = "Provide Role to get profile";
	String AVAILABILITY_GENERATION_DONE = "Availability Generation successfully completed";
	String AVAILABILITY_PREFERENCE_AUTO = "Availability Generation Preference Changed to AUTO";
	String AVAILABILITY_PREFERENCE_MANUAL = "Availability Generation Preference Changed to MANUAL";
	String AVAILABILITY_GENERATION_MANUAL = "Manual Slot(s) Generated Successfully";
	String AVAILABILITY_GENERATION_ACTIVATED = "Availability generation activated.";
	String AVAILABILITY_GENERATION_ACTIVATE_FAIL = "Fail to activate Availability generation.";
	String AVAILABILITY_GENERATION_CUSTOM_ONE_TIME = "Slots generated successfully and Preference Save to CUSTOM_ONE_TIME";
	String AVAILABILITY_GENERATION_CUSTOM_CONTINUOUS = "Slots generated successfully, Preference Save to CUSTOM_CONTINUOUS, Slots will be generated as per preference on daily basis";
	String AVAILABILITY_DELETION_DONE = " Availability Slot(s) Deleted Successfully";
	String ADMIN_REGISTER_SUCCESSFUL = "Admin Registration Successful";
	String ADMIN_REGISTER_FAILED = "Admin Registration Falied";
	String ADMIN_LOGIN_SUCCESSFUL = "Admin Login Successful";
	String LEAVE_APPLY_SUCCESSFUL = "Applied for Leave Sucessfully";
	String LEAVE_APPLY_FAILED = "Failed to Apply for Leave";
	String PROVIDE_NAME = "Provide name to be updated";
	String NAME_UPDATE_SUCCESSFUL = "Name Updated Successfully";
	String NAME_UPDATE_FAIL = "Name Updated Failed";
	String APPOINTMENT_BOOKED_SUCCESSFUL = "Appointment Booked Successfully";
	String APPOINTMENT_BOOKED_FAIL = "Fail to Book Appointment";
	String USER_ACTIVATION_TOGGLED = "User Activation Status Toggled";
	String APPOINTMENT_APPROVED = "Appointment Approved Successfully";
	String APPOINTMENT_CANCELLED = "Appointment Cancelled Successfully.";
	String APPOINTMENT_COMPLETED = "Appointment Completed Successfully.";
	String APPOINTMENT_STATUS_CHANGE = "Appointment Status Changed Successfully";
	String APPOINTMENT_APPROVED_FAIL = "Not able to Approve as current status is : ";
	String APPOINTMENT_CANCELLED_FAIL = "Not able to Cancel as current status is : ";
	String APPOINTMENT_COMPLETED_FAIL = "Not able to Complete as current status is : ";
	String APPOINTMENT_STATUS_CHANGE_FAIL = "Not able to change status as current status is : ";
	String HOLIDAY_ADDED_SUCCESS = "Holiday Added Successfully";
	String HOLIDAY_ADDED_FAIL = "Fail to  add Holiday";
	String HOLIDAY_DELETE_SUCCESS = "Holiday Deleted Successfully";
	String LEAVE_APPROVED = "Leave Approved Successfully";
	String LEAVE_REJECTED = "Leave Rejected Successfully";
	String LEAVE_STATUS_CHANGED = "Leave Status Changed Successfully";
	String LEAVE_STATUS_UPDATE_FAILED = "Unable to update leave status";
	String FILE_NAME_CONSTRAINTS = "Provide Valid File Name";
	
	String DOCTOR_INFO = "doctorInfo";
	String APPOINTMENT_INFO = "appointmentInfo";
	String ID = "id";
	String DOCTOR_NAME = "doctorName";
	String PATIENT_NAME = "patientName";
	String SLOT_ID = "slotId";
	String DATE = "date";
	String FROM = "from";
	String TO = "to";
	String STATUS = "status";
	String DATA = "data";
	String TOTAL_PAGES = "totalPages";
	String PAGE_SIZE = "pageSize";
	String CURRENT_PAGE = "currentPage";
	String IS_FIRST_PAGE = "isFirstPage";
	String IS_LAST_PAGE = "isLastPage";
	String HAS_PREVIOUS_PAGE = "hasPreviousPage";
	String HAS_NEXT_PAGE = "hasNextPage";
	String STATS = "stats";
	String TODAYS_APPOINTMENTS = "todaysAppointments";
	String UPCOMING_APPOINTMENTS = "upcomingAppointments";
	String PENDING_APPROVALS = "pendingApprovals";
	String CANCELLATIONS_TODAY = "cancellationsToday";
	String TODAYS_SCHEDULE = "todaysSchedule";
	String NOTIFICATIONS = "notifications";
	String PERFORMANCE = "performance";
	String DAILY_APPOINTMENTS = "dailyAppointments";
	String APPROVED_APPOINTMENTS = "approvedAppointments";
	String COMPLETED_APPOINTMENTS = "completedAppointments";
	String CANCELLED_APPOINTMENTS = "cancelledAppointments";
	String HEALTH_ACTIVITY = "healthActivity";
	
}
