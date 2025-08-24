package com.ps.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Degree;

public interface DegreeRepository extends JpaRepository<Degree, Integer> {

}
