import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import database.DatabaseManager;
import models.Book;
import models.Member;
import models.BorrowRecord;
import utils.ValidationUtils;
import services.LibraryService;

/**
 * LibraryConsoleApp provides a console-based interface for the Library Management System.
 * Implements all CRUD operations and provides a user-friendly menu-driven interface.
 */
public class LibraryConsoleApp {
    private DatabaseManager dbManager;
    private Scanner scanner;

    // Constructor
    public LibraryConsoleApp() {
        this.dbManager = new DatabaseManager();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Main menu loop
     */
    public void run() {
        System.out.println("\n======== WELCOME TO ST MARY'S DIGITAL LIBRARY SYSTEM ========\n");
        boolean running = true;

        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    manageBooks();
                    break;
                case "2":
                    manageMembers();
                    break;
                case "3":
                    manageBorrowingRecords();
                    break;
                case "4":
                    searchRecords();
                    break;
                case "5":
                    viewReports();
                    break;
                case "6":
                    running = false;
                    System.out.println("\n=== Thank you for using St Mary's Digital Library System ===\n");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        dbManager.closeConnection();
        scanner.close();
    }

    /**
     * Display main menu
     */
    private void displayMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Manage Books");
        System.out.println("2. Manage Members");
        System.out.println("3. Manage Borrowing Records");
        System.out.println("4. Search Records");
        System.out.println("5. View Reports");
        System.out.println("6. Exit System");
        System.out.print("\nSelect an option: ");
    }

    // ===================== BOOK MANAGEMENT =====================

    /**
     * Book management submenu
     */
    private void manageBooks() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n========== BOOK MANAGEMENT ==========");
            System.out.println("1. View All Books");
            System.out.println("2. Add New Book");
            System.out.println("3. Update Book");
            System.out.println("4. Delete Book");
            System.out.println("5. Search Books by Title");
            System.out.println("6. Search Books by Author");
            System.out.println("7. Filter Books by Category");
            System.out.println("8. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllBooks();
                    break;
                case "2":
                    addNewBook();
                    break;
                case "3":
                    updateBook();
                    break;
                case "4":
                    deleteBook();
                    break;
                case "5":
                    searchBooksByTitle();
                    break;
                case "6":
                    searchBooksByAuthor();
                    break;
                case "7":
                    filterBooksByCategory();
                    break;
                case "8":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * View all books
     */
    private void viewAllBooks() {
        List<Book> books = dbManager.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("\n--- No books found in the library ---");
        } else {
            System.out.println("\n========== ALL BOOKS IN LIBRARY ==========");
            for (Book book : books) {
                displayBook(book);
            }
            System.out.println("Total books: " + books.size());
        }
    }

    /**
     * Display book details
     */
    private void displayBook(Book book) {
        System.out.println("BOOK ID: " + book.getBookId());
        System.out.println("TITLE: " + book.getTitle());
        System.out.println("AUTHOR: " + book.getAuthor());
        System.out.println("CATEGORY: " + book.getCategory());
        System.out.println("STATUS: " + book.getAvailabilityStatus());
        System.out.println("---");
    }

    /**
     * Add new book
     */
    private void addNewBook() {
        System.out.println("\n========== ADD NEW BOOK ==========");

        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();
        if (!ValidationUtils.isValidBookTitle(title)) {
            System.out.println("Error: Invalid book title. Please enter a non-empty title.");
            return;
        }

        System.out.print("Enter author name: ");
        String author = scanner.nextLine().trim();
        if (!ValidationUtils.isValidAuthor(author)) {
            System.out.println("Error: Invalid author name. Please enter a non-empty author.");
            return;
        }

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();
        if (!ValidationUtils.isValidCategory(category)) {
            System.out.println("Error: Invalid category. Please enter a non-empty category.");
            return;
        }

        System.out.print("Enter availability status (Available/Borrowed): ");
        String status = scanner.nextLine().trim();
        if (!ValidationUtils.isValidAvailabilityStatus(status)) {
            System.out.println("Error: Invalid status. Please enter 'Available' or 'Borrowed'.");
            return;
        }

        Book book = new Book(0, title, author, category, status);
        if (dbManager.addBook(book)) {
            System.out.println("\n✓ Book added successfully!");
        } else {
            System.out.println("\n✗ Failed to add book. Please try again.");
        }
    }

