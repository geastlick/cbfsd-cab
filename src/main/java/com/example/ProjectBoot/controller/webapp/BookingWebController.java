package com.example.ProjectBoot.controller.webapp;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.ProjectBoot.dto.Page;
import com.example.ProjectBoot.entity.Booking;
import com.example.ProjectBoot.service.BookingService;

@Controller
@RequestMapping("/webapp/bookings")
public class BookingWebController {
	
	@Autowired
	BookingService bookingService;

	private Page setPage(String title) {
		Page page = new Page();
		page.setTitle(title);
		page.setTemplate("th_booking");
		return page;
	}
	
	@GetMapping()
	public ModelAndView Bookings(Model model) {
		model.addAttribute("openBookings", bookingService.findAllByDropoffTimeIsNull());
		model.addAttribute("page",setPage("Bookings"));
		return new ModelAndView("th_layout");
	}
	
	@PostMapping("/book")
	public ModelAndView BookCustomer(Model model, @RequestParam String customerName, @RequestParam String customerEmail, @RequestParam String customerPhone, @RequestParam String pickupLocation) {
		Booking booking = new Booking();
		booking.setCallTime(Timestamp.from(Instant.now()));
		booking.setCustomerName(customerName);
		booking.setCustomerEmail(customerEmail);
		booking.setCustomerPhone(customerPhone);
		booking.setPickupLocation(pickupLocation);
		bookingService.save(booking);
		return new ModelAndView(new RedirectView("/webapp/bookings"));
	}
	
	@PostMapping("/assign")
	public ModelAndView AssignDriver(Model model, @RequestParam Long id) {
		bookingService.assignDriver(id);
		return new ModelAndView(new RedirectView("/webapp/bookings"));
	}
	
	@PostMapping("/pickup")
	public ModelAndView PickUpCustomer(Model model, @RequestParam Long id) {
		bookingService.pickupCustomer(id);
		return new ModelAndView(new RedirectView("/webapp/bookings"));
	}
	
	@PostMapping("/dropoff")
	public ModelAndView DropoffCustomer(Model model, @RequestParam Long id, @RequestParam Integer passengers, @RequestParam Double miles, @RequestParam Double waitMinutes) {
		bookingService.dropoffCustomer(id, passengers, miles, waitMinutes);
		return new ModelAndView(new RedirectView("/webapp/bookings"));
	}

}
