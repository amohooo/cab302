package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is a controller for managing the contact information in the application.
 * Currently, it is an empty class and does not contain any methods or fields.
 * Future implementations will include methods for sending messages through "contact us".
 */
public class ContactController {

    /**
     * This method is used to handle the Cancel button click event.
     * It closes the current window.
     */

    @FXML
    private Button btnCancel, btnSend;
    @FXML
    private Label lblMsg, lblEmail, lblContact, lblCall;
    @FXML
    private Pane paneContact;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtMessage;
    @FXML
    private Stage stage;
    int userId;

    private DataBaseConnection dbConnection = new DataBaseConnection();

    public void setUserId(int userId) {
        this.userId = userId;  // Now you can use this userId to store browsing data linked to the user
    }
    public void btnCancelOnAction(ActionEvent e){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void btnSendOnAction(ActionEvent e){
        this.saveMessage();
    }

    public void saveMessage(){
        if(!validateInputs())return;
        String insertQuery = "INSERT INTO ContactUs (UserID, Email, Message) VALUES(?, ?, ?)";
        try (Connection conn = dbConnection.getConnection(); // Get a fresh connection
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, this.userId);
            pstmt.setString(2, this.txtEmail.getText());
            pstmt.setString(3, this.txtMessage.getText());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        if (txtMessage.getText().isEmpty() || txtEmail.getText().isEmpty() ) {
            lblMsg.setText("Please fill all the information above.");
            return false;
        }

        if (!txtEmail.getText().contains("@")) {
            lblMsg.setText("Invalid email format.");
            return false;
        }

        if (emailExists(txtEmail.getText())) {
            lblMsg.setText("Email address already exists. Please use a different one.");
            return false;
        }

        if(isSameMessage()){
            lblMsg.setText("The message already exist.");
            return false;
        }

        return true;
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

    private boolean isSameMessage(){
        String query = "SELECT COUNT(*) FROM ContactUs WHERE  Email = ? and Message = ? and userId = ? ";
        try (Connection connectDB = new DataBaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, txtEmail.getText());
            preparedStatement.setString(2, txtMessage.getText());
            preparedStatement.setInt(3, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMsg.setText( e.getMessage());
        }
        return false;
    }
    public void applyColors(Color backgroundColor, Color textColor, Color buttonColor) {
        String backgroundHex = getHexColor(backgroundColor);
        String textHex = getHexColor(textColor);
        String buttonHex = getHexColor(buttonColor);

        if (btnCancel != null) {
            btnCancel.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (btnSend != null) {
            btnSend.setStyle("-fx-background-color: " + buttonHex + "; -fx-text-fill: " + textHex + ";");
        }
        if (lblMsg != null) {
            lblMsg.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (lblEmail != null) {
            lblEmail.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (lblContact != null) {
            lblContact.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (lblCall != null) {
            lblCall.setStyle("-fx-text-fill: " + textHex + ";");
        }
        if (paneContact != null) {
            paneContact.setStyle("-fx-background-color: " + backgroundHex + ";");
        }
    }

    private String getHexColor(Color color) {
        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
}
