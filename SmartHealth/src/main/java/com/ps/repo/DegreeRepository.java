package com.ps.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Degree;

public interface DegreeRepository extends JpaRepository<Degree, Integer> {

	Optional<Degree> findByName(String name);
}
