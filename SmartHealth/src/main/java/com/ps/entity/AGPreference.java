package com.ps.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ps.enu.AGMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity representing the availability generation preferences of a doctor.
 * Maps to the 'ag_preference' table and is associated with a Doctor entity via a one-to-one relationship.
 */
@Data
@Entity
@Table(name = "ag_preference")
public class AGPreference {

	@Id
	@Column(name = "doctor_id")
	private Integer id;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "doctor_id")
	private Doctor doctor;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AGMode mode = AGMode.AUTO;
	
	private Integer daysAhead = 5;
	
	@ManyToMany
	private List<SlotInput> slotInputs;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private LocalDate lastGeneratedOn;
	
	private Boolean isActive = true;
	
	private Boolean skipHoliday = false;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(insertable = false)
	private LocalDateTime updatedAt;
}
