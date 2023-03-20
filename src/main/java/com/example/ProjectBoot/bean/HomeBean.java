package com.example.ProjectBoot.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HomeBean {
	private final static String getBookingsURL = "http://localhost:8080/api/bookings/last24";
	
	public HomeBean() {
	}

	public String getBookings(String media) {
		
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		  
		  switch(media) {
			case "XML":
				mediaTypes.add(MediaType.APPLICATION_XML);
				break;
			case "HTML":
				mediaTypes.add(MediaType.TEXT_HTML);
				break;
			case "JSON":
				mediaTypes.add(MediaType.APPLICATION_JSON);
				break;
			default:
				mediaTypes.add(MediaType.TEXT_PLAIN);
				break;
		}

	  RestTemplate restTemplate = new RestTemplate();
	  HttpHeaders headers = new HttpHeaders();
	  headers.setAccept(mediaTypes);
	  HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
	  ResponseEntity<String> response = restTemplate.exchange(getBookingsURL, HttpMethod.GET, httpEntity, String.class);

	return response.getBody();
	}
	
}
