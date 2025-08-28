package com.ps.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {

	Optional<Specialization> findByName(String name);
}
