package com.ps.enu;

/**
 * Enum representing the mode of availability generation for a doctor.
 * Used to define how availability slots are created in the system.
 */
public enum AGMode {
    AUTO,                   // Automatic generating slots
    CUSTOM_ONE_TIME,        // One-time custom slot generation
    CUSTOM_CONTINUOUS,      // Continuous custom slot generation
    MANUAL,                 // Manual slots generation
    SCHEDULED               // Scheduled slot generation (Case of Automatic and Custom Continuous)
}