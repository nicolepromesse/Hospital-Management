import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {

    private static int counter = 1000;
    private int id;

    private Doctor doctor;
    private Patient patient;
    private LocalDateTime date;
    private String status;

    public Appointment(Doctor doctor, Patient patient, LocalDateTime date, String status) {
        this.id = counter++;
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void showInfo() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        System.out.println("\n        APPOINTMENT RECEIPT");
        System.out.println("========================================");

        System.out.println("ID: " + id);
        System.out.println("Patient: " + patient.getName());
        System.out.println("Doctor: " + doctor.getName());
        System.out.println("Specialization: " + doctor.getSpecialization());
        System.out.println("Date: " + date.format(formatter));

        if (status == null || status.trim().isEmpty()) {
            System.out.println("Status: UNKNOWN");
        } else if (status.equalsIgnoreCase("PENDING") ||
                   status.equalsIgnoreCase("CANCELLED")) {
            System.out.println("Status: " + status + " (NOT CONFIRMED)");
        } else {
            System.out.println("Status: " + status);
        }

        System.out.println("========================================\n");
    }
}