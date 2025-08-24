package com.ps.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response DTO for API calls, encapsulating content, message, status, and timestamp.
 *
 * @param <T> The type of the response content.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

	private T content;
	private String message;
	private HttpStatus status;
	private int statusCode;
	private LocalDateTime timeStamp;
}
