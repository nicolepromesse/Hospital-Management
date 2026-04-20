import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter your temperature: ");
        double temp = input.nextDouble();
        input.nextLine();

        if (temp > 40) {
            System.out.println("STATUS: EMERGENCY");
            System.out.println("Go to hospital immediately! No appointment allowed.");
        }

        else if (temp >= 38 && temp <= 40) {
            System.out.println("STATUS: WARNING");
            System.out.println("You MUST make an appointment now.");

            System.out.print("Enter patient name: ");
            String name = input.nextLine();

            System.out.print("Enter age: ");
            int age = input.nextInt();
            input.nextLine();

            System.out.print("Enter doctor name: ");
            String doctor = input.nextLine();

            System.out.print("Enter appointment date: ");
            String date = input.nextLine();

            System.out.println("\nAppointment CONFIRMED for:");
            System.out.println("Name: " + name);
            System.out.println("Age: " + age);
            System.out.println("Doctor: " + doctor);
            System.out.println("Date: " + date);
        }

        else {
            System.out.println("STATUS: NORMAL");
            System.out.println("You can choose to make an appointment.");

            System.out.print("Do you want to book an appointment? (yes/no): ");
            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("yes")) {

                System.out.print("Enter patient name: ");
                String name = input.nextLine();

                System.out.print("Enter age: ");
                int age = input.nextInt();
                input.nextLine();

                System.out.print("Enter doctor name: ");
                String doctor = input.nextLine();

                System.out.print("Enter appointment date: ");
                String date = input.nextLine();

                System.out.println("\nAppointment BOOKED:");
                System.out.println("Name: " + name);
                System.out.println("Age: " + age);
                System.out.println("Doctor: " + doctor);
                System.out.println("Date: " + date);
            }

            else {
                System.out.println("No appointment made.");
            }
        }

        input.close();
    }
}