import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import models.Book;
import models.Member;
import models.BorrowRecord;
import utils.ValidationUtils;
import services.LibraryService;
import java.time.LocalDate;

/**
 * Unit tests for validation utilities and business logic.
 * Demonstrates test-driven development approach for critical functionality.
 */
public class LibrarySystemTests {

    @BeforeEach
    public void setUp() {
        // Test setup - initialize any required objects
    }

    // ============ ValidationUtils Tests ============

    @Test
    public void testEmailValidation_ValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("student@stmarys.ac.uk"));
        assertTrue(ValidationUtils.isValidEmail("john.doe@example.com"));
    }

    @Test
    public void testEmailValidation_InvalidEmail() {
        assertFalse(ValidationUtils.isValidEmail("invalid.email"));
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    public void testNumericValidation_ValidNumber() {
        assertTrue(ValidationUtils.isNumeric("123"));
        assertTrue(ValidationUtils.isNumeric("0"));
        assertTrue(ValidationUtils.isNumeric("999999"));
    }

    @Test
    public void testNumericValidation_InvalidNumber() {
        assertFalse(ValidationUtils.isNumeric("abc"));
        assertFalse(ValidationUtils.isNumeric("12.34"));
        assertFalse(ValidationUtils.isNumeric(""));
        assertFalse(ValidationUtils.isNumeric(null));
    }

    @Test
    public void testDateValidation_ValidDate() {
        assertTrue(ValidationUtils.isValidDate("2026-05-07"));
        assertTrue(ValidationUtils.isValidDate("2025-01-01"));
        assertTrue(ValidationUtils.isValidDate("2027-12-31"));
    }

    @Test
    public void testDateValidation_InvalidDate() {
        assertFalse(ValidationUtils.isValidDate("2026/05/07"));
        assertFalse(ValidationUtils.isValidDate("05-07-2026"));
        assertFalse(ValidationUtils.isValidDate("2026-13-01")); // Invalid month
        assertFalse(ValidationUtils.isValidDate(""));
        assertFalse(ValidationUtils.isValidDate(null));
    }

    @Test
    public void testDueDateValidation() {
        LocalDate borrowDate = LocalDate.of(2026, 5, 1);
        LocalDate validDueDate = LocalDate.of(2026, 5, 15);
        LocalDate invalidDueDate = LocalDate.of(2026, 4, 30);

        assertTrue(ValidationUtils.isDueDateValid(borrowDate, validDueDate));
        assertFalse(ValidationUtils.isDueDateValid(borrowDate, invalidDueDate));
    }

    @Test
    public void testBookTitleValidation() {
        assertTrue(ValidationUtils.isValidBookTitle("Introduction to Java"));
        assertTrue(ValidationUtils.isValidBookTitle("A"));
        assertFalse(ValidationUtils.isValidBookTitle(""));
        assertFalse(ValidationUtils.isValidBookTitle(null));
    }

    @Test
    public void testMembershipTypeValidation() {
        assertTrue(ValidationUtils.isValidMembershipType("Student"));
        assertTrue(ValidationUtils.isValidMembershipType("Staff"));
        assertFalse(ValidationUtils.isValidMembershipType("Alumni"));
        assertFalse(ValidationUtils.isValidMembershipType(""));
        assertFalse(ValidationUtils.isValidMembershipType(null));
    }

    @Test
    public void testAvailabilityStatusValidation() {
        assertTrue(ValidationUtils.isValidAvailabilityStatus("Available"));
        assertTrue(ValidationUtils.isValidAvailabilityStatus("Borrowed"));
        assertFalse(ValidationUtils.isValidAvailabilityStatus("Lost"));
        assertFalse(ValidationUtils.isValidAvailabilityStatus(""));
    }

    @Test
    public void testReturnStatusValidation() {
        assertTrue(ValidationUtils.isValidReturnStatus("Borrowed"));
        assertTrue(ValidationUtils.isValidReturnStatus("Returned"));
        assertTrue(ValidationUtils.isValidReturnStatus("Overdue"));
        assertFalse(ValidationUtils.isValidReturnStatus("Unknown"));
        assertFalse(ValidationUtils.isValidReturnStatus(""));
    }

    // ============ LibraryService Tests ============

    @Test
    public void testBookAvailabilityCheck() {
        assertTrue(LibraryService.isBookAvailable("Available"));
        assertFalse(LibraryService.isBookAvailable("Borrowed"));
    }

    @Test
    public void testOverdueCalculation() {
        LocalDate pastDate = LocalDate.now().minusDays(5);
        LocalDate futureDate = LocalDate.now().plusDays(5);

        assertTrue(LibraryService.isOverdue(pastDate));
        assertFalse(LibraryService.isOverdue(futureDate));
    }

    @Test
    public void testDaysOverdueCalculation() {
        LocalDate overdueDate = LocalDate.now().minusDays(10);
        long daysOverdue = LibraryService.getDaysOverdue(overdueDate);
        
        assertEquals(10, daysOverdue);
    }

    @Test
    public void testDaysOverdue_NotOverdue() {
        LocalDate futureDate = LocalDate.now().plusDays(10);
        long daysOverdue = LibraryService.getDaysOverdue(futureDate);
        
        assertEquals(0, daysOverdue);
    }

    @Test
    public void testBorrowingPeriodCalculation() {
        LocalDate borrowDate = LocalDate.of(2026, 5, 1);
        LocalDate dueDate = LocalDate.of(2026, 5, 15);
        
        long period = LibraryService.getBorrowingPeriod(borrowDate, dueDate);
        assertEquals(14, period);
    }

    // ============ Model Class Tests ============

    @Test
    public void testBookCreation() {
        Book book = new Book(1, "Java Programming", "John Smith", "Programming", "Available");
        
        assertEquals(1, book.getBookId());
        assertEquals("Java Programming", book.getTitle());
        assertEquals("John Smith", book.getAuthor());
        assertEquals("Programming", book.getCategory());
        assertEquals("Available", book.getAvailabilityStatus());
    }

    @Test
    public void testBookModification() {
        Book book = new Book(1, "Original Title", "Author", "Category", "Available");
        
        book.setTitle("Updated Title");
        book.setAvailabilityStatus("Borrowed");
        
        assertEquals("Updated Title", book.getTitle());
        assertEquals("Borrowed", book.getAvailabilityStatus());
    }

    @Test
    public void testMemberCreation() {
        Member member = new Member(1, "John Doe", "john@example.com", "Student");
        
        assertEquals(1, member.getMemberId());
        assertEquals("John Doe", member.getMemberName());
        assertEquals("john@example.com", member.getEmail());
        assertEquals("Student", member.getMembershipType());
    }

    @Test
    public void testBorrowRecordCreation() {
        LocalDate borrowDate = LocalDate.of(2026, 5, 1);
        LocalDate dueDate = LocalDate.of(2026, 5, 15);
        BorrowRecord record = new BorrowRecord(1, 1, 1, borrowDate, dueDate, "Borrowed");
        
        assertEquals(1, record.getRecordId());
        assertEquals(1, record.getBookId());
        assertEquals(1, record.getMemberId());
        assertEquals(borrowDate, record.getBorrowDate());
        assertEquals(dueDate, record.getDueDate());
        assertEquals("Borrowed", record.getReturnStatus());
    }

    // ============ Integration Tests ============

    @Test
    public void testCompleteBookingWorkflow() {
        // Test the complete workflow of borrowing a book
        Book book = new Book(1, "Test Book", "Author", "Category", "Available");
        Member member = new Member(1, "Student", "student@example.com", "Student");
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14);
        
        // Verify book is available
        assertTrue(LibraryService.isBookAvailable(book.getAvailabilityStatus()));
        
        // Create borrow record
        BorrowRecord record = new BorrowRecord(1, book.getBookId(), member.getMemberId(), borrowDate, dueDate, "Borrowed");
        
        // Mark book as borrowed
        book.setAvailabilityStatus("Borrowed");
        assertFalse(LibraryService.isBookAvailable(book.getAvailabilityStatus()));
        
        // Verify record validity
        assertTrue(ValidationUtils.isDueDateValid(record.getBorrowDate(), record.getDueDate()));
        assertEquals("Borrowed", record.getReturnStatus());
    }

    @Test
    public void testOverdueBookDetection() {
        // Test overdue book detection workflow
        LocalDate borrowDate = LocalDate.now().minusDays(30);
        LocalDate dueDate = LocalDate.now().minusDays(10);
        BorrowRecord record = new BorrowRecord(1, 1, 1, borrowDate, dueDate, "Borrowed");
        
        // Check if overdue
        assertTrue(LibraryService.isOverdue(record.getDueDate()));
        
        // Calculate days overdue
        long daysOverdue = LibraryService.getDaysOverdue(record.getDueDate());
        assertTrue(daysOverdue > 0);
    }
}
