package appointment;

import enums.AppointmentStatus;
import java.io.*;
import java.util.*;

/**
 * The AppointmentRecordService class provides functionality to manage appointment outcome
 * records and update the status of appointments in the hospital management system.
 */
public class AppointmentRecordService {
    private static final String APPOINTMENT_RECORD_FILE = "resources/AppointmentRecord.csv";

    /**
     * Adds an appointment outcome record to the system and updates the appointment status
     * to "completed" in the Appointment.csv file.
     *
     * @param appointmentID        The unique ID of the appointment
     * @param diagnosis            The diagnosis made during the appointment
     * @param prescriptionMedicine The prescribed medicine
     * @param prescriptionQuantity The quantity of the prescribed medicine
     * @param treatmentPlan        The treatment plan decided during the appointment
     * @param date                 The date of the appointment
     * @param typeOfService        The type of service provided during the appointment
     * @param consultationNotes    Additional consultation notes
     */
    public void addAppointmentOutcomeRecord(
            String appointmentID,
            String diagnosis,
            String prescriptionMedicine,
            int prescriptionQuantity,
            String treatmentPlan,
            String date,
            String typeOfService,
            String consultationNotes) {

        // Step 1: Add the outcome record to AppointmentRecord.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENT_RECORD_FILE, true))) {
            String line = String.join(",",
                    appointmentID,
                    diagnosis,
                    prescriptionMedicine,
                    String.valueOf(prescriptionQuantity),
                    AppointmentStatus.PENDING.name(), // Prescription status is initially set to "pending"
                    treatmentPlan,
                    date,
                    typeOfService,
                    consultationNotes
            );
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 2: Update the status in Appointment.csv to "completed" for the given AppointmentID
        List<String[]> appointments = loadAppointments();

        for (String[] appointment : appointments) {
            if (appointment[0].equals(appointmentID)) {
                appointment[5] = AppointmentStatus.COMPLETED.name(); // Update the Status column (index 5) to "completed"
                break;
            }
        }

        // Step 3: Save the updated appointments back to Appointment.csv
        saveAppointments(appointments);
    }

    /**
     * Loads all appointments from the Appointment.csv file.
     *
     * @return A list of string arrays, where each array represents an appointment record
     */
    private List<String[]> loadAppointments() {
        List<String[]> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Appointment.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                appointments.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Saves the updated appointments back to the Appointment.csv file.
     *
     * @param appointments The list of updated appointment records
     */
    private void saveAppointments(List<String[]> appointments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/Appointment.csv"))) {
            for (String[] appointment : appointments) {
                writer.write(String.join(",", appointment));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}