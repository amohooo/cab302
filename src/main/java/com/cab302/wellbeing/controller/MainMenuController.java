package com.cab302.wellbeing.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainMenuController{
    @FXML
    private Button btnExplorer;
    @FXML
    private Button btnLogOut;
    @FXML
    private Pane pnExplorer;
    @FXML
    Label lblName, lblExplorer;
    public void displayName(String firstName) {
        lblName.setText(firstName + ", ");
        // Adjust UI based on user type
    }
    public void switchToInternetScene(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/InternetExplorer.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Explorer");
            stage.setScene(new Scene(root1));
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading InternetExplorer.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void btnLogOutOnAction(ActionEvent e){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(("Logging out"));
        alert.setContentText("Save?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            stage.close();
        }
    }
}