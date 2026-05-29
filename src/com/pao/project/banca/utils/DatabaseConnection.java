package com.pao.project.banca.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/com/pao/project/banca/resources/db.properties")) {
            props.load(fis);
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.connection == null || instance.connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
