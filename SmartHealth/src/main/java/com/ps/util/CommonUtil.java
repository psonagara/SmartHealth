package com.ps.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ps.constants.IResponseConstants;
import com.ps.dto.response.ApiResponse;

public interface CommonUtil {
	
	public static ResponseEntity<ApiResponse<?>> prepareResponseWithMessage(String message, HttpStatus status) {
		return prepareResponse(null, message, status);
	}

	public static ResponseEntity<ApiResponse<?>> prepareResponseWithContent(Object content, HttpStatus status) {
		return prepareResponse(content, null, status);
	}

	/**
     * Prepares a standardized API response with content, message, and status code.
     *
     * @param content The response content (e.g., LoginResponse or null).
     * @param message The response message (e.g., success or failure text).
     * @param statusCode The HTTP status code for the response.
     * @return ResponseEntity containing the ApiResponse.
     */
	public static ResponseEntity<ApiResponse<?>> prepareResponse(Object content, String message, HttpStatus status) {
		ApiResponse<?> apiResponse = ApiResponse.builder()
				.content(content)
				.message(message)
				.status(status)
				.statusCode(status.value())
				.timeStamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<>(apiResponse, status);
	}

	public static Map<String, Object> prepareResponseMap(Object response, Page<?> pages) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put(IResponseConstants.DATA, response);
		resultMap.put(IResponseConstants.TOTAL_PAGES, pages.getTotalPages());
		resultMap.put(IResponseConstants.PAGE_SIZE, pages.getSize());
		resultMap.put(IResponseConstants.CURRENT_PAGE, pages.getNumber());
		resultMap.put(IResponseConstants.IS_FIRST_PAGE, pages.isFirst());
		resultMap.put(IResponseConstants.IS_LAST_PAGE, pages.isLast());
		resultMap.put(IResponseConstants.HAS_PREVIOUS_PAGE, pages.hasPrevious());
		resultMap.put(IResponseConstants.HAS_NEXT_PAGE, pages.hasNext());
		return resultMap;
	}
}