    /**
     * Update book
     */
    private void updateBook() {
        System.out.println("\n========== UPDATE BOOK ==========");

        System.out.print("Enter Book ID to update: ");
        String idInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(idInput)) {
            System.out.println("Error: Book ID must be numeric.");
            return;
        }

        int bookId = Integer.parseInt(idInput);
        Book book = dbManager.getBookById(bookId);
        if (book == null) {
            System.out.println("Error: Book not found with ID: " + bookId);
            return;
        }

        System.out.println("Current details:");
        displayBook(book);

        System.out.print("Enter new title (press Enter to keep current): ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty() && !ValidationUtils.isValidBookTitle(title)) {
            System.out.println("Error: Invalid title.");
            return;
        }
        if (!title.isEmpty()) book.setTitle(title);

        System.out.print("Enter new author (press Enter to keep current): ");
        String author = scanner.nextLine().trim();
        if (!author.isEmpty() && !ValidationUtils.isValidAuthor(author)) {
            System.out.println("Error: Invalid author.");
            return;
        }
        if (!author.isEmpty()) book.setAuthor(author);

        System.out.print("Enter new category (press Enter to keep current): ");
        String category = scanner.nextLine().trim();
        if (!category.isEmpty() && !ValidationUtils.isValidCategory(category)) {
            System.out.println("Error: Invalid category.");
            return;
        }
        if (!category.isEmpty()) book.setCategory(category);

        System.out.print("Enter new status (Available/Borrowed, press Enter to keep current): ");
        String status = scanner.nextLine().trim();
        if (!status.isEmpty() && !ValidationUtils.isValidAvailabilityStatus(status)) {
            System.out.println("Error: Invalid status.");
            return;
        }
        if (!status.isEmpty()) book.setAvailabilityStatus(status);

