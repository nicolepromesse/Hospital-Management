package org.example.hospitol.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class AuthController implements Initializable {

    // ── Login card ─────────────────────────────────────────────────────────────
    @FXML private VBox loginCard;
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginError;

    // ── Signup card ────────────────────────────────────────────────────────────
    @FXML private VBox signupCard;
    @FXML private TextField signupEmail;
    @FXML private TextField signupUsername;
    @FXML private PasswordField signupPassword;
    @FXML private ComboBox<String> signupRole;
    @FXML private Label signupError;
    @FXML private Label signupSuccess;

    // ── Patterns ───────────────────────────────────────────────────────────────
    private static final Pattern HAS_LETTER  = Pattern.compile("[a-zA-Z]");
    private static final Pattern HAS_NUMBER  = Pattern.compile("[0-9]");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[^a-zA-Z0-9]");

    // ── File storage ───────────────────────────────────────────────────────────
    private static final String USERS_FILE = "users.txt";
    private final Map<String, String[]> users = new HashMap<>();
    // users map: username -> [password, role, email]

    // ══════════════════════════════════════════════════════════════════════════
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        signupRole.getItems().addAll("Patient", "Doctor");

        loadUsersFromFile();

        // Seed default accounts
        if (!users.containsKey("doctor")) {
            users.put("doctor", new String[]{"Doctor@123", "Doctor", "doctor@hospital.com"});
            saveUsersToFile();
        }
        if (!users.containsKey("patient")) {
            users.put("patient", new String[]{"Patient@123", "Patient", "patient@hospital.com"});
            saveUsersToFile();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LOGIN → navigate to correct dashboard
    // ══════════════════════════════════════════════════════════════════════════
    @FXML
    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();

        loginError.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            shake(loginError, "Please fill in all fields.");
            return;
        }
        if (!users.containsKey(username)) {
            shake(loginError, "Account not found. Please sign up.");
            return;
        }
        String[] data = users.get(username);
        if (!data[0].equals(password)) {
            shake(loginError, "Incorrect password. Try again.");
            return;
        }

        // ── Navigate to the correct dashboard ─────────────────────────────────
        String role = data[1]; // "Doctor" or "Patient"
        try {
            String fxml = role.equals("Doctor")
                    ? "/org/example/hospitol/DoctorDashboard.fxml"
                    : "/org/example/hospitol/PatientDashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());

            // Pass username to patient dashboard so it filters appointments
            if (role.equals("Patient")) {
                PatientDashboardController ctrl = loader.getController();
                ctrl.setCurrentPatient(username);
            }

            Stage stage = (Stage) loginUsername.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hospital Management System — " + role + " Dashboard");
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (IOException e) {
            shake(loginError, "Could not load dashboard. Check FXML files.");
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SIGNUP
    // ══════════════════════════════════════════════════════════════════════════
    @FXML
    private void handleSignup() {
        String email    = signupEmail.getText().trim();
        String username = signupUsername.getText().trim();
        String password = signupPassword.getText().trim();
        String role     = signupRole.getValue();

        signupError.setText("");
        signupSuccess.setText("");

        // Required fields
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || role == null) {
            shake(signupError, "Please fill in all fields.");
            return;
        }

        // Email must be valid
        if (!email.contains("@") || !email.contains(".")) {
            shake(signupError, "Email must be valid (e.g. name@mail.com).");
            return;
        }

        // Password strength
        if (!HAS_LETTER.matcher(password).find()) {
            shake(signupError, "Password must contain at least one letter.");
            return;
        }
        if (!HAS_NUMBER.matcher(password).find()) {
            shake(signupError, "Password must contain at least one number.");
            return;
        }
        if (!HAS_SPECIAL.matcher(password).find()) {
            shake(signupError, "Password must contain at least one special character (!@#…).");
            return;
        }
        if (password.length() < 6) {
            shake(signupError, "Password must be at least 6 characters.");
            return;
        }

        // Username uniqueness
        if (users.containsKey(username)) {
            shake(signupError, "Username already taken. Choose another.");
            return;
        }

        // Save
        users.put(username, new String[]{password, role, email});
        saveUsersToFile();

        // Success → auto-switch to login after 1.5s with username pre-filled
        signupSuccess.setText("Account created! Redirecting to sign in…");
        signupSuccess.setStyle("-fx-text-fill: #38a169; -fx-font-weight: bold;");

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            clearSignupFields();
            showLogin();
            loginUsername.setText(username); // pre-fill for convenience
        });
        pause.play();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  NAVIGATION
    // ══════════════════════════════════════════════════════════════════════════
    @FXML
    private void showSignup() {
        loginCard.setVisible(false);
        loginCard.setManaged(false);
        signupCard.setVisible(true);
        signupCard.setManaged(true);
        loginError.setText("");
    }

    @FXML
    private void showLogin() {
        signupCard.setVisible(false);
        signupCard.setManaged(false);
        loginCard.setVisible(true);
        loginCard.setManaged(true);
        signupError.setText("");
        signupSuccess.setText("");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FILE PERSISTENCE
    // ══════════════════════════════════════════════════════════════════════════
    private void loadUsersFromFile() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    users.put(parts[0], new String[]{parts[1], parts[2], parts[3]});
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load users: " + e.getMessage());
        }
    }

    private void saveUsersToFile() {
        try (FileWriter fw = new FileWriter(USERS_FILE, false)) {
            for (Map.Entry<String, String[]> entry : users.entrySet()) {
                String[] d = entry.getValue();
                fw.write(entry.getKey() + "|" + d[0] + "|" + d[1] + "|" + d[2] + "\n");
            }
        } catch (IOException e) {
            System.err.println("Could not save users: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ANIMATION
    // ══════════════════════════════════════════════════════════════════════════
    private void shake(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 12px;");
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), label);
        tt.setFromX(0); tt.setByX(8);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> label.setTranslateX(0));
        tt.play();
    }

    private void clearSignupFields() {
        signupEmail.clear();
        signupUsername.clear();
        signupPassword.clear();
        signupRole.setValue(null);
        signupError.setText("");
        signupSuccess.setText("");
    }
}