package com.ps.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
