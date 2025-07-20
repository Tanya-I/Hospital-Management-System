package enums;

/**
 * The AppointmentStatus enum represents the various statuses an appointment can have
 * in the hospital management system.
 */
public enum AppointmentStatus {
    /**
     * Indicates that the appointment has been confirmed.
     */
    CONFIRMED,

    /**
     * Indicates that the appointment has been completed.
     */
    COMPLETED,

    /**
     * Indicates that the appointment has been cancelled.
     */
    CANCELLED,

    /**
     * Indicates that the appointment is pending and yet to be confirmed.
     */
    PENDING
}
