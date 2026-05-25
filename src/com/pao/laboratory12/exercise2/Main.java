package com.pao.laboratory12.exercise2;

import com.pao.laboratory12.model.*;
import com.pao.laboratory12.repository.*;
import com.pao.laboratory12.service.AuditService;
import com.pao.laboratory12.service.LibraryService;
import com.pao.laboratory12.util.DatabaseConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        initSchema();

        AuditService audit = AuditService.getInstance();
        AuthorRepository authorRepo = new AuthorRepository();
        BookRepository bookRepo     = new BookRepository();
        ReaderRepository readerRepo = new ReaderRepository();
        LibraryService libraryService = LibraryService.getInstance();

        System.out.println("=== EXERCISE 2 — Full Demo (Transactions & Audit) ===\n");

        // 1. Add author
        Author author = new Author("Gabriel Garcia Marquez", "CO");
        authorRepo.save(author);
        audit.log("add_author");
        System.out.println("1. Autor adaugat: " + author);

        // 2. Add book
        Book book1 = new Book("100 de ani de singuratate", author.getId());
        bookRepo.save(book1);
        audit.log("add_book");
        System.out.println("2. Carte adaugata: " + book1);

        // 3. Add reader
        Reader reader = new Reader("Ion Popescu", "ion.popescu@email.com");
        readerRepo.save(reader);
        audit.log("add_reader");
        System.out.println("3. Cititor adaugat: " + reader);

        // 4. List books
        audit.log("list_books");
        System.out.println("4. Toate cartile: " + bookRepo.findAll());

        // 5. Find book
        audit.log("find_book_by_id");
        System.out.println("5. Cauta carte ID=" + book1.getId() + ": " + bookRepo.findById(book1.getId()));

        // 6. Update book
        book1.setTitle("100 de ani de singuratate (Ed. revizuita)");
        bookRepo.update(book1);
        audit.log("update_book");
        System.out.println("6. Carte actualizata: " + book1);

        // 7. Borrow book (Transaction)
        long loanId = libraryService.borrowBook(reader.getId(), book1.getId());
        audit.log("borrow_book");
        System.out.println("7. Imprumut creat ID=" + loanId);

        // 8. Return book (Transaction)
        libraryService.returnBook(loanId);
        audit.log("return_book");
        System.out.println("8. Carte returnata.");

        // 9. JOIN report
        audit.log("report_active_loans");
        System.out.println("9. Raport JOIN (imprumuturi active): " + libraryService.getActiveLoansWithDetails());

        // 10. Delete reader
        try (java.sql.PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement("DELETE FROM loan WHERE reader_id = ?")) {
            ps.setLong(1, reader.getId());
            ps.executeUpdate();
        }
        readerRepo.delete(reader.getId());
        audit.log("delete_reader");
        System.out.println("10. Cititor sters.");

        System.out.println("\nDemo complet. Verifica audit.csv");
        DatabaseConnection.getInstance().close();
    }

    private static void initSchema() throws SQLException, IOException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String schemaPath = "src/com/pao/laboratory12/resources/schema.sql";
        String content = Files.readString(Paths.get(schemaPath));
        
        try (Statement stmt = conn.createStatement()) {
            String[] lines = content.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                if (!line.trim().startsWith("--")) {
                    sb.append(line).append("\n");
                }
            }
            String[] queries = sb.toString().split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    stmt.execute(query.trim());
                }
            }
        }
    }
}
