package com.ps.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/**
 * Entity representing a slot input configuration for availability generation.
 * Maps to a table with required start time, end time, and gap in minutes.
 */
@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class SlotInput {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NonNull
	private LocalTime startTime;
	
	@NonNull
	private LocalTime endTime;
	
	@NonNull
	private Integer gapInMinutes;
}
