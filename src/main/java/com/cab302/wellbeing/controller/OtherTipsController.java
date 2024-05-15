package com.cab302.wellbeing.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * This class is a controller for the Other Tips functionality in the application.
 * It provides some other wellbeing tips and tricks (text or links...) to the user.
 */
public class OtherTipsController {
    @FXML
    private Button btnGoBackToWell;
    public void btnGoBackToWellAction(ActionEvent e){
        Stage stage = (Stage) btnGoBackToWell.getScene().getWindow();
        // Close the current stage
        stage.close();
    }
}
