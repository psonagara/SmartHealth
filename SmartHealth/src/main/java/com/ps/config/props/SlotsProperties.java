package com.ps.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties class for configuring slot generation settings.
 * Maps to 'smarthealth.slots' prefix in properties file.
 */
@Getter
@Setter
@Component
@ConfigurationProperties("smarthealth.slots")
public class SlotsProperties {

	private Integer maximumGenerationDays;
}
