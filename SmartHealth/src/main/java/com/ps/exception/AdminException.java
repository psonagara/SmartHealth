package com.ps.exception;

import org.springframework.http.HttpStatus;

public class AdminException extends RuntimeException {

	private static final long serialVersionUID = 3370735251281218766L;
	private HttpStatus status;


	public AdminException() {
		super();
	}

	public AdminException(String msg) {
		super(msg);
	}

	public AdminException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AdminException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
