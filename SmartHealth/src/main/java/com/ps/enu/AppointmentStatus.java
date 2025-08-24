package com.ps.enu;

/**
 * Enum representing the status of an appointment.
 * Used to indicate the current state of a patient's appointment in the system.
 */
public enum AppointmentStatus {
    BOOKED,         // Appointment has been booked
    APPROVED,       // Appointment has been approved
    REJECTED,       // Appointment has been rejected
    COMPLETED,      // Appointment has been completed
    P_CANCELLED,    // Appointment cancelled by patient
    D_CANCELLED     // Appointment cancelled by doctor
}