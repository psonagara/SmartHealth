package com.ps.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Doctor;
import com.ps.entity.Specialization;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

	List<Doctor> findByEmailOrPhone(String email, String phone);

	Optional<Doctor> findByEmail(String email);
	
	@Query("SELECT d.id FROM Doctor d WHERE d.email = :email")
	Optional<Integer> findIdByEmail(@Param("email") String email);

	@Query("UPDATE Doctor d SET d.profilePicPath=:profilePicPath WHERE d.email=:email")
	@Modifying
	int updateprofilePicPath(@Param("profilePicPath") String profilePicPath, @Param("email") String email);

	@Query("SELECT d.profilePicPath FROM Doctor d WHERE d.email=:email")
	String getProfilePicName(@Param("email") String email);

	@Query("UPDATE Doctor d SET d.password=:password WHERE d.email=:email")
	@Modifying
	int updatePassword(@Param("password") String password, @Param("email") String email);

	@Query("""
			SELECT DISTINCT d FROM Doctor d
			JOIN Availability a ON a.doctor = d
			WHERE d.isActive = true
			  AND d.profileComplete = true
			  AND a.status = 'AVAILABLE'
			  AND a.date >= :today
			  AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
			  AND (:department IS NULL OR :department MEMBER OF d.departments)
			  AND (:specialization IS NULL OR :specialization MEMBER OF d.specializations)
			  AND (:degree IS NULL OR :degree MEMBER OF d.degrees)
			  AND (:date IS NULL OR a.date = :date)
			""")
	Page<Doctor> searchDoctorsWithSlots(@Param("name") String name, @Param("department") Department department,
   							   @Param("specialization") Specialization specialization, @Param("degree") Degree degree,
   							   @Param("date") LocalDate date, @Param("today") LocalDate today, Pageable pageable);

	long count();

	long countByProfileCompleteFalse();

	long countByIsActiveTrue();

	@Query("""
			SELECT d FROM Doctor d
			WHERE (:id IS NULL OR d.id = :id)
			  AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
			  AND (:email IS NULL OR LOWER(d.email) LIKE LOWER(CONCAT('%', :email, '%')))
			  AND (:phone IS NULL OR d.phone LIKE CONCAT('%', :phone, '%'))
			  AND (:gender IS NULL OR d.gender = :gender)
			  AND (:degree IS NULL OR :degree MEMBER OF d.degrees)
			  AND (:specialization IS NULL OR :specialization MEMBER OF d.specializations)
			  AND (:department IS NULL OR :department MEMBER OF d.departments)
			  AND (:yearOfExperience IS NULL OR d.yearOfExperience = :yearOfExperience)
			  AND (:registrationNumber IS NULL OR d.registrationNumber LIKE CONCAT('%', :registrationNumber, '%'))
			  AND (:profileComplete IS NULL OR d.profileComplete = :profileComplete)
			  AND (:isActive IS NULL OR d.isActive = :isActive)
			""")
	Page<Doctor> searchDoctors(@Param("id") Integer id, @Param("name") String name, @Param("email") String email, @Param("phone") String phone, 
			@Param("gender") String gender, @Param("degree") Degree degree, @Param("specialization") Specialization specialization, 
			@Param("department") Department department, @Param("yearOfExperience") Integer yearOfExperience,
			@Param("registrationNumber") String registrationNumber, @Param("profileComplete") Boolean profileComplete, 
			@Param("isActive") Boolean isActive, Pageable pageable);
	
	@Modifying
	@Query("UPDATE Doctor d SET d.isActive = :isActive WHERE d.id = :id")
	int toggleStatus(@Param("id") Integer id, @Param("isActive") boolean isActive);
	
	public interface DoctorIdNameProjection {
	    Integer getId();
	    String getName();
	}
	
	List<DoctorIdNameProjection> findAllProjectedBy();
}
