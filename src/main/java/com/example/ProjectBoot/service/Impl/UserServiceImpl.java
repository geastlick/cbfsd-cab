package com.example.ProjectBoot.service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ProjectBoot.configuration.UserPrincipal;
import com.example.ProjectBoot.entity.User;
import com.example.ProjectBoot.repository.UserRepository;
import com.example.ProjectBoot.service.UserService;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user;
		if(username.contains("@")) {
			user = userRepo.findByEmail(username.toLowerCase());
		} else {
			user = userRepo.findByUsername(username.toUpperCase());
		}
		if(user.isEmpty()) {
			throw new UsernameNotFoundException(username);
		}
		return new UserPrincipal(user.get());
	}
	
	
	@Override
	public User save(User user) {
		return userRepo.save(user);
	}

	@Override
	public User findById(Long id) {
		Optional<User> user = userRepo.findById(id);
		return user.orElse(null);
	}

	@Override
	public User findByUsername(String username) {
		Optional<User> user = userRepo.findByUsername(username.toUpperCase());
		return user.orElse(null);
	}

	@Override
	public User findByEmail(String email) {
		Optional<User> user = userRepo.findByEmail(email.toLowerCase());
		return user.orElse(null);
	}

	@Override
	public User registerNewUserAccount(User newUser) {
		User user = new User();
		user.setUsername(newUser.getUsername().toUpperCase());
		user.setEmail(newUser.getEmail().toLowerCase());
		user.setFullName(newUser.getFullName());
		user.setPassword(passwordEncoder.encode(newUser.getPassword()));
		return userRepo.save(user);
	}

}
