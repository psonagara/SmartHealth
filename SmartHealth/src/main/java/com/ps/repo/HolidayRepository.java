package com.ps.repo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

	boolean existsByHolidayDate(LocalDate holidayDate);
}
