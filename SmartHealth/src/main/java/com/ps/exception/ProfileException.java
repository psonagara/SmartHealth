package com.ps.exception;

import org.springframework.http.HttpStatus;

public class ProfileException extends RuntimeException {

	private static final long serialVersionUID = -5582812785257998584L;
	
	private HttpStatus status;
	
	public ProfileException() {
		super();
	}
	
	public ProfileException(String msg) {
		super(msg);
	}
	
	public ProfileException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
