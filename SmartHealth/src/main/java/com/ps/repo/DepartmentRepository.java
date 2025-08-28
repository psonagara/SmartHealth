package com.ps.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

	Optional<Department> findByName(String name);
}
