package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    public TextField txtUserName;

    @FXML
    public TextField txtFirstName;

    @FXML
    public TextField txtLastName;

    @FXML
    public TextField txtEmail;

    @FXML
    public TextField txtPassword;

    @FXML
    public Button btnCancel;

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

    public void displayUserProfile(){
        try {
            Connection conn = dbConnection.getConnection(); // Get a fresh connection
            String selectQuery = "SELECT * FROM useraccount WHERE userId = " + this.userId;
            PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);
            ResultSet queryResult = preparedStatement.executeQuery();
            if (queryResult.next()){
                int userId = queryResult.getInt("userId");  // Retrieve user ID
                String storedHash = queryResult.getString("passwordHash"); // Retrieved hashed password
                String accType = queryResult.getString("AccType");
                String userName = queryResult.getString("userName");// Account type
                String firstName = queryResult.getString("firstName");
                String lastName = queryResult.getString("lastName");
                String email = queryResult.getString("emailAddress");
                this.displayPassword(storedHash);
                this.displayLastName(lastName);
                this.displayFirstName(firstName);
                this.displayEmail(email);
                this.displayUserName(userName);
            }
            } catch(SQLException e){
                System.out.println("SQL Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

    public void saveUserProfile(){
        String insertQuery = "UPDATE useraccount SET userName = ?, emailAddress = ?, firstName = ?, lastName = ?, passwordHash = ? WHERE userId = ?";
        try (Connection conn = dbConnection.getConnection(); // Get a fresh connection
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, this.txtUserName.getText());
            pstmt.setString(2, this.txtEmail.getText());
            pstmt.setString(3, this.txtFirstName.getText());
            pstmt.setString(4, this.txtLastName.getText());
            pstmt.setString(5, BCrypt.hashpw(this.txtPassword.getText(), BCrypt.gensalt()));
            pstmt.setInt(6, this.userId);
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
}

