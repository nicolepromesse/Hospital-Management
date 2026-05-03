package org.example.hospitol.service;

import org.example.hospitol.model.Appointment;

import java.io.*;
import java.util.*;

/**
 * AppointmentManager — stores appointments in appointments.txt
 * Format per line: doctorName|patientName|date|time|status|payment
 */
public class AppointmentManager {

    private static final String FILE = "appointments.txt";

    private final List<Appointment> appointments = new ArrayList<>();
    private final Set<String> specializations    = new HashSet<>();
    private final Map<String, Appointment> byPatient = new HashMap<>();

    // ── Constructor: load from file on startup ─────────────────────────────
    public AppointmentManager() {
        loadFromFile();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CRUD
    // ══════════════════════════════════════════════════════════════════════

    public void addAppointment(Appointment a) {
        // Prevent duplicate booking: same doctor, same date, same time
        if (isDoctorBooked(a.getDoctorDisplayName(), a.getDate(), a.getTime())) {
            System.out.println("Doctor already booked at this time.");
            return;
        }

        appointments.add(a);
        if (a.getDoctor() != null) specializations.add(a.getDoctor().getSpecialization());
        byPatient.put(a.getPatientDisplayName(), a);
        saveToFile();
    }

    /** Update status of an appointment identified by its id. */
    public boolean updateStatus(int appointmentId, String newStatus) {
        for (Appointment a : appointments) {
            if (a.getId() == appointmentId) {
                a.setStatus(newStatus);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    /** Delete an appointment by id. */
    public boolean deleteAppointment(int appointmentId) {
        Iterator<Appointment> it = appointments.iterator();
        while (it.hasNext()) {
            Appointment a = it.next();
            if (a.getId() == appointmentId) {
                it.remove();
                byPatient.values().remove(a);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  QUERIES
    // ══════════════════════════════════════════════════════════════════════

    public List<Appointment> getAllAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorName) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (doctorName.equalsIgnoreCase(a.getDoctorDisplayName())) result.add(a);
        }
        return result;
    }

    public List<Appointment> getAppointmentsByPatient(String patientName) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (patientName.equalsIgnoreCase(a.getPatientDisplayName())) result.add(a);
        }
        return result;
    }

    public List<Appointment> getDoneAppointments() {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if ("Done".equalsIgnoreCase(a.getStatus())) result.add(a);
        }
        return result;
    }

    /** Return list of all doctors (for dropdowns) */
    public List<String> getAllDoctors() {
        Set<String> doctors = new HashSet<>();
        for (Appointment a : appointments) {
            String d = a.getDoctorDisplayName();
            if (d != null && !d.isEmpty()) doctors.add(d);
        }
        return new ArrayList<>(doctors);
    }

    /** Prevent duplicate booking: same doctor, same date, same time */
    public boolean isDoctorBooked(String doctor, String date, String time) {
        for (Appointment a : appointments) {
            if (a.getDoctorDisplayName().equalsIgnoreCase(doctor)
                    && a.getDate().equals(date)
                    && a.getTime().equals(time)) {
                return true;
            }
        }
        return false;
    }

    public void findAppointmentByPatient(String name) {
        Appointment a = byPatient.get(name);
        if (a != null) a.showInfo();
        else System.out.println("No appointment found for " + name);
    }

    public void showAllAppointments() {
        if (appointments.isEmpty()) { System.out.println("No appointments."); return; }
        for (Appointment a : appointments) {
            System.out.println("==============================");
            System.out.println("ID:           " + a.getId());
            System.out.println("Patient:      " + a.getPatientDisplayName());
            System.out.println("Doctor:       " + a.getDoctorDisplayName());
            System.out.println("Date:         " + a.getDate());
            System.out.println("Time:         " + a.getTime());
            System.out.println("Status:       " + a.getStatus());
            System.out.println("Payment:      " + a.getPayment());
            System.out.println("==============================");
        }
    }

    public void showSpecializations() {
        if (specializations.isEmpty()) { System.out.println("No specializations."); return; }
        System.out.println("\nDoctor Specializations:");
        specializations.forEach(s -> System.out.println("- " + s));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  FILE PERSISTENCE
    // ══════════════════════════════════════════════════════════════════════

    public void saveToFile() {
        try (FileWriter fw = new FileWriter(FILE, false)) {
            for (Appointment a : appointments) {
                fw.write(
                        escape(a.getDoctorDisplayName())  + "|" +
                                escape(a.getPatientDisplayName()) + "|" +
                                escape(a.getDate())               + "|" +
                                escape(a.getTime())               + "|" +
                                escape(a.getStatus())             + "|" +
                                a.getPayment()                    + "\n"
                );
            }
        } catch (IOException e) {
            System.err.println("Could not save appointments: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length == 6) {
                    // doctorName|patientName|date|time|status|payment
                    Appointment a = new Appointment(
                            p[0],   // doctorName
                            p[2],   // date
                            p[3]    // time
                    );
                    a.setStatus(p[4]);
                    a.setPatientName(p[1]);
                    appointments.add(a);
                    byPatient.put(p[1], a);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load appointments: " + e.getMessage());
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("|", "_");
    }
}
