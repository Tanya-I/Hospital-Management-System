package usermain;

import enums.AppointmentStatus;
import appointment.AppointmentManager;
import appointment.DoctorAvailabilityManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Doctor class represents a doctor in the hospital management system.
 * It provides functionalities to manage appointments, update patient records,
 * set availability, and view schedules and upcoming appointments.
 */
public class Doctor extends User {
    private String doctorID;
    private AppointmentManager appointmentManager;
    private DoctorAvailabilityManager availabilityManager;

    private static final String APPOINTMENT_FILE = "resources/Appointment.csv";
    private static final String APPOINTMENT_RECORD_FILE = "resources/AppointmentRecord.csv";

    /**
     * Constructs a new Doctor object with the provided details.
     *
     * @param doctorID           The unique ID of the doctor
     * @param password           The doctor's password
     * @param role               The role of the user (e.g., "Doctor")
     * @param name               The name of the doctor
     * @param appointmentManager The AppointmentManager instance for managing appointments
     * @param availabilityManager The DoctorAvailabilityManager instance for managing availability
     */
    public Doctor(String doctorID, String password, String role, String name, AppointmentManager appointmentManager, DoctorAvailabilityManager availabilityManager) {
        super(doctorID, password, role, name); // Call the User constructor to initialize ID, password, role, and name
        this.doctorID = doctorID;
        this.appointmentManager = appointmentManager;
        this.availabilityManager = availabilityManager;
    }

    /**
     * Gets the doctor's unique ID.
     *
     * @return The doctor ID
     */
    public String getDoctorID(){
        return doctorID;
    }

    /**
     * Updates a patient's medical record for a specific appointment.
     *
     * @param appointmentID         The ID of the appointment
     * @param newDiagnosis          The updated diagnosis
     * @param newPrescription       The updated prescription medicine
     * @param newPrescriptionQuantity The updated prescription quantity
     * @param newTreatmentPlan      The updated treatment plan
     * @param newConsultationNotes  The updated consultation notes
     */
    public void updatePatientMedicalRecord(String appointmentID, String newDiagnosis, String newPrescription, int newPrescriptionQuantity, String newTreatmentPlan, String newConsultationNotes) {
        List<String[]> records = loadAppointmentRecords();
        boolean updated = false;

        for (String[] record : records) {
            if (record[0].equals(appointmentID)) {
                record[1] = newDiagnosis;
                record[2] = newPrescription;
                record[3] = String.valueOf(newPrescriptionQuantity);
                record[5] = newTreatmentPlan;
                record[8] = newConsultationNotes;
                updated = true;
                break;
            }
        }

        if (updated) {
            saveAppointmentRecords(records);
            System.out.println("Patient medical record updated successfully.");

            // Update the past treatment in Patient_List.csv
            updatePatientPastTreatment(appointmentID, newDiagnosis, newTreatmentPlan);
        } else {
            System.out.println("Appointment ID not found.");
        }
    }

    /**
     * Views the doctor's personal schedule for a specific date.
     *
     * @param date The date for which the schedule is to be viewed
     */
    public void viewPersonalSchedule(String date) {
        String[] availableSlots = availabilityManager.viewDoctorAvailability(doctorID, date);
        System.out.println("Available Slots:");
        for (String slot : availableSlots) {
            System.out.println(slot);
        }
    }

    /**
     * Sets the doctor's availability for a specific date.
     *
     * @param date           The date for which availability is to be set
     * @param availableSlots An array of available time slots
     */
    public void setAvailability(String date, String[] availableSlots) {
        availabilityManager.setDoctorAvailability(doctorID, getName(), date, availableSlots);
        System.out.println("Availability set successfully.");
    }

    /**
     * Accepts an appointment by updating its status to "Confirmed."
     *
     * @param appointmentID The ID of the appointment to be accepted
     */
    public void acceptAppointment(String appointmentID) {
        updateAppointmentStatus(appointmentID, AppointmentStatus.CONFIRMED.name());
    }

    /**
     * Declines an appointment by updating its status to "Cancelled."
     *
     * @param appointmentID The ID of the appointment to be declined
     */
    public void declineAppointment(String appointmentID) {
        updateAppointmentStatus(appointmentID, AppointmentStatus.CANCELLED.name());
    }

