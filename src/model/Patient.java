import java.util.Set;

public class Patient extends Person {

    private Set<String> conditions;

    public Patient(String name, String password, Set<String> conditions) {
        super(name, password);
        this.conditions = conditions;
    }

    public Set<String> getConditions() {
        return conditions;
    }

    @Override
    public void displayRole() {
        System.out.println("Patient");
    }
}