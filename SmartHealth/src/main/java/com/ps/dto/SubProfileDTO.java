package com.ps.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a sub-profile entity.
 * Used to transfer sub-profile details, including name, relation, and phone.
 */
@Data
public class SubProfileDTO {

	private Integer id;
	private String name;
	private RelationDTO relation;
	private String phone;
}
