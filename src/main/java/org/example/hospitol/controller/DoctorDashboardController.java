package org.example.hospitol.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.hospitol.model.Appointment;
import org.example.hospitol.service.AppointmentManager;
import org.example.hospitol.service.ClinicalRecordStore;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DoctorDashboardController {

    @FXML private TableView<Appointment> doctorAppointmentsTable;
    @FXML private TableColumn<Appointment, String> patientColumn;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private Label statusLabel;

    private final AppointmentManager manager = new AppointmentManager();
    private final ClinicalRecordStore recordStore = ClinicalRecordStore.getInstance();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private String currentDoctorName;

    @FXML
    public void initialize() {
        patientColumn.setCellValueFactory(data -> data.getValue().patientProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        timeColumn.setCellValueFactory(data -> data.getValue().timeProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        doctorAppointmentsTable.setItems(appointments);
        loadAppointments();
    }

    public void setCurrentDoctor(String name) {
        this.currentDoctorName = name;
        loadAppointments();
    }

    @FXML
    private void markDone() {
        Appointment selected = doctorAppointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("No appointment selected.", false);
            return;
        }

        try {
            LocalDate appointmentDate = LocalDate.parse(selected.getDate());
            LocalDate today = LocalDate.now();

            if (today.isBefore(appointmentDate)) {
                setStatus("Cannot mark done before the appointment date (" + selected.getDate() + ").", false);
                return;
            }
        } catch (DateTimeParseException e) {
            setStatus("Invalid appointment date format.", false);
            return;
        }

        manager.updateStatus(selected.getId(), "Done");
        selected.setStatus("Done");
        doctorAppointmentsTable.refresh();
        setStatus("Appointment marked as Done.", true);
    }

    @FXML
    private void addClinicalRecord() {
        Appointment selected = doctorAppointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select an appointment first.", false);
            return;
        }

        String patientName = selected.getPatientDisplayName();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Clinical Record");
        dialog.setHeaderText("Patient: " + patientName);
        dialog.setContentText("Enter clinical note:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(note -> {
            if (note.trim().isEmpty()) {
                setStatus("Clinical note cannot be empty.", false);
                return;
            }
            recordStore.addRecord(patientName, note.trim());
            setStatus("Clinical record added for " + patientName + ".", true);
        });
    }

    @FXML
    private void refreshTable() {
        loadAppointments();
        setStatus("Refreshed.", true);
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/hospitol/auth-view.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) doctorAppointmentsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hospital Management System — Login");
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            setStatus("Logout failed: " + e.getMessage(), false);
        }
    }

    private void loadAppointments() {
        appointments.clear();
        if (currentDoctorName != null) {
            appointments.addAll(manager.getAppointmentsByDoctor(currentDoctorName));
        } else {
            appointments.addAll(manager.getAllAppointments());
        }
        if (doctorAppointmentsTable != null) doctorAppointmentsTable.refresh();
    }

    private void setStatus(String msg, boolean ok) {
        if (statusLabel == null) return;
        statusLabel.setText(msg);
        statusLabel.setStyle(ok ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}