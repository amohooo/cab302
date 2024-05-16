package com.cab302.wellbeing.controller;

//import com.cab302.wellbeing.DataBaseConnection;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class is a controller for the Set Time Limit functionality in the application.
 * It provides methods to handle the time limit setting.
 */
public class SetTimeLimitController {
    @FXML
    public TextArea txtLimit;
    public TextArea txtNotification;
    @FXML
    private Label lblSetTime, lblNotification, lblWhen, lblTitle;
    @FXML
    public CheckBox boxExit;
    @FXML
    private Pane paneSetLimit;
    @FXML
    public CheckBox boxResetTimeLimit;


    @FXML
    public Button btnSaveT;

    @FXML
    public Button btnCancelT;
    private int userId;
    private String firstName;
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    //@FXML
    //public void saveTimeLimit() {
    // Database connection
    // DataBaseConnection connectNow = new DataBaseConnection();
    //Connection connectDB = connectNow.getConnection();


    //String saveTimeLimit = "INSERT INTO timelimit (UserID, LimitTimeMinutes, LimitNotification) VALUES (?, ?, ?)";

    //try(PreparedStatement preparedStatement = connectDB.prepareStatement(saveTimeLimit)){
    //preparedStatement.setString(1, getCurrentUserID());
    //preparedStatement.setInt(2, Integer.parseInt(txtLimit.getText()));
    //preparedStatement.setString(3, txtNotification.getText());

    //preparedStatement.executeUpdate();
    //} catch (SQLException e) {
    //throw new RuntimeException(e);
    //}
    //}

    //private String getCurrentUserID() {
    //"7" by default at the stage
    //return "7";
    //}

    @FXML
    public void btnSaveTOnAction(ActionEvent e) {
        //saveTimeLimit();
        startTimer();
        Stage stage = (Stage) btnSaveT.getScene().getWindow();
        stage.close();
        switchToMainMenu();
    }
    @FXML
    public void btnCancelTOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancelT.getScene().getWindow();
        stage.close();
        switchToMainMenu();
    }

    public void boxExitOnAction(ActionEvent actionEvent) {
        if (boxExit.isSelected()) {
            boxResetTimeLimit.setSelected(false);
        }
    }

    public void boxResetOnAction(ActionEvent actionEvent) {
        if (boxResetTimeLimit.isSelected()) {
            boxExit.setSelected(false);
        }
    }

    private void startTimer() {
        int limitTime = Integer.parseInt(txtLimit.getText());
        int limitTimeInMillis = limitTime * 60 * 1000;

        new Thread(() -> {
            try {
                Thread.sleep(limitTimeInMillis);
                Platform.runLater(this::handleTimeLimitReached);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleTimeLimitReached() {
        if (boxExit.isSelected()) {
            System.exit(0);
        } else if (boxResetTimeLimit.isSelected()) {
            showSetTimeLimit();
            showNotification();
        }
    }

    private void showSetTimeLimit() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/settimelimit.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Set Time Limit");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNotification() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/notification.fxml"));
            Parent root = fxmlLoader.load();
            NotificationController controller = fxmlLoader.getController();
            controller.setNotificationText(txtNotification.getText());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Notification");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void applyColors(Color backgroundColor, Color textColor, Color buttonColor) {
        String backgroundHex = getHexColor(backgroundColor);
        String textHex = getHexColor(textColor);
        String buttonHex = getHexColor(buttonColor);

        if (paneSetLimit != null) {
            paneSetLimit.setStyle("-fx-background-color: " + backgroundHex + ";");
        }
        if (lblSetTime != null) {
            lblSetTime.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (lblNotification != null) {
            lblNotification.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (lblWhen != null) {
            lblWhen.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (lblTitle != null) {
            lblTitle.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (boxExit != null) {
            boxExit.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (boxResetTimeLimit != null) {
            boxResetTimeLimit.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (btnSaveT != null) {
            btnSaveT.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnCancelT != null) {
            btnCancelT.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
    }
    private String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
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
}