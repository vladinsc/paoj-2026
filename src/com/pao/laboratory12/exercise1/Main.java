package com.pao.laboratory12.exercise1;

import com.pao.laboratory12.model.Author;
import com.pao.laboratory12.model.Book;
import com.pao.laboratory12.repository.AuthorRepository;
import com.pao.laboratory12.repository.BookRepository;
import com.pao.laboratory12.util.DatabaseConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        initSchema();

        AuthorRepository authorRepo = new AuthorRepository();
        BookRepository bookRepo = new BookRepository();

        System.out.println("=== EXERCISE 1 — CRUD Demo ===\n");

        // 1. Create
        Author author = new Author("J.K. Rowling", "UK");
        authorRepo.save(author);
        System.out.println("Saved Author: " + author);

        Book book = new Book("Harry Potter and the Sorcerer's Stone", author.getId());
        bookRepo.save(book);
        System.out.println("Saved Book: " + book);

        // 2. Read
        Optional<Author> foundAuthor = authorRepo.findById(author.getId());
        System.out.println("Found Author: " + foundAuthor.orElse(null));

        List<Book> allBooks = bookRepo.findAll();
        System.out.println("All Books: " + allBooks);

        // 3. Update
        author.setCountry("United Kingdom");
        authorRepo.update(author);
        System.out.println("Updated Author: " + authorRepo.findById(author.getId()).get());

        // 4. Delete
        bookRepo.delete(book.getId());
        System.out.println("Deleted Book with ID: " + book.getId());
        System.out.println("All Books after delete: " + bookRepo.findAll());

        authorRepo.delete(author.getId());
        System.out.println("Deleted Author with ID: " + author.getId());

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
