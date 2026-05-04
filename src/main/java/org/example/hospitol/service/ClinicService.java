package org.example.hospitol.service;

import org.example.hospitol.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClinicService {

    private static final ClinicService INSTANCE = new ClinicService();
    public static ClinicService getInstance() { return INSTANCE; }
    private ClinicService() {}

    private final List<Doctor>      doctors      = new ArrayList<>();
    private final List<Patient>     patients     = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    public void registerDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctors;
    }

    public List<String> getUniqueSpecialties() {
        return doctors.stream()
                .map(Doctor::getSpecialization)
                .distinct()
                .collect(Collectors.toList());
    }

    public void registerPatient(Patient patient) {
        patients.add(patient);
    }

    public List<Patient> getAllPatients() {
        return patients;
    }

    public boolean bookAppointment(Patient patient, Doctor doctor, LocalDateTime dateTime) {
        String date = dateTime.toLocalDate().toString();
        String time = dateTime.toLocalTime().toString();

        for (Appointment a : appointments) {
            if (a.getDoctorDisplayName().equalsIgnoreCase(doctor.getName())
                    && a.getDate().equals(date)
                    && a.getTime().equals(time)) {
                return false;
            }
        }

        appointments.add(new Appointment(doctor, patient, date, time, "BOOKED", 0.0));
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

    public void addMedicalRecord(Patient patient, MedicalRecords<?> record) {
        patient.getConditions().add(record.getData().toString());
    }
}