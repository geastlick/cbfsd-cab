package com.example.ProjectBoot.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonRootName("Booking")
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String customerName;
	private String customerPhone;
	private String customerEmail;
	
	private String pickupLocation;
	
	private Timestamp callTime;
	private Timestamp pickupTime;
	private Timestamp dropoffTime;
	
	private Integer passengers;
	private Double miles;
	private Double waitMinutes;
	private Double fare;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="driver_id", nullable=false)
	private Driver driver;
}
