package com.example.ProjectBoot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ProjectBoot.entity.Cab;

public interface CabRepository extends JpaRepository<Cab, Long>{

	List<Cab> findByInServiceIsTrue();
	
	Optional<Cab> findByCabNumber(String cabNumber);

}
