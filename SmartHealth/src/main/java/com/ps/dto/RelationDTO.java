package com.ps.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a relationship entity.
 * Used to transfer relation details, such as name.
 */
@Data
public class RelationDTO {

	private Integer id;
	private String name;
}
