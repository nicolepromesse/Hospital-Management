import java.io.*;
import java.util.*;

public class FileUtil {

    public static void writeLines(String fileName, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    public static List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }

        return lines;
    }
}