        if (dbManager.updateBook(book)) {
            System.out.println("\n✓ Book updated successfully!");
        } else {
            System.out.println("\n✗ Failed to update book.");
        }
    }

    /**
     * Delete book
     */
    private void deleteBook() {
        System.out.println("\n========== DELETE BOOK ==========");

        System.out.print("Enter Book ID to delete: ");
        String idInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(idInput)) {
            System.out.println("Error: Book ID must be numeric.");
            return;
        }

        int bookId = Integer.parseInt(idInput);
        Book book = dbManager.getBookById(bookId);
        if (book == null) {
            System.out.println("Error: Book not found with ID: " + bookId);
            return;
        }

        System.out.println("\nBook to delete:");
        displayBook(book);

        System.out.print("Are you sure you want to delete this book? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if ("yes".equals(confirmation)) {
            if (dbManager.deleteBook(bookId)) {
                System.out.println("\n✓ Book deleted successfully!");
            } else {
                System.out.println("\n✗ Failed to delete book.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * Search books by title
     */
    private void searchBooksByTitle() {
        System.out.println("\n========== SEARCH BOOKS BY TITLE ==========");
        System.out.print("Enter title (or part of title): ");
        String title = scanner.nextLine().trim();

        List<Book> books = dbManager.searchBooksByTitle(title);
        if (books.isEmpty()) {
            System.out.println("\n--- No books found matching \"" + title + "\" ---");
        } else {
            System.out.println("\n========== SEARCH RESULTS ==========");
            for (Book book : books) {
                displayBook(book);
            }
            System.out.println("Total results: " + books.size());
        }
    }

    /**
     * Search books by author
     */
    private void searchBooksByAuthor() {
        System.out.println("\n========== SEARCH BOOKS BY AUTHOR ==========");
        System.out.print("Enter author name (or part of it): ");
        String author = scanner.nextLine().trim();

        List<Book> books = dbManager.searchBooksByAuthor(author);
        if (books.isEmpty()) {
            System.out.println("\n--- No books found by \"" + author + "\" ---");
        } else {
            System.out.println("\n========== SEARCH RESULTS ==========");
            for (Book book : books) {
                displayBook(book);
            }
            System.out.println("Total results: " + books.size());
        }
    }

    /**
     * Filter books by category
     */
    private void filterBooksByCategory() {
        System.out.println("\n========== FILTER BOOKS BY CATEGORY ==========");
        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        List<Book> books = dbManager.filterBooksByCategory(category);
        if (books.isEmpty()) {
            System.out.println("\n--- No books found in \"" + category + "\" category ---");
        } else {
            System.out.println("\n========== FILTERED RESULTS ==========");
            for (Book book : books) {
                displayBook(book);
            }
            System.out.println("Total results: " + books.size());
        }
    }

    // ===================== MEMBER MANAGEMENT =====================

    /**
     * Member management submenu
     */
    private void manageMembers() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n========== MEMBER MANAGEMENT ==========");
            System.out.println("1. View All Members");
            System.out.println("2. Add New Member");
            System.out.println("3. Update Member");
            System.out.println("4. Delete Member");
            System.out.println("5. Search Members by Name");
            System.out.println("6. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllMembers();
                    break;
                case "2":
                    addNewMember();
                    break;
                case "3":
                    updateMember();
                    break;
                case "4":
                    deleteMember();
                    break;
                case "5":
                    searchMembersByName();
                    break;
                case "6":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * View all members
     */
    private void viewAllMembers() {
        List<Member> members = dbManager.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("\n--- No members found ---");
        } else {
            System.out.println("\n========== ALL LIBRARY MEMBERS ==========");
            for (Member member : members) {
                displayMember(member);
            }
            System.out.println("Total members: " + members.size());
        }
    }

    /**
     * Display member details
     */
    private void displayMember(Member member) {
        System.out.println("MEMBER ID: " + member.getMemberId());
        System.out.println("MEMBER NAME: " + member.getMemberName());
        System.out.println("EMAIL: " + member.getEmail());
        System.out.println("MEMBERSHIP TYPE: " + member.getMembershipType());
        System.out.println("---");
    }

    /**
     * Add new member
     */
    private void addNewMember() {
        System.out.println("\n========== ADD NEW MEMBER ==========");

        System.out.print("Enter member name: ");
        String name = scanner.nextLine().trim();
        if (!ValidationUtils.isValidMemberName(name)) {
            System.out.println("Error: Invalid member name.");
            return;
        }

        System.out.print("Enter email address: ");
        String email = scanner.nextLine().trim();
        if (!ValidationUtils.isValidEmail(email)) {
            System.out.println("Error: Invalid email address format.");
            return;
        }

        System.out.print("Enter membership type (Student/Staff): ");
        String type = scanner.nextLine().trim();
        if (!ValidationUtils.isValidMembershipType(type)) {
            System.out.println("Error: Invalid membership type. Please enter 'Student' or 'Staff'.");
            return;
        }

        Member member = new Member(0, name, email, type);
        if (dbManager.addMember(member)) {
            System.out.println("\n✓ Member added successfully!");
        } else {
            System.out.println("\n✗ Failed to add member.");
        }
    }

    /**
     * Update member
     */
    private void updateMember() {
        System.out.println("\n========== UPDATE MEMBER ==========");

        System.out.print("Enter Member ID to update: ");
        String idInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(idInput)) {
            System.out.println("Error: Member ID must be numeric.");
            return;
        }

        int memberId = Integer.parseInt(idInput);
        Member member = dbManager.getMemberById(memberId);
        if (member == null) {
            System.out.println("Error: Member not found with ID: " + memberId);
            return;
        }

        System.out.println("Current details:");
        displayMember(member);

        System.out.print("Enter new name (press Enter to keep current): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty() && !ValidationUtils.isValidMemberName(name)) {
            System.out.println("Error: Invalid name.");
            return;
        }
        if (!name.isEmpty()) member.setMemberName(name);

        System.out.print("Enter new email (press Enter to keep current): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            System.out.println("Error: Invalid email.");
            return;
        }
        if (!email.isEmpty()) member.setEmail(email);

        System.out.print("Enter new membership type (Student/Staff, press Enter to keep current): ");
        String type = scanner.nextLine().trim();
        if (!type.isEmpty() && !ValidationUtils.isValidMembershipType(type)) {
            System.out.println("Error: Invalid membership type.");
            return;
        }
        if (!type.isEmpty()) member.setMembershipType(type);

        if (dbManager.updateMember(member)) {
            System.out.println("\n✓ Member updated successfully!");
        } else {
            System.out.println("\n✗ Failed to update member.");
        }
    }

    /**
     * Delete member
     */
    private void deleteMember() {
        System.out.println("\n========== DELETE MEMBER ==========");

        System.out.print("Enter Member ID to delete: ");
        String idInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(idInput)) {
            System.out.println("Error: Member ID must be numeric.");
            return;
        }

        int memberId = Integer.parseInt(idInput);
        Member member = dbManager.getMemberById(memberId);
        if (member == null) {
            System.out.println("Error: Member not found with ID: " + memberId);
            return;
        }

        System.out.println("\nMember to delete:");
        displayMember(member);

        System.out.print("Are you sure you want to delete this member? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if ("yes".equals(confirmation)) {
            if (dbManager.deleteMember(memberId)) {
                System.out.println("\n✓ Member deleted successfully!");
            } else {
                System.out.println("\n✗ Failed to delete member.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * Search members by name
     */
    private void searchMembersByName() {
        System.out.println("\n========== SEARCH MEMBERS BY NAME ==========");
        System.out.print("Enter member name (or part of it): ");
        String name = scanner.nextLine().trim();

        List<Member> members = dbManager.searchMembersByName(name);
        if (members.isEmpty()) {
            System.out.println("\n--- No members found matching \"" + name + "\" ---");
        } else {
            System.out.println("\n========== SEARCH RESULTS ==========");
            for (Member member : members) {
                displayMember(member);
            }
            System.out.println("Total results: " + members.size());
        }
    }

    // ===================== BORROWING RECORDS =====================

    /**
     * Borrowing records management submenu
     */
    private void manageBorrowingRecords() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n========== BORROWING RECORDS MANAGEMENT ==========");
            System.out.println("1. View All Borrowing Records");
            System.out.println("2. Add New Borrowing Record");
            System.out.println("3. Update Borrowing Record");
            System.out.println("4. Delete Borrowing Record");
            System.out.println("5. View Member's Borrowing History");
            System.out.println("6. View Book's Borrowing History");
            System.out.println("7. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllBorrowRecords();
                    break;
                case "2":
                    addNewBorrowRecord();
                    break;
                case "3":
                    updateBorrowRecord();
                    break;
                case "4":
                    deleteBorrowRecord();
                    break;
                case "5":
                    viewMemberBorrowingHistory();
                    break;
                case "6":
                    viewBookBorrowingHistory();
                    break;
                case "7":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * View all borrow records
     */
    private void viewAllBorrowRecords() {
        List<BorrowRecord> records = dbManager.getAllBorrowRecords();
        if (records.isEmpty()) {
            System.out.println("\n--- No borrowing records found ---");
        } else {
            System.out.println("\n========== ALL BORROWING RECORDS ==========");
            for (BorrowRecord record : records) {
                displayBorrowRecord(record);
            }
            System.out.println("Total records: " + records.size());
        }
    }

    /**
     * Display borrow record
     */
    private void displayBorrowRecord(BorrowRecord record) {
        System.out.println("RECORD ID: " + record.getRecordId());
        System.out.println("BOOK ID: " + record.getBookId());
        System.out.println("MEMBER ID: " + record.getMemberId());
        System.out.println("BORROW DATE: " + record.getBorrowDate());
        System.out.println("DUE DATE: " + record.getDueDate());
        System.out.println("STATUS: " + record.getReturnStatus());

        if (LibraryService.isOverdue(record.getDueDate()) && "Borrowed".equals(record.getReturnStatus())) {
            long daysOverdue = LibraryService.getDaysOverdue(record.getDueDate());
            System.out.println("⚠ OVERDUE BY " + daysOverdue + " DAYS!");
        }
        System.out.println("---");
    }

    /**
     * Add new borrow record
     */
    private void addNewBorrowRecord() {
        System.out.println("\n========== ADD NEW BORROWING RECORD ==========");

        System.out.print("Enter Book ID: ");
        String bookIdInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(bookIdInput)) {
            System.out.println("Error: Book ID must be numeric.");
            return;
        }
        int bookId = Integer.parseInt(bookIdInput);

        System.out.print("Enter Member ID: ");
        String memberIdInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(memberIdInput)) {
            System.out.println("Error: Member ID must be numeric.");
            return;
        }
        int memberId = Integer.parseInt(memberIdInput);

        System.out.print("Enter borrow date (YYYY-MM-DD): ");
        String borrowDateStr = scanner.nextLine().trim();
        if (!ValidationUtils.isValidDate(borrowDateStr)) {
            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
            return;
        }
        LocalDate borrowDate = LocalDate.parse(borrowDateStr);

        System.out.print("Enter due date (YYYY-MM-DD): ");
        String dueDateStr = scanner.nextLine().trim();
        if (!ValidationUtils.isValidDate(dueDateStr)) {
            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
            return;
        }
        LocalDate dueDate = LocalDate.parse(dueDateStr);

        if (!ValidationUtils.isDueDateValid(borrowDate, dueDate)) {
            System.out.println("Error: Due date must be after borrow date.");
            return;
        }

        System.out.print("Enter status (Borrowed/Returned/Overdue): ");
        String status = scanner.nextLine().trim();
        if (!ValidationUtils.isValidReturnStatus(status)) {
            System.out.println("Error: Invalid status.");
            return;
        }

        BorrowRecord record = new BorrowRecord(0, bookId, memberId, borrowDate, dueDate, status);
        if (dbManager.addBorrowRecord(record)) {
            System.out.println("\n✓ Borrowing record added successfully!");
        } else {
            System.out.println("\n✗ Failed to add borrowing record.");
        }
    }

    /**
     * Update borrow record
     */
    private void updateBorrowRecord() {
        System.out.println("\n========== UPDATE BORROWING RECORD ==========");

        System.out.print("Enter Record ID to update: ");
        String recordIdInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(recordIdInput)) {
            System.out.println("Error: Record ID must be numeric.");
            return;
        }

        int recordId = Integer.parseInt(recordIdInput);
        List<BorrowRecord> allRecords = dbManager.getAllBorrowRecords();
        BorrowRecord record = allRecords.stream().filter(r -> r.getRecordId() == recordId).findFirst().orElse(null);

        if (record == null) {
            System.out.println("Error: Record not found with ID: " + recordId);
            return;
        }

        System.out.println("Current details:");
        displayBorrowRecord(record);

        System.out.print("Enter new status (Borrowed/Returned/Overdue, press Enter to keep current): ");
        String status = scanner.nextLine().trim();
        if (!status.isEmpty() && !ValidationUtils.isValidReturnStatus(status)) {
            System.out.println("Error: Invalid status.");
            return;
        }
        if (!status.isEmpty()) record.setReturnStatus(status);

        if (dbManager.updateBorrowRecord(record)) {
            System.out.println("\n✓ Borrowing record updated successfully!");
        } else {
            System.out.println("\n✗ Failed to update borrowing record.");
        }
    }

    /**
     * Delete borrow record
     */
    private void deleteBorrowRecord() {
        System.out.println("\n========== DELETE BORROWING RECORD ==========");

        System.out.print("Enter Record ID to delete: ");
        String recordIdInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(recordIdInput)) {
            System.out.println("Error: Record ID must be numeric.");
            return;
        }

        int recordId = Integer.parseInt(recordIdInput);
        List<BorrowRecord> allRecords = dbManager.getAllBorrowRecords();
        BorrowRecord record = allRecords.stream().filter(r -> r.getRecordId() == recordId).findFirst().orElse(null);

        if (record == null) {
            System.out.println("Error: Record not found with ID: " + recordId);
            return;
        }

        System.out.println("\nRecord to delete:");
        displayBorrowRecord(record);

        System.out.print("Are you sure you want to delete this record? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if ("yes".equals(confirmation)) {
            if (dbManager.deleteBorrowRecord(recordId)) {
                System.out.println("\n✓ Record deleted successfully!");
            } else {
                System.out.println("\n✗ Failed to delete record.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * View member's borrowing history
     */
    private void viewMemberBorrowingHistory() {
        System.out.println("\n========== MEMBER BORROWING HISTORY ==========");

        System.out.print("Enter Member ID: ");
        String memberIdInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(memberIdInput)) {
            System.out.println("Error: Member ID must be numeric.");
            return;
        }

        int memberId = Integer.parseInt(memberIdInput);
        Member member = dbManager.getMemberById(memberId);
        if (member == null) {
            System.out.println("Error: Member not found with ID: " + memberId);
            return;
        }

        System.out.println("Member: " + member.getMemberName());
        List<BorrowRecord> records = dbManager.getBorrowRecordsByMember(memberId);

        if (records.isEmpty()) {
            System.out.println("--- No borrowing records for this member ---");
        } else {
            for (BorrowRecord record : records) {
                Book book = dbManager.getBookById(record.getBookId());
                System.out.println("\nBook: " + (book != null ? book.getTitle() : "Unknown"));
                displayBorrowRecord(record);
            }
        }
    }

    /**
     * View book's borrowing history
     */
    private void viewBookBorrowingHistory() {
        System.out.println("\n========== BOOK BORROWING HISTORY ==========");

        System.out.print("Enter Book ID: ");
        String bookIdInput = scanner.nextLine().trim();
        if (!ValidationUtils.isNumeric(bookIdInput)) {
            System.out.println("Error: Book ID must be numeric.");
            return;
        }

        int bookId = Integer.parseInt(bookIdInput);
        Book book = dbManager.getBookById(bookId);
        if (book == null) {
            System.out.println("Error: Book not found with ID: " + bookId);
            return;
        }

        System.out.println("Book: " + book.getTitle());
        List<BorrowRecord> records = dbManager.getBorrowRecordsByBook(bookId);

        if (records.isEmpty()) {
            System.out.println("--- No borrowing history for this book ---");
        } else {
            for (BorrowRecord record : records) {
                Member member = dbManager.getMemberById(record.getMemberId());
                System.out.println("\nMember: " + (member != null ? member.getMemberName() : "Unknown"));
                displayBorrowRecord(record);
            }
        }
    }

    // ===================== SEARCH & REPORTS =====================

    /**
     * Search records submenu
     */
    private void searchRecords() {
        System.out.println("\n========== SEARCH RECORDS ==========");
        System.out.println("1. Search Books");
        System.out.println("2. Search Members");
        System.out.println("3. Back to Main Menu");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                searchBooks();
                break;
            case "2":
                searchMembers();
                break;
            case "3":
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Search books options
     */
    private void searchBooks() {
        System.out.println("\n========== SEARCH BOOKS ==========");
        System.out.println("1. By Title");
        System.out.println("2. By Author");
        System.out.println("3. By Category");
        System.out.print("Select search type: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                searchBooksByTitle();
                break;
            case "2":
                searchBooksByAuthor();
                break;
            case "3":
                filterBooksByCategory();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    /**
     * Search members options
     */
    private void searchMembers() {
        System.out.println("\n========== SEARCH MEMBERS ==========");
        System.out.print("Enter member name to search: ");
        String name = scanner.nextLine().trim();

        List<Member> members = dbManager.searchMembersByName(name);
        if (members.isEmpty()) {
            System.out.println("\n--- No members found ---");
        } else {
            System.out.println("\n========== SEARCH RESULTS ==========");
            for (Member member : members) {
                displayMember(member);
            }
        }
    }

    /**
     * View reports submenu
     */
    private void viewReports() {
        boolean viewing = true;
        while (viewing) {
            System.out.println("\n========== LIBRARY REPORTS ==========");
            System.out.println("1. Full System Report");
            System.out.println("2. Overdue Books Report");
            System.out.println("3. Book Availability Report");
            System.out.println("4. Member Statistics");
            System.out.println("5. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    fullSystemReport();
                    break;
                case "2":
                    overdueBooksReport();
                    break;
                case "3":
                    bookAvailabilityReport();
                    break;
                case "4":
                    memberStatistics();
                    break;
                case "5":
                    viewing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Full system report
     */
    private void fullSystemReport() {
        System.out.println("\n========== FULL SYSTEM REPORT ==========");

        List<Book> books = dbManager.getAllBooks();
        List<Member> members = dbManager.getAllMembers();
        List<BorrowRecord> records = dbManager.getAllBorrowRecords();

        System.out.println("\n--- LIBRARY STATISTICS ---");
        System.out.println("Total Books: " + books.size());
        long available = books.stream().filter(b -> "Available".equals(b.getAvailabilityStatus())).count();
        System.out.println("Available Books: " + available);
        System.out.println("Borrowed Books: " + (books.size() - available));

        System.out.println("\nTotal Members: " + members.size());
        long students = members.stream().filter(m -> "Student".equals(m.getMembershipType())).count();
        System.out.println("Student Members: " + students);
        System.out.println("Staff Members: " + (members.size() - students));

        System.out.println("\nTotal Borrowing Records: " + records.size());

        List<BorrowRecord> overdue = dbManager.getOverdueRecords();
        System.out.println("Overdue Records: " + overdue.size());
    }

    /**
     * Overdue books report
     */
    private void overdueBooksReport() {
        System.out.println("\n========== OVERDUE BOOKS REPORT ==========");

        List<BorrowRecord> overdueRecords = dbManager.getOverdueRecords();
        if (overdueRecords.isEmpty()) {
            System.out.println("--- No overdue books in the system ---");
        } else {
            for (BorrowRecord record : overdueRecords) {
                Member member = dbManager.getMemberById(record.getMemberId());
                Book book = dbManager.getBookById(record.getBookId());
                long daysOverdue = LibraryService.getDaysOverdue(record.getDueDate());

                System.out.println("\nBook: " + (book != null ? book.getTitle() : "Unknown (ID: " + record.getBookId() + ")"));
                System.out.println("Member: " + (member != null ? member.getMemberName() : "Unknown (ID: " + record.getMemberId() + ")"));
                System.out.println("Due Date: " + record.getDueDate());
                System.out.println("Days Overdue: " + daysOverdue);
            }
        }
    }

    /**
     * Book availability report
     */
    private void bookAvailabilityReport() {
        System.out.println("\n========== BOOK AVAILABILITY REPORT ==========");

        List<Book> books = dbManager.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("--- No books in the library ---");
        } else {
            int availableCount = 0;
            int borrowedCount = 0;

            System.out.println("\n--- AVAILABLE BOOKS ---");
            for (Book book : books) {
                if ("Available".equals(book.getAvailabilityStatus())) {
                    System.out.println("• " + book.getTitle() + " by " + book.getAuthor());
                    availableCount++;
                }
            }
            System.out.println("Total Available: " + availableCount);

            System.out.println("\n--- BORROWED BOOKS ---");
            for (Book book : books) {
                if ("Borrowed".equals(book.getAvailabilityStatus())) {
                    System.out.println("• " + book.getTitle() + " by " + book.getAuthor());
                    borrowedCount++;
                }
            }
            System.out.println("Total Borrowed: " + borrowedCount);
        }
    }

    /**
     * Member statistics
     */
    private void memberStatistics() {
        System.out.println("\n========== MEMBER STATISTICS ==========");

        List<Member> members = dbManager.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("--- No members in the system ---");
        } else {
            System.out.println("\n--- TOTAL MEMBERS: " + members.size() + " ---");

            System.out.println("\nSTUDENTS:");
            int studentCount = 0;
            for (Member member : members) {
                if ("Student".equals(member.getMembershipType())) {
                    System.out.println("• " + member.getMemberName() + " (" + member.getEmail() + ")");
                    studentCount++;
                }
            }
            System.out.println("Total Students: " + studentCount);

            System.out.println("\nSTAFF:");
            int staffCount = 0;
            for (Member member : members) {
                if ("Staff".equals(member.getMembershipType())) {
                    System.out.println("• " + member.getMemberName() + " (" + member.getEmail() + ")");
                    staffCount++;
                }
            }
            System.out.println("Total Staff: " + staffCount);
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        LibraryConsoleApp app = new LibraryConsoleApp();
        app.run();
    }
}
