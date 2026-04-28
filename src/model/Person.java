public abstract class Person {

    protected String name;
    protected String password;

    public Person(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public abstract void displayRole();
}