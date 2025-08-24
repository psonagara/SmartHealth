package com.ps.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.Availability;
import com.ps.entity.Doctor;
import com.ps.enu.SlotStatus;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

	boolean existsByDoctorAndDateAndStartTimeAndEndTime(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime);
	
	@Query("""
			SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
			FROM Availability a
			WHERE a.doctor.id = :doctorId
			  AND a.date = :date
			  AND (:startTime < a.endTime AND :endTime > a.startTime)
	""")
	boolean hasOverlappingSlot(@Param("doctorId") Integer doctorId, @Param("date") LocalDate date, 
							   @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);
	
	
	@Query("""
			SELECT a 
			FROM Availability a
			WHERE a.doctor.email = :email
			AND (:startDate IS NULL OR a.date >= :startDate)
			AND (:endDate IS NULL OR a.date <= :endDate)
			""")
	Page<Availability> fetchAvailabilitySlotsByDateRange(@Param("email") String email, @Param("startDate") LocalDate startDate, 
														 @Param("endDate") LocalDate endDate, Pageable pageable);
	
	@Query("""
			DELETE FROM Availability a
			WHERE a.doctor.email = :email
			  AND a.id = :id
			  AND a.status IN (:status)
			""")
	@Modifying
	int deleteByIdAndEmailAndStatus(@Param("id") Integer id, @Param("email") String email, @Param("status") List<SlotStatus> status);
	
	@Query("""
			DELETE FROM Availability a
			WHERE a.doctor.email = :email
			  AND a.date >= :startDate
			  AND a.date <= :endDate
			  AND (:startTime IS NULL OR a.startTime >= :startTime)
			  AND (:endTime IS NULL OR a.endTime <= :endTime)
			  AND a.status IN (:status)
			""")
	@Modifying
	int deleteByDateAndTimeRange(@Param("email") String email, @Param("startDate") LocalDate startDate,
								 @Param("endDate") LocalDate endDate, @Param("startTime") LocalTime startTime,
								 @Param("endTime") LocalTime endTime, @Param("status") List<SlotStatus> status);
	
	@Query("""
			SELECT a FROM Availability a
			WHERE a.doctor.id = :id 
			  AND a.status IN ('AVAILABLE', 'RE_AVAILABLE')
			  AND (
				  		(:date IS NULL AND ((a.date > :today) OR (a.date = :today AND a.endTime > :time)))
				   	 OR (:date < :today AND ((a.date > :today) OR (a.date = :today AND a.endTime > :time)))
				   	 OR (:date = :today AND a.endTime > :time)
				   	 OR (:date > :today AND a.date = :date)
			   	  )
			""")
	List<Availability> viewSlots(@Param("id") Integer doctorId, @Param("date") LocalDate date, 
								 @Param("today") LocalDate today, @Param("time") LocalTime time);
	
	@Query("SELECT a FROM Availability a WHERE a.id = :id AND a.status IN ('AVAILABLE', 'RE_AVAILABLE')")
	Optional<Availability> fetchByIdAndStatusIn(@Param("id") Integer id);
	
	@Modifying
	@Query("UPDATE Availability a SET a.status = :status, a.updatedAt = :updatedAt WHERE a.id = :id")
	int updateStatusById(@Param("status") SlotStatus status, @Param("id") Integer id, @Param("updatedAt") LocalDateTime updatedAt);

	@Modifying
	@Query("UPDATE Availability a SET a.status = :status, a.updatedAt = :updatedAt WHERE a.id IN :ids")
	int updateStatusByIds(@Param("status") SlotStatus status, @Param("ids") List<Integer> ids, @Param("updatedAt") LocalDateTime updatedAt);
	
	long count();
	
	long countByStatus(SlotStatus status);
	
	@Query("""
			SELECT a FROM Availability a
		    WHERE (:id IS NULL OR a.doctor.id = :id)
		      AND (:date IS NULL OR a.date = :date)
		      AND (:status IS NULL OR a.status = :status)
			""")
	Page<Availability> searchAvailability(@Param("id") Integer id, @Param("date") LocalDate date, @Param("status") SlotStatus status,
			Pageable pageable);
	
	@Modifying
	@Query("DELETE FROM Availability a WHERE a.id = :id AND a.status = 'AVAILABLE'")
	int deleteByIdAndStatus(@Param("id") Integer id);
	
	List<Availability> findByDoctorIdAndDateBetween(Integer id, LocalDate from, LocalDate to);
	
}
