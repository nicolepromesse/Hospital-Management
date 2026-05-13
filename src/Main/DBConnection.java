import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:2000/Hospital";

    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    public static Connection connect() {
        try {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    } catch (Exception e) {
        System.out.println("Database connection failed!");
        e.printStackTrace();
        return null;
    }
    }
}