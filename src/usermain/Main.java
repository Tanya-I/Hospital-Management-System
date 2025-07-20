package usermain;

import enums.UserRole;
import java.io.*;
import java.util.*;
import usermenu.*;
import appointment.AppointmentService;
import appointment.DoctorAvailabilityService;

/**
 * The Main class for the Hospital Management System.
 * It handles user authentication and directs users to their specific menus
 * based on their roles (Patient, Doctor, Pharmacist, Administrator).
 */
public class Main {
    /**
     * The entry point of the application.
     * Handles user authentication and redirects to role-specific menus.
     *
     * @param args Command-line arguments (not used in this application).
     * @throws IOException if there is an error reading or writing to files.
     */
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String id, password, name = null;
        String role = null;
        String filePath = "resources/User.csv"; // Path to User.csv
        String patientFilePath = "resources/Patient_List.csv"; // Path to Patient_List.csv
        boolean authenticated = false;
        Object user = null;

        while (true) { // Main loop to restart login if needed
            System.out.println("==== Login ====");
            System.out.print("Enter your user ID: ");
            id = sc.nextLine();
            System.out.print("Enter your password: ");
            password = sc.nextLine();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean userFound = false;
                authenticated = false; // Reset authenticated for each login attempt
                br.readLine(); // Skip header

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    String userId = data[0];
                    String userPassword = data[1];
                    role = data[2];
                    name = data[3];

                    if (userId.equals(id) && userPassword.equals(password)) {
                        userFound = true;

                        if (password.equals("password")) {
                            System.out.println("You are using the default password. Please change your password.");

                            String newPassword, confirmPassword;
                            while (true) {
                                System.out.print("Enter new password: ");
                                newPassword = sc.nextLine();
                                System.out.print("Confirm new password: ");
                                confirmPassword = sc.nextLine();
                                if (newPassword.isEmpty()) {
                                    System.out.println("Password cannot be empty. Please try again.");
                                    continue; // Restart the loop to ask for a valid password
                                }

                                if (newPassword.equals(confirmPassword)) {
                                    updatePasswordInCSV(filePath, patientFilePath, id, newPassword);
                                    System.out.println("Password changed successfully. Please login again with your new password.\n");
                                    break;
                                } else {
                                    System.out.println("Passwords do not match. Please try again.");
                                }
                            }
                            // After password change, return to login loop
                            break;
                        }

                        authenticated = true;

                        if (role.equals(UserRole.PATIENT.name())) {
                            try (BufferedReader brPatient = new BufferedReader(new FileReader(patientFilePath))) {

                                String linePatient;
                                brPatient.readLine();

                                while ((linePatient = brPatient.readLine()) != null) {
                                    String[] dataPatient = linePatient.split(",");
                                    String patientId = dataPatient[0];

                                    if (patientId.equals(id)) {
                                        String dob = dataPatient[4];
                                        String gender = dataPatient[3];
                                        String contactNo = dataPatient[5];
                                        String email = dataPatient[6];
                                        String bloodType = dataPatient[7];
                                        String pastTreatment = dataPatient[8];

                                        AppointmentService appointmentService = new AppointmentService();
                                        DoctorAvailabilityService doctorAvailabilityService = new DoctorAvailabilityService();

                                        user = new Patient(patientId, password, role, name, dob, gender, contactNo, email, bloodType, pastTreatment, appointmentService, doctorAvailabilityService);
                                        break;
                                    }
                                }
                            }
                        } else if (role.equals(UserRole.DOCTOR.name())) {
                            AppointmentService appointmentService = new AppointmentService();
                            DoctorAvailabilityService doctorAvailabilityService = new DoctorAvailabilityService();
                            user = new Doctor(id, password, role, name, appointmentService, doctorAvailabilityService);
                        } else if (role.equals(UserRole.PHARMACIST.name())) {
                            user = new Pharmacist(id, password, role, name);
                        } else if (role.equals(UserRole.ADMINISTRATOR.name())) {
                            user = new Administrator();
                        }
                        break;
                    }
                }

                if (!userFound) {
                    System.out.println("Invalid user ID or password. Please try again.\n");
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }

            if (authenticated) {
                break; // Exit loop if authentication is successful
            }
        }

        // Once authenticated, use the role to show the corresponding menu
        AbstractMenu menu = null;

        switch (role) {
            case "PATIENT":
                menu = new PatientMenu((Patient) user);
                break;
            case "DOCTOR":
                menu = new DoctorMenu((Doctor) user);
                break;
            case "PHARMACIST":
                menu = new PharmacistMenu((Pharmacist) user);
                break;
            case "ADMINISTRATOR":
                menu = new AdministratorMenu((Administrator) user);
                break;
            default:
                System.out.println("Role not recognized.");
                return;
        }

        // Display the appropriate menu based on the role
        menu.displayMenu();
    }

    /**
     * Updates the password of a user in the specified CSV files.
     *
     * @param userFilePath      The file path of the User.csv file.
     * @param patientFilePath   The file path of the Patient_List.csv file.
     * @param userId            The ID of the user whose password needs to be updated.
     * @param newPassword       The new password to set for the user.
     * @throws IOException if there is an error reading or writing to the files.
     */
    private static void updatePasswordInCSV(String userFilePath, String patientFilePath, String userId, String newPassword) throws IOException {
        // Update password in User.csv
        List<String> userLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(userFilePath))) {
            String line;
            userLines.add(reader.readLine()); // Read header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(userId)) {
                    data[1] = newPassword;
                    line = String.join(",", data);
                }
                userLines.add(line);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFilePath))) {
            for (String updatedLine : userLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        }

        // Update password in Patient_List.csv (only if the user is a patient)
        List<String> patientLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(patientFilePath))) {
            String line;
            patientLines.add(reader.readLine()); // Read header

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(userId)) { // Check if this is the patient
                    data[1] = newPassword;
                    line = String.join(",", data);
                }
                patientLines.add(line);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(patientFilePath))) {
            for (String updatedLine : patientLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        }
    }
}
