import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);
    static final double FEE = 300;

    public static void main(String[] args) {

        try {
            System.out.println("========================================");
            System.out.println("     MEDICAL APPOINTMENT SYSTEM");
            System.out.println("========================================");

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
                return;
            }

            if (temp >= 30 && temp <= 37) {
                System.out.println("\nSTATUS: NORMAL");
                System.out.println("ACTION: Appointment is OPTIONAL.");

                System.out.print("Do you want to book appointment? (yes/no): ");
                String choice = input.nextLine().trim();

                if (choice.equalsIgnoreCase("yes")) {
                    bookAppointment("OPTIONAL");
                } else {
                    System.out.println("No appointment made.");
                }
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
            throw new InvalidAppointmentException("Conditions cannot be 0 or negative.");
        }

        String[] conditions = new String[count];

        for (int i = 0; i < count; i++) {
            conditions[i] = readNonEmpty("Enter condition " + (i + 1) + ": ");
        }

        System.out.println("\n--- Doctor Information ---");
        String doctorName = readNonEmpty("Enter doctor name: ");
        String specialization = readNonEmpty("Enter specialization: ");

        System.out.println("\n--- Schedule ---");
        String date = readNonEmpty("Enter appointment date: ");

        if (date.length() < 5) {
            throw new InvalidAppointmentException("Invalid appointment date.");
        }

        double payment = readPayment("Enter payment (Fee = 300): ");

        String finalStatus = evaluateStatus(count, payment);

        Patient patient = new Patient(patientName, age, conditions);
        Doctor doctor = new Doctor(doctorName, 35, specialization);

        Appointment appointment = new Appointment(doctor, patient, date, finalStatus, payment);
        appointment.showInfo();
    }

    static double readTemperature() {
        while (true) {
            try {
                System.out.print("\nEnter patient temperature (°C): ");

                if (!input.hasNextDouble()) {
                    input.nextLine();
                    throw new InvalidTemperatureException("Temperature must be a number.");
                }

                double temp = input.nextDouble();
                input.nextLine();

                if (temp < 0) {
                    throw new InvalidTemperatureException("Temperature cannot be negative.");
                }

                return temp;

            } catch (InvalidTemperatureException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    static String readNonEmpty(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String value = input.nextLine().trim();

                if (value.isEmpty()) {
                    throw new EmptyFieldException("Field cannot be empty.");
                }

                return value;

            } catch (EmptyFieldException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    static int readAge(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);

                if (!input.hasNextInt()) {
                    input.nextLine();
                    throw new InvalidAgeException("Must be a number.");
                }

                int age = input.nextInt();
                input.nextLine();

                if (age <= 0 || age > 120) {
                    throw new InvalidAgeException("Age must be 1 to 120.");
                }

                return age;

            } catch (InvalidAgeException e) {
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
}