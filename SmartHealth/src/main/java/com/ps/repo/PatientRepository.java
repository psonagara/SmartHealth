package com.ps.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

	List<Patient> findByEmailOrPhone(String email, String phone);

	Optional<Patient> findByEmail(String email);
	
	@Query("SELECT p.id FROM Patient p WHERE p.email = :email")
	Optional<Integer> findIdByEmail(@Param("email") String email);

	@Query("UPDATE Patient p SET p.password=:password WHERE p.email=:email")
	@Modifying
	int updatePassword(@Param("password") String password, @Param("email") String email);

	@Query("UPDATE Patient p SET p.profilePicPath=:profilePicPath WHERE p.email=:email")
	@Modifying
	int updateprofilePicPath(@Param("profilePicPath") String profilePicPath, @Param("email") String email);

	@Query("SELECT p.profilePicPath FROM Patient p WHERE p.email=:email")
	String getProfilePicName(@Param("email") String email);

	long count();

	long countByProfileCompleteFalse();

	long countByIsActiveTrue();

	@Query("""
			SELECT p FROM Patient p
			WHERE (:id IS NULL OR p.id = :id)
			  AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
			  AND (:email IS NULL OR LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%')))
			  AND (:phone IS NULL OR p.phone LIKE CONCAT('%', :phone, '%'))
			  AND (:gender IS NULL OR p.gender = :gender)
			  AND (:profileComplete IS NULL OR p.profileComplete = :profileComplete)
			  AND (:isActive IS NULL OR p.isActive = :isActive)
			""")
	Page<Patient> searchPatients(@Param("id") Integer id, @Param("name") String name, @Param("email") String email, @Param("phone") String phone, 
			@Param("gender") String gender, @Param("profileComplete") Boolean profileComplete, @Param("isActive") Boolean isActive, 
			Pageable pageable);

	@Modifying
	@Query("UPDATE Patient p SET p.isActive = :isActive WHERE p.id = :id")
	int toggleStatus(@Param("id") Integer id, @Param("isActive") Boolean isActive);
	
	public interface PatientIdNameProjection {
	    Integer getId();
	    String getName();
	}
	
	List<PatientIdNameProjection> findAllProjectedBy();
}
