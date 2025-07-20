package usermain;

import enums.AppointmentStatus;
import appointment.AppointmentManager;
import appointment.DoctorAvailabilityManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * The Patient class represents a patient in the hospital management system.
 * It provides functionalities to manage and view personal information, medical records,
 * appointments, and treatment outcomes.
 */
public class Patient extends User {
    private String patientID;
    private String dob;
    private String gender;
    private String contactNo;
    private String email;
    private String bloodType;
    private String pastTreatment;
    private AppointmentManager appointmentManager;
    private DoctorAvailabilityManager availabilityManager;

    /**
     * Constructs a new Patient object with the provided details.
     *
     * @param patientID         The unique ID of the patient
     * @param password          The patient's password
     * @param role              The role of the user (e.g., "Patient")
     * @param name              The name of the patient
     * @param dob               The patient's date of birth
     * @param gender            The gender of the patient
     * @param contactNo         The contact number of the patient
     * @param email             The email address of the patient
     * @param bloodType         The blood type of the patient
     * @param pastTreatment     Details of past treatments
     * @param appointmentManager The AppointmentManager instance for managing appointments
     * @param availabilityManager The DoctorAvailabilityManager instance for managing doctor availability
     */
    public Patient(String patientID, String password, String role, String name, String dob, String gender, String contactNo, String email, String bloodType, String pastTreatment, AppointmentManager appointmentManager, DoctorAvailabilityManager availabilityManager) {
        super(patientID, password, role, name); // Call the User constructor to initialize ID, password, role, and name
        this.patientID = patientID;
        this.dob = dob;
        this.gender = gender;
        this.contactNo = contactNo;
        this.email = email;
        this.bloodType = bloodType;
        this.pastTreatment = pastTreatment;
        this.appointmentManager = appointmentManager;
        this.availabilityManager = availabilityManager;
    }

    /**
     * Gets the patient's unique ID.
     *
     * @return The patient ID
     */
    public String getPatientID() {
        return patientID;
    }

    /**
     * Displays the patient's medical record, including personal details and past treatments.
     */
    public void viewMedicalRecord() {
        updatePastTreatmentFromCSV(); // Update pastTreatment field before viewing

        System.out.println("Medical Record:");
        System.out.println("Patient ID: " + patientID);
        System.out.println("Name: " + getName());
        System.out.println("Date of Birth: " + dob);
        System.out.println("Gender: " + gender);
        System.out.println("Contact No: " + contactNo);
        System.out.println("Email: " + email);
        System.out.println("Blood Type: " + bloodType);
        System.out.println("Past Treatments: ");
        System.out.println("- Appointment ID - Diagnosis - Past Treatment");

        // Display each past treatment (AppointmentID, Diagnosis, and Treatment Plan)
        if (pastTreatment.equals("N/A") || pastTreatment.isEmpty()) {
            System.out.println("No past treatments recorded.");
        } else {
            String[] treatments = pastTreatment.split("; ");
            for (String treatment : treatments) {
                System.out.println(" - " + treatment); // Display each treatment in a new line
            }
        }
    }

