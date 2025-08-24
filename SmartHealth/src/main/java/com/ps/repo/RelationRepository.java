package com.ps.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Relation;

public interface RelationRepository extends JpaRepository<Relation, Integer> {

}
