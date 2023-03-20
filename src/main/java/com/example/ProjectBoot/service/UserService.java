package com.example.ProjectBoot.service;

import com.example.ProjectBoot.entity.User;

public interface UserService {
	User save(User user);
	
	User findById(Long id);
	User findByUsername(String username);
	User findByEmail(String email);
	User registerNewUserAccount(User user);
}
