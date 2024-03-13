package com.cab302.wellbeing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WellBeingApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try{
            //FXMLLoader fxmlLoader = new FXMLLoader(WellBeingApplication.class.getResource("login.fxml"));
            //Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);

            stage.setTitle("Well Being");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}