package com.example.ProjectBoot.configuration;

import java.sql.Timestamp;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.ProjectBoot.entity.User;

public class UserPrincipal implements UserDetails {
	private static final long serialVersionUID = 4137973856821671423L;

	private User user;

	public UserPrincipal(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return !user.getAccountExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return !user.getAccountLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return user.getPasswordExpires() == null || user.getPasswordExpires().after(new Timestamp(Instant.now().toEpochMilli()));
	}

	@Override
	public boolean isEnabled() {
		return user.getEnabled();
	}

}
