package com.ps.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entity representing a relationship type (e.g., spouse, child) for sub-profiles.
 * Maps to a table with a unique constraint on name.
 */
@Entity
@Data
public class Relation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(unique = true, nullable = false)
	private String name;
}
