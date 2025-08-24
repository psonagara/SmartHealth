package com.ps.exception;

import org.springframework.http.HttpStatus;

public class ResourceException extends RuntimeException {

	private static final long serialVersionUID = -3447810240833408802L;
	
	private HttpStatus status;
	
	public ResourceException() {
		super();
	}
	
	public ResourceException(String msg) {
		super(msg);
	}
	
	public ResourceException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
