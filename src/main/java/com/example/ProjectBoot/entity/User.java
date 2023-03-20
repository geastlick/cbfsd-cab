package com.example.ProjectBoot.entity;

import java.sql.Timestamp;

import com.example.ProjectBoot.enumerator.RoleEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String email;
	private String fullName;
	private String password;
	private Boolean accountExpired = false;
	private Boolean accountLocked = false;
	private Timestamp passwordExpires;
	private Boolean enabled = true;
	@Enumerated(EnumType.STRING)
	private RoleEnum role = RoleEnum.REGISTERED;
}
