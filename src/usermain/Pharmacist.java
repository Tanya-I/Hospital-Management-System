package usermain;

import enums.BillStatus;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Random;
import enums.PrescriptionStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Pharmacist user in the Hospital Management System.
 * The Pharmacist can perform operations such as viewing appointment outcomes,
 * updating prescription statuses, managing medication inventory, submitting replenishment requests, and generating bills.
 */
public class Pharmacist extends User {
    private static final Map<String, Double> MEDICINE_PRICES = new HashMap<>();

    static {
        // Initialize medicine prices
        MEDICINE_PRICES.put("paracetamol", 0.125);
        MEDICINE_PRICES.put("ibuprofen", 0.50);
        MEDICINE_PRICES.put("amoxicillin", 0.95);
    }

    /**
     * Constructor to initialize a Pharmacist object with full user details.
     *
     * @param id       The unique ID of the pharmacist.
     * @param password The password of the pharmacist.
     * @param role     The role of the user (Pharmacist).
     * @param name     The name of the pharmacist.
     */
    public Pharmacist(String id, String password, String role, String name) {
        super(id, password, role, name);
    }

    /**
     * Constructor to initialize a Pharmacist object with only an ID.
     *
     * @param id The unique ID of the pharmacist.
     */
    public Pharmacist(String id) {
        super(id);
    }

