package com.pao.project.banca.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try {
            String schema = new String(Files.readAllBytes(Paths.get("src/com/pao/project/banca/resources/schema.sql")));
            String[] commands = schema.split(";");
            
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {
                for (String command : commands) {
                    if (!command.trim().isEmpty()) {
                        stmt.execute(command);
                    }
                }
                System.out.println("Database initialized successfully.");
            } catch (SQLException e) {
                System.err.println("Error executing schema: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error reading src/com/pao/project/banca/resources/schema.sql: " + e.getMessage());
        }
    }
}
