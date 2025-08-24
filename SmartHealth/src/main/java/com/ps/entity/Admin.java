package com.ps.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entity representing an Admin user.
 */
@Data
@Entity
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(unique = true, nullable = false)
	private String phone;
	
	@Column(nullable = false)
	private String password;
	
	private String profilePicPath;
	
	@ElementCollection
	private Set<String> roles;
	
	@Column(nullable = false)
	private Boolean isActive = false;
}
