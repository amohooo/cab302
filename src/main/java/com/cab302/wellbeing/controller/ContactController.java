package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    private Button btnCancel;

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
}
