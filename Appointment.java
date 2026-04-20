public class Appointment {
    private Doctor doctor;
    private Patient patient;
    private String date;

    public Appointment(Doctor doctor, Patient patient, String date) {
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
    }

    public void showInfo() {
        System.out.println("Appointment Details:");
        System.out.println("Doctor: " + doctor.getName());
        System.out.println("Patient: " + patient.getName());
        System.out.println("Date: " + date);
    }
}