package com.ps.exception;

import org.springframework.http.HttpStatus;

public class AvailabilityException extends RuntimeException {

	private static final long serialVersionUID = 5713477241237994207L;
	private HttpStatus status;
	
	
	public AvailabilityException() {
		super();
	}
	
	public AvailabilityException(String msg) {
		super(msg);
	}
	
	public AvailabilityException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public AvailabilityException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
}
