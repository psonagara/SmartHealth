package com.ps.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.DoctorLeave;
import com.ps.enu.LeaveStatus;

public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Integer> {

	@Query("""
			SELECT l FROM DoctorLeave l
			WHERE l.doctor.email = :email
			  AND (:from IS NULL OR l.from >= :from)
			  AND (:to IS NULL OR l.to <= :to)
			  AND (:status IS NULL OR l.status = :status)
			""")
	Page<DoctorLeave> searchLeaves(@Param("email") String email, @Param("from") LocalDate from, @Param("to") LocalDate to,
			@Param("status") LeaveStatus status, Pageable pageable);
	
	@Query("""
			SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
			FROM DoctorLeave l 
			WHERE l.doctor.email = :email 
			AND ((l.from = :from AND l.to = :to) OR (:from <= l.to AND :to >= l.from))
			AND l.status IN ('BOOKED', 'APPROVED')
			 """)
	boolean hasOverlappingSlot(@Param("email") String email, @Param("from") LocalDate from, @Param("to") LocalDate to);
	
	@Query("""
			SELECT l FROM DoctorLeave l
			WHERE (:id IS NULL OR l.doctor.id = :id)
			  AND (:from IS NULL OR l.from >= :from)
			  AND (:to IS NULL OR l.to <= :to)
			  AND (:status IS NULL OR l.status = :status)
			""")
	Page<DoctorLeave> searchLeavesByAdmin(@Param("id") Integer id, @Param("from") LocalDate from, @Param("to") LocalDate to,
			@Param("status") LeaveStatus status, Pageable pageable);
	
	@Modifying
	@Query("UPDATE DoctorLeave l SET l.status = :status, l.updationTime = :updatedAt WHERE l.id = :id")
	int changeLeaveStatus(@Param("id") Integer id, @Param("updatedAt") LocalDateTime updatedAt,  @Param("status") LeaveStatus status);
	
	@Query("""
			SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
			FROM DoctorLeave l
			WHERE l.doctor.email = :email
			  AND :date >= l.from 
			  AND :date <= l.to
			  AND l.status = 'APPROVED'
			""")
	boolean existsByDoctorAndDay(@Param("email") String email, @Param("date") LocalDate date);
	
	List<DoctorLeave> findTop10ByFromGreaterThanEqualAndStatusOrderByFromAsc(LocalDate from, LeaveStatus status);
}
