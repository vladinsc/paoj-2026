package com.pao.project.banca.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static AuditService instance;
    private static final String FILE_PATH = "audit.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditService() {
        // Ensure header exists if file is new
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            // No header logic here to strictly follow "append" mode requirement and CSV simplicity
            // but usually a header is good. The requirement says: nume_actiune,timestamp
        } catch (IOException e) {
            System.err.println("Error initializing AuditService: " + e.getMessage());
        }
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public synchronized void logAction(String actionName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            out.println(actionName + "," + timestamp);
        } catch (IOException e) {
            System.err.println("Error writing to audit file: " + e.getMessage());
        }
    }
}
