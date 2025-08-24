package com.ps.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.dto.response.DailyAppointments;
import com.ps.entity.Appointment;
import com.ps.enu.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	@Modifying
	@Query("UPDATE Appointment a SET a.status = :status, a.updatedAt = :updatedAt WHERE a.availability.id = :id AND a.status IN :statuses")
	int updateStatusByAvailabilityId(@Param("status") AppointmentStatus status, @Param("id") Integer id, 
						 @Param("updatedAt") LocalDateTime updatedAt, @Param("statuses") List<AppointmentStatus> statuses);
	
	@Modifying
	@Query("UPDATE Appointment a SET a.status = :status, a.updatedAt = :updatedAt WHERE a.availability.id IN :aids AND a.status IN :statuses")
	int updateStatusByAvailabilityIds(@Param("status") AppointmentStatus status, @Param("aids") List<Integer> ids,
	                                  @Param("updatedAt") LocalDateTime updatedAt, @Param("statuses") List<AppointmentStatus> statuses);

	@Query("SELECT a FROM Appointment a WHERE a.availability.id = :id AND (:appointmentId IS NULL OR a.id = :appointmentId)")
	List<Appointment> fetchByAvailabilityIdAndAppointmentId(@Param("id") Integer id, @Param("appointmentId") Integer appointmentId);
	
	Optional<Appointment> findTopByAvailabilityIdOrderByIdDesc(Integer id);
	
	@Query("""
			SELECT a FROM Appointment a
			WHERE a.patient.email = :email
			  AND (:status IS NULL OR a.status = :status)
			  AND (:date IS NULL OR a.availability.date = :date)
			  AND (:name IS NULL OR LOWER(a.availability.doctor.name) LIKE LOWER(CONCAT('%', :name, '%')))
			""")
	Page<Appointment> filterAppointmentsForPatient(@Param("email") String email, @Param("name") String name,
										 @Param("date") LocalDate date, @Param("status") AppointmentStatus status, 
										 Pageable pageable);
	
	@Query("""
			SELECT a FROM Appointment a
			WHERE a.availability.doctor.email = :email
			  AND (:status IS NULL OR a.status = :status)
			  AND (:date IS NULL OR a.availability.date = :date)
			  AND (:name IS NULL OR LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :name, '%')))
			  AND (:slotId IS NULL OR a.availability.id = :slotId)
			""")
	Page<Appointment> filterAppointemtnsForDoctor(@Param("email") String email, @Param("name") String name,
										 @Param("date") LocalDate date, @Param("status") AppointmentStatus status, 
										 @Param("slotId") Integer slotId, Pageable pageable);
	
	@Modifying
	@Query("UPDATE Appointment a SET a.status = :status, a.updatedAt = :updatedAt WHERE a.id = :id")
	int updateStatusByAppId(@Param("status") AppointmentStatus status, @Param("id") Integer id, @Param("updatedAt") LocalDateTime updatedAt);

	long count();
	
	long countByStatus(AppointmentStatus status);
	
	@Query("""
			SELECT a FROM Appointment a
		    WHERE (:doctorId IS NULL OR a.availability.doctor.id = :doctorId)
		      AND (:patientId IS NULL OR a.patient.id = :patientId)
		      AND (:date IS NULL OR a.availability.date = :date)
		      AND (:status IS NULL OR a.status = :status)
			""")
	Page<Appointment> searchAppointments(@Param("doctorId") Integer doctorId, @Param("patientId") Integer patientId,
			@Param("date") LocalDate date, @Param("status") AppointmentStatus status, Pageable pageable);
	
	long countByAvailabilityDoctorEmailAndAvailabilityDate(String email, LocalDate date);
	
	long countByAvailabilityDoctorEmailAndAvailabilityDateGreaterThanEqual(String email, LocalDate date);

	long countByAvailabilityDoctorEmailAndAvailabilityDateGreaterThanEqualAndStatus(String email, LocalDate date, AppointmentStatus status);

	long countByAvailabilityDoctorEmailAndAvailabilityDateAndStatusIn(String email, LocalDate date, List<AppointmentStatus> status);
	
	List<Appointment> findByAvailabilityDoctorEmailAndAvailabilityDate(String email, LocalDate date);
	
	@Query("""
		    SELECT new com.ps.dto.response.DailyAppointments(
		        a.availability.date,
		        SUM(CASE WHEN a.status = 'BOOKED' THEN 1 ELSE 0 END),
		        SUM(CASE WHEN a.status = 'APPROVED' THEN 1 ELSE 0 END),
		        SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END),
		        SUM(CASE WHEN a.status = 'D_CANCELLED' OR a.status = 'P_CANCELLED' THEN 1 ELSE 0 END)
		    )
		    FROM Appointment a
		    WHERE a.availability.doctor.email = :email
		      AND a.availability.date >= :startDate
		      AND a.availability.date <= :today
		    GROUP BY a.availability.date
		    ORDER BY a.availability.date ASC
		""")
	List<DailyAppointments> findAppointmentsTrendsByDate(@Param("email") String email, @Param("startDate") LocalDate startDate, @Param("today") LocalDate today);

	long countByPatientEmailAndAvailabilityDateGreaterThanEqual(String email, LocalDate date);
	
	long countByPatientEmailAndStatus(String email, AppointmentStatus status);

	long countByPatientEmailAndStatusIn(String email, List<AppointmentStatus> status);
	
	List<Appointment> findByPatientEmailAndAvailabilityDateGreaterThanEqual(String email, LocalDate date);
	
	@Query("""
		    SELECT new com.ps.dto.response.DailyAppointments(
		        a.availability.date,
		        SUM(CASE WHEN a.status = 'BOOKED' THEN 1 ELSE 0 END),
		        SUM(CASE WHEN a.status = 'APPROVED' THEN 1 ELSE 0 END),
		        SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END),
		        SUM(CASE WHEN a.status = 'D_CANCELLED' OR a.status = 'P_CANCELLED' THEN 1 ELSE 0 END)
		    )
		    FROM Appointment a
		    WHERE a.availability.date >= :startDate
		      AND a.availability.date <= :today
		    GROUP BY a.availability.date
		    ORDER BY a.availability.date ASC
		""")
	List<DailyAppointments> findAppointmentsTrendsByDate(@Param("startDate") LocalDate startDate, @Param("today") LocalDate today);
	
	@Query("""
			SELECT a.availability.date, COUNT(a)
	        FROM Appointment a
	        WHERE a.availability.date BETWEEN :startDate AND :endDate
	        GROUP BY DATE(a.availability.date)
	        """)
	List<Object[]> countAppointmentsByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	List<Appointment> findByAvailabilityDateOrderByAvailabilityStartTimeAsc(LocalDate date);
}
