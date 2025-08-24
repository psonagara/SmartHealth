package com.ps.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ps.config.props.CorsProperties;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS) settings.
 * Defines CORS policies to allow safe cross-origin requests to the SmartHealth API.
 * Restricts origins, methods, and headers for security and compliance.
 */
@Configuration
public class CorsConfig {
	
	@Autowired
	private CorsProperties corsProperties;
	
	/**
     * Creates a WebMvcConfigurer bean to configure CORS mappings.
     * Allows origins, HTTP methods, and headers as defined in CorsProperties.
     * Supports wildcard (*) for origins if configured.
     *
     * @return a WebMvcConfigurer instance with CORS settings
     */
	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedOrigins(resolve(corsProperties.getAllowedOrigins()))
	            .allowedMethods(resolve(corsProperties.getAllowedMethods()))
	            .allowedHeaders(resolve(corsProperties.getAllowedHeaders()))
	            .maxAge(corsProperties.getMaxAge());
			}
		};
	}
	
	/**
     * Resolves a list of values into a String array, handling wildcard (*) for origins.
     * If the list contains a single "*" entry, returns an array with "*", otherwise converts the list.
     *
     * @param values the list of values to resolve
     * @return a String array representing the resolved values
     * @throws IllegalArgumentException if the input list is null
     */
	private String[] resolve(List<String> values) {
		if (values == null) {
            throw new IllegalArgumentException("Input values list cannot be null");
        }
        if (values.size() == 1 && "*".equals(values.get(0))) {
            return new String[] { "*" };
        }
        return values.toArray(new String[0]);
	}
}
