package services;

import java.time.LocalDate;

/**
 * LibraryService provides business logic layer operations.
 * Encapsulates library operations and rules.
 */
public class LibraryService {

    /**
     * Check if a book is available
     */
    public static boolean isBookAvailable(String status) {
        return "Available".equals(status);
    }

    /**
     * Mark book as borrowed
     */
    public static String markAsBooked() {
        return "Borrowed";
    }

    /**
     * Mark book as available (when returned)
     */
    public static String markAsAvailable() {
        return "Available";
    }

    /**
     * Calculate if a borrow record is overdue
     */
    public static boolean isOverdue(LocalDate dueDate) {
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Get days overdue
     */
    public static long getDaysOverdue(LocalDate dueDate) {
        if (!isOverdue(dueDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Get borrowing period in days
     */
    public static long getBorrowingPeriod(LocalDate borrowDate, LocalDate dueDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(borrowDate, dueDate);
    }
}
