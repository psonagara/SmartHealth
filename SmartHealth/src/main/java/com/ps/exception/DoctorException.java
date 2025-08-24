package com.ps.exception;

import org.springframework.http.HttpStatus;

public class DoctorException extends RuntimeException {

	private static final long serialVersionUID = 5713477241237994207L;
	private HttpStatus status;
	
	
	public DoctorException() {
		super();
	}
	
	public DoctorException(String msg) {
		super(msg);
	}
	
	public DoctorException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public DoctorException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
}
