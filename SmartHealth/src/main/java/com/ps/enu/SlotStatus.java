package com.ps.enu;

/**
 * Enum representing the status of an availability slot.
 * Used to indicate the current state of a doctor's slot in the system.
 */
public enum SlotStatus {
    AVAILABLE,      // Slot is open for booking
    RE_AVAILABLE,   // Slot has been re-opened (e.g., after cancellation by Patient)
    BOOKED,         // Slot is booked by a patient
    CANCELLED       // Slot has been cancelled
}
