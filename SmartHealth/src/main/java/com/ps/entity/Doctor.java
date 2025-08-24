package com.ps.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;

/**
 * Entity representing a doctor.
 * Maps to a table with relationships to Degree, Specialization, Department, and AGPreference entities.
 */
@Data
@Entity
public class Doctor {

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
	
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Degree> degrees;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Specialization> specializations;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<Department> departments;
	
	private Integer yearOfExperience;
	private String profilePicPath;
	private String address;
	private String registrationNumber;
	private Boolean profileComplete = false;
	private Boolean isActive = true;
	
	@OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	private AGPreference agPreference;
	
	@ElementCollection
	private Set<String> roles;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime creationTime;
	
	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updationTime;
}
