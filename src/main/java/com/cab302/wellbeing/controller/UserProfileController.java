package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is a controller for the User Profile functionality in the application.
 * It provides methods to handle the user profile (name, email, password, etc.) of the application.
 */
public class UserProfileController {
    @FXML
    public TextField txtUserName, txtFirstName, txtLastName, txtEmail, txtPassword,txtA1, txtA2;
    @FXML
    private ToggleGroup accTypeGroup;
    @FXML
    public ChoiceBox<String> chbQ1, chbQ2;
    @FXML
    public Button btnCancel, btnSave;
    @FXML
    public RadioButton radbAdm, radbGen, radbDev;
    @FXML
    public Label lblMsg, lblUserPro, lblUser, lblFirst, lblLast, lblEmail, lblPwd, lblAccType, lblSQ1, lblSQ2, lblAn1, lblAn2;
    @FXML
    private Pane paneProfile;
    int userId;

    private DataBaseConnection dbConnection = new DataBaseConnection();

    public void setUserId(int userId) {
        this.userId = userId;  // Now you can use this userId to store browsing data linked to the user
    }


    public void displayUserName(String userName){
        this.txtUserName.setText(userName);
    }

    public void displayFirstName(String firstName){
        this.txtFirstName.setText(firstName);
    }

    public void displayLastName(String lastName){
        this.txtLastName.setText(lastName);
    }

    public void displayEmail(String email){
        this.txtEmail.setText(email);
    }

    public void displayPassword(String password){
        this.txtPassword.setText(password);
    }

    public void displayAccType(String accType){
        if("Developer".equals(accType)){
            this.radbDev.setSelected(true);
            this.radbDev.setDisable(true);
            this.radbAdm.setDisable(true);
            this.radbGen.setDisable(true);
        }else if("Admin".equals(accType)){
            this.radbAdm.setSelected(true);
            this.radbAdm.setDisable(true);
            this.radbDev.setDisable(true);
            this.radbGen.setDisable(true);
        }else{
            this.radbGen.setSelected(true);
            this.radbGen.setDisable(true);
            this.radbDev.setDisable(true);
            this.radbAdm.setDisable(true);
        }
    }

    public void displayA1(String a1){
        this.txtA1.setText(a1);
    }

    public void displayA2(String a2){
        this.txtA2.setText(a2);
    }

