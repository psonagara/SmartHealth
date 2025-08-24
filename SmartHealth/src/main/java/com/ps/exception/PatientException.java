package com.ps.exception;

import org.springframework.http.HttpStatus;

public class PatientException extends RuntimeException {

	private static final long serialVersionUID = 1931058797487719419L;
	private HttpStatus status;
	
	
	public PatientException() {
		super();
	}
	
	public PatientException(String msg) {
		super(msg);
	}
	
	public PatientException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public PatientException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}

	public PatientException(String msg, Throwable cause, HttpStatus status) {
		super(msg, cause);
		this.status = status;
	}
	
	
	public HttpStatus getStatus() {
		return status;
	}
}
