package com.ps.dto.response;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing response for appointment status count.
 * Used to count of different type of status of appointment for daily basis to display at dashboard. 
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DailyAppointments {
	
	private String day;
	private int booked;
	private int approved;
	private int completed;
	private int cancelled;
	private int total;

	public DailyAppointments(LocalDate date) {
		this.day = date.toString();
	}
	
	public DailyAppointments(LocalDate date, Long booked, Long approved, Long completed, Long cancelled) {
        this.day = date.toString();
        this.booked = booked != null ? booked.intValue() : 0;
        this.approved = approved != null ? approved.intValue() : 0;
        this.completed = completed != null ? completed.intValue() : 0;
        this.cancelled = cancelled != null ? cancelled.intValue() : 0;
    }
}
