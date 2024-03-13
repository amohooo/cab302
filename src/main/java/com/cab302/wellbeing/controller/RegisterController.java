package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import com.cab302.wellbeing.WellBeingApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterController {

    @FXML
    private TextField txtFName, txtLName, txtUsername, txtPwd;
    @FXML
    private RadioButton radbGnrl, radbAdm;
    @FXML
    private Button btnRgst, btnCncl;
    @FXML
    private Label lblMsg;

    public void registerUser() {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDB = connectNow.getConnection();

        String AccType = radbAdm.isSelected() ? "Admin" : "General";

        String registerUser = "INSERT INTO useraccount (FirstName, LastName, Username, Password, AccType) VALUES (?, ?, ?, ?, ?)";

        // Validation before attempting the insert
        if (txtFName.getText().isBlank() || txtLName.getText().isBlank() || txtUsername.getText().isBlank() || txtPwd.getText().isBlank()) {
            lblMsg.setText("Please fill all the information above.");
            return; // Exit the method if validation fails
        }

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(registerUser);
            preparedStatement.setString(1, txtFName.getText());
            preparedStatement.setString(2, txtLName.getText());
            preparedStatement.setString(3, txtUsername.getText());
            preparedStatement.setString(4, txtPwd.getText());
            preparedStatement.setString(5, AccType);

            int rowsAffected = preparedStatement.executeUpdate(); // Use executeUpdate for INSERT, UPDATE, DELETE

            if (rowsAffected > 0) {
                lblMsg.setText("Successfully registered.");
            } else {
                lblMsg.setText("Registration failed. Please try again.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblMsg.setText("Registration error: " + ex.getMessage());
        }
    }

    public void setBtnRgst(ActionEvent e) {
        registerUser(); // Just call registerUser without parameters
    }
//    public void setBtnRgst(ActionEvent e){
//        String FName = txtFName.getText();
//        String LName = txtLName.getText();
//        String Username = txtUsername.getText();
//        String Password = txtPwd.getText();
//        String Type = new String();
//        registerUser(FName, LName, Username, Password, Type);
//    }
    public void setBtnCncl(ActionEvent e){
        try {
            //FXMLLoader loader = new F
            Parent mainMenuParent = FXMLLoader.load(getClass().getResource("/com/cab302/wellbeing/MainMenu.fxml"));
            Scene mainMenuScene = new Scene(mainMenuParent);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(mainMenuScene);
            stage.show();

        } catch (IOException ex) {
            System.err.println("Error loading InternetExplorer.fxml: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
