package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    public TextField txtFName, txtLName, txtUsername, txtEmail, txtA1, txtA2;
    @FXML
    public PasswordField ptxtPwd, ptxtRetp;
    @FXML
    public RadioButton radbAdm;
    @FXML
    private Button btnRgst, btnCncl;
    @FXML
    public ChoiceBox<String> chbQ1, chbQ2;
    @FXML
    public Label lblMsg;
    @FXML
    public CheckBox ckUser;
//    private DataBaseConnection dbConnection;
//    public RegisterController(DataBaseConnection dbConnection) {
//        this.dbConnection = dbConnection;  // Dependency injection of the database connection
//    }

    @FXML
    public void initialize() {
        loadQuestionsToChoiceBox(chbQ1, "PwdQuestions1", "Question_1", "QuestionID_1");
        loadQuestionsToChoiceBox(chbQ2, "PwdQuestions2", "Question_2", "QuestionID_2");
    }

    private void loadQuestionsToChoiceBox(ChoiceBox<String> choiceBox, String tableName, String questionColumn, String idColumn) {
        String query = "SELECT " + idColumn + ", " + questionColumn + " FROM " + tableName;
        try (Connection connectDB = new DataBaseConnection().getConnection();
             PreparedStatement pstmt = connectDB.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String question = rs.getString(questionColumn);
                choiceBox.getItems().add(question);
            }
        } catch (SQLException e) {
            System.err.println("Error loading questions from " + tableName + ": " + e.getMessage());
        }
    }
    public void registerUser() {
        if (!validateInputs()) {
            return; // Exit if inputs are not valid
        }

        String accType = radbAdm.isSelected() ? "Admin" : "General";
        String hashedPassword = BCrypt.hashpw(ptxtPwd.getText(), BCrypt.gensalt());
        String question1 = chbQ1.getSelectionModel().getSelectedItem();
        String question2 = chbQ2.getSelectionModel().getSelectedItem();

        // Use try-with-resources for managing database connection and prepared statement
        try (Connection connectDB = new DataBaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(
                     "INSERT INTO useraccount (userName, firstName, lastName, passwordHash, emailAddress, QuestionID_1, QuestionID_2, Answer_1, Answer_2, accType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            int questionID1 = getQuestionID(question1, "PwdQuestions1", "Question_1", "QuestionID_1", connectDB);
            int questionID2 = getQuestionID(question2, "PwdQuestions2", "Question_2", "QuestionID_2", connectDB);

            preparedStatement.setString(1, txtUsername.getText());
            preparedStatement.setString(2, txtFName.getText());
            preparedStatement.setString(3, txtLName.getText());
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setString(5, txtEmail.getText());
            preparedStatement.setInt(6, questionID1);
            preparedStatement.setInt(7, questionID2);
            preparedStatement.setString(8, BCrypt.hashpw(txtA1.getText(), BCrypt.gensalt()));
            preparedStatement.setString(9, BCrypt.hashpw(txtA2.getText(), BCrypt.gensalt()));
            preparedStatement.setString(10, accType);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                lblMsg.setText("Successfully registered.");
                closeWindowWithDelay();
            } else {
                lblMsg.setText("Registration failed. Please try again.");
            }
        } catch (SQLException ex) {
            lblMsg.setText("Registration error: " + ex.getMessage());
            System.err.println("SQL error during registration: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void closeWindowWithDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.2)); // Introduce a delay before closing the window for UX reasons
        delay.setOnFinished(event -> closeWindow());
        delay.play();
    }


    private int getQuestionID(String question, String tableName, String questionColumn, String idColumn, Connection connectDB) {
        String query = "SELECT " + idColumn + " FROM " + tableName + " WHERE " + questionColumn + " = ?";
        try (PreparedStatement pstmt = connectDB.prepareStatement(query)) {
            pstmt.setString(1, question);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(idColumn);
                } else {
                    throw new SQLException("Question not found: " + question);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving question ID from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return -1; // Handle this as appropriate in your context
        }
    }

    public void closeWindow() {
        if (lblMsg != null && lblMsg.getScene() != null) {
            Stage stage = (Stage) lblMsg.getScene().getWindow();
            if (stage != null) {
                stage.close();
            } else {
                System.out.println("Stage is null, cannot close the window");
            }
        } else {
            System.out.println("Scene is null or lblMsg is null, cannot close the window");
        }
    }

    public boolean validateInputs() {
        // Check for empty fields
        if (txtFName.getText().isEmpty() || txtLName.getText().isEmpty() || txtUsername.getText().isEmpty() || ptxtPwd.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || ptxtRetp.getText().isEmpty() || chbQ1.getSelectionModel().isEmpty() || chbQ1.getSelectionModel().isEmpty() ||
                chbQ1.getSelectionModel().isEmpty() || chbQ2.getSelectionModel().isEmpty() || txtA1.getText().trim().isEmpty() || txtA2.getText().trim().isEmpty()){
            lblMsg.setText("Please fill all the information above.");
            return false;
        }

        // Check if the ChoiceBoxes for security questions have selections
        if (chbQ1.getSelectionModel().getSelectedItem() == null || chbQ2.getSelectionModel().getSelectedItem() == null ||
                (chbQ1.getSelectionModel().getSelectedItem() == null && chbQ2.getSelectionModel().getSelectedItem() == null)) {
            lblMsg.setText("Please select security questions.");
            return false;
        }

        // Check for valid email format
        if (!txtEmail.getText().contains("@")) {
            lblMsg.setText("Invalid email format.");
            return false;
        }

        // Check for matching passwords
        if (!ptxtPwd.getText().equals(ptxtRetp.getText())) {
            lblMsg.setText("Passwords do not match.");
            return false;
        }

        // Check for existing username
        if (usernameExists(txtUsername.getText())) {
            lblMsg.setText("Username already exists. Please choose a different one.");
            return false;
        }

        // Check for existing email
        if (emailExists(txtEmail.getText())) {
            lblMsg.setText("Email address already exists. Please use a different one.");
            return false;
        }

        // Check for user agreement acceptance
        if (!ckUser.isSelected()) {
            lblMsg.setText("You must agree to the user agreement to register.");
            return false;
        }

        if (txtA1.getText().trim().isEmpty() || txtA2.getText().trim().isEmpty() || (txtA1.getText().trim().isEmpty() && txtA2.getText().trim().isEmpty())) {
            lblMsg.setText("Please fill in the security answers.");
            return false;
        }

        return true;
    }

    public void setBtnRgst(ActionEvent e) {
        registerUser(); // Just call registerUser without parameters
    }

    public void setBtnCncl(ActionEvent e) {
        closeWindow();
    }

    private boolean usernameExists(String username) {
        return exists("Username", username);
    }

    private boolean emailExists(String email) {
        return exists("emailAddress", email);
    }

    private boolean exists(String columnName, String value) {
        try (Connection connectDB = new DataBaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement("SELECT COUNT(*) FROM useraccount WHERE " + columnName + " = ?")) {
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // True if count is greater than 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMsg.setText("Failed to validate " + columnName + ": " + e.getMessage());
        }
        return false;
    }
    public void switchToUserAgreementScene(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/UserAgreement.fxml"));
            Parent root1 = fxmlLoader.load();
            UserAgreementController userAgreementController = fxmlLoader.getController();
            userAgreementController.setRegisterCheckbox(ckUser);

            Stage stage = new Stage();
            stage.setTitle("Explorer");
            stage.setScene(new Scene(root1));
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading InternetExplorer.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
