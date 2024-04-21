package com.cab302.wellbeing;

import java.sql.*;

public class DataBaseConnection {

    public Connection databaseLink;
    private void createDatabase() {
        String url = "jdbc:mysql://127.0.0.1:3306/";
        String databaseUser = "cab302";
        String databasePassword = "cab302";
        try (Connection conn = DriverManager.getConnection(url, databaseUser, databasePassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS WellBeing");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        if (databaseLink != null) {
            return databaseLink; // return existing connection if already established
        }

        String databaseName = "WellBeing";
        String databaseUser = "cab302";
        String databasePassword = "cab302";
        String url = "jdbc:mysql://127.0.0.1:3306/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
            createDatabase(); // Ensure the database exists
            createTable(); // Ensure the user table exists
        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaseLink;
    }
    private void createTable() {
        try (Statement statement = databaseLink.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS useraccount ( "
                    + "userId INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "userName VARCHAR(255) UNIQUE NOT NULL, "
                    + "emailAddress VARCHAR(255) UNIQUE NOT NULL CHECK (emailAddress LIKE '%@%'), "
                    + "firstName VARCHAR(255) NOT NULL, "
                    + "lastName VARCHAR(255) NOT NULL, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "accType ENUM('Admin', 'General') NOT NULL"
                    + ")";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void insertUser() {
        String userName = "cab302";
        String firstName = "cab302";
        String lastName = "cab302";
        String password = "cab302";  // Remember to hash this in real applications
        String emailAddress = "cab302@qut.edu.au";
        String accType = "Admin";

        String query = "INSERT INTO useraccount (userName, firstName, lastName, password, emailAddress, accType) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = databaseLink.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, password);
            pstmt.setString(5, emailAddress);
            pstmt.setString(6, accType);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User inserted successfully.");
            }
        } catch (SQLException e) {
            if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
                // Specific handling for duplicate entry based on the exception message or error code
                System.out.println("User not inserted due to duplicate entry: " + e.getMessage());
            } else {
                System.err.println("Error inserting user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void initializeAndInsertUser() {
        getConnection();  // Establish connection and ensure the database and table are set up
        insertUser();     // Insert the user data
    }

}
