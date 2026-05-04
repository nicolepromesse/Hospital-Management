package org.example.hospitol.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.hospitol.model.Appointment;
import org.example.hospitol.service.AppointmentManager;
import org.example.hospitol.service.ClinicalRecordStore;

import java.io.IOException;
import java.util.List;

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

    @FXML private VBox recordsSection;
    @FXML private ListView<String> recordsListView;

    private final AppointmentManager manager = new AppointmentManager();
    private final ClinicalRecordStore recordStore = ClinicalRecordStore.getInstance();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private String currentPatientName = "Patient";

    @FXML
    public void initialize() {
        doctorColumn.setCellValueFactory(data -> data.getValue().doctorProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        timeColumn.setCellValueFactory(data -> data.getValue().timeProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        appointmentTable.setItems(appointments);
        loadMyAppointments();
    }

    public void setCurrentPatient(String name) {
        this.currentPatientName = name;
        loadMyAppointments();
    }

    public void setDoctorNames(List<String> doctorNames) {
        if (doctorNames == null || doctorNames.isEmpty()) {
            doctorComboBox.setItems(FXCollections.observableArrayList("No doctors available"));
            doctorComboBox.setDisable(true);
        } else {
            doctorComboBox.setItems(FXCollections.observableArrayList(doctorNames));
            doctorComboBox.setDisable(false);
        }
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
        recordsSection.setVisible(false);
        recordsSection.setManaged(false);
        setStatus("Refreshed.", true);
    }

    @FXML
    private void viewAppointments() {
        loadMyAppointments();
        recordsSection.setVisible(false);
        recordsSection.setManaged(false);
        setStatus("Showing your appointments.", true);
    }

    @FXML
    private void viewRecords() {
        List<String> records = recordStore.getRecords(currentPatientName);
        if (records.isEmpty()) {
            setStatus("No clinical records found.", false);
            recordsSection.setVisible(false);
            recordsSection.setManaged(false);
        } else {
            recordsListView.setItems(FXCollections.observableArrayList(records));
            recordsSection.setVisible(true);
            recordsSection.setManaged(true);
            setStatus("Showing your clinical records.", true);
        }
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/hospitol/auth-view.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) appointmentTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hospital Management System — Login");
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            setStatus("Logout failed: " + e.getMessage(), false);
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