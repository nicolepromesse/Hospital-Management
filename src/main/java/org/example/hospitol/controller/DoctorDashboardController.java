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

import java.io.IOException;

public class DoctorDashboardController {

    @FXML private TableView<Appointment> doctorAppointmentsTable;
    @FXML private TableColumn<Appointment, String> patientColumn;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private Label statusLabel;

    private final AppointmentManager manager = new AppointmentManager();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        patientColumn.setCellValueFactory(data -> data.getValue().patientProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        timeColumn.setCellValueFactory(data -> data.getValue().timeProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        appointments.addAll(manager.getAllAppointments());
        doctorAppointmentsTable.setItems(appointments);
    }

    @FXML
    private void markDone() {
        Appointment selected = doctorAppointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("No appointment selected.", false);
            return;
        }
        manager.updateStatus(selected.getId(), "Done");
        selected.setStatus("Done");
        doctorAppointmentsTable.refresh();
        setStatus("Appointment marked as Done.", true);
    }

    @FXML
    private void deleteAppointment() {
        Appointment selected = doctorAppointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("No appointment selected.", false);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete appointment for " + selected.getPatientDisplayName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                manager.deleteAppointment(selected.getId());
                appointments.remove(selected);
                setStatus("Appointment deleted.", true);
            }
        });
    }

    @FXML
    private void refreshTable() {
        appointments.clear();
        appointments.addAll(manager.getAllAppointments());
        doctorAppointmentsTable.refresh();
        setStatus("Refreshed.", true);
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

    private void setStatus(String msg, boolean ok) {
        if (statusLabel == null) return;
        statusLabel.setText(msg);
        statusLabel.setStyle(ok ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}
