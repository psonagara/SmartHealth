package com.ps.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ps.enu.LeaveStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * Entity representing a doctor's leave.
 * Maps to a table with relationships to the Doctor entity and leave details.
 */
@Entity
@Data
public class DoctorLeave {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Doctor doctor;
	
	@Column(name = "from_date", nullable = false)
	private LocalDate from;

	@Column(name = "to_date", nullable = false)
	private LocalDate to;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LeaveStatus status = LeaveStatus.BOOKED;
	
	@Column(nullable = false)
	private Integer days;
	
	@Column(length = 500)
	private String reason;
	
	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime creationTime;

	@Column(insertable = false)
	@UpdateTimestamp
	private LocalDateTime updationTime;
}
