package com.ps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for application-wide beans and settings.
 */
@Configuration
public class AppConfig {
	
	/**
     * Creates and registers a BCryptPasswordEncoder bean for password encryption.
     * This bean is used by Spring Security to hash and verify passwords.
     *
     * @return a BCryptPasswordEncoder instance
     */
	@Bean
	PasswordEncoder passwordEncoder() { 
		return new BCryptPasswordEncoder();
	}
}
