package com.example.ProjectBoot.testcontroller.api;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ProjectBoot.configuration.FareConfiguration;
import com.example.ProjectBoot.entity.Booking;
import com.example.ProjectBoot.entity.Cab;
import com.example.ProjectBoot.entity.Driver;
import com.example.ProjectBoot.repository.BookingRepository;
import com.example.ProjectBoot.repository.DriverRepository;
import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
	
	@Captor
	private ArgumentCaptor<Booking> captor;
	
	@MockBean
	private BookingRepository bookingRepo;
	
	@MockBean
	private DriverRepository driverRepo;

	@Autowired
	private FareConfiguration fareConfig;
	
    @Autowired
    private WebApplicationContext context;

	private MockMvc mockMvc;
    @BeforeEach
    public void setup() {
    	mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(springSecurity())
          .build();
    }

	
	private static final Gson gson = new Gson();

    @WithMockUser("user")
	@Test
	void bookWithDriver() throws Exception {
		Booking booking = new Booking();
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("ABC123");
		cab.setInService(true);
		
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setName("Tim Allen");
		driver.setLicense("NY12345");
		driver.setCab(cab);
		
		Mockito.when(driverRepo.findAvailableDriver()).thenReturn(driver);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/api/bookings")
				.accept(MediaType.APPLICATION_JSON)
				.content(gson.toJson(booking))
				.contentType(MediaType.APPLICATION_JSON);

		booking.setId(1L);
		Mockito.when(bookingRepo.save(captor.capture())).thenReturn(booking);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		
		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.CREATED, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertNull(response.getErrorMessage(),"Response Error Message");
		org.junit.jupiter.api.Assertions.assertEquals(driver, captor.getValue().getDriver(),"Driver Assigned");
		org.junit.jupiter.api.Assertions.assertNotNull(captor.getValue().getCallTime(),"Call Time");
	}

    @WithMockUser("user")
	@Test
	void bookWithoutDriver() throws Exception {
		Booking booking = new Booking();
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		
		Mockito.when(driverRepo.findAvailableDriver()).thenReturn(null);
		
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/api/bookings")
				.accept(MediaType.APPLICATION_JSON)
				.content(gson.toJson(booking))
				.contentType(MediaType.APPLICATION_JSON);
		
		booking.setId(1L);
		Mockito.when(bookingRepo.save(captor.capture())).thenReturn(booking);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.CREATED, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertNull(response.getErrorMessage(),"Response Error Message");
		org.junit.jupiter.api.Assertions.assertNull(captor.getValue().getDriver(),"No Driver Available");
		org.junit.jupiter.api.Assertions.assertNotNull(captor.getValue().getCallTime(),"Call Time");
	}
	
    @WithMockUser("user")
	@Test
	void bookThenAssignDriver() throws Exception {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		booking.setCallTime(new Timestamp(Instant.now().toEpochMilli()));
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("ABC123");
		cab.setInService(true);
		
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setName("Tim Allen");
		driver.setLicense("NY12345");
		driver.setCab(cab);

		Mockito.when(driverRepo.findAvailableDriver()).thenReturn(driver);
		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/assign")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();

		Mockito.verify(bookingRepo).save(captor.capture());
		
		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.OK, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertNull(response.getErrorMessage(),"Response Error Message");
		org.junit.jupiter.api.Assertions.assertEquals(driver,captor.getValue().getDriver(),"Driver Assigned");
	}

    @WithMockUser("user")
	@Test
	void pickupPassenger() throws Exception {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		booking.setCallTime(new Timestamp(Instant.now().toEpochMilli()));
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("ABC123");
		cab.setInService(true);
		
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setName("Tim Allen");
		driver.setLicense("NY12345");
		driver.setCab(cab);
		
		booking.setDriver(driver);

		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/pickup")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();

		Mockito.verify(bookingRepo).save(captor.capture());
		
		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.OK, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertNull(response.getErrorMessage(),"Response Error Message");
		org.junit.jupiter.api.Assertions.assertNotNull(captor.getValue().getCallTime(),"Call Time");
	}
	
    @WithMockUser("user")
	@Test
	void pickupPassengerTwice() throws Exception {
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		booking.setCallTime(new Timestamp(Instant.now().toEpochMilli()));
		booking.setPickupTime(new Timestamp(Instant.now().toEpochMilli()));
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("ABC123");
		cab.setInService(true);
		
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setName("Tim Allen");
		driver.setLicense("NY12345");
		driver.setCab(cab);
		
		booking.setDriver(driver);

		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/pickup")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Customer has already been picked up",errorMsg,"Response Error Message");
	}


    @WithMockUser("user")
	@Test
	void dropoffPassenger() throws Exception {
		Integer passengers = 1;
		Double miles = 5.14;
		Double waitMinutes = 2.2;
		Double fare = passengers*(fareConfig.getBase()+fareConfig.getMile()*miles+fareConfig.getMinuteWait()*waitMinutes);

		Booking booking = new Booking();
		booking.setId(1L);
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		booking.setCallTime(new Timestamp(Instant.now().toEpochMilli()));
		booking.setPickupTime(new Timestamp(Instant.now().toEpochMilli()));
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("ABC123");
		cab.setInService(true);
		
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setName("Tim Allen");
		driver.setLicense("NY12345");
		driver.setCab(cab);
		
		booking.setDriver(driver);

		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/dropoff")
				.param("passengers", passengers.toString())
				.param("miles", miles.toString())
				.param("waitMinutes", waitMinutes.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();

		Mockito.verify(bookingRepo).save(captor.capture());
		
		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.OK, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertNull(response.getErrorMessage(),"Response Error Message");
		org.junit.jupiter.api.Assertions.assertNotNull(captor.getValue().getDropoffTime(),"Dropoff Time");
		org.junit.jupiter.api.Assertions.assertEquals(passengers, captor.getValue().getPassengers(),"Passengers");
		org.junit.jupiter.api.Assertions.assertEquals(miles, captor.getValue().getMiles(),"Miles");
		org.junit.jupiter.api.Assertions.assertEquals(waitMinutes, captor.getValue().getWaitMinutes(),"Wait Minutes");
		org.junit.jupiter.api.Assertions.assertEquals(fare, captor.getValue().getFare(),"Fare");
	}

    @WithMockUser("user")
	@Test
	void dropoffPassengerNotPickedUp() throws Exception {
		Integer passengers = 1;
		Double miles = 5.14;
		Double waitMinutes = 2.2;

		Booking booking = new Booking();
		booking.setId(1L);
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		booking.setCallTime(new Timestamp(Instant.now().toEpochMilli()));
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("ABC123");
		cab.setInService(true);
		
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setName("Tim Allen");
		driver.setLicense("NY12345");
		driver.setCab(cab);
		
		booking.setDriver(driver);

		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/dropoff")
				.param("passengers", passengers.toString())
				.param("miles", miles.toString())
				.param("waitMinutes", waitMinutes.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Customer cannot be dropped off as they haven't yet been picked up",errorMsg,"Response Error Message");
	}
	
    @WithMockUser("user")
	@Test
	void dropoffPassengerTwice() throws Exception {
		Integer passengers = 1;
		Double miles = 5.14;
		Double waitMinutes = 2.2;
		Double fare = passengers*(fareConfig.getBase()+fareConfig.getMile()*miles+fareConfig.getMinuteWait()*waitMinutes);

		Booking booking = new Booking();
		booking.setId(1L);
		booking.setCustomerName("Jane Smith");
		booking.setCustomerPhone("1234567890");
		booking.setPickupLocation("Marco");
		booking.setCallTime(new Timestamp(Instant.now().toEpochMilli()));
		booking.setPickupTime(new Timestamp(Instant.now().toEpochMilli()));
		booking.setDropoffTime(new Timestamp(Instant.now().toEpochMilli()));
		booking.setPassengers(passengers);
		booking.setMiles(miles);
		booking.setWaitMinutes(waitMinutes);
		booking.setFare(fare);
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("ABC123");
		cab.setInService(true);
		
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setName("Tim Allen");
		driver.setLicense("NY12345");
		driver.setCab(cab);
		
		booking.setDriver(driver);

		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/dropoff")
				.param("passengers", passengers.toString())
				.param("miles", miles.toString())
				.param("waitMinutes", waitMinutes.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("A customer can only be dropped off once",errorMsg,"Response Error Message");
	}

    @WithMockUser("user")
	@Test
	void calculateFare() throws Exception {
		Integer passengers = 1;
		Double miles = 5.14;
		Double waitMinutes = 2.2;
		Double fare = passengers*(fareConfig.getBase()+fareConfig.getMile()*miles+fareConfig.getMinuteWait()*waitMinutes);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
			.get("/api/bookings/fare")
			.param("passengers", passengers.toString())
			.param("miles", miles.toString())
			.param("waitMinutes", waitMinutes.toString())
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.OK, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertNull(response.getErrorMessage(),"Response Error Message");
		org.junit.jupiter.api.Assertions.assertEquals(fare, jsonContext.read("$['fare']"), "Fare");
	}
	
    @WithMockUser("user")
	@Test
	void pickupNonexistentPassenger() throws Exception {
		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/pickup")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Booking with id 1 not found.",errorMsg,"Response Error Message");
	}
	
    @WithMockUser("user")
	@Test
	void dropoffNonexistentPassenger() throws Exception {
		Integer passengers = 1;
		Double miles = 5.14;
		Double waitMinutes = 2.2;

		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/bookings/1/dropoff")
				.param("passengers", passengers.toString())
				.param("miles", miles.toString())
				.param("waitMinutes", waitMinutes.toString())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Booking with id 1 not found.",errorMsg,"Response Error Message");
	}
	
    @WithMockUser("user")
	@Test
	void getByIdNonexistentPassenger() throws Exception {
		Mockito.when(bookingRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/api/bookings/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Booking with id 1 not found.",errorMsg,"Response Error Message");
	}

}
