package com.example.ProjectBoot.controller.api;

import java.lang.reflect.Field;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ProjectBoot.entity.Booking;
import com.example.ProjectBoot.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@GetMapping(value = "/last24", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<List<Booking>> getLast24() {
		Timestamp ts = new Timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
		return ResponseEntity.ok(bookingService.findAllByDropoffTimeIsNullOrCallTimeGreaterThan(ts));
	}
	@GetMapping(value = "/last24", produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> getLast24Text() {
		Timestamp ts = new Timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
		return ResponseEntity.ok(bookingService.findAllByDropoffTimeIsNullOrCallTimeGreaterThan(ts).toString());
	}
	@GetMapping(value = "/last24", produces = {MediaType.TEXT_HTML_VALUE})
	public ResponseEntity<String> getLast24Html() throws IllegalArgumentException, IllegalAccessException {
		Timestamp ts = new Timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
		StringBuilder sb = new StringBuilder();

		sb.append("<table>");
		for(Field field : Booking.class.getDeclaredFields()) {
			sb.append("<th>"+field.getName()+"</th>");
		}
		for(Booking b: bookingService.findAllByDropoffTimeIsNullOrCallTimeGreaterThan(ts)) {
			sb.append("<tr>");
			for(Field field : Booking.class.getDeclaredFields()) {
				field.setAccessible(true);
				sb.append("<th>"+field.get(b)+"</th>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		
		return ResponseEntity.ok(sb.toString());
	}
	
	@GetMapping()
	public ResponseEntity<List<Booking>> getAll() {
		return ResponseEntity.ok(bookingService.findAll());
	}
	
	@GetMapping("/open")
	public ResponseEntity<List<Booking>> getAllOpen() {
		return ResponseEntity.ok(bookingService.findAllByDropoffTimeIsNull());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Booking> getById(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.findById(id));
	}
	
	@PostMapping()
	public ResponseEntity<Booking> book(@RequestBody Booking bookingRequest) {
		Booking savedBooking = bookingService.book(bookingRequest);
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedBooking.getId())
				.toUri();
		return ResponseEntity.created(location).body(savedBooking);
	}
	
	@PutMapping("/{id}/assign")
	public ResponseEntity<Booking> assign(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.assignDriver(id));
	}
	
	@PutMapping("/{id}/pickup")
	public ResponseEntity<Booking> pickup(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.pickupCustomer(id));
	}
	
	@PutMapping("/{id}/dropoff")
	public ResponseEntity<Booking> dropoff(@PathVariable Long id, @RequestParam Integer passengers, @RequestParam Double miles, @RequestParam Double waitMinutes) {
		return ResponseEntity.ok(bookingService.dropoffCustomer(id, passengers, miles, waitMinutes));
	}
	
	@GetMapping("/fare")
	public ResponseEntity<String> fare(@RequestParam Integer passengers, @RequestParam Double miles, @RequestParam Double waitMinutes) {
		return ResponseEntity.ok("{\"fare\": "+bookingService.getFare(passengers, miles, waitMinutes)+"}");
	}

}
