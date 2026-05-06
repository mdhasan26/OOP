package utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * ValidationUtils provides validation methods for user input.
 * Ensures data integrity before storing information in the database.
 */
public class ValidationUtils {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    /**
     * Validate if string is empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }

    /**
     * Validate numeric input
     */
    public static boolean isNumeric(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate date format (yyyy-MM-dd)
     */
    public static boolean isValidDate(String dateString) {
        if (!isNotEmpty(dateString)) {
            return false;
        }
        try {
            LocalDate.parse(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validate if due date is after borrow date
     */
    public static boolean isDueDateValid(LocalDate borrowDate, LocalDate dueDate) {
        return dueDate.isAfter(borrowDate);
    }

    /**
     * Validate book title (not empty, reasonable length)
     */
    public static boolean isValidBookTitle(String title) {
        return isNotEmpty(title) && title.length() <= 255;
    }

    /**
     * Validate author name
     */
    public static boolean isValidAuthor(String author) {
        return isNotEmpty(author) && author.length() <= 255;
    }

    /**
     * Validate category
     */
    public static boolean isValidCategory(String category) {
        return isNotEmpty(category) && category.length() <= 100;
    }

    /**
     * Validate availability status
     */
    public static boolean isValidAvailabilityStatus(String status) {
        return status != null && (status.equals("Available") || status.equals("Borrowed"));
    }

    /**
     * Validate member name
     */
    public static boolean isValidMemberName(String name) {
        return isNotEmpty(name) && name.length() <= 255;
    }

    /**
     * Validate membership type
     */
    public static boolean isValidMembershipType(String type) {
        return type != null && (type.equals("Student") || type.equals("Staff"));
    }

    /**
     * Validate return status
     */
    public static boolean isValidReturnStatus(String status) {
        return status != null && (status.equals("Borrowed") || status.equals("Returned") || status.equals("Overdue"));
    }
}
