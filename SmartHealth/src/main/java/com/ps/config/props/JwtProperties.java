package com.ps.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties class for configuring JSON Web Token (JWT) settings.
 * Maps to 'smarthealth.jwt' prefix in properties file.
 */
@Getter
@Setter
@ConfigurationProperties("smarthealth.jwt")
@Component
public class JwtProperties {

	private Long expirationTimeInMinutes;
	private String secreteKey;
}
