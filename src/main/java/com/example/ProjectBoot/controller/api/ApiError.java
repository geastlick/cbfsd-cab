package com.example.ProjectBoot.controller.api;

import java.time.Instant;

import lombok.Data;

@Data
public class ApiError {
	private String errorMsg;
	private Instant timestamp = Instant.now();
	private String details;

	public ApiError(String errorMsg, String details) {
		this.errorMsg = errorMsg;
		this.details = details;
	}
}
