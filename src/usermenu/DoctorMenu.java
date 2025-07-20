package usermenu;
import enums.AppointmentStatus;
import enums.DoctorAvailabilityStatus;
import usermain.Doctor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import appointment.AppointmentService;
import enums.MedicineList;

/**
 * The DoctorMenu class provides the menu interface for doctors in the hospital management system.
 * It includes functionalities for viewing and updating patient medical records, managing schedules,
 * handling appointment requests, and recording appointment outcomes.
 */
public class DoctorMenu extends AbstractMenu {
    private Doctor doctor;
    private Scanner sc;
    private static final List<String> VALID_PRESCRIPTIONS = List.of(MedicineList.AMOXICILLIN.name(), MedicineList.IBUPROFEN.name(),MedicineList.PARACETAMOL.name(),MedicineList.NA.name());
    private static final String APPOINTMENT_FILE = "resources/Appointment.csv";
    private AppointmentService appointmentService = new AppointmentService();

    /**
     * Constructs a new DoctorMenu instance for the given doctor.
     *
     * @param doctor The Doctor object representing the doctor using the menu
     */
    public DoctorMenu(Doctor doctor) {
        this.doctor = doctor;
        this.sc = new Scanner(System.in);
    }

    /**
     * Displays the doctor menu and handles user input for menu options.
     * The menu provides options for managing medical records, schedules, and appointments.
     */
    @Override
    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n==== Doctor Menu ====");
            System.out.println("(1) View Patient Medical Records");
            System.out.println("(2) Update Patient Medical Records");
            System.out.println("(3) View Personal Schedule");
            System.out.println("(4) Set Availability for Appointments");
            System.out.println("(5) Accept or Decline Appointment Requests");
            System.out.println("(6) View Upcoming Appointments");
            System.out.println("(7) Record Appointment Outcome");

            displayLogoutOption(8); // Call the common logout option method

            try {
                choice = sc.nextInt();
                sc.nextLine(); // Clear the newline character from the buffer

                switch (choice) {
                    case 1:
                        viewPatientMedicalRecords();
                        break;
                    case 2:
                        updatePatientMedicalRecords();
                        break;
                    case 3:
                        String date;

                        // Validate date format
                        while (true) {
                            System.out.print("Enter to view Schedule (e.g., DD-MM-YY): ");
                            date = sc.nextLine();
                            if (isValidDateFormat(date)) {
                                break;
                            } else {
                                System.out.println("Invalid date format. Please use DD-MM-YY.");
                            }
                        }
                        doctor.viewPersonalSchedule(date);
                        System.out.println();
                        doctor.viewAppointmentsByDate(date);
                        break;
                    case 4:
                        setAvailabilityForAppointments();
                        break;
                    case 5:
                        acceptOrDeclineAppointmentRequests();
                        break;
                    case 6:
                        doctor.viewUpcomingAppointments();
                        break;
                    case 7:
                        recordAppointmentOutcome();
                        break;
                    case 8:
                        System.out.println("Logging out...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // Clear invalid input from the scanner buffer
                choice = -1;
            }
        } while (choice != 8);
    }

