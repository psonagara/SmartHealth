package com.ps.validator;

import com.ps.annotation.ValidateAR;
import com.ps.constants.IValidationConstants;
import com.ps.dto.RelationDTO;
import com.ps.dto.SubProfileDTO;
import com.ps.dto.request.AppointmentRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the @ValidateAR annotation.
 * Validates AppointmentRequest fields, ensuring doctorId, patientId, and slotId are present,
 * and sub-profile details are valid when isSubProfile is true.
 */
public class ARValidator implements ConstraintValidator<ValidateAR, AppointmentRequest> {

	@Override
	public boolean isValid(AppointmentRequest value, ConstraintValidatorContext context) {

		context.disableDefaultConstraintViolation();
		if (value == null) {
			buildConstraints(IValidationConstants.AR_NOT_NULL, "request", context);
			return false;
		}
		if (value.getDoctorId() == null || value.getDoctorId() <= 0) {
			buildConstraints(IValidationConstants.DOCTOR_ID_CONSTRAINT, "doctorId", context);
			return false;
		}
		if (value.getPatientId() == null || value.getPatientId() <= 0) {
			buildConstraints(IValidationConstants.PATINET_ID_CONSTRAINT, "patientId", context);
			return false;
		}
		if (value.getSlotId() == null || value.getSlotId() <= 0) {
			buildConstraints(IValidationConstants.SLOT_ID_CONSTRAINT, "slotId", context);
			return false;
		}
		if (value.getNote() != null && !value.getNote().isBlank() && value.getNote().length() > 500) {
			buildConstraints(IValidationConstants.AR_NOTE_CONSTRAINT, "slotId", context);
			return false;			
		}
		if (value.getIsSubProfile() != null && value.getIsSubProfile()) {
			SubProfileDTO subProfile = value.getSubProfile();
			if (subProfile != null) {
				if (subProfile.getId() != null && subProfile.getId() <= 0) {
					buildConstraints(IValidationConstants.SUB_PROFILE_ID_CONSTRAINT, "subProfile.id", context);
					return false;
				}
				if (subProfile.getName() == null || subProfile.getName().isBlank()) {
					buildConstraints(IValidationConstants.SUB_PROFILE_NAME_CONSTRAINT, "subProfile.name", context);
					return false;
				}
				if (subProfile.getPhone() == null || subProfile.getPhone().isBlank() || subProfile.getPhone().length() != 10) {
					buildConstraints(IValidationConstants.SUB_PROFILE_PHONE_CONSTRAINT, "subProfile.phone", context);
					return false;
				}
				RelationDTO relation = subProfile.getRelation();
				if (relation == null || relation.getName() == null || relation.getId() == null) {
					buildConstraints(IValidationConstants.SUB_PROFILE_RELATION_CONSTRAINT, "subProfile.relation", context);
					return false;
				}
			} else {
				buildConstraints(IValidationConstants.SUB_PROFILE_CONSTRAINT, "subProfile", context);
				return false;
			}
		}
		return true;
	}

	private void buildConstraints(String message, String field, ConstraintValidatorContext context) {
		context.buildConstraintViolationWithTemplate(message)
		.addPropertyNode(field)
		.addConstraintViolation();
	}
}
