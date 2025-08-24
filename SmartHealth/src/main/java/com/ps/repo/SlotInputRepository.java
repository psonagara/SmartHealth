package com.ps.repo;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.SlotInput;

public interface SlotInputRepository extends JpaRepository<SlotInput, Integer> {

	Optional<SlotInput> findByStartTimeAndEndTimeAndGapInMinutes(LocalTime startTime, LocalTime endTime, Integer gapInMinutes);
}
