package com.ps.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ps.enu.AppointmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * Entity representing an appointment booked by a patient.
 * Maps to a table with relationships to Patient, SubProfile, and Availability entities.
 */
@Entity
@Data
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Patient patient;

	@ManyToOne
	private SubProfile subProfile;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Availability availability;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AppointmentStatus status = AppointmentStatus.BOOKED;
	
	@Column(length = 500)
	private String note;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;
}
