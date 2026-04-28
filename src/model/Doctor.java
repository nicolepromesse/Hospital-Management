public class Doctor extends Person {

    private String specialization;

    public Doctor(String name, String password, String specialization) {
        super(name, password);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Doctor | Specialization: " + specialization);
    }
}