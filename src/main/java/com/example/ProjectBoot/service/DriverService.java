package com.example.ProjectBoot.service;

import java.util.List;

import com.example.ProjectBoot.entity.Driver;

public interface DriverService {
	List<Driver> findAll();
	Driver findAvailableDriver();
	Driver findById(Long id);
	Driver save(Driver driver);
	Driver updateCab(Long id, Long cabId);
}
