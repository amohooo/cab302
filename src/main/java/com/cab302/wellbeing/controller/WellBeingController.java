package com.cab302.wellbeing.controller;

import com.cab302.wellbeing.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class WellBeingController {
    @FXML
    private Button btnExit;

    @FXML
    private Button btnLogOut;
    @FXML
    TextField txtUsr;
    @FXML
    private PasswordField txtPwd;
    private Parent root;
    private Scene scene;
    private Stage stage;
    private void switchToMainMenuScene(ActionEvent e) {

        try {
            String username = txtUsr.getText();

            // Load the MainMenu FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cab302/wellbeing/MainMenu.fxml"));
            root = loader.load();
            //Parent mainMenuParent = FXMLLoader.load(getClass().getResource("/com/cab302/wellbeing/MainMenu.fxml"));
            MainMenuController mainMenuController = loader.getController();
            mainMenuController.displayName(username);
            if(mainMenuController.lblName.getText().equals("cab302")){
                mainMenuController.btnRegst.setVisible(true);
            } else {
                mainMenuController.btnRegst.setVisible(false);}
            scene= new Scene(root);

            // Get the current stage (window) using the event source.
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();

            // Set the scene to the MainMenu scene.
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println("Error loading MainMenu.fxml: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void btnExitOnAction(ActionEvent e){

        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();

    }

    @FXML
    private Label lblLoginMsg;
    public void lblLoginMsgOnAction(ActionEvent e){
        lblLoginMsg.setText("Your username or password is wrong");

        if(txtUsr.getText().isBlank() == true){
            lblLoginMsg.setText("Please fill in your username");

        } else if(txtPwd.getText().isBlank() == true){
            lblLoginMsg.setText("Please fill in your password");
        } else {
            validateLogin(e);
        }
    }


    public void validateLogin(ActionEvent e){
        DataBaseConnection connectNow = new DataBaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM useraccount WHERE UserName = '"+txtUsr.getText()+"' AND Password = '"+txtPwd.getText()+"'";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while(queryResult.next()){
                if (queryResult.getInt(1) == 1 && "cab302".equals(txtUsr.getText())){
                    lblLoginMsg.setText("Welcome "+ txtUsr.getText());
                    //btnExitOnAction(e);
                    switchToMainMenuScene(e);
                } else if (queryResult.getInt(1) == 1) {
                    lblLoginMsg.setText("Login Success");
                    //btnExitOnAction(e);
                    switchToMainMenuScene(e);
                } else {
                    lblLoginMsg.setText("Your username or password is wrong");
                }
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}