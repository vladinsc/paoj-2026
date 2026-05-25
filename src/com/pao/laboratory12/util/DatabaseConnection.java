package com.pao.laboratory12.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws IOException, SQLException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("com/pao/laboratory12/resources/db.properties")) {
            if (is == null) {
                // Fallback for different build setups
                try (InputStream is2 = getClass().getClassLoader().getResourceAsStream("db.properties")) {
                    if (is2 == null) {
                        throw new IOException("Nu gasesc db.properties in resources/");
                    }
                    props.load(is2);
                }
            } else {
                props.load(is);
            }
        }
        String url  = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");

        if (url.startsWith("jdbc:sqlite:")) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }

        this.connection = DriverManager.getConnection(url, user, pass);

        try (var stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException ignored) {}
    }

    public static synchronized DatabaseConnection getInstance()
            throws IOException, SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
