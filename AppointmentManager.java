import java.util.*;

public class AppointmentManager {

    private List<Appointment> appointments;
    private Set<String> specializations;
    private Map<String, Appointment> appointmentMap;

    public AppointmentManager() {
        appointments = new ArrayList<>();
        specializations = new HashSet<>();
        appointmentMap = new HashMap<>();
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        specializations.add(appointment.getDoctor().getSpecialization());
        appointmentMap.put(appointment.getPatient().getName(), appointment);
    }

    public void showAllAppointments() {

        if (appointments.isEmpty()) {
            System.out.println("No appointments available.");
            return;
        }

        for (Appointment a : appointments) {

            System.out.println("\n==============================");
            System.out.println("ID: " + a.getId());
            System.out.println("Patient: " + a.getPatient().getName());
            System.out.println("Age: " + a.getPatient().getAge());
            System.out.println("Conditions: " + a.getPatient().getConditions());
            System.out.println("Doctor: " + a.getDoctor().getName());
            System.out.println("Specialization: " + a.getDoctor().getSpecialization());
            System.out.println("Date: " + a.getDate());
            System.out.println("Payment: " + a.getPayment());
            System.out.println("Status: " + a.getStatus());
            System.out.println("==============================");
        }
    }

    public void findAppointmentByPatient(String name) {
        Appointment a = appointmentMap.get(name);
        if (a != null) {
            a.showInfo();
        } else {
            System.out.println("No appointment found for " + name);
        }
    }

    public void removeAppointment(String name) {
        Appointment a = appointmentMap.remove(name);
        if (a != null) {
            appointments.remove(a);
            System.out.println("Appointment removed.");
        } else {
            System.out.println("No appointment to remove.");
        }
    }

    public void showSpecializations() {

        if (specializations.isEmpty()) {
            System.out.println("No specializations available.");
            return;
        }

        System.out.println("\nDoctor Specializations:");
        for (String s : specializations) {
            System.out.println("- " + s);
        }
    }
}