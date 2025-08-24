package com.ps.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.AGPreference;
import com.ps.enu.AGMode;

public interface AGPreferenceRepository extends JpaRepository<AGPreference, Integer> {

	List<AGPreference> findByModeInAndIsActive(List<AGMode> mode, boolean isActive);
	
	@Query("SELECT a FROM AGPreference a JOIN a.doctor d WHERE d.email = :email")
	Optional<AGPreference> findByDoctorEmail(@Param("email") String email);
}
