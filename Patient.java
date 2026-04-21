public class Patient extends Person {
    private String[] conditions;

    public Patient(String name, int age, String[] conditions) {
        super(name, age);
        this.conditions = conditions;
    }

    public String[] getConditions() {
        return conditions;
    }

    @Override
    public void displayRole() {
        System.out.print("Role: Patient | Conditions: ");
        for (String condition : conditions) {
            System.out.print(condition + ", ");
        }
        System.out.println();
    }
}