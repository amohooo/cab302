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


public class MainMenuController {
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

    public void setUserId(int userId) {
        this.userId = userId;
        if (this.firstName == null) {
            this.firstName = fetchFirstNameFromDatabase(userId);

            System.out.println("userId: " + userId);
        }
        loadSavedColors();
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void displayName(String firstName) {
        lblName.setText(firstName + ", wish you are having a bright day!");
    }

    private String fetchFirstNameFromDatabase(int userId) {
        String query = "SELECT firstName FROM useraccount WHERE userId = ?";
        try (Connection conn = new DataBaseConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("firstName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "User"; // Default value if the user is not found
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public enum SceneType {
        INTERNET, REPORT, WEBE, USER_PROFILE, SETTING, CONTACT
    }

    public void switchScene(ActionEvent event, SceneType sceneType) {
        String fxmlFile = "";
        String title = "Explorer";

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
                if ("Developer".equals(accType)) {
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

            // Fetch current color settings
            Color backgroundColor = (Color) paneMenu.getBackground().getFills().get(0).getFill();
            Color textColor = (Color) lblName.getTextFill();
            Color buttonColor = (Color) btnExplorer.getBackground().getFills().get(0).getFill();

            switch (sceneType) {
                case INTERNET:
                    InternetExplorerController internetController = fxmlLoader.getController();
                    internetController.setUserId(userId);
                    internetController.setFirstName(firstName);
                    //setInternetExplorerController(internetController);
                    internetController.applyColors(backgroundColor, textColor, buttonColor);
                    break;

                case REPORT:
                    ReportController reportController = fxmlLoader.getController();
                    reportController.setUserId(userId);
                    reportController.displayLineChart();
                    reportController.displayBarChart();
                    reportController.applyColors(backgroundColor, textColor, buttonColor);
                    break;

                case WEBE:
                    WellBeingTipsController webeController = fxmlLoader.getController();
                    webeController.setUserId(userId);
                    webeController.setFirstName(firstName);
                    //setWellBeingTipsController(webeController);
                    webeController.applyColors(backgroundColor, textColor, buttonColor);
                    break;

                case USER_PROFILE:
                    UserProfileController userProfileController = fxmlLoader.getController();
                    userProfileController.setUserId(userId);
                    userProfileController.loadQuestions();
                    userProfileController.displayUserProfile();
                    userProfileController.applyColors(backgroundColor, textColor, buttonColor);
                    break;

                case SETTING:
                    SettingController settingController = fxmlLoader.getController();
                    settingController.setUserId(userId);
                    settingController.applyColors(backgroundColor, textColor, buttonColor);
                    break;

                case CONTACT:
                    if ("Developer".equals(accType)) {
                        DeveloperController developerController = fxmlLoader.getController();
                        developerController.displayTable();
                        developerController.applyColors(backgroundColor, textColor, buttonColor);
                    } else {
                        ContactController contactController = fxmlLoader.getController();
                        contactController.setUserId(userId);
                        contactController.applyColors(backgroundColor, textColor, buttonColor);
                    }
                    break;
            }
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
        if (sceneType == SceneType.SETTING) {
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            stage.close();
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
        String query = "SELECT BackgroundColor, TextColor, ButtonColor, ButtonTextColor FROM ColorSettings WHERE UserID = ?";
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