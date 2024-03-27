package com.cab302.wellbeing.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
    Button btnRegst; // Assume this is your "Register" button
    @FXML
    Label lblName;

    public void displayName(String username) {
        lblName.setText(username);
        // Adjust UI based on user type
    }
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        // Initially hide the register button until user info is set
//        //btnRegst.setVisible(false);
//        if (lblName.getText().equals("cab302")){
//            btnRegst.setVisible(true);
//        } else {//btnRegst.setVisible(true);
//        btnRegst.setVisible(false);}
//    }

//    public void register(){
//        btnRegst.setVisible("cab302".equals(lblName.getText().toString()));
//    }


    public void switchToInternetScene(ActionEvent event) {
        try {
//            Parent internetExplorerParent = FXMLLoader.load(getClass().getResource("/com/cab302/wellbeing/InternetExplorer.fxml"));
//            Scene internetExplorerScene = new Scene(internetExplorerParent);
//            Stage window = new Stage();
//            //Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
//
//            window.setTitle("Explorer");
//            window.setScene(new Scene(internetExplorerParent));
//            //window.setScene(internetExplorerScene);
//            window.show();

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

    public void switchToRegisterScene(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/Register.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root1));
            stage.setResizable(true);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
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


    public void setAccountType(String accType) {
        if ("Admin".equals(accType)) {
            btnRegst.setVisible(true);
        } else {
            btnRegst.setVisible(false);
        }
    }

}