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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WellBeingController {
    @FXML
    private Button btnExit, btnLogOut;
    @FXML
    private TextField txtUsr;
    @FXML
    private PasswordField txtPwd;
    @FXML
    private Label lblLoginMsg;

    private Parent root;
    private Scene scene;
    private Stage stage;

    private void switchToMainMenuScene(ActionEvent e, String accType) {
        try {
            String username = txtUsr.getText();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/MainMenu.fxml"));
            root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.displayName(username);
            mainMenuController.setAccountType(accType); // Set visibility of btnRegst based on account type

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

        String verifyLogin = "SELECT count(1), AccType FROM useraccount WHERE UserName = ? AND Password = ? GROUP BY AccType";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);
            preparedStatement.setString(1, txtUsr.getText());
            preparedStatement.setString(2, txtPwd.getText());

            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next() && queryResult.getInt(1) == 1) {
                String accType = queryResult.getString("AccType"); // Get the account type
                lblLoginMsg.setText("Welcome " + txtUsr.getText());
                switchToMainMenuScene(e, accType); // Pass the account type
            } else {
                lblLoginMsg.setText("Your username or password is wrong");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}