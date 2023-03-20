package com.example.ProjectBoot.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.ProjectBoot.exception.BadRequestException;
import com.example.ProjectBoot.exception.ConflictException;
import com.example.ProjectBoot.exception.NotFoundException;
import com.example.ProjectBoot.exception.NotModifiedException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({BadRequestException.class})
	public final ResponseEntity<ApiError> handleBadRequestException(Exception ex, WebRequest request) {
		ApiError error = new ApiError(ex.getMessage(), ex.getStackTrace().toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler({ConflictException.class})
	public final ResponseEntity<ApiError> handleConflictException(Exception ex, WebRequest request) {
		ApiError error = new ApiError(ex.getMessage(), ex.getStackTrace().toString());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	@ExceptionHandler({NotFoundException.class})
	public final ResponseEntity<ApiError> handleNotFoundException(Exception ex, WebRequest request) {
		ApiError error = new ApiError(ex.getMessage(), ex.getStackTrace().toString());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	@ExceptionHandler({NotModifiedException.class})
	public final ResponseEntity<ApiError> handleNotModifiedException(Exception ex, WebRequest request) {
		ApiError error = new ApiError(ex.getMessage(), ex.getStackTrace().toString());
		return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(error);
	}

}
