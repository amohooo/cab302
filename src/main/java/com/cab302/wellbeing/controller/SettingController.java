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
    public Button btnSetTime, btnTheme, btnBack, btnMode;
    @FXML
    public Label lblSetting;
    @FXML
    private AnchorPane paneSetting;
    private DataBaseConnection dbConnection = new DataBaseConnection();
    private int userId;
    private static final Color DEFAULT_COLOR = Color.web("#009ee0");
    private static final Color DEFAULT_TEXT_COLOR = Color.web("#ffffff");
    private MainMenuController mainMenuController;

    public void setUserId(int userId) {
        this.userId = userId;
        loadSavedColors();
    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
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
    private void handleThemeButton(ActionEvent event) {
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

            // Pass the user ID and main menu controller if the controller is of the appropriate type
            if (sceneType == SettingController.SceneType.THEME) {
                themeController controller = fxmlLoader.getController();
                controller.setUserId(userId);
                controller.setParentController(this);
                controller.setMainMenuController(mainMenuController);
                System.out.println("userId: " + userId);
            }

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void applyColors(Color backgroundColor, Color textColor, Color buttonColor) {
        String backgroundHex = getHexColor(backgroundColor);
        String textHex = getHexColor(textColor);
        String buttonHex = getHexColor(buttonColor);

        if (paneSetting != null) {
            paneSetting.setStyle("-fx-background-color: " + backgroundHex + ";");
        }
        if (lblSetting != null) {
            lblSetting.setStyle("-fx-text-fill: " + textHex + ";");
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
        if (btnMode != null) {
            btnMode.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }

        // Update MainMenuController colors
        if (mainMenuController != null) {
            mainMenuController.applyColors(backgroundColor, textColor, buttonColor);
        }
    }

    private String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }

    private void loadSavedColors() {
        if (dbConnection == null) {
            System.err.println("Database connection is null.");
            return;
        }
        String query = "SELECT BackgroundColor, TextColor, ButtonColor, ButtonTextColor FROM ColorSettings WHERE ID = 2 AND UserID = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String backgroundColorHex = resultSet.getString("BackgroundColor");
                String textColorHex = resultSet.getString("TextColor");
                String buttonColorHex = resultSet.getString("ButtonColor");
                String buttonTextColorHex = resultSet.getString("ButtonTextColor");

                Color backgroundColor = Color.web(backgroundColorHex);
                Color textColor = Color.web(textColorHex);
                Color buttonColor = Color.web(buttonColorHex);

                applyColors(backgroundColor, textColor, buttonColor);
            } else {
                applyColors(DEFAULT_COLOR, DEFAULT_TEXT_COLOR, DEFAULT_COLOR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}