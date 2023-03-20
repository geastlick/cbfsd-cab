package com.example.ProjectBoot.testcontroller.api;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
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

import com.example.ProjectBoot.entity.Cab;
import com.example.ProjectBoot.entity.Driver;
import com.example.ProjectBoot.repository.CabRepository;
import com.example.ProjectBoot.repository.DriverRepository;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class DriverControllerTest {

	@MockBean
	private DriverRepository driverRepo;
	
	@MockBean
	private CabRepository cabRepo;
	
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

	//private static final Gson gson = new Gson();

	@Test
    @WithMockUser(username="user", password="password", roles="USER")
	void getByIdNonexistentDriver() throws Exception {
		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/api/drivers/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Driver with Id 1 not found.",errorMsg,"Response Error Message");
	}
	
	@Test
    @WithMockUser(username="user", password="password", roles="USER")
	void removeCabInvalidId() throws Exception {
		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/drivers/1/cab/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Driver with Id 1 not found.",errorMsg,"Response Error Message");
	}
	
    @WithMockUser("user")
	@Test
	void updateCabInvalidId() throws Exception {
		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/drivers/1/cab/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Driver with Id 1 not found.",errorMsg,"Response Error Message");
	}
	
    @WithMockUser("user")
	@Test
	void updateCabInvalidCab() throws Exception {
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setLicense("TX1234");
		driver.setName("Tim Jordan");
		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.of(driver));
		Mockito.when(cabRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/drivers/1/cab/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Cab is invalid, cannot be assigned to driver.",errorMsg,"Invalid Cab");
	}
	
    @WithMockUser("user")
	@Test
	void updateCabInactiveCab() throws Exception {
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setLicense("TX1234");
		driver.setName("Tim Jordan");
		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.of(driver));
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("X11111");
		cab.setInService(false);
		Mockito.when(cabRepo.findById(1L)).thenReturn(Optional.of(cab));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/drivers/1/cab/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Cab is not in service, cannot be assigned to driver.",errorMsg,"Cab not in service");
	}
	
    @WithMockUser("user")
	@Test
	void updateDriverAlreadyAssignedDifferentCab() throws Exception {
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setLicense("TX1234");
		driver.setName("Tim Jordan");
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("X11111");
		cab.setInService(true);
		driver.setCab(cab);

		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.of(driver));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/drivers/1/cab/2")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Driver cannot be assigned to two cabs",errorMsg,"Error Message");
	}

    @WithMockUser("user")
	@Test
	void updateDriverAlreadyAssignedSameCab() throws Exception {
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setLicense("TX1234");
		driver.setName("Tim Jordan");
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("X11111");
		cab.setInService(true);
		driver.setCab(cab);

		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.of(driver));

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/drivers/1/cab/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		//Driver content = gson.fromJson(response.getContentAsString(), Driver.class);

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_MODIFIED, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Cab is already assigned to driver",errorMsg,"Error Message");
	}

    @WithMockUser("user")
	@Test
	void updateCabAssignedAnotherDriver() throws Exception {
		Driver driver = new Driver();
		driver.setId(1L);
		driver.setLicense("TX1234");
		driver.setName("Tim Jordan");
		
		Cab cab = new Cab();
		cab.setId(1L);
		cab.setCabNumber("X11111");
		cab.setInService(true);

		Mockito.when(driverRepo.findById(1L)).thenReturn(Optional.of(driver));
		Mockito.when(cabRepo.findById(1L)).thenReturn(Optional.of(cab));

		DataIntegrityViolationException dataIntegrityViolationException = Mockito.mock(DataIntegrityViolationException.class);
	    SQLIntegrityConstraintViolationException violationException = Mockito.mock(SQLIntegrityConstraintViolationException.class);
	    Mockito.when(violationException.getMessage()).thenReturn("constraint [driver_uk1]");
	    Mockito.when(dataIntegrityViolationException.getRootCause()).thenReturn(violationException);
	    Mockito.when(driverRepo.save(Mockito.any(Driver.class))).thenThrow(dataIntegrityViolationException);
	    
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/drivers/1/cab/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.BAD_REQUEST, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Cab is assigned to another driver.",errorMsg, "Error Message");
	}
}
