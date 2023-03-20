package com.example.ProjectBoot.service;

import java.util.List;

import com.example.ProjectBoot.entity.Cab;

public interface CabService {
	List<Cab> findByInServiceIsTrue();
	List<Cab> findAll();
	Cab findByCabNumber(String cabNumber);
	Cab findById(Long id);
	Cab save(Cab cab);
}
