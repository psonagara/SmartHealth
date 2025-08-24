package com.ps.util;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;

import com.ps.constants.IExceptionConstants;
import com.ps.exception.ResourceException;

public interface DataUtil {

	public static Resource getResourceFromPath(Path filePath) {
		try {
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) 
				return resource;
			else 
				throw new ResourceException(IExceptionConstants.RESOURCE_NOT_FOUND + filePath, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException exception) {
			throw new ResourceException(IExceptionConstants.RESOURCE_FETCH_FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
