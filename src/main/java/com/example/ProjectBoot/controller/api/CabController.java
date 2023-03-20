package com.example.ProjectBoot.controller.api;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ProjectBoot.entity.Cab;
import com.example.ProjectBoot.service.CabService;

@RestController()
@RequestMapping("/api/cabs")
public class CabController {

	@Autowired
	CabService cabService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Cab> getById(@PathVariable Long id) {
		return ResponseEntity.ok(cabService.findById(id));
	}
	
	@GetMapping("/cab-number/{cabNumber}")
	public ResponseEntity<Cab> getByCabNumber(@PathVariable String cabNumber) {
		return ResponseEntity.ok(cabService.findByCabNumber(cabNumber));
	}

	@GetMapping()
	public ResponseEntity<List<Cab>> getAll() {
		return ResponseEntity.ok(cabService.findAll());
	}
	
	@PostMapping()
	public ResponseEntity<Cab> create(@RequestBody Cab cab) {
		Cab savedCab = cabService.save(cab);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedCab.getId())
				.toUri();
		return ResponseEntity.created(location).body(savedCab);
	}
	
	@PutMapping()
	public ResponseEntity<Cab> update(@RequestBody Cab cab) {
		return ResponseEntity.ok(cabService.save(cab));
	}
}
