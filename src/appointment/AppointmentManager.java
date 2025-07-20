package appointment;

/**
 * The AppointmentManager interface defines the methods required for managing appointments
 * in the hospital management system. It includes functionalities for scheduling,
 * rescheduling, canceling, and viewing appointment statuses.
 */
public interface AppointmentManager {

    /**
     * Schedules a new appointment for a patient.
     *
     * @param patientID The ID of the patient requesting the appointment
     * @return The ID of the scheduled appointment, or null if scheduling fails
     */
    String scheduleAppointment(String patientID);

    /**
     * Reschedules an existing appointment.
     *
     * @param appointmentID The ID of the appointment to be rescheduled
     */
    void rescheduleAppointment(String appointmentID);

    /**
     * Cancels an existing appointment.
     *
     * @param appointmentID The ID of the appointment to be canceled
     */
    void cancelAppointment(String appointmentID);

    /**
     * Views the status of a specific appointment.
     *
     * @param appointmentID The ID of the appointment whose status is to be viewed
     * @return The current status of the appointment
     */
    String viewAppointmentStatus(String appointmentID);
}