    /**
     * Displays all upcoming confirmed appointments for the doctor.
     */
    public void viewUpcomingAppointments() {
        System.out.println("Upcoming Appointments for Doctor ID: " + doctorID);

        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            boolean hasAppointments = false;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields[1].equals(doctorID) && fields[5].equals(AppointmentStatus.CONFIRMED.name())) {
                    hasAppointments = true;
                    System.out.println("Appointment ID: " + fields[0]);
                    System.out.println("Patient ID: " + fields[2]);
                    System.out.println("Date: " + fields[3]);
                    System.out.println("Time Slot: " + fields[4]);
                    System.out.println("Status: " + fields[5]);
                    System.out.println("-------------------------");
                }
            }

            if (!hasAppointments) {
                System.out.println("No upcoming confirmed appointments found for this doctor.");
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }
    }

    /**
     * Records the outcome of an appointment, including diagnosis, prescription, and treatment plan.
     *
     * @param appointmentID         The ID of the appointment
     * @param diagnosis             The diagnosis made during the appointment
     * @param prescriptionMedicine  The prescribed medicine
     * @param quantity              The quantity of prescribed medicine
     * @param treatmentPlan         The treatment plan decided during the appointment
     * @param date                  The date of the appointment
     * @param typeOfService         The type of service provided
     * @param notes                 Additional consultation notes
     */
    public void recordAppointmentOutcome(String appointmentID, String diagnosis, String prescriptionMedicine, int quantity, String treatmentPlan, String date, String typeOfService, String notes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENT_RECORD_FILE, true))) {
            String line = String.join(",",
                    appointmentID,
                    diagnosis,
                    prescriptionMedicine,
                    String.valueOf(quantity),
                    AppointmentStatus.PENDING.name(), // Prescription status initially set to "pending"
                    treatmentPlan,
                    date,
                    typeOfService,
                    notes
            );
            writer.write(line);
            writer.newLine();
            System.out.println("Appointment outcome recorded successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to AppointmentRecord.csv: " + e.getMessage());
        }

        // Update the appointment status to completed in Appointment.csv
        updateAppointmentStatus(appointmentID, AppointmentStatus.COMPLETED.name());

        // Update Patient_List.csv with the new diagnosis and treatment plan as past treatment
        updatePatientPastTreatment(appointmentID, diagnosis, treatmentPlan);
    }

    /**
     * Updates a patient's past treatment record in the Patient_List.csv file.
     *
     * @param appointmentID The ID of the appointment
     * @param diagnosis     The diagnosis made during the appointment
     * @param treatmentPlan The treatment plan decided during the appointment
     */
    private void updatePatientPastTreatment(String appointmentID, String diagnosis, String treatmentPlan) {
        String patientFilePath = "resources/Patient_List.csv";
        String patientID = getPatientIDByAppointment(appointmentID);

        if (patientID == null) {
            System.out.println("No patient found for Appointment ID: " + appointmentID);
            return;
        }

        List<String[]> records = new ArrayList<>();
        boolean isUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(patientFilePath))) {
            String line;
            records.add(reader.readLine().split(",")); // Add header to records

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(patientID)) {
                    // If `Past Treatments` already contains this appointment ID, update it; otherwise, append it
                    String pastTreatments = fields[8];
                    String[] treatmentsArray = pastTreatments.split("; ");
                    StringBuilder updatedTreatments = new StringBuilder();

                    boolean found = false;
                    for (String treatment : treatmentsArray) {
                        if (treatment.startsWith(appointmentID + "-")) {
                            updatedTreatments.append(appointmentID).append(" - ").append(diagnosis).append("- ").append(treatmentPlan);
                            found = true;
                        } else {
                            updatedTreatments.append(treatment);
                        }
                        updatedTreatments.append("; ");
                    }

                    // If the appointment was not found, add it as a new entry
                    if (!found) {
                        updatedTreatments.append(appointmentID).append("- ").append(diagnosis).append("- ").append(treatmentPlan);
                    } else {
                        // Remove the trailing "; "
                        updatedTreatments.setLength(updatedTreatments.length() - 2);
                    }

                    fields[8] = updatedTreatments.toString(); // Update the PastTreatment field
                    isUpdated = true;
                }
                records.add(fields);
            }
        } catch (IOException e) {
            System.err.println("Error reading Patient_List.csv: " + e.getMessage());
        }

        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(patientFilePath))) {
                for (String[] record : records) {
                    writer.write(String.join(",", record));
                    writer.newLine();
                }
                //System.out.println("Patient's past treatments updated successfully in Patient_List.csv.");
            } catch (IOException e) {
                System.err.println("Error writing to Patient_List.csv: " + e.getMessage());
            }
        } else {
            System.out.println("Patient record not found.");
        }
    }

    /**
     * Retrieves the patient ID associated with a specific appointment.
     *
     * @param appointmentID The ID of the appointment
     * @return The patient ID if found, otherwise null
     */
    private String getPatientIDByAppointment(String appointmentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentID)) {
                    return fields[2]; // Patient ID is the third column in Appointment.csv
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates the status of an appointment.
     *
     * @param appointmentID The ID of the appointment to be updated
     * @param newStatus     The new status of the appointment
     */
    private void updateAppointmentStatus(String appointmentID, String newStatus) {
        List<String[]> appointments = loadAppointments();
        boolean updated = false;

        for (String[] appointment : appointments) {
            if (appointment[0].equals(appointmentID)) {
                appointment[5] = newStatus;
                updated = true;
                break;
            }
        }

        if (updated) {
            saveAppointments(appointments);
            System.out.println("Appointment status updated to " + newStatus + " for Appointment ID: " + appointmentID);
        } else {
            System.out.println("Appointment ID not found.");
        }
    }

    /**
     * Loads all appointments from the Appointment CSV file.
     *
     * @return A list of string arrays, where each array represents an appointment record
     */
    private List<String[]> loadAppointments() {
        List<String[]> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                appointments.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }
        return appointments;
    }

    /**
     * Saves the updated list of appointments back to the Appointment CSV file.
     *
     * @param appointments The list of updated appointment records
     */
    private void saveAppointments(List<String[]> appointments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENT_FILE))) {
            for (String[] appointment : appointments) {
                writer.write(String.join(",", appointment));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to Appointment.csv: " + e.getMessage());
        }
    }

    /**
     * Loads all appointment records from the AppointmentRecord CSV file.
     *
     * @return A list of string arrays, where each array represents an appointment record
     */
    private List<String[]> loadAppointmentRecords() {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_RECORD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error reading AppointmentRecord.csv: " + e.getMessage());
        }
        return records;
    }

    /**
     * Saves the updated appointment records back to the AppointmentRecord CSV file.
     *
     * @param records The list of updated appointment records
     */
    private void saveAppointmentRecords(List<String[]> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENT_RECORD_FILE))) {
            for (String[] record : records) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to AppointmentRecord.csv: " + e.getMessage());
        }
    }

    /**
     * Views all confirmed appointments for the doctor on a specific date.
     *
     * @param date The date for which appointments are to be viewed
     */
    /**
     * Views all confirmed appointments for the doctor on a specific date.
     *
     * @param date The date for which appointments are to be viewed
     */
    public void viewAppointmentsByDate(String date) {
        System.out.println("Confirmed Appointments for Doctor ID: " + doctorID + " on " + date);

        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            boolean hasAppointments = false;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // Validate fields length to avoid ArrayIndexOutOfBoundsException
                if (fields.length >= 6 &&
                        fields[1].equals(doctorID) &&
                        fields[3].equals(date) &&
                        fields[5].equals(AppointmentStatus.CONFIRMED.name())) {

                    hasAppointments = true;
                    System.out.println("Appointment ID: " + fields[0]);
                    System.out.println("Patient ID: " + fields[2]);
                    System.out.println("Date: " + fields[3]);
                    System.out.println("Time Slot: " + fields[4]);
                    System.out.println("Status: " + fields[5]);
                    System.out.println("-------------------------");
                }
            }

            if (!hasAppointments) {
                System.out.println("No confirmed appointments found for this doctor on " + date + ".");
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }
    }
}
