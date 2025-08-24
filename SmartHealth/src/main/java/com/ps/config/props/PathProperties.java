package com.ps.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties class for configuring file path settings.
 * Maps to 'smarthealth.paths' prefix in properties file.
 */
@Getter
@Setter
@ConfigurationProperties("smarthealth.paths")
@Component
public class PathProperties {

	private String defaultProfilePicName;
	private String imageStoragePath;
	private String patientImagePath;
	private String doctorImagePath;
	private String adminImagePath;
}
