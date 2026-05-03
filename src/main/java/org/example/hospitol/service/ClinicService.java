package org.example.hospitol.service;

import org.example.hospitol.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClinicService {
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    // Register new doctor
    public void registerDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    // Register new patient
    public void registerPatient(Patient patient) {
        patients.add(patient);
    }

    public List<Doctor> getAllDoctors() {
        return doctors;
    }

    public List<Patient> getAllPatients() {
        return patients;
    }

    public List<String> getUniqueSpecialties() {
        return doctors.stream()
                .map(Doctor::getSpecialization)
                .distinct()
                .collect(Collectors.toList());
    }

    // Book appointment with duplicate prevention
    public boolean bookAppointment(Patient patient, Doctor doctor, LocalDateTime dateTime) {
        String date = dateTime.toLocalDate().toString();  // e.g. "2026-05-05"
        String time = dateTime.toLocalTime().toString();  // e.g. "10:00"

        // Prevent duplicate booking: same doctor, same date, same time
        for (Appointment a : appointments) {
            if (a.getDoctorDisplayName().equalsIgnoreCase(doctor.getName())
                    && a.getDate().equals(date)
                    && a.getTime().equals(time)) {
                System.out.println("Doctor " + doctor.getName() + " is already booked at " + date + " " + time);
                return false;
            }
        }

        appointments.add(new Appointment(
                doctor,
                patient,
                date,
                time,
                "BOOKED",
                0.0
        ));
        return true;
    }

    public List<Appointment> getAllAppointments() {
        return appointments;
    }

    public List<Appointment> getAppointmentsByPatient(String name) {
        return appointments.stream()
                .filter(a -> a.getPatientDisplayName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByDoctor(String name) {
        return appointments.stream()
                .filter(a -> a.getDoctorDisplayName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    // Add medical record
    public void addMedicalRecord(Patient patient, MedicalRecords<?> record) {
        patient.getConditions().add(record.getData().toString());
    }
}
