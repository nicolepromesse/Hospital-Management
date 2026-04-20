public class Patient extends Person {
    private String illness;

    public Patient(String name, int age, String illness) {
        super(name, age);
        this.illness = illness;
    }

    public String getIllness() {
        return illness;
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Patient");
    }
}