    /**
     * Displays the medical records of a specific patient based on the entered patient ID.
     */
    private void viewPatientMedicalRecords() {
        System.out.print("Enter Patient ID to view medical records: ");
        String patientID = sc.nextLine();

        String patientFile = "resources/Patient_List.csv";
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(patientFile))) {
            String line = reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(patientID)) {
                    System.out.println("\n==== Patient Medical Record ====");
                    System.out.println("Patient ID: " + fields[0]);
                    System.out.println("Name: " + fields[2]);
                    System.out.println("Gender: " + fields[3]);
                    System.out.println("Date of Birth: " + fields[4]);
                    System.out.println("Contact Number: " + fields[5]);
                    System.out.println("Email: " + fields[6]);
                    System.out.println("Blood Type: " + fields[7]);
                    System.out.println("Past Treatments: " + fields[8]);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("No patient found with ID: " + patientID);
            }
        } catch (IOException e) {
            System.err.println("Error reading Patient_List.csv: " + e.getMessage());
        }
    }

    /**
     * Updates the medical records of a specific patient based on the entered appointment ID.
     * Allows the doctor to input a diagnosis, prescription, quantity, treatment plan, and consultation notes.
     */
    private void updatePatientMedicalRecords() {
        String appointmentID;

        while (true) {
            System.out.print("Enter Appointment ID to update records: ");
            appointmentID = sc.nextLine();
            if (isValidAppointmentID(appointmentID)) {
                break;
            } else {
                System.out.println("Invalid Appointment ID. Please enter a valid ID.");
            }
        }

        System.out.print("Enter new diagnosis: ");
        String diagnosis = sc.nextLine();

        String prescription;
        while (true) {
            System.out.print("Enter new prescription (Paracetamol, Ibuprofen, Amoxicillin, NA): ");
            prescription = sc.nextLine().trim();
            if (isValidPrescription(prescription)) {
                prescription = prescription.toUpperCase(); // Convert to uppercase to match enum format
                break;
            } else {
                System.out.println("Invalid prescription. Please enter a valid prescription.");
            }
        }

        int quantity;
        while (true) {
            System.out.print("Enter prescription quantity: ");
            if (sc.hasNextInt()) {
                quantity = sc.nextInt();
                sc.nextLine(); // Clear newline
                if (quantity >= 0) {
                    break;
                } else {
                    System.out.println("Quantity must be 0 or a positive integer. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Quantity must be 0 or a positive integer.");
                sc.nextLine(); // Clear invalid input
            }
        }

        System.out.print("Enter new treatment plan: ");
        String treatmentPlan = sc.nextLine();
        System.out.print("Enter new consultation notes: ");
        String notes = sc.nextLine();

        doctor.updatePatientMedicalRecord(appointmentID, diagnosis, prescription, quantity, treatmentPlan, notes);
    }

    /**
     * Allows the doctor to set availability for appointments on a specific date.
     * The doctor can select time slots to mark as available.
     */
    private void setAvailabilityForAppointments() {
        String date;

        // Validate date format
        while (true) {
            System.out.print("Enter date for availability (e.g., DD-MM-YY): ");
            date = sc.nextLine();
            if (isValidDateFormat(date)) {
                break;
            } else {
                System.out.println("Invalid date format. Please use DD-MM-YY.");
            }
        }

        List<String> timeSlots = new ArrayList<>();
        int startHour = 9;
        int endHour = 17;

        System.out.println("Available time slots:");
        int slotNumber = 1;
        for (int hour = startHour; hour < endHour; hour++) {
            String slot1 = String.format("%02d:00-%02d:30", hour, hour);
            String slot2 = String.format("%02d:30-%02d:00", hour, hour + 1);
            timeSlots.add(slot1);
            timeSlots.add(slot2);
            System.out.printf("(%d) %s\n", slotNumber++, slot1);
            System.out.printf("(%d) %s\n", slotNumber++, slot2);
        }

        List<String> availableSlots = new ArrayList<>();
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter the numbers for available time slots (e.g., 1,3,5): ");
            String[] selectedSlots = sc.nextLine().split(",");

            availableSlots.clear(); // Clear previous selections in case of invalid input
            validInput = true;

            for (String slot : selectedSlots) {
                try {
                    int slotIndex = Integer.parseInt(slot.trim()) - 1;
                    if (slotIndex >= 0 && slotIndex < timeSlots.size()) {
                        availableSlots.add(timeSlots.get(slotIndex));
                    } else {
                        System.out.println("Invalid slot number: " + slot);
                        validInput = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + slot);
                    validInput = false;
                }
            }

            if (!validInput) {
                System.out.println("Please enter valid slot numbers.");
            }
        }

        doctor.setAvailability(date, availableSlots.toArray(new String[0]));
    }

    /**
     * Allows the doctor to accept or decline pending appointment requests.
     * Updates the appointment status and slot availability based on the decision.
     */
    private void acceptOrDeclineAppointmentRequests() {
        String doctorID = doctor.getDoctorID();
        boolean foundPending = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line = reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length < 6) {
                    //System.out.println("Skipping malformed line in Appointment.csv: " + line);
                    continue;
                }

                String appointmentID = fields[0];
                String appointmentDoctorID = fields[1];
                String patientID = fields[2];
                String date = fields[3];
                String timeSlot = fields[4];
                String status = fields[5];

                if (appointmentDoctorID.equals(doctorID) && status.equals(AppointmentStatus.PENDING.name())) {
                    foundPending = true;
                    System.out.println("\n==== Pending Appointment ====");
                    System.out.println("Appointment ID: " + appointmentID);
                    System.out.println("Patient ID: " + patientID);
                    System.out.println("Date: " + date);
                    System.out.println("Time Slot: " + timeSlot);

                    int response;
                    while (true) {
                        System.out.print("Do you want to (1) Accept or (2) Decline this appointment? Enter 1 or 2: ");
                        if (sc.hasNextInt()) {
                            response = sc.nextInt();
                            sc.nextLine(); // Clear newline

                            if (response == 1) {
                                doctor.acceptAppointment(appointmentID);
                                System.out.println("Appointment accepted.");
                                break;
                            } else if (response == 2) {
                                doctor.declineAppointment(appointmentID);
                                System.out.println("Appointment declined.");
                                appointmentService.updateSlotStatus(doctorID, date, timeSlot, DoctorAvailabilityStatus.AVAILABLE.name());
                                break;
                            } else {
                                System.out.println("Invalid option. Please enter 1 to accept or 2 to decline.");
                            }
                        } else {
                            System.out.println("Invalid input. Please enter a number (1 to accept or 2 to decline).");
                            sc.nextLine(); // Clear invalid input
                        }
                    }
                }
            }

            if (!foundPending) {
                System.out.println("No pending appointments found.");
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }
    }

    /**
     * Records the outcome of a specific appointment, including diagnosis, prescription,
     * treatment plan, and consultation notes.
     */
    private void recordAppointmentOutcome() {
        String appointmentID;

        // Prompt user to enter an Appointment ID and validate it
        while (true) {
            System.out.print("Enter Appointment ID: ");
            appointmentID = sc.nextLine();
            if (isValidAppointmentID(appointmentID)) {
                // Check if the appointment ID already exists in the CSV file
                if (isAppointmentOutcomeRecorded(appointmentID)) {
                    System.out.println("Outcome already recorded. Please go to Update Patient Medical Records instead.");
                    return;
                }
                break;
            } else {
                System.out.println("Invalid Appointment ID. Please enter a valid ID.");
            }
        }

        // Get correct appointment date for validation
        String correctDate = getCorrectAppointmentDate(appointmentID);
        if (correctDate == null) {
            System.out.println("No appointment found with ID: " + appointmentID);
            return;
        }

        System.out.print("Enter diagnosis: ");
        String diagnosis = sc.nextLine();

        String prescription;
        while (true) {
            System.out.print("Enter prescription medicine (Paracetamol, Ibuprofen, Amoxicillin, NA): ");
            prescription = sc.nextLine().trim();
            if (isValidPrescription(prescription)) {
                prescription = prescription.toUpperCase(); // Convert to uppercase to match enum format
                break;
            } else {
                System.out.println("Invalid prescription. Please enter a valid prescription.");
            }
        }

        int quantity;
        while (true) {
            System.out.print("Enter prescription quantity: ");
            if (sc.hasNextInt()) {
                quantity = sc.nextInt();
                sc.nextLine(); // Clear newline
                if (quantity >= 0) {
                    break;
                } else {
                    System.out.println("Quantity must be 0 or a positive integer. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Quantity must be 0 or a positive integer.");
                sc.nextLine(); // Clear invalid input
            }
        }

        System.out.print("Enter treatment plan: ");
        String treatmentPlan = sc.nextLine();

        String date;
        while (true) {
            System.out.print("Enter date of appointment (e.g., DD-MM-YY): ");
            date = sc.nextLine();
            if (isValidDateFormat(date)) {
                if (date.equals(correctDate)) {
                    break;
                } else {
                    System.out.println("The entered date does not match the scheduled appointment date. Please enter the correct date.");
                }
            } else {
                System.out.println("Invalid date format. Please use DD-MM-YY format.");
            }
        }

        System.out.print("Enter type of service (e.g., consultation, X-ray): ");
        String typeOfService = sc.nextLine();
        System.out.print("Enter consultation notes: ");
        String notes = sc.nextLine();

        doctor.recordAppointmentOutcome(appointmentID, diagnosis, prescription, quantity, treatmentPlan, date, typeOfService, notes);
    }

    /**
     * Checks if the outcome of a specific appointment is already recorded.
     *
     * @param appointmentID The appointment ID to check
     * @return true if the outcome is recorded; false otherwise
     */
    private boolean isAppointmentOutcomeRecorded(String appointmentID) {
        try (BufferedReader br = new BufferedReader(new FileReader("resources/AppointmentRecord.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].equals(appointmentID)) {
                    return true; // Appointment outcome already recorded
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading AppointmentRecord.csv: " + e.getMessage());
        }
        return false; // Appointment outcome not recorded
    }

    /**
     * Checks if a prescription is valid by comparing it with a predefined list of valid prescriptions.
     *
     * @param prescription The prescription to validate
     * @return true if the prescription is valid; false otherwise
     */
    private boolean isValidPrescription(String prescription) {
        return VALID_PRESCRIPTIONS.contains(prescription.toUpperCase());
    }

    /**
     * Checks if an appointment ID is valid by looking it up in the appointment records.
     *
     * @param appointmentID The appointment ID to validate
     * @return true if the appointment ID is valid; false otherwise
     */
    private boolean isValidAppointmentID(String appointmentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves the correct date for a specific appointment based on the appointment ID.
     *
     * @param appointmentID The appointment ID to look up
     * @return The date of the appointment, or null if not found
     */
    private String getCorrectAppointmentDate(String appointmentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(appointmentID)) {
                    return fields[3];
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if the given date is in a valid format (DD-MM-YY).
     *
     * @param date The date to validate
     * @return true if the date format is valid; false otherwise
     */
    private boolean isValidDateFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}