package com.example.ProjectBoot.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ProjectBoot.entity.Cab;
import com.example.ProjectBoot.exception.NotFoundException;
import com.example.ProjectBoot.repository.CabRepository;
import com.example.ProjectBoot.service.CabService;

@Service
public class CabServiceImpl implements CabService {
	
	@Autowired
	private CabRepository cabRepo;

	@Override
	public List<Cab> findByInServiceIsTrue() {
		return cabRepo.findByInServiceIsTrue();
	}

	@Override
	public List<Cab> findAll() {
		return cabRepo.findAll();
	}

	@Override
	public Cab findByCabNumber(String cabNumber) {
		Optional<Cab> cab = cabRepo.findByCabNumber(cabNumber);
		if(cab.isEmpty()) throw new NotFoundException("Cab with Number "+cabNumber+" not found.");
		return cab.get();
	}

	@Override
	public Cab findById(Long id) {
		Optional<Cab> cab = cabRepo.findById(id);
		if(cab.isEmpty()) throw new NotFoundException("Cab with Id "+id+" not found.");
		return cab.get();
	}

	@Override
	public Cab save(Cab cab) {
		return cabRepo.save(cab);
	}

}
