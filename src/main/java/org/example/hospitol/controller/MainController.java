package org.example.hospitol.controller;

import org.example.hospitol.model.*;
import org.example.hospitol.service.ClinicService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;


public class MainController {
    private ClinicService service;
    private Patient currentPatient;   // session user if patient
    private Doctor currentDoctor;     // session user if doctor
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private Label welcomeLabel;
    @FXML private TabPane mainTabPane;

    @FXML private Tab tabRegisterPatient, tabRegisterDoctor, tabViewDoctors, tabBookAppointmentAdmin,
            tabViewAllAppointments, tabAddRecord, tabViewRecordsAdmin, tabPatientBook,
            tabPatientAppointments, tabPatientRecords, tabDoctorSchedule, tabDoctorAddRecord, tabDoctorViewRecords;

    @FXML private TextField patientIdField, patientNameField, doctorIdField, doctorNameField, doctorSpecField;
    @FXML private Label patientStatusLabel, doctorStatusLabel, apptStatusLabel, recordStatusLabel, patientApptStatusLabel, doctorRecordStatusLabel;
    @FXML private TextArea doctorsListArea, allAppointmentsArea, recordDataField, recordsListArea, patientAppointmentsArea, patientRecordsArea,
            doctorScheduleArea, doctorRecordDataField, doctorPatientRecordsArea;

    // Admin Appointment Selectors
    @FXML private ComboBox<Patient> adminApptPatientCombo;
    @FXML private ComboBox<Doctor> adminApptDoctorCombo;
    @FXML private DatePicker adminApptDatePicker;
    @FXML private ComboBox<String> adminApptHourCombo, adminApptMinuteCombo;

    // Patient Appointment Selectors
    @FXML private ComboBox<Doctor> patientDoctorCombo;
    @FXML private DatePicker patientApptDatePicker;
    @FXML private ComboBox<String> patientApptHourCombo, patientApptMinuteCombo;
    @FXML private ComboBox<String> patientSpecialtyFilter;

    // Records Selectors
    @FXML private ComboBox<Patient> adminRecordPatientCombo, adminViewRecordPatientCombo,
            doctorPatientRecordCombo, doctorViewPatientCombo;

    public void initSession(Person user, ClinicService clinicService) {
        this.service = clinicService;
        if (user instanceof Patient p) {
            this.currentPatient = p;
            welcomeLabel.setText("Session: " + p.getName() + " [PATIENT]");
            applyRolePermissions("PATIENT");
        } else if (user instanceof Doctor d) {
            this.currentDoctor = d;
            welcomeLabel.setText("Session: " + d.getName() + " [DOCTOR]");
            applyRolePermissions("DOCTOR");
        } else {
            welcomeLabel.setText("Session: ADMIN");
            applyRolePermissions("ADMIN");
        }

        initTimeDropdowns();
        setupSelectionData();
    }

    private void initTimeDropdowns() {
        List<String> hours = new ArrayList<>();
        for (int i = 8; i <= 18; i++) hours.add(String.format("%02d", i));
        List<String> minutes = List.of("00", "15", "30", "45");

        ObservableList<String> hList = FXCollections.observableArrayList(hours);
        ObservableList<String> mList = FXCollections.observableArrayList(minutes);

        if (adminApptHourCombo != null) {
            adminApptHourCombo.setItems(hList);
            adminApptMinuteCombo.setItems(mList);
        }
        if (patientApptHourCombo != null) {
            patientApptHourCombo.setItems(hList);
            patientApptMinuteCombo.setItems(mList);
        }
    }

