package com.cab302.wellbeing.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InternetExplorer implements Initializable {

    @FXML
    private WebView webView;

    @FXML
    private TextField txtAddr;
    @FXML
    private Button btnRfrsh;
    @FXML
    private Button btnZmIn;
    @FXML
    private Button btnZmOut;
    private double webZoom;
    private WebHistory history;

    private WebEngine engine;
    private String homePage;
    private long startTime;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        startTime = System.currentTimeMillis();
        engine = webView.getEngine();
        homePage = "www.google.com";
        txtAddr.setText(homePage);
        webZoom = 1;
        LoadPage();
    }
    public void LoadPage(){
        engine.load("http://" + txtAddr.getText());
    }
    public void refreshPage(){
        engine.reload();
    }
    public void zoomIn(){

        if (webZoom <= 2){
            webZoom += 0.25;
            webView.setZoom(webZoom);
        } else {
            webZoom = 5;
        }

    }
    public void zoomOut(){
        if (webZoom >= 0.25){
        webZoom -= 0.25;
        webView.setZoom(webZoom);
        } else {
            webZoom = 0;
        }
    }
    public void displayHistory(){
        history = engine.getHistory();;
        ObservableList<WebHistory.Entry> entries = history.getEntries();

        for(WebHistory.Entry entry : entries){
            System.out.println(entry.getUrl() + " " + entry.getLastVisitedDate());
        }
    }

    public void back(){
        history = engine.getHistory();;
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        history.go(-1);
        txtAddr.setText(entries.get(history.getCurrentIndex()).getUrl());
    }

    public void forward(){
        history = engine.getHistory();;
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        history.go(1);
        txtAddr.setText(entries.get(history.getCurrentIndex()).getUrl());
    }

    public void executeJS() {
        engine.executeScript("window.location = \"https://www.youtube.com\";");
        //ObservableList<WebHistory.Entry> entries = history.getEntries();
        //txtAddr.setText(entries.get(history.getCurrentIndex()).getUrl());
    }
    public void onClose(ActionEvent event) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Internet Explorer was open for " + duration + " milliseconds.");

        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}
