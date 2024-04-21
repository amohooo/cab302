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
    private TextField txtFName, txtLName, txtUsername, txtPwd, txtEmail, txtRetp;
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
        String email = txtEmail.getText();
        String Pwd = txtPwd.getText();
        String Repwd = txtRetp.getText();

        String registerUser = "INSERT INTO useraccount (userName, firstName, lastName, password, emailAddress, accType) VALUES (?, ?, ?, ?, ?, ?)";

        if (usernameExists(txtUsername.getText())) {
            lblMsg.setText("Username already exists. Please choose a different one.");
            return;
        }

        if (txtFName.getText().isBlank() || txtLName.getText().isBlank() || txtUsername.getText().isBlank() || txtPwd.getText().isBlank() || txtEmail.getText().isBlank() || txtRetp.getText().isBlank()) {
            lblMsg.setText("Please fill all the information above.");
            return;
        }

        if (!email.contains("@")) {
            lblMsg.setText("Invalid email format.");
            return;
        }

        if (emailExists(txtEmail.getText())) {
            lblMsg.setText("Email address already exists. Please fill in a different one.");
            return;
        }

        if (Pwd.matches(Repwd)) {
            lblMsg.setText("Email address matches.");
        }else{
            lblMsg.setText("Email address does not match, please type in the same Email address.");
            return;
        }

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(registerUser);
            preparedStatement.setString(1, txtUsername.getText());
            preparedStatement.setString(2, txtFName.getText());
            preparedStatement.setString(3, txtLName.getText());
            preparedStatement.setString(4, txtPwd.getText());
            preparedStatement.setString(5, txtEmail.getText());
            preparedStatement.setString(6, AccType);

            int rowsAffected = preparedStatement.executeUpdate();

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

    public void setBtnCncl(ActionEvent e){
        // Get the current stage information using the source of the event
        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
        window.close(); // This closes the current window
    }

    public boolean usernameExists(String username) {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT COUNT(*) FROM useraccount WHERE Username = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Return true if count is greater than 0, meaning the username exists
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Return false if the username doesn't exist or an error occurs
    }

    private boolean emailExists(String email) {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDB = connectNow.getConnection();

        String query = "SELECT COUNT(*) FROM useraccount WHERE emailAddress = ?";

        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Returns true if count is greater than 0
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
