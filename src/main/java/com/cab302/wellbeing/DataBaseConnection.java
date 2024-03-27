package com.cab302.wellbeing;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection {

    public Connection databaseLink;

    public Connection getConnection(){

        String databaseName = "wellbeing";
        String databaseUser = "root";
        String databasePassword ="Hmhzalk1";
        String url = "jdbc:mysql://127.0.0.1:3306/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return databaseLink;

    }
}
