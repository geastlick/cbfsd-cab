package com.example.ProjectBoot.controller.api;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ProjectBoot.entity.Driver;
import com.example.ProjectBoot.service.DriverService;

@RestController()
@RequestMapping("/api/drivers")
public class DriverController {
	
	@Autowired
	DriverService driverService;

	@GetMapping()
	public List<Driver> getDrivers() {
		return driverService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
		Driver driver;
		driver = driverService.findById(id);
		return ResponseEntity.ok(driver);
	}
	
	@PostMapping()
	public ResponseEntity<Driver> create(@RequestBody Driver driver) {
		Driver savedDriver = driverService.save(driver);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedDriver.getId())
				.toUri();
		return ResponseEntity.created(location).body(savedDriver);
	}
	
	@PutMapping()
	public ResponseEntity<Driver> update(@RequestBody Driver driver) {
		return ResponseEntity.ok(driverService.save(driver));
	}
	
	@PutMapping("/{id}/cab/{cabId}")
	public ResponseEntity<Driver> updateCab(@PathVariable Long id, @PathVariable Long cabId) {
		Driver driver = driverService.updateCab(id, cabId);
		return ResponseEntity.ok(driver);
	}
	
	@DeleteMapping("/{id}/cab")
	public ResponseEntity<Driver> removeCab(@PathVariable Long id) {
		Driver driver = driverService.findById(id);
		driver.setCab(null);
		return ResponseEntity.ok(driverService.save(driver));
	}
}
