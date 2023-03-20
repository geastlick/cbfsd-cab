package com.example.ProjectBoot.service.Impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ProjectBoot.entity.Driver;
import com.example.ProjectBoot.configuration.FareConfiguration;
import com.example.ProjectBoot.entity.Booking;
import com.example.ProjectBoot.exception.BadRequestException;
import com.example.ProjectBoot.exception.NotFoundException;
import com.example.ProjectBoot.repository.BookingRepository;
import com.example.ProjectBoot.service.BookingService;
import com.example.ProjectBoot.service.DriverService;

@Service
public class BookingServiceImpl implements BookingService {
	
	@Autowired
	private FareConfiguration fareConfig;

	@Autowired
	private BookingRepository bookingRepo;
	
	@Autowired
	private DriverService driverService;

	@Override
	public List<Booking> findAll() {
		return bookingRepo.findAll();
	}

	@Override
	public List<Booking> findAllByDropoffTimeIsNull() {
		return bookingRepo.findAllByDropoffTimeIsNull();
	}

	@Override
	public List<Booking> findAllByDropoffTimeIsNullOrCallTimeGreaterThan(Timestamp callTime) {
		return bookingRepo.findAllByDropoffTimeIsNullOrCallTimeGreaterThan(callTime);
	}

	@Override
	public Booking save(Booking booking) {
		return bookingRepo.save(booking);
	}

	@Override
	public Booking findById(Long id) {
		Optional<Booking> bookingOpt = bookingRepo.findById(id);
		if(bookingOpt.isEmpty()) {
			throw new NotFoundException("Booking with id "+id+" not found.");
		}
		return bookingOpt.get();
	}

	@Override
	public Booking getReferenceById(Long id) {
		return bookingRepo.getReferenceById(id);
	}

	@Override
	public Booking assignDriver(Long id) {
		Optional<Booking> bookingOpt = bookingRepo.findById(id);
		if(bookingOpt.isEmpty()) {
			throw new NotFoundException("Booking with id "+id+" not found.");
		}
		Booking booking = bookingOpt.get();
		if(booking.getDriver() == null) {
			Driver driver = driverService.findAvailableDriver();
			booking.setDriver(driver);
			return bookingRepo.save(booking);
		} else {
			throw new BadRequestException("Driver is already assigned.");
		}
	}

	@Override
	public Double getFare(Integer passengers, Double miles, Double waitMinutes) {
		return passengers*(fareConfig.getBase()+miles*fareConfig.getMile()+waitMinutes*fareConfig.getMinuteWait());
	}

	@Override
	public Booking dropoffCustomer(Long id, Integer passengers, Double miles, Double waitMinutes) {
		Optional<Booking> bookingOpt = bookingRepo.findById(id);
		if(bookingOpt.isEmpty()) {
			throw new NotFoundException("Booking with id "+id+" not found.");
		}
		Booking booking = bookingOpt.get();
		if(booking.getPickupTime()==null) {
			throw new BadRequestException("Customer cannot be dropped off as they haven't yet been picked up");
		}
		if(booking.getDropoffTime()!=null) {
			throw new BadRequestException("A customer can only be dropped off once");
		}
		booking.setDropoffTime(new Timestamp(new Date().getTime()));
		booking.setPassengers(passengers);
		booking.setMiles(miles);
		booking.setWaitMinutes(waitMinutes);
		booking.setFare(getFare(passengers,miles,waitMinutes));
		return bookingRepo.save(booking);
	}

	@Override
	public Booking pickupCustomer(Long id) {
		Optional<Booking> bookingOpt = bookingRepo.findById(id);
		if(bookingOpt.isEmpty()) {
			throw new NotFoundException("Booking with id "+id+" not found.");
		}
		Booking booking = bookingOpt.get();
		if(booking.getPickupTime()!=null) {
			throw new BadRequestException("Customer has already been picked up");
		}
		booking.setPickupTime(new Timestamp(new Date().getTime()));
		return bookingRepo.save(booking);
	}

	@Override
	public Booking book(Booking bookingRequest) {
		Driver driver = driverService.findAvailableDriver();
		bookingRequest.setCallTime(new Timestamp(new Date().getTime()));
		if(driver != null) {
			bookingRequest.setDriver(driver);
		}
		return bookingRepo.save(bookingRequest);
	}

}
