import java.util.*;

public class UserManager {

    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();

    public void registerPatient(Patient p) {
        patients.add(p);
        savePatients();
        System.out.println("Patient registered successfully!");
    }

    public void registerDoctor(Doctor d) {
        doctors.add(d);
        saveDoctors();
        System.out.println("Doctor registered successfully!");
    }

    public Patient loginPatient(String name, String password) {
        for (Patient p : patients) {
            if (p.getName().equals(name) &&
                p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }

    public Doctor loginDoctor(String name, String password) {
        for (Doctor d : doctors) {
            if (d.getName().equals(name) &&
                d.getPassword().equals(password)) {
                return d;
            }
        }
        return null;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void saveDoctors() {
        List<String> lines = new ArrayList<>();
        for (Doctor d : doctors) {
            lines.add(d.getName() + "," + d.getPassword() + "," + d.getSpecialization());
        }
        FileUtil.writeLines("doctors.txt", lines);
    }

    public void savePatients() {
        List<String> lines = new ArrayList<>();
        for (Patient p : patients) {
            lines.add(p.getName() + "," + p.getPassword());
        }
        FileUtil.writeLines("patients.txt", lines);
    }

    public void loadDoctors() {
        List<String> lines = FileUtil.readLines("doctors.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            doctors.add(new Doctor(parts[0], parts[1], parts[2]));
        }
    }

    public void loadPatients() {
        List<String> lines = FileUtil.readLines("patients.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            patients.add(new Patient(parts[0], parts[1], new HashSet<>()));
        }
    }
}