    private void applyRolePermissions(String role) {
        mainTabPane.getTabs().clear();
        switch (role) {
            case "ADMIN" -> mainTabPane.getTabs().addAll(tabRegisterPatient, tabRegisterDoctor, tabBookAppointmentAdmin,
                    tabViewAllAppointments, tabAddRecord, tabViewRecordsAdmin, tabViewDoctors);
            case "PATIENT" -> mainTabPane.getTabs().addAll(tabPatientBook, tabPatientAppointments, tabPatientRecords, tabViewDoctors);
            case "DOCTOR" -> mainTabPane.getTabs().addAll(tabDoctorSchedule, tabDoctorAddRecord, tabDoctorViewRecords, tabViewDoctors);
        }
    }

    private void setupSelectionData() {
        List<Doctor> doctors = service.getAllDoctors();
        List<Patient> patients = service.getAllPatients();

        if (adminApptPatientCombo != null) adminApptPatientCombo.setItems(FXCollections.observableArrayList(patients));
        if (adminApptDoctorCombo != null) adminApptDoctorCombo.setItems(FXCollections.observableArrayList(doctors));
        if (adminRecordPatientCombo != null) adminRecordPatientCombo.setItems(FXCollections.observableArrayList(patients));
        if (adminViewRecordPatientCombo != null) adminViewRecordPatientCombo.setItems(FXCollections.observableArrayList(patients));
        if (doctorPatientRecordCombo != null) doctorPatientRecordCombo.setItems(FXCollections.observableArrayList(patients));
        if (doctorViewPatientCombo != null) doctorViewPatientCombo.setItems(FXCollections.observableArrayList(patients));

        if (patientSpecialtyFilter != null) {
            patientSpecialtyFilter.setItems(FXCollections.observableArrayList(service.getUniqueSpecialties()));
            patientSpecialtyFilter.setOnAction(e -> {
                String spec = patientSpecialtyFilter.getValue();
                if (spec != null) {
                    patientDoctorCombo.setItems(FXCollections.observableArrayList(
                            doctors.stream().filter(d -> spec.equals(d.getSpecialization())).toList()
                    ));
                }
            });
        }
    }

    @FXML private void handleRegisterPatient() {
        try {
            String id = patientIdField.getText();
            String name = patientNameField.getText();
            if (id.isEmpty() || name.isEmpty()) throw new IllegalArgumentException("Fields cannot be empty.");

            service.registerPatient(new Patient(name, 0, Set.of())); // adjust age/conditions as needed
            patientStatusLabel.setText("Patient Registered.");
            patientIdField.clear(); patientNameField.clear();
            setupSelectionData();
        } catch (Exception e) { patientStatusLabel.setText(e.getMessage()); }
    }

    @FXML private void handleRegisterDoctor() {
        try {
            String id = doctorIdField.getText();
            String name = doctorNameField.getText();
            String spec = doctorSpecField.getText();
            if (id.isEmpty() || name.isEmpty() || spec.isEmpty()) throw new IllegalArgumentException("Fields cannot be empty.");

            service.registerDoctor(new Doctor(name, 0, spec));
            doctorStatusLabel.setText("Doctor Registered.");
            doctorIdField.clear(); doctorNameField.clear(); doctorSpecField.clear();
            setupSelectionData();
        } catch (Exception e) { doctorStatusLabel.setText(e.getMessage()); }
    }

    @FXML private void handleBookAppointment() {
        try {
            Patient patient = adminApptPatientCombo.getValue();
            Doctor doctor = adminApptDoctorCombo.getValue();
            LocalDate date = adminApptDatePicker.getValue();
            String h = adminApptHourCombo.getValue();
            String m = adminApptMinuteCombo.getValue();

            if (patient == null || doctor == null || date == null || h == null || m == null) {
                apptStatusLabel.setText("Error: Missing appointment details.");
                return;
            }

            LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(Integer.parseInt(h), Integer.parseInt(m)));
            service.bookAppointment(patient, doctor, ldt);
            apptStatusLabel.setText("Booked for " + ldt.format(DT_FORMAT));
            apptStatusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            apptStatusLabel.setText(e.getMessage());
            apptStatusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Similar adjustments for patient booking, records, and viewing methods...
}
