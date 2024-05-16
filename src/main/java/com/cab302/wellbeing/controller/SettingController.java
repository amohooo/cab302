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
    private String firstName;
    private static final Color DEFAULT_COLOR = Color.web("#009ee0");
    private static final Color DEFAULT_TEXT_COLOR = Color.web("#ffffff");
    private MainMenuController mainMenuController;
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        System.out.println("firstName: " + firstName);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("userId: " + userId);
    }
    public void btnBackOnAction(ActionEvent e) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
        switchToMainMenu();
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
            Color backgroundColor = (Color) paneSetting.getBackground().getFills().get(0).getFill();
            Color textColor = (Color) btnBack.getTextFill();
            Color buttonColor = (Color) btnBack.getBackground().getFills().get(0).getFill();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            // Pass the user ID and main menu controller if the controller is of the appropriate type
            if (sceneType == SceneType.THEME) {
                themeController controller = fxmlLoader.getController();
                controller.setUserId(userId);
                controller.setParentController(this);
                controller.setFirstName(firstName);
                controller.setMainMenuController(mainMenuController);
                controller.applyColors(backgroundColor, textColor, buttonColor);
                System.out.println("userId: " + userId);
            }
            if (sceneType == SceneType.MODE) {
                ModeController controller = fxmlLoader.getController();
                controller.setUserId(userId);
                controller.setFirstName(firstName);
                controller.applyColors(backgroundColor, textColor, buttonColor);
                System.out.println("userId: " + userId);
            }
            if (sceneType == SceneType.SETTIME) {
                SetTimeLimitController controller = fxmlLoader.getController();
                controller.setUserId(userId);
                controller.setFirstName(firstName);
                controller.applyColors(backgroundColor, textColor, buttonColor);
                System.out.println("userId: " + userId);
            }

            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void switchToMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/MainMenu.fxml"));
            Parent root = fxmlLoader.load();
            MainMenuController mainMenuController = fxmlLoader.getController();
            mainMenuController.setFirstName(firstName);
            mainMenuController.setUserId(userId);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Main Menu");
            stage.show();
        } catch (IOException e) {
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
    }

    private String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
}