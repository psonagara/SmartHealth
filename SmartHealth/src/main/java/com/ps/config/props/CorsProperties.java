package com.ps.config.props;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Properties class for configuring Cross-Origin Resource Sharing (CORS) settings.
 * Maps to 'smarthealth.cors' prefix in properties file.
 */
@Data
@Component
@ConfigurationProperties(prefix = "smarthealth.cors")
public class CorsProperties {
	
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private Long maxAge;
}
