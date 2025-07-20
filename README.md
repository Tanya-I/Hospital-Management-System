# Hospital Management System (HMS)    
---

## **Introduction**  
The Hospital Management System (HMS) is designed to streamline hospital operations by managing roles such as Doctors, Pharmacists, Patients, and Administrators. It automates tasks like appointment scheduling, inventory management, and billing while adhering to Object-Oriented Programming (OOP) principles and SOLID design principles.

Link to JavaDoc: https://madhav-byte-debug.github.io/SC2002-Project/ 

The architecture ensures modularity and scalability through high-level and low-level packages, reducing dependencies and enhancing maintainability.  
---

## **System Design Principles**  

### **SOLID Design Principles**  

1. **Single Responsibility Principle (SRP)**:  
   - Classes such as `PharmacistMenu`, `AdministratorMenu`, and `DoctorMenu` focus solely on managing menu options for specific user roles. This separation improves code organization and reduces dependencies.  

2. **Open/Closed Principle (OCP)**:  
   - The `User` class serves as a base class, extended by `Doctor`, `Patient`, `Pharmacist`, and `Administrator`. This allows adding user-specific behavior without modifying the core `User` class, ensuring stability during extensions.  

3. **Liskov Substitution Principle (LSP)**:  
   - Subclasses like `Doctor` and `Patient` inherit from the `User` class and can replace `User` objects without affecting the program's functionality. All subclasses implement shared attributes like `id` and `password` while adding specific functionality.  

4. **Interface Segregation Principle (ISP)**:  
   - Interfaces like `Menu`, `DoctorAvailabilityManager`, and `AppointmentManager` ensure implementing classes only focus on relevant methods, avoiding unnecessary dependencies.  
   - For example, `DoctorAvailabilityManager` includes methods for setting and viewing doctor availability, while `AppointmentManager` handles scheduling, rescheduling, and canceling appointments.  

5. **Dependency Injection Principle (DIP)**:  
   - Dependencies are injected rather than created internally, enhancing flexibility and maintainability. In the `Main` class, objects like `Doctor` or `Patient` are instantiated through dependency injection, centralizing configuration and reducing coupling.  
---
**Object-Oriented Principles**:  
   - **Abstraction**: Interfaces like `DoctorAvailabilityManager` hide implementation details, simplifying testing and extending functionality.  
   - **Inheritance**: Classes like `Doctor` and `Patient` inherit common attributes from `User`, promoting code reuse.  
   - **Encapsulation**: Private attributes are accessed and modified only through authorized getters, setters, and relevant methods, protecting data integrity. 
---

## **Additional Design Features**  

1. **Enumeration-Based State Tracking**:  
   - Enums such as `PrescriptionStatus` (PENDING, DISPENSED) and `BillStatus` (PENDING, PAID) improve code readability, prevent invalid values, and centralize status management.  

2. **Billing & Feedback**:  
   - Completed appointments trigger billing and feedback options. Bills are linked to appointments via `Bill.csv`, and the `BillStatus` enum ensures accurate status tracking. Feedback collection enhances user engagement and service insights.   
---

## **System Design Approach**  
The system is divided into:  
- **High-Level Package**:  
  - `UserMenu`: Manages user interface components for role-specific interactions.  
- **Low-Level Packages**:  
  - `UserMain`: Manages user roles and responsibilities.  
  - `Appointment`: Handles scheduling, rescheduling, and cancellations.  

This modular design ensures minimal impact of changes and promotes scalability.  

---

## **Features by Role**  

### **General Features**  
- User authentication via unique Hospital ID (e.g., D001, P003) and default password.  
- Role-specific menus with tailored functionality.  

### **Patient**  
- View and update personal medical records (non-medical fields only).  
- Book, reschedule, or cancel one appointment at a time.  
- View appointment status and past outcomes.  

### **Doctor**  
- View and update patient medical records.  
- Set availability (mandatory for appointment scheduling).  
- Manage schedules and record appointment outcomes.  

### **Pharmacist**  
- Manage prescriptions and update statuses (e.g., PENDING → DISPENSED).  
- Monitor medication inventory and submit replenishment requests.  

### **Administrator**  
- Add, update, or remove staff accounts.  
- Approve replenishment requests and manage inventory.  
- View detailed appointment reports.  

---

## **Assumptions**  

1. Users are based in Singapore (8-digit phone numbers).  
2. User IDs follow the format `[Role][ThreeDigits]` (e.g., D001, A003).  
3. Patient accounts are pre-existing; administrators can add new staff.  
4. Only three medicines are dispensed:  
   - Paracetamol: $0.125/unit  
   - Ibuprofen: $0.50/unit  
   - Amoxicillin: $0.95/unit  
5. Doctors must set availability before appointments can be booked.  
6. Input validation prompts users to re-enter invalid inputs.  
7. Employee ages are restricted to 18–100 years.  

---

