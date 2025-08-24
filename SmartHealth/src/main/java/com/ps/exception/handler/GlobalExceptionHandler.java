package com.ps.exception.handler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ps.dto.response.ErrorResponse;
import com.ps.exception.AdminException;
import com.ps.exception.AppointmentException;
import com.ps.exception.AvailabilityException;
import com.ps.exception.DoctorException;
import com.ps.exception.PatientException;
import com.ps.exception.ProfileException;
import com.ps.exception.ResourceException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> hadleValidationException(MethodArgumentNotValidException exception) {
		LOG.warn("Validation exception occurred: {}", exception.getMessage());
		Map<String, String> errors = new HashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(PatientException.class)
	public ResponseEntity<?> hadlePatientException(PatientException exception) {
		LOG.warn("PatientException Caught: {}", exception.getMessage());
		HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(prepareErrorResponse(exception.getMessage(), status), status);
	}
	

	@ExceptionHandler(DoctorException.class)
	public ResponseEntity<?> hamdleDoctorException(DoctorException exception) {
		LOG.warn("DoctorException Caught: {}", exception.getMessage());
		HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(prepareErrorResponse(exception.getMessage(), status), status);
	}

	@ExceptionHandler(ProfileException.class)
	public ResponseEntity<?> handleProfileException(ProfileException exception) {
		LOG.warn("ProfileException Caught: {}", exception.getMessage());
		HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(prepareErrorResponse(exception.getMessage(), status), status);
	}
	
	@ExceptionHandler(ResourceException.class)
	public ResponseEntity<?> handleResourceException(ResourceException exception) {
		LOG.warn("ResourceException Caught: {}", exception.getMessage());
		HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		return new ResponseEntity<>(prepareErrorResponse(exception.getMessage(), status), status);
	}

	@ExceptionHandler(AvailabilityException.class)
	public ResponseEntity<?> handleAvailabilityException(AvailabilityException exception) {
		LOG.warn("AvailabilityException Caught: {}", exception.getMessage());
		HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(prepareErrorResponse(exception.getMessage(), status), status);
	}

	@ExceptionHandler(AppointmentException.class)
	public ResponseEntity<?> handleAppointmentException(AppointmentException exception) {
		LOG.warn("AppointmentException Caught: {}", exception.getMessage());
		HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(prepareErrorResponse(exception.getMessage(), status), status);
	}

	@ExceptionHandler(AdminException.class)
	public ResponseEntity<?> handleAdminException(AdminException exception) {
		LOG.warn("AdminException Caught: {}", exception.getMessage());
		HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(prepareErrorResponse(exception.getMessage(), status), status);
	}
	
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<?> hadleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
		LOG.warn("SQLIntegrityConstraintViolationException Caught: {}", exception.getMessage());
		return ResponseEntity.internalServerError().body(prepareErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private static ErrorResponse prepareErrorResponse(String message, HttpStatus status) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(message);
		errorResponse.setStatus(status);
		errorResponse.setStatusCode(status.value());
		errorResponse.setTimeStamp(LocalDateTime.now());
		return errorResponse;
	}
}