    /**
     * Updates the pastTreatment field by reading data from the Patient_List.csv file.
     */
    private void updatePastTreatmentFromCSV() {
        String filePath = "resources/Patient_List.csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(patientID)) {
                    this.pastTreatment = fields[8]; // PastTreatment field contains AppointmentID, Diagnosis, and Treatment Plan
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Patient_List.csv: " + e.getMessage());
        }
    }

    /**
     * Updates the patient's personal information (email and contact number).
     *
     * @param newEmail    The new email address of the patient
     * @param newContactNo The new contact number of the patient
     */
    public void updatePersonalInfo(String newEmail, String newContactNo) {
        this.email = newEmail;
        this.contactNo = newContactNo;

        if (updatePatientInfoInCSV(this.patientID, newEmail, newContactNo)) {
            System.out.println("Personal information updated successfully.");
        } else {
            System.out.println("Failed to update personal information in CSV file.");
        }
    }

    /**
     * Updates the patient's information in the Patient_List.csv file.
     *
     * @param patientID   The patient's ID
     * @param newEmail    The new email address
     * @param newContactNo The new contact number
     * @return true if the update was successful, false otherwise
     */
    private boolean updatePatientInfoInCSV(String patientID, String newEmail, String newContactNo) {
        String filePath = "resources/Patient_List.csv";
        List<String[]> records = new ArrayList<>();
        boolean isUpdated = false;

        // Read the CSV file and store each record in a list
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Read header line
            if (line != null) {
                records.add(line.split(",")); // Add header to records
            }

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                // Check if this is the record to be updated
                if (fields[0].equals(patientID)) {
                    fields[5] = newContactNo; // Update contact number
                    fields[6] = newEmail; // Update email
                    isUpdated = true;
                }
                records.add(fields);
            }
        } catch (IOException e) {
            System.err.println("Error reading Patient_List.csv: " + e.getMessage());
            return false;
        }

        // Write the updated records back to the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] record : records) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to Patient_List.csv: " + e.getMessage());
            return false;
        }

        return isUpdated;
    }

    /**
     * Displays available appointment slots for a specific doctor on a given date.
     *
     * @param doctorID The doctor's ID
     * @param date     The desired date for the appointment
     */
    public void viewAvailableAppointmentSlots(String doctorID, String date) {
        String[] availableSlots = availabilityManager.viewDoctorAvailability(doctorID, date);
        System.out.println("Available Slots:");
        for (String slot : availableSlots) {
            System.out.println(slot);  // Each slot includes doctor name, date, and time slot
        }
    }

    /**
     * Schedules a new appointment for the patient.
     *
     * @param patientID The patient's ID
     */
    public void scheduleAppointment(String patientID) {
        String appointmentID = appointmentManager.scheduleAppointment(patientID);
        if (appointmentID != null) {
            System.out.println("Appointment scheduled successfully. The appointment ID is " + appointmentID);
        } else {
            System.out.println("Failed to schedule the appointment. Please check the details and try again.");
        }
    }

    /**
     * Reschedules an existing appointment by its ID.
     *
     * @param appointmentID The ID of the appointment to be rescheduled
     */
    public void rescheduleAppointment(String appointmentID) {
        appointmentManager.rescheduleAppointment(appointmentID);
    }

    /**
     * Cancels an existing appointment by its ID.
     *
     * @param appointmentID The ID of the appointment to be canceled
     */
    public void cancelAppointment(String appointmentID) {
        appointmentManager.cancelAppointment(appointmentID);
    }

    /**
     * Displays the outcomes of past completed appointments for the patient.
     */
    public void viewPastAppointmentOutcome() {
        String appointmentFilePath = "resources/Appointment.csv";
        String recordFilePath = "resources/AppointmentRecord.csv";
        System.out.println("Past Appointment Outcomes for Patient ID: " + patientID);

        // List to store completed appointment IDs for this patient
        List<String> completedAppointments = new ArrayList<>();

        // Step 1: Read Appointment.csv to find completed appointments for this patient
        try (BufferedReader appointmentReader = new BufferedReader(new FileReader(appointmentFilePath))) {
            String line = appointmentReader.readLine(); // Skip header line

            while ((line = appointmentReader.readLine()) != null) {
                String[] fields = line.split(",");

                // Check if the line has the expected number of fields
                if (fields.length < 6) {
                    System.out.println("Skipping malformed line in Appointment.csv: " + line);
                    continue;
                }

                String appointmentID = fields[0];
                String appointmentPatientID = fields[2];
                String status = fields[5];

                // If the appointment is completed for the current patient, store the appointmentID
                if (appointmentPatientID.equals(patientID) && status.equalsIgnoreCase(AppointmentStatus.COMPLETED.name())) {
                    completedAppointments.add(appointmentID);
                }
            }

            if (completedAppointments.isEmpty()) {
                System.out.println("No completed appointments found for this patient.");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
            return;
        }

        // Step 2: Read AppointmentRecord.csv to print details for completed appointments
        try (BufferedReader recordReader = new BufferedReader(new FileReader(recordFilePath))) {
            String line;
            boolean hasRecord = false;

            while ((line = recordReader.readLine()) != null) {
                String[] fields = line.split(",");

                // Check if the line has the expected number of fields
                if (fields.length < 9) {
                    //System.out.println("Skipping malformed line in AppointmentRecord.csv: " + line);
                    continue;
                }

                String appointmentID = fields[0];

                // If the appointmentID is in the completedAppointments list, print the record
                if (completedAppointments.contains(appointmentID)) {
                    hasRecord = true;
                    System.out.println("Appointment ID: " + fields[0]);
                    System.out.println("Diagnosis: " + fields[1]);
                    System.out.println("Prescription Medicine: " + fields[2]);
                    System.out.println("Prescription Quantity: " + fields[3]);
                    System.out.println("Prescription Status: " + fields[4]);
                    System.out.println("Treatment Plan: " + fields[5]);
                    System.out.println("Date: " + fields[6]);
                    System.out.println("Type of Service: " + fields[7]);
                    System.out.println("Consultation Notes: " + fields[8]);
                    System.out.println("-------------------------");
                }
            }

            if (!hasRecord) {
                System.out.println("No past appointment records found in AppointmentRecord.csv for this patient.");
            }
        } catch (IOException e) {
            System.err.println("Error reading AppointmentRecord.csv: " + e.getMessage());
        }
    }
}