    public void displayUserProfile(){
        try {
            Connection conn = dbConnection.getConnection(); // Get a fresh connection
            String selectQuery = " SELECT userId, userName, emailAddress, firstName, lastName, passwordHash, accType,Question_1, Question_2, Answer_1, Answer_2  FROM WellBeing.useraccount " +
                    " LEFT JOIN WellBeing.PwdQuestions1 ON WellBeing.PwdQuestions1.QuestionID_1 = WellBeing.useraccount.QuestionID_1 " +
                    " LEFT JOIN WellBeing.PwdQuestions2 ON WellBeing.PwdQuestions2.QuestionID_2 = WellBeing.useraccount.QuestionID_2 " +
                    " WHERE userId = " + this.userId;
            PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);
            ResultSet queryResult = preparedStatement.executeQuery();
            if (queryResult.next()){
                int userId = queryResult.getInt("userId");  // Retrieve user ID
                String storedHash = queryResult.getString("passwordHash"); // Retrieved hashed password
                String accType = queryResult.getString("accType");
                String userName = queryResult.getString("userName");// Account type
                String firstName = queryResult.getString("firstName");
                String lastName = queryResult.getString("lastName");
                String email = queryResult.getString("emailAddress");
                String q1 = queryResult.getString("Question_1");
                String q2 = queryResult.getString("Question_2");
                String a1 = queryResult.getString("Answer_1");
                String a2 = queryResult.getString("Answer_2");

                this.displayPassword(storedHash);
                this.displayLastName(lastName);
                this.displayFirstName(firstName);
                this.displayEmail(email);
                this.displayUserName(userName);
                this.displayAccType(accType);
                this.chbQ1.setValue(q1);
                this.chbQ2.setValue(q2);
                this.displayA1(a1);
                this.displayA2(a2);


            }
            } catch(SQLException e){
                System.out.println("SQL Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

    public void saveUserProfile(){
        if (!validateInputs()) {
            return; // Exit if inputs are not valid
        }

        String insertQuery = "UPDATE useraccount SET userName = ?, emailAddress = ?, firstName = ?, lastName = ?, passwordHash = ?, QuestionID_1 = ? , QuestionID_2 = ? , Answer_1 = ?, Answer_2 = ? WHERE userId = ?";
        try (Connection conn = dbConnection.getConnection(); // Get a fresh connection
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, this.txtUserName.getText());
            pstmt.setString(2, this.txtEmail.getText());
            pstmt.setString(3, this.txtFirstName.getText());
            pstmt.setString(4, this.txtLastName.getText());
            pstmt.setString(5, BCrypt.hashpw(this.txtPassword.getText(), BCrypt.gensalt()));
            int questionID1 = getQuestionID(chbQ1.getSelectionModel().getSelectedItem(), "PwdQuestions1", "Question_1", "QuestionID_1", conn);
            int questionID2 = getQuestionID(chbQ2.getSelectionModel().getSelectedItem(), "PwdQuestions2", "Question_2", "QuestionID_2", conn);
            pstmt.setInt(6, questionID1);
            pstmt.setInt(7,questionID2);
            String hashedAnswer1 = BCrypt.hashpw(txtA1.getText(), BCrypt.gensalt());
            String hashedAnswer2 = BCrypt.hashpw(txtA2.getText(), BCrypt.gensalt());
            pstmt.setString(8,hashedAnswer1);
            pstmt.setString(9,hashedAnswer2);
            pstmt.setInt(10,userId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void cancelOnAction(){
        Stage stage = (Stage) txtUserName.getScene().getWindow();
        stage.close();  // Closes the current window
    }

    public void saveOnAction(){
        this.saveUserProfile();
        this.displayUserProfile();
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
            return -1;
        }
    }

    private boolean validateInputs() {
        if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || chbQ1.getSelectionModel().isEmpty() || chbQ2.getSelectionModel().isEmpty() ||
                txtA1.getText().trim().isEmpty() || txtA2.getText().trim().isEmpty()) {
            lblMsg.setText("Please fill all the information above.");
            return false;
        }

        if (!txtEmail.getText().contains("@")) {
            lblMsg.setText("Invalid email format.");
            return false;
        }

        if (usernameExists(txtUserName.getText())) {
            lblMsg.setText("Username already exists. Please choose a different one.");
            return false;
        }

        if (emailExists(txtEmail.getText())) {
            lblMsg.setText("Email address already exists. Please use a different one.");
            return false;
        }

        return true;
    }

    private boolean usernameExists(String username) {
        return exists("userName", username);
    }

    private boolean emailExists(String email) {
        return exists("emailAddress", email);
    }

    private boolean exists(String columnName, String value) {
        String query = "SELECT COUNT(*) FROM useraccount WHERE " + columnName + " = ? and userId != " + userId;
        try (Connection connectDB = new DataBaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMsg.setText("Failed to validate " + columnName + ": " + e.getMessage());
        }
        return false;
    }

    public void loadQuestions(){
        loadQuestionsToChoiceBox(chbQ1, "PwdQuestions1", "Question_1");
        loadQuestionsToChoiceBox(chbQ2, "PwdQuestions2", "Question_2");
    }

    private void loadQuestionsToChoiceBox(ChoiceBox<String> choiceBox, String tableName, String questionColumn) {
        String query = "SELECT " + questionColumn + " FROM " + tableName;
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
    public void applyColors(Color backgroundColor, Color textColor, Color buttonColor) {
        String backgroundHex = getHexColor(backgroundColor);
        String textHex = getHexColor(textColor);
        String buttonHex = getHexColor(buttonColor);

        if (paneProfile != null) {
            paneProfile.setStyle("-fx-background-color: " + backgroundHex + ";");
        }
        if (btnCancel != null) {
            btnCancel.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnSave != null) {
            btnSave.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (radbAdm != null) {
            radbAdm.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (radbGen != null) {
            radbGen.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (radbDev != null) {
            radbDev.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblMsg != null) {
            lblMsg.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblUserPro != null) {
            lblUserPro.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblUser != null) {
            lblUser.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblFirst != null) {
            lblFirst.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblLast != null) {
            lblLast.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblEmail != null) {
            lblEmail.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblPwd != null) {
            lblPwd.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblAccType != null) {
            lblAccType.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblSQ1 != null) {
            lblSQ1.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblSQ2 != null) {
            lblSQ2.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblAn1 != null) {
            lblAn1.setStyle(" -fx-text-fill: " + textHex + ";");
        }
        if (lblAn2 != null) {
            lblAn2.setStyle(" -fx-text-fill: " + textHex + ";");
        }
    }
    private String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
}

