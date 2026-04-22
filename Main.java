import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class Main {

    static Scanner input = new Scanner(System.in);
    static final double FEE = 300;
    static AppointmentManager manager = new AppointmentManager();
  

    public static void main(String[] args) {
        seedData();

        try {
            System.out.println("     MEDICAL APPOINTMENT SYSTEM");
            System.out.println("========================================");

            System.out.println("1. enter as Patient");
            System.out.println("2. enter as Doctor");
            System.out.print("Choose option: ");
            int role = input.nextInt();
            input.nextLine();

            if (role == 1) {

                double temp = readTemperature();

                if (temp < 30 || temp > 40) {
                    System.out.println("\nSTATUS: EMERGENCY");
                    System.out.println("ACTION: Go to hospital immediately!");
                    return;
                }

                if (temp >= 38 && temp <= 40) {
                    System.out.println("\nSTATUS: WARNING");
                    System.out.println("ACTION: Appointment is REQUIRED.");
                    bookAppointment("REQUIRED");

                    manager.showAllAppointments();
                    manager.showSpecializations();
                    return;
                }

                if (temp >= 30 && temp <= 37) {
                    System.out.println("\nSTATUS: NORMAL");
                    System.out.println("ACTION: Appointment is OPTIONAL.");

                    System.out.print("Do you want to book appointment? (yes/no): ");
                    String choice = input.nextLine().trim();

                    if (choice.equalsIgnoreCase("yes")) {
                        bookAppointment("OPTIONAL");

                        manager.showAllAppointments();
                        manager.showSpecializations();
                    } else {
                        System.out.println("No appointment made.");
                    }
                }

            } else if (role == 2) {

                System.out.print("Enter doctor password: ");
                String password = input.nextLine();

                if (!password.equals("123")) {
                    System.out.println("ACCESS DENIED! you passaword is incorret");
                    return;
                }

                System.out.println("\n========== DOCTOR DASHBOARD ==========");
                manager.showAllAppointments();
                manager.showSpecializations();

            } else {
                System.out.println("Invalid option");
            }

        } catch (Exception e) {
            System.out.println("SYSTEM ERROR: " + e.getMessage());
        } finally {
            input.close();
            System.out.println("\nSystem closed safely.");
        }
    }

    static void bookAppointment(String status) {

        System.out.println("\n--- Patient Information ---");
        String patientName = readNonEmpty("Enter patient name: ");
        int age = readAge("Enter age: ");

        int count = readAge("How many conditions do you have? ");

        if (count <= 0) {
            throw new RuntimeException("Conditions cannot be 0 or negative.");
        }

        Set<String> conditions = new HashSet<>();

        for (int i = 0; i < count; i++) {
            conditions.add(readNonEmpty("Enter condition " + (i + 1) + ": "));
        }

        System.out.println("\n--- Doctor Information ---");
        String doctorName = readNonEmpty("Enter doctor name: ");
        String specialization = readNonEmpty("Enter specialization: ");

        System.out.println("\n--- Schedule ---");
        String date = readNonEmpty("Enter appointment date: ");

        if (date.length() < 5) {
            throw new RuntimeException("Invalid appointment date.");
        }

        double payment = readPayment("Enter payment (Fee = 300): ");

        String finalStatus = evaluateStatus(count, payment);

        Patient patient = new Patient(patientName, age, conditions);
        Doctor doctor = new Doctor(doctorName, 35, specialization);

        Appointment appointment = new Appointment(doctor, patient, date, finalStatus, payment);

        manager.addAppointment(appointment);
    }

    static double readTemperature() {
        while (true) {
            try {
                System.out.print("\nEnter patient temperature (°C): ");

                if (!input.hasNextDouble()) {
                    input.nextLine();
                    throw new RuntimeException("Temperature must be a number.");
                }

                double temp = input.nextDouble();
                input.nextLine();

                if (temp < 0) {
                    throw new RuntimeException("Temperature cannot be negative.");
                }

                return temp;

            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("ERROR: Field cannot be empty.");
        }
    }

    static int readAge(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);

                if (!input.hasNextInt()) {
                    input.nextLine();
                    throw new RuntimeException("Must be a number.");
                }

                int age = input.nextInt();
                input.nextLine();

                if (age <= 0 || age > 120) {
                    throw new RuntimeException("Invalid age range.");
                }

                return age;

            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    static double readPayment(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);

                if (!input.hasNextDouble()) {
                    input.nextLine();
                    throw new RuntimeException("Payment must be a number.");
                }

                double p = input.nextDouble();
                input.nextLine();

                if (p < 0) {
                    throw new RuntimeException("Invalid payment.");
                }

                return p;

            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    static String evaluateStatus(int count, double payment) {

        if (count >= 4) {
            return "HIGH RISK - EMERGENCY";
        }

        boolean fullPayment = payment >= FEE;

        if (count == 1) {
            return fullPayment ? "CONFIRMED" : "CANCELLED";
        }

        if (count == 2 || count == 3) {
            return fullPayment ? "CONFIRMED" : "PENDING";
        }

        return "PENDING";
    }

    static void seedData() {

    Set<String> c1 = new HashSet<>();
    c1.add("fever");

    Set<String> c2 = new HashSet<>();
    c2.add("cold");

    Set<String> c3 = new HashSet<>();
    c3.add("headache");

    Patient p1 = new Patient("Alice", 25, c1);
    Patient p2 = new Patient("Bob", 30, c2);
    Patient p3 = new Patient("John", 40, c3);

    Doctor d1 = new Doctor("Dr Smith", 45, "Cardiology");
    Doctor d2 = new Doctor("Dr Jane", 38, "Dermatology");

    manager.addAppointment(new Appointment(d1, p1, "10/02", "CONFIRMED", 300));
    manager.addAppointment(new Appointment(d2, p2, "11/02", "PENDING", 200));
    manager.addAppointment(new Appointment(d1, p3, "12/02", "CONFIRMED", 300));
}
}