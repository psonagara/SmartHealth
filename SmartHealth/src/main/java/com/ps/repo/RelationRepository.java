package com.ps.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Relation;

public interface RelationRepository extends JpaRepository<Relation, Integer> {

	Optional<Relation> findByName(String name);
}
