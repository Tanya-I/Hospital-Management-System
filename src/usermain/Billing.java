package usermain;

import enums.BillStatus;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles billing operations for patients in the Hospital Management System.
 * This includes processing bills for completed appointments, prompting for payment,
 * and collecting feedback.
 */
public class Billing {
    private String patientID;
    private Scanner sc;

    /**
     * Constructor to initialize the Billing instance for a specific patient.
     *
     * @param patientID The ID of the patient for whom billing is processed.
     */
    public Billing(String patientID) {
        this.patientID = patientID;
        this.sc = new Scanner(System.in);
    }

    /**
     * Processes billing for the patient's completed appointments.
     * It identifies pending bills, prompts the user for payment,
     * updates the billing records, and collects feedback.
     */
    public void processBilling() {
        List<String> billRecords = new ArrayList<>();
        boolean hasPendingBills = false;

        try (BufferedReader appointmentReader = new BufferedReader(new FileReader("resources/Appointment.csv"));
             BufferedReader billReader = new BufferedReader(new FileReader("resources/Bill.csv"))) {

            String line;

            // Read Appointment.csv to find completed appointments for the patient
            List<String> completedAppointments = new ArrayList<>();
            while ((line = appointmentReader.readLine()) != null) {
                String[] fields = line.split(",");
                String appointmentID = fields[0];
                String appointmentPatientID = fields[2];
                String status = fields[5];

                // Check if appointment is completed and matches the patient ID
                if (appointmentPatientID.equals(patientID) && status.equalsIgnoreCase("completed")) {
                    completedAppointments.add(appointmentID);
                }
            }

            // Read Bill.csv and update records only for pending bills of completed appointments
            while ((line = billReader.readLine()) != null) {
                String[] billFields = line.split(",");
                String billAppointmentID = billFields[0];
                String billAmount = billFields[1];
                String billStatus = billFields[2];
                String feedback = billFields.length > 3 ? billFields[3] : ""; // Feedback might be empty initially

                // Process only if the appointment is completed and bill status is pending
                if (completedAppointments.contains(billAppointmentID) && billStatus.equalsIgnoreCase(BillStatus.PENDING.name())) {
                    hasPendingBills = true;
                    System.out.println("\n==== Billing Information ====");
                    System.out.println("Appointment ID: " + billAppointmentID);
                    System.out.println("Bill Amount: $" + billAmount);
                    System.out.println("Current Status: " + billStatus);

                    // Prompt user to pay the bill
                    int payChoice = getInputChoice("Would you like to pay this bill now? (1: Yes, 2: No): ");
                    if (payChoice == 1) {
                        billStatus = BillStatus.PAID.name();

                        // Prompt user for feedback
                        feedback = getValidFeedback();

                        // Confirmation message for successful payment and feedback
                        System.out.println("Bill paid successfully. Thank you for your feedback!");
                    }

                    // Reconstruct the line with updated data
                    billRecords.add(billAppointmentID + "," + billAmount + "," + billStatus + "," + feedback);
                } else {
                    // Keep the line unchanged for non-matching records or already paid bills
                    billRecords.add(line);
                }
            }

            // If no pending bills are found, inform the user
            if (!hasPendingBills) {
                System.out.println("No pending bills for completed appointments.");
                return;
            }

            // Write the modified records back to Bill.csv
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/Bill.csv"))) {
                for (String record : billRecords) {
                    writer.write(record);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.err.println("Error processing billing: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a choice with a specific message, validating the input to ensure
     * it is either 1 (Yes) or 2 (No).
     *
     * @param message The message to display to the user.
     * @return The user's choice as an integer (1 or 2).
     */
    private int getInputChoice(String message) {
        int choice;
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            if (input.equals("1") || input.equals("2")) {
                choice = Integer.parseInt(input);
                return choice;
            } else {
                System.out.println("Invalid choice. Please enter 1 for Yes or 2 for No.");
            }
        }
    }

    /**
     * Prompts the user to provide feedback on the appointment.
     * Ensures that the feedback is non-numeric and meaningful.
     *
     * @return A validated feedback string from the user.
     */
    private String getValidFeedback() {
        String feedback;
        while (true) {
            System.out.print("Please enter your feedback on the appointment: ");
            feedback = sc.nextLine().trim();
            if (!feedback.isEmpty() && !feedback.matches("\\d+")) { // Checks if feedback is not empty and not purely numeric
                return feedback;
            } else {
                System.out.println("Invalid feedback. Please enter meaningful text feedback without numbers.");
            }
        }
    }
}
