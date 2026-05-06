package database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import models.Book;
import models.Member;
import models.BorrowRecord;
import java.util.function.Consumer;

/**
 * DatabaseManager class handles all database operations for the library system.
 * Manages SQLite database connections and CRUD operations for books, members, and borrow records.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library_system.db";
    private Connection connection;

    // Constructor - establishes database connection
    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            if (this.connection != null) {
                initializeDatabase();
                System.out.println("✓ Database initialized successfully");
            } else {
                System.err.println("Database connection error: Failed to create connection");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Database connection error: SQLite JDBC driver not found - " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    /**
     * Connect to the database asynchronously on a background thread.
     * The provided callback will be invoked with the created DatabaseManager
     * once initialization completes.
     */
    public static void connectAsync(Consumer<DatabaseManager> callback) {
        new Thread(() -> {
            DatabaseManager mgr = new DatabaseManager();
            if (callback != null) callback.accept(mgr);
        }, "Database-Loader").start();
    }

    /**
     * Initializes database tables if they don't exist
     */
    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Create books table
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "book_id INTEGER PRIMARY KEY," +
                    "title TEXT NOT NULL," +
                    "author TEXT NOT NULL," +
                    "category TEXT NOT NULL," +
                    "availability_status TEXT NOT NULL)");

            // Create members table
            stmt.execute("CREATE TABLE IF NOT EXISTS members (" +
                    "member_id INTEGER PRIMARY KEY," +
                    "member_name TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "membership_type TEXT NOT NULL)");

            // Create borrow_records table
            stmt.execute("CREATE TABLE IF NOT EXISTS borrow_records (" +
                    "record_id INTEGER PRIMARY KEY," +
                    "book_id INTEGER NOT NULL," +
                    "member_id INTEGER NOT NULL," +
                    "borrow_date DATE NOT NULL," +
                    "due_date DATE NOT NULL," +
                    "return_status TEXT NOT NULL)");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    // ============ BOOK OPERATIONS ============

    /**
     * Create - Add a new book to the database
     */
    public boolean addBook(Book book) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "INSERT INTO books (title, author, category, availability_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setString(4, book.getAvailabilityStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieve - Get all books
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return books;
        }
        String sql = "SELECT * FROM books";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("availability_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Retrieve - Get book by ID
     */
    public Book getBookById(int bookId) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return null;
        }
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("availability_status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieve - Search books by title
     */
    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return books;
        }
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("availability_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching books by title: " + e.getMessage());
        }
        return books;
    }

    /**
     * Retrieve - Search books by author
     */
    public List<Book> searchBooksByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return books;
        }
        String sql = "SELECT * FROM books WHERE author LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + author + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("availability_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching books by author: " + e.getMessage());
        }
        return books;
    }

    /**
     * Retrieve - Filter books by category
     */
    public List<Book> filterBooksByCategory(String category) {
        List<Book> books = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return books;
        }
        String sql = "SELECT * FROM books WHERE category = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getString("availability_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error filtering books by category: " + e.getMessage());
        }
        return books;
    }

    /**
     * Update - Modify book information
     */
    public boolean updateBook(Book book) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, availability_status = ? WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setString(4, book.getAvailabilityStatus());
            pstmt.setInt(5, book.getBookId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete - Remove a book from the database
     */
    public boolean deleteBook(int bookId) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    // ============ MEMBER OPERATIONS ============

    /**
     * Create - Add a new member
     */
    public boolean addMember(Member member) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "INSERT INTO members (member_name, email, membership_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getMemberName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getMembershipType());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieve - Get all members
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return members;
        }
        String sql = "SELECT * FROM members";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("email"),
                        rs.getString("membership_type")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving members: " + e.getMessage());
        }
        return members;
    }

    /**
     * Retrieve - Get member by ID
     */
    public Member getMemberById(int memberId) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return null;
        }
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("email"),
                        rs.getString("membership_type")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving member: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieve - Search members by name
     */
    public List<Member> searchMembersByName(String name) {
        List<Member> members = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return members;
        }
        String sql = "SELECT * FROM members WHERE member_name LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(new Member(
                        rs.getInt("member_id"),
                        rs.getString("member_name"),
                        rs.getString("email"),
                        rs.getString("membership_type")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching members by name: " + e.getMessage());
        }
        return members;
    }

    /**
     * Update - Modify member information
     */
    public boolean updateMember(Member member) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "UPDATE members SET member_name = ?, email = ?, membership_type = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getMemberName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getMembershipType());
            pstmt.setInt(4, member.getMemberId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete - Remove a member from the database
     */
    public boolean deleteMember(int memberId) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    // ============ BORROW RECORD OPERATIONS ============

    /**
     * Create - Add a new borrow record
     */
    public boolean addBorrowRecord(BorrowRecord record) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "INSERT INTO borrow_records (book_id, member_id, borrow_date, due_date, return_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, record.getBookId());
            pstmt.setInt(2, record.getMemberId());
            pstmt.setString(3, record.getBorrowDate().toString());
            pstmt.setString(4, record.getDueDate().toString());
            pstmt.setString(5, record.getReturnStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding borrow record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieve - Get all borrow records
     */
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return records;
        }
        String sql = "SELECT * FROM borrow_records";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        LocalDate.parse(rs.getString("borrow_date")),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getString("return_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving borrow records: " + e.getMessage());
        }
        return records;
    }

    /**
     * Retrieve - Get borrow records by member
     */
    public List<BorrowRecord> getBorrowRecordsByMember(int memberId) {
        List<BorrowRecord> records = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return records;
        }
        String sql = "SELECT * FROM borrow_records WHERE member_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        LocalDate.parse(rs.getString("borrow_date")),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getString("return_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving borrow records by member: " + e.getMessage());
        }
        return records;
    }

    /**
     * Retrieve - Get borrow records by book
     */
    public List<BorrowRecord> getBorrowRecordsByBook(int bookId) {
        List<BorrowRecord> records = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return records;
        }
        String sql = "SELECT * FROM borrow_records WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        LocalDate.parse(rs.getString("borrow_date")),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getString("return_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving borrow records by book: " + e.getMessage());
        }
        return records;
    }

    /**
     * Retrieve - Get overdue books
     */
    public List<BorrowRecord> getOverdueRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return records;
        }
        String sql = "SELECT * FROM borrow_records WHERE due_date < date('now') AND return_status = 'Borrowed'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                records.add(new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        LocalDate.parse(rs.getString("borrow_date")),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getString("return_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving overdue records: " + e.getMessage());
        }
        return records;
    }

    /**
     * Update - Modify borrow record
     */
    public boolean updateBorrowRecord(BorrowRecord record) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "UPDATE borrow_records SET book_id = ?, member_id = ?, borrow_date = ?, due_date = ?, return_status = ? WHERE record_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, record.getBookId());
            pstmt.setInt(2, record.getMemberId());
            pstmt.setString(3, record.getBorrowDate().toString());
            pstmt.setString(4, record.getDueDate().toString());
            pstmt.setString(5, record.getReturnStatus());
            pstmt.setInt(6, record.getRecordId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating borrow record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete - Remove a borrow record
     */
    public boolean deleteBorrowRecord(int recordId) {
        if (connection == null) {
            System.err.println("Error: Database connection is not initialized");
            return false;
        }
        String sql = "DELETE FROM borrow_records WHERE record_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting borrow record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
