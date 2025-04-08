package com.buymeproject.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/buymeauction"; // DB Name
    private static final String USER = "root"; // MySQL user name
    private static final String PASSWORD = "ahnaf123"; // MySQL password

   
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Test the database connection
    public static void main(String[] args) {
        try (Connection conn = DBConfig.getConnection()) {
            System.out.println("Successfully connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
