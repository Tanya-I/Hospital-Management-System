package appointment;

import enums.DoctorAvailabilityStatus; // Import the enum for availability status
import java.io.*;
import java.util.*;

/**
 * The DoctorAvailabilityService class implements the DoctorAvailabilityManager interface
 * and provides methods to manage the availability of doctors in the hospital management system.
 * It includes functionalities for setting, viewing, and updating doctor availability.
 */
public class DoctorAvailabilityService implements DoctorAvailabilityManager {
    private static final String DOCTOR_AVAILABILITY_FILE = "resources/DoctorAvailability.csv";

    /**
     * Sets the availability of a doctor with specified time slots.
     * Each time slot is marked as "Available" in the availability record.
     *
     * @param doctorID       The unique ID of the doctor
     * @param doctorName     The name of the doctor
     * @param date           The date for which the availability is being set
     * @param availableSlots An array of time slots during which the doctor is available
     */
    @Override
    public void setDoctorAvailability(String doctorID, String doctorName, String date, String[] availableSlots) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOCTOR_AVAILABILITY_FILE, true))) {
            for (String slot : availableSlots) {
                // Use enum for availability status
                String line = String.join(",", doctorID, doctorName, date, slot, DoctorAvailabilityStatus.AVAILABLE.name());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the available slots for a specific doctor on a given date.
     * Only slots marked as "Available" are returned.
     *
     * @param doctorID The unique ID of the doctor
     * @param date     The date for which availability is to be viewed
     * @return An array of formatted strings representing the available slots for the doctor
     */
    @Override
    public String[] viewDoctorAvailability(String doctorID, String date) {
        List<String> availableSlots = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DOCTOR_AVAILABILITY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Assuming data structure: doctorID, doctorName, date, slot, status
                if (data[0].equals(doctorID) && data[2].equals(date) && data[4].equals(DoctorAvailabilityStatus.AVAILABLE.name())) {
                    String formattedSlot = String.format("Doctor: %s, Date: %s, Time Slot: %s", data[1], data[2], data[3]);
                    availableSlots.add(formattedSlot);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableSlots.toArray(new String[0]);
    }

    /**
     * Updates the availability status of a specific time slot for a doctor.
     * The time slot is marked as "Available" or "Booked" based on the provided status.
     *
     * @param doctorID    The unique ID of the doctor
     * @param doctorName  The name of the doctor
     * @param date        The date of the time slot
     * @param timeSlot    The specific time slot to be updated
     * @param isAvailable A boolean indicating whether the time slot is available (true) or booked (false)
     */
    public void updateDoctorAvailability(String doctorID, String doctorName, String date, String timeSlot, boolean isAvailable) {
        List<String[]> availabilityData = new ArrayList<>();

        // Load current availability data
        try (BufferedReader reader = new BufferedReader(new FileReader(DOCTOR_AVAILABILITY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(doctorID) && data[2].equals(date) && data[3].equals(timeSlot)) {
                    // Use enum for status based on the boolean isAvailable
                    data[4] = isAvailable ? DoctorAvailabilityStatus.AVAILABLE.name() : DoctorAvailabilityStatus.BOOKED.name();
                }
                availabilityData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save the updated availability data back to DoctorAvailability.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOCTOR_AVAILABILITY_FILE))) {
            for (String[] data : availabilityData) {
                writer.write(String.join(",", data));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}