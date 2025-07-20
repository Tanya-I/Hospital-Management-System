/**
 * The Administrator class provides functionalities to manage staff members, appointments,
 * medication inventory, and replenishment requests in a hospital management system.
 * It includes methods for viewing, adding, updating, and removing staff, as well as
 * handling appointment details and medication stock updates.
 */
package usermain;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import enums.PrescriptionStatus;
import enums.ReplenishmentRequestStatus;

import java.util.*;

public class Administrator {

    private static final String STAFF_FILE_PATH = "resources/Staff.csv";
    private static final String USER_FILE_PATH = "resources/User.csv";
    private static final String APPOINTMENT_FILE_PATH = "resources/Appointment.csv";
    private static final String MEDICINE_FILE_PATH = "resources/Medicine_List.csv";
    private static final String APPOINTMENT_RECORD_FILE = "resources/AppointmentRecord.csv";
    private static final String REPLENISHMENT_REQUEST_FILE = "resources/ReplenishmentRequest.csv";

    /**
     * Displays the list of staff members from the staff CSV file.
     *
     * @throws IOException if there is an error reading the file
     */
    public void viewStaffList() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(STAFF_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    /**
     * Displays the details of a specific appointment by its ID.
     *
     * @param appointmentID The ID of the appointment to be viewed
     */
    public void viewAppointmentDetails(String appointmentID) {
        boolean appointmentFound = false;

        // Read from Appointment.csv
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE_PATH))) {
            String line;
            System.out.println("Appointment Details:");
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Check if the appointment ID matches
                if (data[0].equals(appointmentID)) {
                    System.out.println("Appointment ID: " + data[0]);
                    System.out.println("Doctor ID: " + data[1]);
                    System.out.println("Patient ID: " + data[2]);
                    System.out.println("Date: " + data[3]);
                    System.out.println("Time Slot: " + data[4]);
                    System.out.println("Status: " + data[5]);
                    appointmentFound = true;
                    break;
                }
            }
            if (!appointmentFound) {
                System.out.println("No appointment found with ID: " + appointmentID);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read from AppointmentRecord.csv
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_RECORD_FILE))) {
            String line;
            System.out.println("\nAppointment Record Details:");
            boolean recordFound = false;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Check if the appointment ID matches
                if (data[0].equals(appointmentID)) {
                    System.out.println("Diagnosis: " + data[1]);
                    System.out.println("Prescription Medicine: " + data[2]);
                    System.out.println("Prescription Quantity: " + data[3]);
                    System.out.println("Prescription Status: " + data[4]);
                    System.out.println("Treatment Plan: " + data[5]);
                    System.out.println("Date: " + data[6]);
                    System.out.println("Type of Service: " + data[7]);
                    System.out.println("Consultation Notes: " + data[8]);
                    recordFound = true;
                    break;
                }
            }
            if (!recordFound) {
                System.out.println("No appointment record found with ID: " + appointmentID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new staff member to the system and creates a corresponding user entry.
     *
     * @param id     The ID of the staff member
     * @param name   The name of the staff member
     * @param role   The role of the staff member
     * @param gender The gender of the staff member
     * @param age    The age of the staff member
     * @throws IOException if there is an error writing to the file
     */
    public void addStaff(String id, String name, String role, String gender, int age) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STAFF_FILE_PATH, true))) {
            bw.write(id + "," + name + "," + role + "," + gender + "," + age);
            bw.newLine();  // Add a newline at the end
            System.out.println("Staff member added successfully.");
        }

        // Add corresponding entry to User.csv
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE_PATH, true))) {
            String defaultPassword = "password"; // Define a default password
            bw.write(id + "," + defaultPassword + "," + role + "," + name);
            bw.newLine();
            //System.out.println("Staff member added successfully.");
        }
    }

    /**
     * Updates the details of an existing staff member in the system.
     *
     * @param id       The ID of the staff member to be updated
     * @param newName  The new name of the staff member
     * @param newRole  The new role of the staff member
     * @param newGender The new gender of the staff member
     * @param newAge   The new age of the staff member
     * @throws IOException if there is an error reading or writing to the file
     */
    public void updateStaff(String id, String newName, String newRole, String newGender, int newAge) throws IOException {
        List<String[]> staffList = readCSV(STAFF_FILE_PATH);
        boolean staffFound = false;

        // Find the staff member by ID and update details in Staff.csv
        for (String[] staff : staffList) {
            if (staff[0].equals(id)) {
                staff[1] = newName;
                staff[2] = newRole;
                staff[3] = newGender;
                staff[4] = String.valueOf(newAge);
                staffFound = true;
                break;
            }
        }

        if (staffFound) {
            writeStaffCSV(staffList, STAFF_FILE_PATH);
            System.out.println("Staff member updated successfully.");
        } else {
            System.out.println("Staff member with ID " + id + " not found.");
        }

        // Update corresponding entry in User.csv
        List<String[]> userList = readCSV(USER_FILE_PATH);
        boolean userFound = false;

        for (String[] user : userList) {
            if (user[0].equals(id)) {
                user[2] = newRole;
                user[3] = newName;
                userFound = true;
                break;
            }
        }

        if (userFound) {
            writeUserCSV(userList, USER_FILE_PATH);
            //System.out.println("Staff member updated successfully.");
        } else {
            //System.out.println("Staff member with ID " + id + " not found.");
        }
    }

    /**
     * Removes a staff member from the system by their ID.
     *
     * @param id The ID of the staff member to be removed
     * @throws IOException if there is an error reading or writing to the file
     */
    public void removeStaff(String id) throws IOException {
        List<String[]> staffList = readCSV(STAFF_FILE_PATH);
        boolean staffFound = false;

        // Find and remove the staff member by ID in Staff.csv
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i)[0].equals(id)) {
                staffList.remove(i);
                staffFound = true;
                break;
            }
        }

        if (staffFound) {
            writeStaffCSV(staffList, STAFF_FILE_PATH);
            System.out.println("Staff member removed from successfully.");
        } else {
            System.out.println("Staff member with ID " + id + " not found in.");
        }

        // Remove corresponding entry in User.csv
        List<String[]> userList = readCSV(USER_FILE_PATH);
        boolean userFound = false;

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i)[0].equals(id)) {
                userList.remove(i);
                userFound = true;
                break;
            }
        }

        if (userFound) {
            writeUserCSV(userList, USER_FILE_PATH);
            //System.out.println("Staff member removed successfully.");
        } else {
            //System.out.println("Staff member with ID " + id + " not found.");
        }
    }


    /**
     * Displays the medication inventory from the CSV file.
     *
     * @throws IOException if there is an error reading the file
     */
    public void viewMedicationInventory() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_FILE_PATH))) {
            String line;
            boolean isFirstLine = true; // To skip the header line
            System.out.println("Medication Inventory:");
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                System.out.println(line);
            }
        }
    }

    /**
     * Updates the stock level of a specific medication in the inventory.
     *
     * @param medicineName  The name of the medication to be updated
     * @param newStockLevel The new stock level for the medication
     * @throws IOException if there is an error reading or writing to the file
     */
    public void updateMedicationStock(String medicineName, int newStockLevel) throws IOException {
        List<String[]> medicineList = readCSV(MEDICINE_FILE_PATH);
        boolean medicineFound = false;

        for (String[] medicine : medicineList) {
            if (medicine[0].equalsIgnoreCase(medicineName)) {
                medicine[1] = String.valueOf(newStockLevel); // Update stock level
                medicineFound = true;
                break;
            }
        }

        if (medicineFound) {
            writeMedicineCSV(medicineList, MEDICINE_FILE_PATH);
            System.out.println("Stock level for " + medicineName + " updated to " + newStockLevel);
        } else {
            System.out.println("Medication " + medicineName + " not found.");
        }
    }

    /**
     * Updates the low stock level alert for a specific medication.
     *
     * @param medicineName       The name of the medication to be updated
     * @param newLowStockLevel   The new low stock alert level for the medication
     * @throws IOException if there is an error reading or writing to the file
     */
    public void updateLowStockLevel(String medicineName, int newLowStockLevel) throws IOException {
        List<String[]> medicineList = readCSV(MEDICINE_FILE_PATH);
        boolean medicineFound = false;

        for (String[] medicine : medicineList) {
            if (medicine[0].equalsIgnoreCase(medicineName)) {
                medicine[2] = String.valueOf(newLowStockLevel); // Update low stock level
                medicineFound = true;
                break;
            }
        }

        if (medicineFound) {
            writeMedicineCSV(medicineList, MEDICINE_FILE_PATH);
            System.out.println("Low stock level for " + medicineName + " updated to " + newLowStockLevel);
        } else {
            System.out.println("Medication " + medicineName + " not found.");
        }
    }

    /**
     * Reads data from a CSV file and converts it into a list of string arrays.
     * Each array represents a row in the CSV file, with its elements corresponding to the columns.
     *
     * @param filePath The path to the CSV file to be read
     * @return A list of string arrays, where each array represents a row in the CSV file
     * @throws IOException if there is an error reading the file
     */
    private List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> staffList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true; // Skip the header

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header
                }
                String[] staff = line.split(",");
                staffList.add(staff);
            }
        }
        return staffList;
    }

    /**
     * Writes an updated list of medicines back to a CSV file.
     * The method ensures the file includes the correct header and updated content.
     *
     * @param medicineList A list of string arrays representing the updated medicine data
     * @param filePath     The path to the CSV file to be updated
     * @throws IOException if there is an error writing to the file
     */
    private void writeMedicineCSV(List<String[]> medicineList, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // Write header first for the medicine list
            bw.write("Medicine Name,Initial Stock,Low Stock Level Alert");
            bw.newLine();

            // Write the rest of the medicine list
            for (String[] medicine : medicineList) {
                bw.write(String.join(",", medicine));
                bw.newLine();
            }
        }
    }

    /**
     * Writes an updated list of staff members back to a CSV file.
     * The method ensures the file includes the correct header and updated content.
     *
     * @param staffList A list of string arrays representing the updated staff data
     * @param filePath  The path to the CSV file to be updated
     * @throws IOException if there is an error writing to the file
     */
    private void writeStaffCSV(List<String[]> staffList, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // Write header first for the staff list
            bw.write("Staff ID,Name,Role,Gender,Age");
            bw.newLine();

            // Write the rest of the staff list
            for (String[] staff : staffList) {
                bw.write(String.join(",", staff));
                bw.newLine();
            }
        }
    }

    /**
     * Writes an updated list of users back to the User CSV file.
     * The method ensures the file includes the correct header and updated content.
     *
     * @param userList A list of string arrays representing the updated user data
     * @param filePath The path to the User CSV file to be updated
     * @throws IOException if there is an error writing to the file
     */
    private void writeUserCSV(List<String[]> userList, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // Write header first for the user list
            bw.write("ID,Password,Role,Name");
            bw.newLine();

            // Write the rest of the user list
            for (String[] user : userList) {
                bw.write(String.join(",", user));
                bw.newLine();
            }
        }
    }

    /**
     * Approves pending replenishment requests for medications.
     * Updates the replenishment request file and medication stock levels accordingly.
     */
    public void approveReplenishmentRequests() {
        List<String[]> replenishmentRequests = new ArrayList<>();
        List<String[]> updatedRequests = new ArrayList<>();
        Map<String, Integer> medicineStock = loadMedicineStock();

        // Load pending replenishment requests
        try (BufferedReader reader = new BufferedReader(new FileReader(REPLENISHMENT_REQUEST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[3].equalsIgnoreCase(PrescriptionStatus.PENDING.name())) {
                    replenishmentRequests.add(data);
                }
                updatedRequests.add(data); // Store for later updates
            }
        } catch (IOException e) {
            System.err.println("Error reading ReplenishmentRequest.csv: " + e.getMessage());
            return;
        }

        // Process each pending request
        Scanner scanner = new Scanner(System.in);
        for (String[] request : replenishmentRequests) {
            String requestId = request[0];
            String medicineName = request[1];
            int quantity = Integer.parseInt(request[2]);
            String status = request[3];

            System.out.println("\nReplenishment Request:");
            System.out.println("Request ID: " + requestId);
            System.out.println("Medicine: " + medicineName);
            System.out.println("Quantity: " + quantity);
            System.out.println("Status: " + status);

            // Prompt for approval
            String approval;
            while (true) {
                System.out.print("Approve request? (Y/N): ");
                approval = scanner.nextLine().trim().toUpperCase();
                if (approval.equals("Y") || approval.equals("N")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'Y' for Yes or 'N' for No.");
                }
            }

            if (approval.equals("Y")) {
                request[3] = ReplenishmentRequestStatus.APPROVED.name(); // Update status to approved

                // Update the medicine stock by adding the requested quantity to the original stock
                if (medicineStock.containsKey(medicineName)) {
                    int currentStock = medicineStock.get(medicineName);
                    medicineStock.put(medicineName, currentStock + quantity);// Add replenishment quantity
                    System.out.println("Request approved successfully for " + medicineName + " with quantity " + quantity + ".");
                } else {
                    System.out.println("Medicine " + medicineName + " not found in the stock list.");
                }
            }
        }

        // Write updated requests back to ReplenishmentRequest.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPLENISHMENT_REQUEST_FILE))) {
            for (String[] data : updatedRequests) {
                writer.write(String.join(",", data));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to ReplenishmentRequest.csv: " + e.getMessage());
        }

        // Write updated stock back to Medicine_List.csv
        saveUpdatedMedicineStock(medicineStock);
    }

    /**
     * Loads the current stock of medications from the CSV file into a map.
     *
     * @return A map containing medication names as keys and stock levels as values
     */
    private Map<String, Integer> loadMedicineStock() {
        Map<String, Integer> stock = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE_PATH))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String medicineName = data[0].trim();
                int initialStock = Integer.parseInt(data[1].trim());
                stock.put(medicineName, initialStock);
            }
        } catch (IOException e) {
            System.err.println("Error reading Medicine_List.csv: " + e.getMessage());
        }
        return stock;
    }

    /**
     * Saves the updated stock levels of medications back to the CSV file.
     *
     * @param medicineStock A map containing updated medication stock levels
     */
    private void saveUpdatedMedicineStock(Map<String, Integer> medicineStock) {
        List<String[]> updatedStock = new ArrayList<>();

        // Read existing data and update stock values
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE_PATH))) {
            String line;
            updatedStock.add(reader.readLine().split(",")); // Add header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String medicineName = data[0].trim();
                if (medicineStock.containsKey(medicineName)) {
                    data[1] = String.valueOf(medicineStock.get(medicineName)); // Update stock value by adding replenishment quantity
                }
                updatedStock.add(data);
            }
        } catch (IOException e) {
            System.err.println("Error reading Medicine_List.csv for updates: " + e.getMessage());
            return;
        }

        // Write the updated data back to Medicine_List.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_FILE_PATH))) {
            for (String[] data : updatedStock) {
                writer.write(String.join(",", data));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to Medicine_List.csv: " + e.getMessage());
        }
    }
}