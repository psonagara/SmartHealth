package com.ps.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ps.dto.request.AdminProfileRequest;
import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.PasswordRequest;
import com.ps.dto.request.PatientProfileRequest;
import com.ps.util.JwtUtil;

/**
 * Aspect for logging method executions in controllers and services.
 * Logs entry, arguments, exit status, and execution time.
 * Applies to all methods within @RestController and @Service annotated classes.
 */
@Aspect
@Component
public class LoggingAspect {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);
	
	/**
     * Logs the execution of methods in @RestController classes.
     * Captures method entry, key request parameters, exit status, and execution time.
     *
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if an error occurs during method execution
     */
	@Around("execution(* com.ps.rest..*(..)) && @within(org.springframework.web.bind.annotation.RestController)")
	public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		
		// Log method entry with class and method name
		LOG.info("Entering {}.{}", className, methodName);
		
		// Log key request parameters (avoid sensitive data like passwords)
		String email = JwtUtil.getEmailFromToken();
		if (!(email == null || email.equals("anonymousUser"))) {			
			LOG.info(" -| Email: {}", email);
		}
		for (Object arg : arguments) {
			if (arg instanceof PatientProfileRequest request) {
				LOG.info(" -| Email: {}", request.getEmail());
			} else if (arg instanceof LoginRequest request) {
				LOG.info(" -| Email/Phone: {}", request.getUser());
			} else if (arg instanceof DoctorProfileRequest request) {
				LOG.info(" -| Email: {}", request.getEmail());
			} else if (arg instanceof AdminProfileRequest request) {
				LOG.info(" -| Email: {}", request.getEmail());
			} else if (arg instanceof PasswordRequest request) {
				LOG.info(" -| Password Change Request");
			} else {
				LOG.info(" -| {}", arg);
			}
		}
		
		Object result;
		long startTime = System.currentTimeMillis();
		try {
			result = joinPoint.proceed();
		} catch (Throwable e) {
			LOG.error("Exception in {}.{}, Message: {}", className, methodName, e.getMessage(), e);
			throw e;
		}
		long executionTime = System.currentTimeMillis() - startTime;
		
		// Log method exit with status and execution time
		if (result instanceof ResponseEntity<?> response) {
			LOG.info("Exiting {}.{}, Status: {}, Execution Time: {}ms", className, methodName, response.getStatusCode(), executionTime);
		} else {
			LOG.info("Exiting {}.{}, Execution Time: {}ms", className, methodName, executionTime);
		}
		return result;
	}
	
	/**
     * Logs the execution of methods in @Service implementation classes.
     * Captures method entry, exit status, and execution time at DEBUG level.
     *
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if an error occurs during method execution
     */
	@Around("execution(* com.ps.service.impl..*(..)) && @within(org.springframework.stereotype.Service)")
	public Object logServiceImplMethods(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		
		// Log method entry at DEBUG level
        LOG.debug("Entering {}.{}", className, methodName);
        
		Object result;
		long start = System.currentTimeMillis();
		try {
			result = joinPoint.proceed();
		} catch (Throwable e) {
			LOG.error("Exception in {}.{}, Message: {}", className, methodName, e.getMessage());
			throw e;
		}
		long executionTime = System.currentTimeMillis() - start;
		
		// Log method exit with status and execution time
		if (result instanceof ResponseEntity<?> response) {
			LOG.debug("Exiting {}.{}, Status: {}, Execution Time: {}ms", className, methodName, response.getStatusCode(), executionTime);
		} else {
			LOG.debug("Exiting {}.{}, Execution Time: {}ms", className, methodName, executionTime);
		}
		return result;
	}
}
