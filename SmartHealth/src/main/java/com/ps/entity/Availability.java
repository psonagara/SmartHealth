package com.ps.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ps.enu.AGMode;
import com.ps.enu.SlotStatus;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * Entity representing an availability slot for a doctor.
 */
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"doctor_id", "date", "start_time", "end_time"})})
public class Availability {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;
	
	@Column(nullable = false)
	private LocalDate date;
	
	@Column(nullable = false)
	private LocalTime startTime;
	
	@Column(nullable = false)
	private LocalTime endTime;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SlotStatus status = SlotStatus.AVAILABLE;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AGMode mode;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;
}
