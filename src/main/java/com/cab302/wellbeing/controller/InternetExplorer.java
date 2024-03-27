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
import java.util.HashMap;
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
    private long endTime;
    private HashMap<String, Long> siteTimeSpent; // Tracks time spent per site
    private long dailyUsageLimit; // Total daily usage limit in milliseconds
    private HashMap<String, Long> siteUsageLimits; // Site-specific limits

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        startTime = System.currentTimeMillis();
        siteTimeSpent = new HashMap<>();
        siteUsageLimits = new HashMap<>();
        // Example limit: 1 hour total daily usage.
        dailyUsageLimit = 3600000;

        // Example of setting a site-specific limit (30 minutes for youtube.com).
        siteUsageLimits.put("www.youtube.com", 1800000L);
        engine = webView.getEngine();
        homePage = "www.google.com";
        txtAddr.setText(homePage);
        webZoom = 1;
        LoadPage();
    }

    private void updateAndCheckUsage(String site, long visitStartTime) {
        long visitEndTime = System.currentTimeMillis();
        long visitDuration = visitEndTime - visitStartTime;

        // Update siteTimeSpent and check against siteUsageLimits and dailyUsageLimit
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