    /**
     * Displays the outcome of a completed appointment based on the appointment ID.
     *
     * @param appointmentID The ID of the appointment to view.
     */
    public void viewAppointmentOutcome(String appointmentID) {
        String appointmentFilePath = "resources/Appointment.csv";
        String recordFilePath = "resources/AppointmentRecord.csv";
        System.out.println("Appointment Outcome for Appointment ID: " + appointmentID);

        // Verify that the appointment is completed
        boolean isCompleted = false;

        try (BufferedReader appointmentReader = new BufferedReader(new FileReader(appointmentFilePath))) {
            String line = appointmentReader.readLine(); // Skip header line

            while ((line = appointmentReader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 6) {
                    System.out.println("Skipping malformed line in Appointment.csv: " + line);
                    continue;
                }

                String currentAppointmentID = fields[0];
                String status = fields[5];

                if (currentAppointmentID.equals(appointmentID) && status.equalsIgnoreCase("completed")) {
                    isCompleted = true;
                    break;
                }
            }

            if (!isCompleted) {
                System.out.println("No completed appointment found for the given appointment ID.");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
            return;
        }

        // Read AppointmentRecord.csv to display appointment details
        try (BufferedReader recordReader = new BufferedReader(new FileReader(recordFilePath))) {
            String line;
            boolean hasRecord = false;

            while ((line = recordReader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 9) continue;

                String recordAppointmentID = fields[0];
                if (recordAppointmentID.equals(appointmentID)) {
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
                    break;
                }
            }

            if (!hasRecord) {
                System.out.println("No past appointment record found for the given appointment ID.");
            }
        } catch (IOException e) {
            System.err.println("Error reading AppointmentRecord.csv: " + e.getMessage());
        }
    }

    /**
     * Updates the prescription status for a specific appointment.
     * Checks medicine stock and updates the inventory and appointment records.
     *
     * @param appointmentID The ID of the appointment for which the prescription is being updated.
     */
    public void updatePrescriptionStatus(String appointmentID) {
        String recordFilePath = "resources/AppointmentRecord.csv";
        String medicineFilePath = "resources/Medicine_List.csv";
        boolean appointmentFound = false;
        boolean stockSufficient = false;
        List<String[]> records = new ArrayList<>();
        String prescribedMedicine = "";
        int prescribedQuantity = 0;

        // Read AppointmentRecord.csv to find the appointment and prescription details
        try (BufferedReader recordReader = new BufferedReader(new FileReader(recordFilePath))) {
            String line;

            while ((line = recordReader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentID)) {
                    appointmentFound = true;
                    prescribedMedicine = fields[2];
                    prescribedQuantity = Integer.parseInt(fields[3]);

                    if (!fields[4].equalsIgnoreCase(PrescriptionStatus.PENDING.name())) {
                        System.out.println("Prescription is already dispensed for this appointment.");
                        return;
                    }
                }
                records.add(fields);
            }
        } catch (IOException e) {
            System.err.println("Error reading AppointmentRecord.csv: " + e.getMessage());
            return;
        }

        if (!appointmentFound) {
            System.out.println("Appointment ID " + appointmentID + " not found.");
            return;
        }

        // Check stock in Medicine_List.csv
        List<String[]> medicineList = new ArrayList<>();
        try (BufferedReader medicineReader = new BufferedReader(new FileReader(medicineFilePath))) {
            String line;

            while ((line = medicineReader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equalsIgnoreCase(prescribedMedicine)) {
                    int currentStock = Integer.parseInt(fields[1]);
                    if (currentStock >= prescribedQuantity) {
                        stockSufficient = true;
                        currentStock -= prescribedQuantity;
                        fields[1] = String.valueOf(currentStock);
                        System.out.println("Dispensed " + prescribedQuantity + " units of " + prescribedMedicine + ". Updated stock: " + currentStock);
                    } else {
                        System.out.println("Insufficient stock for " + prescribedMedicine + ". Please submit a stock replenishment request.");
                        return;
                    }
                }
                medicineList.add(fields);
            }
        } catch (IOException e) {
            System.err.println("Error reading Medicine_List.csv: " + e.getMessage());
            return;
        }

        if (!stockSufficient) {
            System.out.println("Medicine " + prescribedMedicine + " not found.");
            return;
        }

        // Update the prescription status and medicine inventory
        try (BufferedWriter recordWriter = new BufferedWriter(new FileWriter(recordFilePath))) {
            for (String[] fields : records) {
                if (fields[0].equals(appointmentID)) {
                    fields[4] = PrescriptionStatus.DISPENSED.name();
                }
                recordWriter.write(String.join(",", fields));
                recordWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to AppointmentRecord.csv: " + e.getMessage());
        }

        try (BufferedWriter medicineWriter = new BufferedWriter(new FileWriter(medicineFilePath))) {
            for (String[] fields : medicineList) {
                medicineWriter.write(String.join(",", fields));
                medicineWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to Medicine_List.csv: " + e.getMessage());
        }

        // Generate the bill after dispensing the medicine
        generateBill(appointmentID, prescribedMedicine, prescribedQuantity);
    }

    /**
     * Displays the current medication inventory with stock levels.
     */
    public void viewMedicationInventory() {
        String medicineFilePath = "resources/Medicine_List.csv";

        System.out.println("\n==== Medication Inventory ====");
        System.out.printf("%-20s %-15s %-20s%n", "Medicine Name", "Initial Stock", "Low Stock Level Alert");

        try (BufferedReader reader = new BufferedReader(new FileReader(medicineFilePath))) {
            String line = reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 3) {
                    String medicineName = fields[0].trim();
                    String initialStock = fields[1].trim();
                    String lowStockLevel = fields[2].trim();
                    System.out.printf("%-20s %-15s %-20s%n", medicineName, initialStock, lowStockLevel);
                } else {
                    System.out.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Medicine_List.csv: " + e.getMessage());
        }
    }

    /**
     * Submits a replenishment request for a specific medicine and quantity.
     *
     * @param medicineName The name of the medicine to be replenished.
     * @param quantity     The quantity to be requested.
     */
    public void submitReplenishmentRequest(String medicineName, int quantity) {
        String replenishmentFilePath = "resources/ReplenishmentRequest.csv";
        String status = PrescriptionStatus.PENDING.name();

        // Generate RRID with "RR" followed by 3 random digits
        String rrid = "RR" + String.format("%03d", new Random().nextInt(1000));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(replenishmentFilePath, true))) {
            writer.write(rrid + "," + medicineName + "," + quantity + "," + status);
            writer.newLine();
            System.out.println("Replenishment request submitted for " + medicineName + " with quantity " + quantity + ". Status: " + status);
        } catch (IOException e) {
            System.err.println("Error writing to ReplenishmentRequest.csv: " + e.getMessage());
        }
    }

    /**
     * Generates a bill for a specific appointment based on the prescribed medicine and quantity.
     *
     * @param appointmentID      The ID of the appointment for which the bill is generated.
     * @param prescribedMedicine The name of the prescribed medicine.
     * @param prescribedQuantity The quantity of the prescribed medicine.
     */
    public void generateBill(String appointmentID, String prescribedMedicine, int prescribedQuantity) {
        String billFilePath = "resources/Bill.csv";
        double unitPrice = MEDICINE_PRICES.getOrDefault(prescribedMedicine.toLowerCase(), 0.0);
        double billAmount = unitPrice * prescribedQuantity;

        // Set the status as "PENDING" and feedback as "na"
        String status = BillStatus.PENDING.name();
        String feedback = "na";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(billFilePath, true))) {
            writer.write(appointmentID + "," + billAmount + "," + status + "," + feedback);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to Bill.csv: " + e.getMessage());
        }
    }
}
