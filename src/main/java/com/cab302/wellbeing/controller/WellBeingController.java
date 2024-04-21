package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WellBeingController {
    @FXML
    private Button btnExit, btnLogOut, btnRegst;
    @FXML
    private TextField txtUsr;
    @FXML
    private PasswordField txtPwd;
    @FXML
    private Label lblLoginMsg;

    private Parent root;
    private Scene scene;
    private Stage stage;

    private void switchToMainMenuScene(ActionEvent e, String firstName, String accType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/MainMenu.fxml"));
            root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.displayName(firstName);
//            mainMenuController.setAccountType(accType); // Set visibility of btnRegst based on account type

            scene = new Scene(root);
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println("Error loading MainMenu.fxml: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void btnExitOnAction(ActionEvent e) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void lblLoginMsgOnAction(ActionEvent e) {
        lblLoginMsg.setText("Your username or password is wrong");

        if (txtUsr.getText().isBlank()) {
            lblLoginMsg.setText("Please fill in your username");
        } else if (txtPwd.getText().isBlank()) {
            lblLoginMsg.setText("Please fill in your password");
        } else {
            validateLogin(e);
        }
    }

    public void validateLogin(ActionEvent e) {
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDB = connectNow.getConnection();

        String username = txtUsr.getText();
        String password = txtPwd.getText();  // This is the plaintext password entered by the user

        // Query to retrieve the hashed password and account type from the database for the given username
        String fetchUserDetails = "SELECT passwordHash, AccType, firstName FROM useraccount WHERE UserName = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(fetchUserDetails);
            preparedStatement.setString(1, username);

            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next()) {
                String storedHash = queryResult.getString("passwordHash"); // Retrieved hashed password
                String accType = queryResult.getString("AccType");         // Account type
                String firstName = queryResult.getString("firstName");

                // Use BCrypt to check if the entered password matches the hashed password
                if (BCrypt.checkpw(password, storedHash)) {
                    lblLoginMsg.setText("Welcome " + firstName);
                    switchToMainMenuScene(e, firstName, accType); // Pass the account type
                } else {
                    lblLoginMsg.setText("Your username or password is wrong");
                }
            } else {
                lblLoginMsg.setText("Your username or password is wrong");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lblLoginMsg.setText("Failed to connect to database.");
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
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading InternetExplorer.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void openPasswordResetScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/PasswordReset.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Password Reset");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void btnForgotPwdOnAction(ActionEvent e) {
        openPasswordResetScene();
    }

}