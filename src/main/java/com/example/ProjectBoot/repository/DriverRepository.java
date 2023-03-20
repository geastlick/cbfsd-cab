package com.example.ProjectBoot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ProjectBoot.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long>{

	@Query("SELECT r FROM Driver r WHERE r.id = (SELECT MAX(d.id) FROM Driver d WHERE d.cab IS NOT NULL AND NOT EXISTS (SELECT b FROM Booking b WHERE b.driver = d AND b.dropoffTime IS NULL))")
	public Driver findAvailableDriver();

}
