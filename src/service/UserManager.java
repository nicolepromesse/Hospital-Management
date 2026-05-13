
import java.sql.*;
import java.util.*;

public class UserManager {

    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();

    public void registerPatient(Patient patient) {

        patients.add(patient);

        String sql =
                "INSERT INTO patients(name,password) VALUES(?,?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getPassword());

            stmt.executeUpdate();

            System.out.println("Patient registered successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerDoctor(Doctor doctor) {

        doctors.add(doctor);

        String sql =
                "INSERT INTO doctors(name,password,specialization) VALUES(?,?,?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getPassword());
            stmt.setString(3, doctor.getSpecialization());

            stmt.executeUpdate();

            System.out.println("Doctor registered successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Patient loginPatient(String name, String password) {

        String sql =
                "SELECT * FROM patients WHERE name=? AND password=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Patient(name, password, new HashSet<>());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Doctor loginDoctor(String name, String password) {

        String sql =
                "SELECT * FROM doctors WHERE name=? AND password=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String specialization = rs.getString("specialization");

                return new Doctor(name, password, specialization);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void loadDoctors() {

        doctors.clear();

        String sql = "SELECT * FROM doctors";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String name = rs.getString("name");
                String password = rs.getString("password");
                String specialization = rs.getString("specialization");

                doctors.add(new Doctor(name, password, specialization));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPatients() {

        patients.clear();

        String sql = "SELECT * FROM patients";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String name = rs.getString("name");
                String password = rs.getString("password");

                patients.add(new Patient(name, password, new HashSet<>()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}