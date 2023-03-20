package com.example.ProjectBoot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class NotModifiedException extends RuntimeException {
	private static final long serialVersionUID = 1102810937334979822L;

	public NotModifiedException(String s) {
		super(s);
	}

}
