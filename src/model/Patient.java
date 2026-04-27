import java.util.Set;

public class Patient extends Person {
    private Set<String> conditions;

    public Patient(String name, int age, Set<String> conditions) {
        super(name, age);
        this.conditions = conditions;
    }

    public Set<String> getConditions() {
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