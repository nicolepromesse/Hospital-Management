package org.example.hospitol.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Appointment {

    private static int counter = 1000;
    private final int id;

    // Full object references (used when booked via UI)
    private Doctor  doctor;
    private Patient patient;

    // String fallbacks (used when loaded from file)
    private String doctorName;
    private String patientName;

    private String date;
    private String time;
    private String status;
    private double payment;

    // ── Full constructor (UI booking) ──────────────────────────────────────
    public Appointment(Doctor doctor, Patient patient,
                       String date, String time, String status, double payment) {
        this.id          = counter++;
        this.doctor      = doctor;
        this.patient     = patient;
        this.doctorName  = doctor  != null ? doctor.getName()  : "";
        this.patientName = patient != null ? patient.getName() : "";
        this.date        = date;
        this.time        = time;
        this.status      = status;
        this.payment     = payment;
    }

    // ── Simple string constructor (file loading / quick booking) ───────────
    public Appointment(String doctorName, String date, String time) {
        this.id          = counter++;
        this.doctorName  = doctorName != null ? doctorName : "";
        this.patientName = "Unknown";
        this.date        = date;
        this.time        = time;
        this.status      = "Pending";
        this.payment     = 0.0;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public int    getId()      { return id; }
    public Doctor getDoctor()  { return doctor; }
    public Patient getPatient(){ return patient; }
    public String getDate()    { return date; }
    public String getTime()    { return time; }
    public String getStatus()  { return status; }
    public double getPayment() { return payment; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setStatus(String status)      { this.status = status; }
    public void setPatientName(String name)   { this.patientName = name; }

    // ── Display helpers (work regardless of which constructor was used) ─────
    public String getDoctorDisplayName() {
        return doctor != null ? doctor.getName() : (doctorName != null ? doctorName : "");
    }

    public String getPatientDisplayName() {
        return patient != null ? patient.getName() : (patientName != null ? patientName : "");
    }

    // ── JavaFX Property methods (TableView column binding) ─────────────────
    public StringProperty doctorProperty() {
        return new SimpleStringProperty(getDoctorDisplayName());
    }

    public StringProperty patientProperty() {
        return new SimpleStringProperty(getPatientDisplayName());
    }

    public StringProperty dateProperty() {
        return new SimpleStringProperty(date != null ? date : "");
    }

    public StringProperty timeProperty() {
        return new SimpleStringProperty(time != null ? time : "");
    }

    public StringProperty statusProperty() {
        return new SimpleStringProperty(status != null ? status : "");
    }

    // ── Console info ───────────────────────────────────────────────────────
    public void showInfo() {
        System.out.println("        APPOINTMENT RECEIPT");
        System.out.println("========================================");
        System.out.println("ID:      " + id);
        System.out.println("Patient: " + getPatientDisplayName());
        if (patient != null) {
            System.out.println("Age:     " + patient.getAge());
            System.out.print("Conditions: ");
            patient.getConditions().forEach(c -> System.out.print(c + " "));
            System.out.println();
        }
        System.out.println("Doctor:  " + getDoctorDisplayName());
        if (doctor != null) System.out.println("Spec:    " + doctor.getSpecialization());
        System.out.println("Date:    " + date + "  Time: " + time);
        System.out.println("Payment: " + payment);
        System.out.println("Status:  " + status);
        System.out.println("========================================\n");
    }
}