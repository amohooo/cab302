module com.cab302.wellbeing {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;

    opens com.cab302.wellbeing to javafx.fxml;
    exports com.cab302.wellbeing;
    exports com.cab302.wellbeing.controller;
    opens com.cab302.wellbeing.controller to javafx.fxml;
}