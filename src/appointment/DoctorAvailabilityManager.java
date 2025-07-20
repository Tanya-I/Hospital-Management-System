package appointment;

/**
 * The DoctorAvailabilityManager interface defines the methods required for managing
 * doctor availability in the hospital management system. It includes functionalities
 * for setting and viewing availability for specific doctors.
 */
public interface DoctorAvailabilityManager {

    /**
     * Sets the availability of a doctor for a specific date.
     *
     * @param doctorID       The unique ID of the doctor
     * @param doctorName     The name of the doctor
     * @param date           The date for which the availability is being set
     * @param availableSlots An array of time slots during which the doctor is available
     */
    void setDoctorAvailability(String doctorID, String doctorName, String date, String[] availableSlots);

    /**
     * Retrieves the availability of a doctor for a specific date.
     *
     * @param doctorID The unique ID of the doctor
     * @param date     The date for which availability is to be viewed
     * @return An array of available time slots for the doctor on the specified date
     */
    String[] viewDoctorAvailability(String doctorID, String date);
}
