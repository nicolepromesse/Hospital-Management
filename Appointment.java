public class Appointment {

    private static int counter = 1000;
    private int id;

    private Doctor doctor;
    private Patient patient;
    private String date;
    private String status;
    private double payment;

    public Appointment(Doctor doctor, Patient patient, String date, String status, double payment) {
        this.id = counter++;
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
        this.status = status;
        this.payment = payment;
    }

    public void showInfo() {

        System.out.println("\n========================================");
        System.out.println("        APPOINTMENT RECEIPT");
        System.out.println("========================================");

        System.out.println("ID: " + id);
        System.out.println("Patient: " + patient.getName());
        System.out.println("Age: " + patient.getAge());

        System.out.print("Conditions: ");
        for (String c : patient.getConditions()) {
            System.out.print(c + " ");
        }

        System.out.println("\nDoctor: " + doctor.getName());
        System.out.println("Specialization: " + doctor.getSpecialization());
        System.out.println("Payment: " + payment);
        if (status.trim().equalsIgnoreCase("PENDING") || status.trim().equalsIgnoreCase("CANCELLED")) {
    System.out.println("Status: " + status + " (INSUFFICIENT AMOUNT)");
} else {
    System.out.println("Status: " + status);
}

        System.out.println("========================================\n");
    }
}