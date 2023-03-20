package com.example.ProjectBoot.testcontroller.api;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.ProjectBoot.repository.CabRepository;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CabControllerTest {

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

	@Test
    @WithMockUser("user")
	void getByInvalidId() throws Exception {
		Mockito.when(cabRepo.findById(1L)).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/api/cabs/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");
		
		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Cab with Id 1 not found.",errorMsg,"Response Error Message");
	}
	
	@Test
    @WithMockUser("user")
	void getByInvalidCabNumber() throws Exception {
		Mockito.when(cabRepo.findByCabNumber("TX1234")).thenReturn(Optional.empty());

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/api/cabs/cab-number/TX1234")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		DocumentContext jsonContext = JsonPath.parse(response.getContentAsString());
		String errorMsg = jsonContext.read("$['errorMsg']");

		org.junit.jupiter.api.Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatusCode.valueOf(response.getStatus()),"HTTP Response Status");
		org.junit.jupiter.api.Assertions.assertEquals("Cab with Number TX1234 not found.",errorMsg,"Response Error Message");
	}
	
}
