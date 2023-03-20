package com.example.ProjectBoot.service;

import java.sql.Timestamp;
import java.util.List;

import com.example.ProjectBoot.entity.Booking;

public interface BookingService {
	List<Booking> findAll();
	List<Booking> findAllByDropoffTimeIsNull();
	List<Booking> findAllByDropoffTimeIsNullOrCallTimeGreaterThan(Timestamp callTime);
	Booking save(Booking booking);
	Booking findById(Long id);
	Booking getReferenceById(Long id);
	Booking book(Booking bookingRequest);
	Booking assignDriver(Long id);
	Booking pickupCustomer(Long id);
	Booking dropoffCustomer(Long id, Integer passengers, Double miles, Double waitMinutes);
	Double getFare(Integer passengers, Double miles, Double waitMinutes);
}
