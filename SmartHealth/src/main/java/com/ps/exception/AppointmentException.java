package com.ps.exception;

import org.springframework.http.HttpStatus;

public class AppointmentException extends RuntimeException {

	private static final long serialVersionUID = -533514418793040608L;
	private HttpStatus status;
	
	
	public AppointmentException() {
		super();
	}
	
	public AppointmentException(String msg) {
		super(msg);
	}
	
	public AppointmentException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public AppointmentException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
}
