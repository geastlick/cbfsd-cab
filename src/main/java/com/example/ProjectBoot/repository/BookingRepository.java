package com.example.ProjectBoot.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ProjectBoot.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long>{
	List<Booking> findAllByDropoffTimeIsNull();
	List<Booking> findAllByDropoffTimeIsNullOrCallTimeGreaterThan(Timestamp callTime);
}
