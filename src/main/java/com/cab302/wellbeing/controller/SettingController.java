package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is a controller for the Settings menu in the application.
 * It provides three linked switch scenes: set time limits, set modes, and set theme.
 */
public class SettingController {

    @FXML
    public Button btnMode;
    @FXML
    public Button btnSetTime, btnTheme, btnBack;
    @FXML
    public Label lblSetting;
    @FXML
    private AnchorPane paneSetting;
    private DataBaseConnection dbConnection = new DataBaseConnection();
    private int userId;
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void btnBackOnAction(ActionEvent e) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleModeButton(ActionEvent event) {
        switchScene(event, SceneType.MODE);
    }
    @FXML
    private void handleSetTimeButton(ActionEvent event) {
        switchScene(event, SceneType.SETTIME);
    }
    @FXML
    private void handleThemeButton(ActionEvent event) { // Method to handle theme button click
        switchScene(event, SceneType.THEME);
    }
    public enum SceneType {
        MODE, SETTIME, THEME
    }
    public void switchScene(ActionEvent event, SceneType sceneType) {
        String fxmlFile = "";
        String title = "TIPS";
        switch (sceneType) {
            case MODE:
                fxmlFile = "/com/cab302/wellbeing/Mode.fxml";
                break;
            case SETTIME:
                fxmlFile = "/com/cab302/wellbeing/SetTimeLimit.fxml";
                break;
            case THEME:
                fxmlFile = "/com/cab302/wellbeing/theme.fxml";
                break;
            default:
                System.err.println("Unsupported scene type: " + sceneType);
                return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            // Pass the user ID if the controller is of the appropriate type
            Object controller = fxmlLoader.getController();
            if (controller instanceof SettingController) {
                ((SettingController) controller).setUserId(userId);
            }

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadSavedColors() {
        String query = "SELECT BackgroundColor, TextColor, ButtonColor FROM ColorSettings WHERE UserID = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String backgroundColorHex = resultSet.getString("BackgroundColor");
                String textColorHex = resultSet.getString("TextColor");
                String buttonColorHex = resultSet.getString("ButtonColor");

                Color backgroundColor = Color.web(backgroundColorHex);
                Color textColor = Color.web(textColorHex);
                Color buttonColor = Color.web(buttonColorHex);

                applyColors(backgroundColor, textColor, buttonColor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void applyColors(Color backgroundColor, Color textColor, Color buttonColor) {
        String backgroundHex = String.format("#%02x%02x%02x", (int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255), (int) (backgroundColor.getBlue() * 255));
        String textHex = String.format("#%02x%02x%02x", (int) (textColor.getRed() * 255),
                (int) (textColor.getGreen() * 255), (int) (textColor.getBlue() * 255));
        String buttonHex = String.format("#%02x%02x%02x", (int) (buttonColor.getRed() * 255),
                (int) (buttonColor.getGreen() * 255), (int) (buttonColor.getBlue() * 255));

        if (paneSetting != null) {
            paneSetting.setStyle("-fx-background-color: " + backgroundHex + ";");
        }
        if (lblSetting != null) {
            lblSetting.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (btnMode != null) {
            btnMode.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnSetTime != null) {
            btnSetTime.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnTheme != null) {
            btnTheme.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnBack != null) {
            btnBack.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
    }
}