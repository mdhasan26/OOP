# St Mary's Digital Library Management System

## Overview

A comprehensive Java-based library management system for St Mary's University Library that helps manage books, members, and borrowing activities. The system includes both a console-based interface for basic operations and a JavaFX GUI for enhanced user experience.

## Features

### Basic Features (Implemented)

- **Book Management**: Add, view, update, delete, and search books
- **Member Management**: Manage library members (students and staff)
- **Borrowing Management**: Track borrowing transactions and manage returns
- **Console Interface**: User-friendly menu-driven console application
- **SQLite Database**: Persistent data storage with SQLite

### Enhanced Features (Implemented)

- **JavaFX GUI**: Professional graphical interface with tabs
- **Advanced Search**: Search books by title, author, or category
- **Data Validation**: Comprehensive input validation for data integrity
- **Error Handling**: Graceful error management for database and user input errors
- **Reports**: View system statistics, overdue books, and availability reports
- **Member History**: Track borrowing history for members and books

### Advanced Features (Implemented)

- **Overdue Detection**: Automatic identification of overdue books
- **Book Availability Tracking**: Real-time availability status updates
- **Data Filtering**: Filter and sort records by various attributes
- **Business Logic Services**: Encapsulated library service calculations

## Project Structure

```
StMarysLibrarySystem/
├── src/
│   ├── LibraryConsoleApp.java      # Main console application
│   ├── database/
│   │   └── DatabaseManager.java    # Database operations
│   ├── models/
```
│   │   ├── Book.java              # Book entity
│   │   ├── Member.java            # Member entity
│   │   └── BorrowRecord.java       # Borrow transaction entity
│   ├── services/
│   │   └── LibraryService.java    # Business logic
│   ├── ui/
│   │   └── LibraryManagementGUI.java # JavaFX GUI application
│   └── utils/
│       └── ValidationUtils.java   # Input validation utilities
└── README.md                        # This file
```

## Technical Stack

- **Language**: Java (JDK 22+)
- **Database**: SQLite 3 with SQLite JDBC driver
- **GUI Framework**: JavaFX 24.0.1
- **Testing Framework**: JUnit 5 (Jupiter)
- **Version Control**: Git

## Initial Setup

Before running the application, you need to download the required dependencies (JavaFX and JUnit):

### Quick Setup (Automatic)

**Windows:**

```bash
setup-dependencies.bat
```

**Linux/Mac:**

```bash
chmod +x setup-dependencies.sh
./setup-dependencies.sh
```

### Build the Project

**Windows:**

```bash
build.bat
```

**Linux/Mac:**

```bash
chmod +x build.sh
./build.sh
```

For detailed dependency information and troubleshooting, see [DEPENDENCIES.md](DEPENDENCIES.md).

## Running the Application

### Console Application

**Windows:**

```bash
java -cp bin;lib/* LibraryConsoleApp
```

**Linux/Mac:**

```bash
java -cp bin:lib/* LibraryConsoleApp
```

### JavaFX GUI Application

**Windows:**

```bash
java --module-path lib --add-modules javafx.controls,javafx.fxml -cp bin;lib/* ui.LibraryManagementGUI

If the JavaFX jars are not present directly under `lib/`, an SDK is included at `lib/openjfx-24/javafx-sdk-24/lib` and you can run the GUI with:

```bash
java --module-path lib\openjfx-24\javafx-sdk-24\lib --add-modules javafx.controls,javafx.fxml -cp "bin;lib/*" ui.LibraryManagementGUI
```

Notes:
- The project now performs database initialization on a background thread in the GUI so the UI is not blocked during startup.
- The `slf4j` jars were removed from `lib/` because they are not used by the code and are not permitted by the assignment packaging rules.
```

**Linux/Mac:**

```bash
java --module-path lib --add-modules javafx.controls,javafx.fxml -cp bin:lib/* ui.LibraryManagementGUI
```

### Unit Tests

**Windows:**

```bash
java -cp bin;lib/* org.junit.platform.console.ConsoleLauncher --scan-classpath
```

**Linux/Mac:**

```bash
java -cp bin:lib/* org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## Database Schema

### Books Table

```sql
CREATE TABLE books (
    book_id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    category TEXT NOT NULL,
    availability_status TEXT NOT NULL
);
```

### Members Table

```sql
CREATE TABLE members (
    member_id INTEGER PRIMARY KEY,
    member_name TEXT NOT NULL,
    email TEXT NOT NULL,
    membership_type TEXT NOT NULL
);
```

### Borrow Records Table

```sql
CREATE TABLE borrow_records (
    record_id INTEGER PRIMARY KEY,
    book_id INTEGER NOT NULL,
    member_id INTEGER NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_status TEXT NOT NULL
);
```

## Sample Data

The application initializes with sample data:

- 3 books (Introduction to Java, Database Systems, Software Engineering Principles)
- 3 members (Alice Johnson, Michael Lee, Sara Ahmed)
- 3 borrowing records demonstrating various states (Borrowed, Returned, Borrowed)

## Key Classes

### DatabaseManager

Handles all database operations including:

- Connection management
- CRUD operations for books, members, and borrowing records
- Advanced search and filtering
- Overdue record identification

### LibraryService

Provides business logic for:

- Book availability checking
- Overdue calculation
- Borrowing period calculation
- Status updates

### ValidationUtils

Ensures data integrity with validation for:

- Email format
- Numeric input
- Date format and validity
- Membership types
- Return statuses

## Usage Examples

### Console Application

1. Launch the application
2. Select "Manage Books", "Manage Members", or "Manage Borrowing Records"
3. Choose CRUD operations or search functions
4. Follow the on-screen prompts

### GUI Application

1. Launch the JavaFX application
2. Use tabs to navigate between different modules
3. Fill in forms to add/update records
4. View system reports and statistics

## Error Handling

The system handles various error scenarios:

- Database connection failures
- Invalid user input
- Constraint violations
- Missing records

All errors are reported with meaningful messages to the user.

## Object-Oriented Principles Applied

1. **Encapsulation**: Classes encapsulate data and behavior (e.g., Book, Member, BorrowRecord)
2. **Abstraction**: DatabaseManager abstracts database operations
3. **Single Responsibility**: Each class has a focused responsibility
4. **DRY Principle**: Validation and business logic in separate utility classes

## Future Enhancements

- Multi-threading for concurrent database operations
- User authentication and authorization
- Email notifications for overdue books
- Advanced reporting with charts and graphs
- Book recommendations based on borrowing history
- Mobile application interface

## Author

Developed for CPS4005 - Object-Oriented Programming Assessment

## License

Proprietary - St Mary's University
