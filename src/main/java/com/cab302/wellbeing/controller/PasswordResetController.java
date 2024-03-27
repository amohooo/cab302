package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class PasswordResetController {
        @FXML
        private TextField txtUserName;
        @FXML
        private PasswordField ptxtPwd, ptxtRePwd;
        @FXML
        private Label lblMsg;

        public void resetPassword() {

            String username = txtUserName.getText();
            String newPassword = ptxtPwd.getText();

            if (username.isEmpty() || newPassword.isEmpty()) {
                lblMsg.setText("Please fill in all fields.");
//            } else if (!isUsernameExisting(username)) {
//                lblMessage.setText("The username does not exist. Please try again.");
            } else if (!newPassword.equals(ptxtRePwd.getText())) {
                lblMsg.setText("Passwords do not match.");
            } else {
                try {
                    DataBaseConnection connectNow = new DataBaseConnection();
                    Connection connectDB = connectNow.getConnection();
                    String query = "UPDATE useraccount SET Password = ? WHERE UserName = ?";
                    PreparedStatement pst = connectDB.prepareStatement(query);
                    pst.setString(1, newPassword);
                    pst.setString(2, username);
                    int result = pst.executeUpdate();

                    if (result > 0) {
                        lblMsg.setText("Password successfully reset.");
                    } else {
                        lblMsg.setText("Failed to reset password. User may not exist.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    lblMsg.setText("Error: " + e.getMessage());
                }
            }
        }

//        public boolean isUsernameExisting(String username) {
//            DataBaseConnection connectNow = new DataBaseConnection();
//            Connection connectDB = connectNow.getConnection();
//            String query = "SELECT COUNT(*) FROM useraccount WHERE UserName = ?";
//
//            try (PreparedStatement pst = connectDB.prepareStatement(query)) {
//                pst.setString(1, username);
//                ResultSet rs = pst.executeQuery();
//
//                if (rs.next()) {
//                    int count = rs.getInt(1);
//                    return count > 0; // True if username exists, false otherwise
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return false; // Assume username does not exist if an exception occurs or no data is found
//        }

    }


