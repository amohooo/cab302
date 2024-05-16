package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.AppSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class is a controller for the Mode functionality in the application.
 * It provides methods to handle the mode (Dark, Light or Auto) of the application.
 */
public class ModeController {

    @FXML
    private RadioButton lightRadioButton;
    @FXML
    private RadioButton nightRadioButton;
    @FXML
    private RadioButton autoRadioButton;
    @FXML
    private CheckBox eyeProtectCheckBox;
    @FXML
    private Pane paneMode;

    @FXML
    public Button btnSaveM;

    @FXML
    public Button btnCancelM;
    private int userId;
    private String firstName;

    @FXML
    private ToggleGroup modeGroup;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        System.out.println("firstName: " + firstName);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("userId: " + userId);
    }
    @FXML
    public void initialize() {
        modeGroup = new ToggleGroup();
        lightRadioButton.setToggleGroup(modeGroup);
        nightRadioButton.setToggleGroup(modeGroup);
        autoRadioButton.setToggleGroup(modeGroup);

        // Use a listener to apply the current mode once the scene is set
        btnSaveM.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    applyCurrentMode(newScene);
                }
            }
        });
    }

    private void applyCurrentMode(Scene scene) {
        String currentMode = AppSettings.getCurrentMode();
        updateSceneStyle(scene, currentMode);
    }

    private void updateSceneStyle(Scene scene, String currentMode) {
        scene.getStylesheets().clear();
        String stylesheet = null;
        switch (currentMode) {
            case AppSettings.MODE_NIGHT:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/night.css").toExternalForm();
                break;
            case AppSettings.MODE_LIGHT:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/light.css").toExternalForm();
                break;
            case AppSettings.MODE_AUTO:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/auto.css").toExternalForm();
                break;
            case AppSettings.MODE_EYEPROTECT:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/eyeprotect.css").toExternalForm();
                break;
        }
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet);
        }
    }

    @FXML
    public void handleModeSelection(ActionEvent event) {
        Scene scene = lightRadioButton.getScene();

        if (event.getSource() == eyeProtectCheckBox) {
            modeGroup.selectToggle(null);
            AppSettings.setCurrentMode(AppSettings.MODE_EYEPROTECT);
        } else {
            eyeProtectCheckBox.setSelected(false);
            if (lightRadioButton.isSelected()) {
                AppSettings.setCurrentMode(AppSettings.MODE_LIGHT);
            } else if (nightRadioButton.isSelected()) {
                AppSettings.setCurrentMode(AppSettings.MODE_NIGHT);
            } else if (autoRadioButton.isSelected()) {
                AppSettings.setCurrentMode(AppSettings.MODE_AUTO);
            }
        }
        updateSceneStyle(scene, AppSettings.getCurrentMode());
    }

    @FXML
    public void btnSaveMOnAction(ActionEvent actionEvent) {
        saveSelectedMode();
        applyModeToAllWindows();
        Stage stage = (Stage) btnSaveM.getScene().getWindow();
        stage.close();
        switchToMainMenu();
    }

    private void applyModeToAllWindows() {
        String currentMode = AppSettings.getCurrentMode();
        String stylesheet = null;

        switch (currentMode) {
            case AppSettings.MODE_NIGHT:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/night.css").toExternalForm();
                break;
            case AppSettings.MODE_LIGHT:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/light.css").toExternalForm();
                break;
            case AppSettings.MODE_AUTO:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/auto.css").toExternalForm();
                break;
            case AppSettings.MODE_EYEPROTECT:
                stylesheet = getClass().getResource("/com/cab302/wellbeing/styles/eyeprotect.css").toExternalForm();
                break;
        }

        for (Stage stage : Stage.getWindows().stream().filter(window -> window instanceof Stage).map(window -> (Stage) window).toList()) {
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet);
            }
        }
    }

    private void saveSelectedMode() {
        if (nightRadioButton.isSelected()) {
            AppSettings.setCurrentMode(AppSettings.MODE_NIGHT);
        } else if (lightRadioButton.isSelected()) {
            AppSettings.setCurrentMode(AppSettings.MODE_LIGHT);
        } else if (autoRadioButton.isSelected()) {
            AppSettings.setCurrentMode(AppSettings.MODE_AUTO);
        }
        if (eyeProtectCheckBox.isSelected()) {
            AppSettings.setCurrentMode(AppSettings.MODE_EYEPROTECT);
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
    @FXML
    public void btnCancelMOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancelM.getScene().getWindow();
        stage.close();
        switchToMainMenu();
    }
    public void applyColors(Color backgroundColor, Color textColor, Color buttonColor) {
        String backgroundHex = getHexColor(backgroundColor);
        String textHex = getHexColor(textColor);
        String buttonHex = getHexColor(buttonColor);

        if (paneMode != null) {
            paneMode.setStyle("-fx-background-color: " + backgroundHex + ";");
        }
        if (lightRadioButton != null) {
            lightRadioButton.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (nightRadioButton != null) {
            nightRadioButton.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (autoRadioButton != null) {
            autoRadioButton.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (eyeProtectCheckBox != null) {
            eyeProtectCheckBox.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (btnSaveM != null) {
            btnSaveM.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnCancelM != null) {
            btnCancelM.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
    }

    private String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
}