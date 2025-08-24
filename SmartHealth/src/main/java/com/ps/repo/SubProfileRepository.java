package com.ps.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.SubProfile;

public interface SubProfileRepository extends JpaRepository<SubProfile, Integer> {

	List<SubProfile> findByPatientId(Integer id);
}
