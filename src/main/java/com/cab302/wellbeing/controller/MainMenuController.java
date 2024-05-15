package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class MainMenuController{
    @FXML
    public Button btnLogOut, btnExplorer, btnReport, btnWebe, btnUser, btnSetting, btnContact;
    @FXML
    public Label lblName;
    @FXML
    public Pane paneMenu;
    private int userId;
    private String firstName;
    private DataBaseConnection dbConnection = new DataBaseConnection();
    private String accType;
    private static final Color DEFAULT_COLOR = Color.web("#009ee0");
    private static final Color DEFAULT_TEXT_COLOR = Color.web("#ffffff");
    @FXML
    private void handleInternetButton(ActionEvent event) {
        switchScene(event, SceneType.INTERNET);
    }
    @FXML
    private void handleReportButton(ActionEvent event) {
        switchScene(event, SceneType.REPORT);
    }
    @FXML
    private void handleWebeButton(ActionEvent event) {
        switchScene(event, SceneType.WEBE);
    }
    @FXML
    private void handleUserProfileButton(ActionEvent event) {
        switchScene(event, SceneType.USER_PROFILE);
    }
    @FXML
    private void handleUserSettingButton(ActionEvent event) {
        switchScene(event, SceneType.SETTING);
    }
    @FXML
    private void handleContactButton(ActionEvent event) {
        switchScene(event, SceneType.CONTACT);
    }

    public void displayName(String firstName) {
        lblName.setText(firstName + ", wish you are having a bright day!");
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadSavedColors();
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setAccType(String accType) { this.accType = accType;}
    public enum SceneType {
        INTERNET, REPORT, WEBE, USER_PROFILE, SETTING, CONTACT
    }
    public void switchScene(ActionEvent event, SceneType sceneType) {
        String fxmlFile = "";
        String title = "Explorer";  // Assuming a common title for simplicity, can be adjusted if needed

        switch (sceneType) {
            case INTERNET:
                fxmlFile = "/com/cab302/wellbeing/InternetExplorer.fxml";
                break;
            case REPORT:
                fxmlFile = "/com/cab302/wellbeing/Report.fxml";
                break;
            case WEBE:
                fxmlFile = "/com/cab302/wellbeing/WellBeingTips.fxml";
                break;
            case USER_PROFILE:
                fxmlFile = "/com/cab302/wellbeing/UserProfile.fxml";
                break;
            case SETTING:
                fxmlFile = "/com/cab302/wellbeing/Setting.fxml";
                break;
            case CONTACT:
                fxmlFile = "/com/cab302/wellbeing/Contact.fxml";
                if("Developer".equals(accType)){
                    fxmlFile = "/com/cab302/wellbeing/DeveloperPage.fxml";
                }
                break;
            default:
                System.err.println("Unsupported scene type: " + sceneType);
                return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            // Check if the scene is INTERNET and then set the user ID
            if (sceneType == SceneType.INTERNET) {
                InternetExplorerController controller = fxmlLoader.getController();
                controller.setUserId(userId);  // Pass the user ID to the InternetExplorer controller
                controller.setFirstName(firstName);  // Pass the user ID to the InternetExplorer controller
            }

            if (sceneType == SceneType.USER_PROFILE) {
                UserProfileController controller = fxmlLoader.getController();
                controller.setUserId(userId);  // Pass the user ID to the UserProfile controller
                controller.displayUserProfile();
            }

            if (sceneType == SceneType.SETTING) {
                SettingController controller = fxmlLoader.getController();
                controller.setUserId(userId);  // Pass the user ID to the UserProfile controller
            }

            if (sceneType == SceneType.REPORT) {
                ReportController controller = fxmlLoader.getController();
                controller.setUserId(userId);  // Pass the user ID to the Report controller
                controller.displayLineChart();
                controller.displayBarChart();
            }

            if (sceneType == SceneType.CONTACT) {
                if("Developer".equals(accType)){
                    DeveloperController controller = fxmlLoader.getController();
                    controller.displayTable();
                }else{
                    ContactController controller = fxmlLoader.getController();
                    controller.setUserId(userId);  // Pass the user ID
                }
            }

            if (sceneType == SceneType.WEBE) {
                WellBeingTipsController controller = fxmlLoader.getController();
                controller.setUserId(userId);  // Pass the user ID
            }
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected Alert createAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public void btnLogOutOnAction(ActionEvent e) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, "Logout Confirmation", "Logging out", "Are you sure you want to log out?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Close the current window
                Stage stage = (Stage) btnLogOut.getScene().getWindow();
                stage.close();

                // Load and display the login page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/Login.fxml"));
                Parent root = loader.load();
                Stage loginStage = new Stage();
                loginStage.setTitle("Login");
                loginStage.setScene(new Scene(root));
                loginStage.show();
            } catch (IOException ex) {
                System.err.println("Error loading Login.fxml: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    public void applyColors(Color backgroundColor, Color textColor, Color buttonColor) {
        String backgroundHex = getHexColor(backgroundColor);
        String textHex = getHexColor(textColor);
        String buttonHex = getHexColor(buttonColor);

        if (paneMenu != null) {
            paneMenu.setStyle("-fx-background-color: " + backgroundHex + ";");
        }
        if (lblName != null) {
            lblName.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (btnLogOut != null) {
            btnLogOut.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnExplorer != null) {
            btnExplorer.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnReport != null) {
            btnReport.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnWebe != null) {
            btnWebe.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnUser != null) {
            btnUser.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnSetting != null) {
            btnSetting.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnContact != null) {
            btnContact.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
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

    private void closeCurrentWindow() {
        Stage stage = (Stage) btnLogOut.getScene().getWindow();
        stage.close();
    }

}