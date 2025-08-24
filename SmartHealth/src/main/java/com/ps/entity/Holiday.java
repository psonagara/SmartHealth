package com.ps.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entity representing a holiday.
 * Maps to a table storing holiday dates and reasons with a creation time stamp.
 */
@Entity
@Data
public class Holiday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, unique = true)
	private LocalDate holidayDate;
	
	@Column(nullable = false)
	private String reason;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime creationTime;
}
