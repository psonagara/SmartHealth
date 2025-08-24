package com.ps.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entity representing a patient.
 */
@Data
@Entity
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(unique = true, nullable = false)
	private String phone;
	
	private String password;
	private LocalDate dob;
	private String gender;
	private Double height;
	private Integer weight;
	private String profilePicPath;
	private Boolean profileComplete = false;
	private Boolean isActive = true;
	
	@ElementCollection
	private Set<String> roles;

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime creationTime;

	@Column(insertable = false)
	@UpdateTimestamp
	private LocalDateTime updationTime;
}
