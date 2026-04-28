import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    static Scanner input = new Scanner(System.in);

    static UserManager userManager = new UserManager();
    static AppointmentManager appointmentManager = new AppointmentManager();

    public static void main(String[] args) {

        userManager.loadDoctors();
        userManager.loadPatients();
        appointmentManager.loadAppointments(userManager);

        while (true) {

            System.out.println("\n===== MEDICAL APPOINTMENT SYSTEM =====");
            System.out.println("1. Register Doctor");
            System.out.println("2. Register Patient");
            System.out.println("3. Patient Login & Book Appointment");
            System.out.println("4. Doctor Login (View Appointments)");
            System.out.println("5. View All Appointments");
            System.out.println("6. Cancel Appointment");
            System.out.println("7. Exit");

            System.out.print("Choose option: ");

            try {
                int choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1 -> registerDoctor();
                    case 2 -> registerPatient();
                    case 3 -> patientFlow();
                    case 4 -> doctorFlow();
                    case 5 -> appointmentManager.showAllAppointments();
                    case 6 -> cancelAppointmentFlow();
                    case 7 -> {
                        System.out.println("System closed.");
                        return;
                    }
                    default -> System.out.println("Invalid option!");
                }

            } catch (Exception e) {
                System.out.println("Invalid input!");
                input.nextLine();
            }
        }
    }

    static boolean isEmpty(String v) {
        return v == null || v.trim().isEmpty();
    }

    static void registerDoctor() {

    String name;

    while (true) {
        System.out.print("Doctor name: ");
        name = input.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("Name is required.");
        } else {
            break;
        }
    }

    String password;

    while (true) {
        System.out.print("Password: ");
        password = input.nextLine();
        if (password.trim().isEmpty()) {
            System.out.println("Password is required.");
        } else {
            break;
        }
    }

    String specialization;

    while (true) {
        System.out.print("Specialization: ");
        specialization = input.nextLine();
        if (specialization.trim().isEmpty()) {
            System.out.println("Specialization is required.");
        } else {
            break;
        }
    }

    Doctor d = new Doctor(name, password, specialization);
    userManager.registerDoctor(d);
}

   static void registerPatient() {

    String name;

    while (true) {
        System.out.print("Patient name: ");
        name = input.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("Name is required.");
        } else {
            break;
        }
    }

    String password;

    while (true) {
        System.out.print("Password: ");
        password = input.nextLine();
        if (password.trim().isEmpty()) {
            System.out.println("Password is required.");
        } else {
            break;
        }
    }

    Set<String> conditions = new HashSet<>();

    Patient p = new Patient(name, password, conditions);
    userManager.registerPatient(p);
}

    static void patientFlow() {

        System.out.print("Name: ");
        String name = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        if (isEmpty(name) || isEmpty(password)) {
            System.out.println("Name and password are required.");
            return;
        }

        Patient p = userManager.loginPatient(name, password);

        if (p == null) {
            System.out.println("Name or password is incorrect.");
            return;
        }

        if (userManager.getDoctors().isEmpty()) {
            System.out.println("No doctors available.");
            return;
        }

        System.out.println("\nAvailable Doctors:");
        List<Doctor> doctors = userManager.getDoctors();

        for (int i = 0; i < doctors.size(); i++) {
            System.out.println((i + 1) + ". " +
                    doctors.get(i).getName() + " - " +
                    doctors.get(i).getSpecialization());
        }

        try {
            System.out.print("Choose doctor: ");
            int index = input.nextInt();
            input.nextLine();

            Doctor d = doctors.get(index - 1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            System.out.print("Enter date (dd-MM-yyyy HH:mm): ");
            String inputDate = input.nextLine();

            LocalDateTime date = LocalDateTime.parse(inputDate, formatter);

            if (date.isBefore(LocalDateTime.now())) {
                System.out.println("You cannot book an appointment in the past.");
                return;
            }

            if (!appointmentManager.isDoctorAvailable(d, date)) {
                System.out.println("Doctor not available at this time.");
                return;
            }

            Appointment a = new Appointment(d, p, date, "CONFIRMED");
            appointmentManager.addAppointment(a);

            System.out.println("Appointment booked successfully!");
            a.showInfo();

        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("Invalid date format. Use dd-MM-yyyy HH:mm");
        } catch (Exception e) {
            System.out.println("Booking error!");
            input.nextLine();
        }
    }

    static void doctorFlow() {

        System.out.print("Doctor name: ");
        String name = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        if (isEmpty(name) || isEmpty(password)) {
            System.out.println("Name and password are required.");
            return;
        }

        Doctor d = userManager.loginDoctor(name, password);

        if (d == null) {
            System.out.println("Doctor name or password is incorrect.");
            return;
        }

        System.out.println("Welcome Dr. " + d.getName());
        appointmentManager.showAllAppointments();
    }

    static void cancelAppointmentFlow() {

        System.out.print("Patient name: ");
        String name = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        if (isEmpty(name) || isEmpty(password)) {
            System.out.println("Name and password are required.");
            return;
        }

        appointmentManager.cancelAppointment(name, password, userManager);
    }
}