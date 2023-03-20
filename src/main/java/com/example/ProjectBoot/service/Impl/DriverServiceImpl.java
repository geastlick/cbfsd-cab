package com.example.ProjectBoot.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.ProjectBoot.entity.Cab;
import com.example.ProjectBoot.entity.Driver;
import com.example.ProjectBoot.exception.BadRequestException;
import com.example.ProjectBoot.exception.NotFoundException;
import com.example.ProjectBoot.exception.NotModifiedException;
import com.example.ProjectBoot.repository.DriverRepository;
import com.example.ProjectBoot.service.CabService;
import com.example.ProjectBoot.service.DriverService;

@Service
public class DriverServiceImpl implements DriverService {
	
	@Autowired
	private DriverRepository driverRepo;
	
	@Autowired
	private CabService cabService;

	@Override
	public List<Driver> findAll() {
		return driverRepo.findAll();
	}

	@Override
	public Driver findAvailableDriver() {
		return driverRepo.findAvailableDriver();
	}

	@Override
	public Driver findById(Long id) {
		Optional<Driver> driver = driverRepo.findById(id);
		if(driver.isEmpty()) throw new NotFoundException("Driver with Id "+id+" not found.");
		return driver.get();
	}

	@Override
	public Driver save(Driver driver) {
		return driverRepo.save(driver);
	}

	@Override
	public Driver updateCab(Long id, Long cabId) {
		Driver driver = findById(id);
		if(driver.getCab() != null) {
			// Driver is already assigned requested cab
			if(driver.getCab().getId() == cabId) throw new NotModifiedException("Cab is already assigned to driver");
			
			throw new BadRequestException("Driver cannot be assigned to two cabs");
		}
		Cab cab;
		try {
			cab = cabService.findById(cabId);
		} catch(NotFoundException e) {
			// Cab with Id {id} not found.
			throw new BadRequestException("Cab is invalid, cannot be assigned to driver.");	
		}
		if(!cab.getInService()) {
			throw new BadRequestException("Cab is not in service, cannot be assigned to driver.");
		}
		driver.setCab(cab);

		try {
			return driverRepo.save(driver);
		} catch(DataIntegrityViolationException e) {
			String msg = e.getRootCause().getMessage().toLowerCase();
			if(msg.contains("driver_uk1")) {
				throw new BadRequestException("Cab is assigned to another driver.");
			}
			if(msg.contains("driver_fk1")) {
				throw new BadRequestException("Cab is invalid, cannot be assigned to driver.");
			}
			throw e;
		}
	}

}
