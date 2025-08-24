package com.ps.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public interface RestTestUtil {

	public static Map<String, Object> prepareResponseMap(Object object) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", object);
		resultMap.put("totalPages", 1);
		resultMap.put("pageSize", 10);
		resultMap.put("currentPage", 0);
		resultMap.put("isFirstPage", true);
		resultMap.put("isLastPage", true);
		resultMap.put("hasPreviousPage", false);
		resultMap.put("hasNextPage", false);
		return resultMap;
	}
    
    public static String toJsonString(Object object) throws JsonProcessingException {
    	ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    	return objectMapper.writeValueAsString(object);
    }
    
    public static <T> T toObjectFromJson(String json, TypeReference<T> typeReference) throws JsonMappingException, JsonProcessingException {
    	ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    	return objectMapper.readValue(json, typeReference);
    }
}
