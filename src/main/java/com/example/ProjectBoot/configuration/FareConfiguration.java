package com.example.ProjectBoot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("fare")
@Data
public class FareConfiguration {
	private Double base;
	private Double mile;
	private Double minuteWait;
}
