package com.ps.enu;

/**
 * Enum representing the status of a doctor's leave request.
 * Used to indicate the current state of a leave in the system.
 */
public enum LeaveStatus {
    BOOKED,     // Leave has been requested
    APPROVED,   // Leave has been approved
    REJECTED    // Leave has been rejected
}
