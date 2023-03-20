package com.example.ProjectBoot.bean.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
	private String username;
	private String email;
	private String fullName;
	private String password;
	private String passwordConfirm;
}
