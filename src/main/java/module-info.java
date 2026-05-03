module org.example.hospitol {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    // Open packages that have FXML controllers to javafx.fxml
    opens org.example.hospitol to javafx.fxml;
    opens org.example.hospitol.Main to javafx.fxml;
    opens org.example.hospitol.controller to javafx.fxml;

    // Export all packages
    exports org.example.hospitol;
    exports org.example.hospitol.Main;
    exports org.example.hospitol.controller;
    exports org.example.hospitol.model;
    exports org.example.hospitol.service;
    exports org.example.hospitol.exception;
    exports org.example.hospitol.util;
}