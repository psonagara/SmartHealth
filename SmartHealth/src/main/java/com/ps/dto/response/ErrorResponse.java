package com.ps.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a error response.
 * Used to return error details like message, status and time stamp.
 */
@Getter
@Setter
public class ErrorResponse {

	private String message;
	private LocalDateTime timeStamp;
	private HttpStatus status;
	private int statusCode;
}
