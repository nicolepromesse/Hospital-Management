package org.example.hospitol.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.hospitol.model.Appointment;
import org.example.hospitol.model.Doctor;
import org.example.hospitol.service.AppointmentManager;
import org.example.hospitol.service.ClinicService;

import java.io.IOException;

public class PatientDashboardController {

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> doctorColumn;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;

    @FXML private ComboBox<String> doctorComboBox;
    @FXML private TextField dateField;
    @FXML private TextField timeField;
    @FXML private Label statusLabel;

    @FXML private Button viewAppointmentsBtn;
    @FXML private Button viewRecordsBtn;
    @FXML private Button logoutBtn;

    private final AppointmentManager manager = new AppointmentManager();
    private final ClinicService clinicService = new ClinicService();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private String currentPatientName = "Patient";

    @FXML
    public void initialize() {
        doctorColumn.setCellValueFactory(data -> data.getValue().doctorProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        timeColumn.setCellValueFactory(data -> data.getValue().timeProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        // Load doctors directly from ClinicService
        doctorComboBox.setItems(FXCollections.observableArrayList(
                clinicService.getAllDoctors().stream().map(Doctor::getName).toList()
        ));

        loadMyAppointments();
        appointmentTable.setItems(appointments);
    }

    public void setCurrentPatient(String name) {
        this.currentPatientName = name;
        loadMyAppointments();
    }

    @FXML
    private void bookAppointment() {
        String doctor = doctorComboBox.getValue();
        String date   = dateField.getText().trim();
        String time   = timeField.getText().trim();

        if (doctor == null || date.isEmpty() || time.isEmpty()) {
            setStatus("Please select doctor, date and time.", false);
            return;
        }

        if (manager.isDoctorBooked(doctor, date, time)) {
            setStatus("Doctor already booked at this time.", false);
            return;
        }

        Appointment appt = new Appointment(doctor, date, time);
        appt.setPatientName(currentPatientName);
        manager.addAppointment(appt);
        appointments.add(appt);

        doctorComboBox.setValue(null);
        dateField.clear();
        timeField.clear();

        setStatus("Appointment booked!", true);
    }

    @FXML
    private void cancelAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("No appointment selected.", false);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel appointment on " + selected.getDate() + " at " + selected.getTime() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                manager.deleteAppointment(selected.getId());
                appointments.remove(selected);
                setStatus("Appointment cancelled.", true);
            }
        });
    }

    @FXML
    private void refreshTable() {
        loadMyAppointments();
        setStatus("Refreshed.", true);
    }

    @FXML
    private void viewAppointments() {
        refreshTable();
        setStatus("Showing your appointments.", true);
    }

    @FXML
    private void viewRecords() {
        loadMyAppointments();
        setStatus("Showing your records.", true);
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/hospitol/Auth.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hospital Management System — Login");
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMyAppointments() {
        appointments.clear();
        appointments.addAll(manager.getAppointmentsByPatient(currentPatientName));
        if (appointmentTable != null) appointmentTable.refresh();
    }

    private void setStatus(String msg, boolean ok) {
        if (statusLabel == null) return;
        statusLabel.setText(msg);
        statusLabel.setStyle(ok ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}
