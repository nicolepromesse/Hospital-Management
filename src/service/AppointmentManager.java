import java.time.LocalDateTime;
import java.util.*;

public class AppointmentManager {

    private List<Appointment> appointments = new ArrayList<>();

    public void addAppointment(Appointment a) {
        appointments.add(a);
        saveAppointments();
    }

    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime newDate) {
        for (Appointment a : appointments) {
            if (a.getDoctor().getName().equals(doctor.getName())) {
                LocalDateTime existing = a.getDate();
                LocalDateTime end = existing.plusMinutes(90);
                if (!newDate.isAfter(end) && !newDate.isBefore(existing)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showAllAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments available.");
            return;
        }
        for (Appointment a : appointments) {
            a.showInfo();
        }
    }

    public void cancelAppointment(String name, String password, UserManager userManager) {

        Patient p = userManager.loginPatient(name, password);

        if (p == null) {
            System.out.println("Invalid patient credentials.");
            return;
        }

        boolean found = false;

        for (Appointment a : appointments) {
            if (a.getPatient().getName().equals(name)) {
                a.setStatus("CANCELLED");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No appointment found.");
            return;
        }

        saveAppointments();
        System.out.println("Appointment cancelled successfully.");
    }

    public void saveAppointments() {
        List<String> lines = new ArrayList<>();
        for (Appointment a : appointments) {
            lines.add(
                a.getId() + "," +
                a.getDoctor().getName() + "," +
                a.getPatient().getName() + "," +
                a.getDate().toString() + "," +
                a.getStatus()
            );
        }
        FileUtil.writeLines("appointments.txt", lines);
    }

    public void loadAppointments(UserManager userManager) {

        List<String> lines = FileUtil.readLines("appointments.txt");

        for (String line : lines) {

            String[] p = line.split(",");

            String doctorName = p[1];
            String patientName = p[2];

            Doctor doctor = null;
            Patient patient = null;

            for (Doctor d : userManager.getDoctors()) {
                if (d.getName().equals(doctorName)) doctor = d;
            }

            for (String pl : FileUtil.readLines("patients.txt")) {
                String[] data = pl.split(",");
                if (data[0].equals(patientName)) {
                    patient = new Patient(data[0], data[1], new HashSet<>());
                }
            }

            if (doctor != null && patient != null) {
                appointments.add(new Appointment(
                        doctor,
                        patient,
                        LocalDateTime.parse(p[3]),
                        p[4]
                ));
            }
        }
    }
}