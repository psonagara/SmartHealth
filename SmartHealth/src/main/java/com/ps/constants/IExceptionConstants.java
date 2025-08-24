package com.ps.constants;

/**
 * Interface containing constants specific to exception and related operations.
 */
public interface IExceptionConstants {

	String RESOURCE_FETCH_FAIL = "Failed to get resource";
	String RESOURCE_NOT_FOUND = "Resource not found on path: ";
	String PROVIDE_VALID_ROLE = "Provide Valid Role";
	String PASSWORD_VALIDATION = "Password must not be empty and having minimum length of 5";
	String PATIENT_ALREADY_EXIST = "Patient Already Registered";
	String INCORRECT_PASSWORD = "Incorrect Password";
	String INACTIVE_PROFILE = "Your Profile is not Active";
	String PATIENT_NOT_FOUND= "Patient Not Found";
	String MULTIPLE_PATIENT_FOUND= "Multiple Patient records found for user: ";
	String DOCTOR_ALREADY_EXIST = "Doctor Already Registered";
	String DOCTOR_NOT_FOUND = "Doctor Not Found";
	String MULTIPLE_DOCTOR_FOUND= "Multiple Doctor records found for user: ";
	String INVALID_PATIEN_TOKEN_ROLE = "Patient's Token/Role is not Valid";
	String INVALID_DOCTOR_TOKEN_ROLE = "Doctor Token/Role is not Valid";
	String PROFILE_PIC_UPLOAD_FAIL = "Failed to upload profile picture";
	String AG_PREFERENCE_NOT_FOUND = "Availability Preference not found for doctor";
	String INCOMPLETE_DOCTOR_PROFILE = "Please complete your profile first to activate Availability Slot generation";
	String INVALID_MODE = "Invalid Mode to save preference/generate availability";
	String PREFERENCE_NOT_FOUND = "Preference Not found for doctor, identity: ";
	String AVAILABILITY_SLOTS_NOT_FOUND = "Availability Slots not found OR already booked";
	String DOCTOR_SLOTS_MISMATCHED = "Selected Slot and Doctor doesn't match";
	String SUB_PROFILES_NOT_FOUND = "No Sub-Profiles found for given patient";
	String SESSION_MISMATCHED = "Session mismathced";
	String SUB_PROFILE_NOT_FOUND = "No Sub-Profile found";
	String SUB_PROFILE_MISMATHCED = "Sub-Profile mismatched with Patient";
	String CANT_BOOK_PAST_SLOTS = "You can't book slots in past";
	String NOT_ABLE_TO_DELETE_SLOT = "Not able to delete Slot(s)";
	String NO_APPOINTMENT_FOR_SLOT = "No Appointment found for given slot";
	String INVALID_STATUS_IN_CHANGE_REQUEST = "Invalid Status for status change request";
	String NO_APPOINTMENTS_FOUND = "You don't have any Appointments";
	String APPOINTMENT_NOT_FOUND = "Appoinment not found";
	String DO_NOT_ACCESS_TO_APPOINTMENT = "You don't have access to this Appointment";
	String ADMIN_ALREADY_EXIST = "Admin Already Registered";
	String MULTIPLE_ADMIN_FOUND= "Multiple Admin records found for user: ";
	String ADMIN_NOT_FOUND= "Admin Not Found";
	String SOMETHING_WENT_WRONG = "Something Went Wrong";
	String LEAVE_BOOKING_OVERLAP = "Overlap found in Leave Booking Dates";
	String LEAVE_NOT_FOUND = "Leave not found";
	String HOLIDAY_SUNDAY_CONFLICT = "Do not add Holiday on Sunday";
	String INVALID_ADMIN_TOKEN_ROLE = "Admin Token/Role is not Valid";
	String APPOINTMENT_STATUS_CHANGE_FAIL = "Not able to change status to %s as current status is: %s";
	String APPOINTMENT_UPDATE_STATUS_FAIL = "Unable to update appointment status";
}
