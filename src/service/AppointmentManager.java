import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class AppointmentManager {

    private List<Appointment> appointments = new ArrayList<>();

    public void addAppointment(Appointment a) {

        appointments.add(a);

        String sql =
                "INSERT INTO appointments(doctor_name, patient_name, appointment_date, status) VALUES(?,?,?,?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, a.getDoctor().getName());
            stmt.setString(2, a.getPatient().getName());
            stmt.setTimestamp(3, Timestamp.valueOf(a.getDate()));
            stmt.setString(4, a.getStatus());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDoctorAvailable(Doctor doctor,
                                     LocalDateTime newDate) {

        for (Appointment a : appointments) {

            if (a.getDoctor().getName().equals(doctor.getName())) {

                LocalDateTime existing = a.getDate();
                LocalDateTime end = existing.plusMinutes(90);

                if (!newDate.isAfter(end)
                        && !newDate.isBefore(existing)) {
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

    public void cancelAppointment(String name,
                                  String password,
                                  UserManager userManager) {

        Patient p = userManager.loginPatient(name, password);

        if (p == null) {
            System.out.println("Invalid patient credentials.");
            return;
        }

        String sql =
                "UPDATE appointments SET status='CANCELLED' WHERE patient_name=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Appointment cancelled successfully.");
            } else {
                System.out.println("No appointment found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAppointments(UserManager userManager) {

        appointments.clear();

        String sql = "SELECT * FROM appointments";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String doctorName = rs.getString("doctor_name");
                String patientName = rs.getString("patient_name");
                LocalDateTime date =
                        rs.getTimestamp("appointment_date")
                                .toLocalDateTime();
                String status = rs.getString("status");

                Doctor doctor = null;
                Patient patient = null;

                for (Doctor d : userManager.getDoctors()) {
                    if (d.getName().equals(doctorName)) {
                        doctor = d;
                    }
                }

                patient = new Patient(patientName,
                        "",
                        new HashSet<>());

                if (doctor != null) {
                    appointments.add(new Appointment(
                            doctor,
                            patient,
                            date,
                            status
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}