package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import database.DatabaseManager;
import models.Book;
import models.Member;
import models.BorrowRecord;
import utils.ValidationUtils;
import services.LibraryService;
import javafx.application.Platform;
import java.util.function.Consumer;

import java.time.LocalDate;
import java.util.List;

/**
 * Main GUI application for St Mary's Digital Library System.
 * Provides a tabbed interface for managing books, members, and borrowing records.
 */
public class LibraryManagementGUI extends Application {
    private DatabaseManager dbManager;
    private TabPane tabPane;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        VBox top = createTopBar();
        root.setTop(top);

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Initially disable tabs until DB is ready and show a loading tab
        tabPane.setDisable(true);
        Tab loading = new Tab("Loading...", new VBox(new Label("Loading database, please wait...")));
        loading.setClosable(false);
        tabPane.getTabs().add(loading);

        root.setCenter(tabPane);

        // Load database in background to avoid blocking the UI thread
        DatabaseManager.connectAsync(new Consumer<DatabaseManager>() {
            @Override
            public void accept(DatabaseManager mgr) {
                Platform.runLater(() -> {
                    dbManager = mgr;
                    initializeSampleData();
                    tabPane.getTabs().clear();
                    tabPane.getTabs().addAll(
                            createBooksTab(),
                            createMembersTab(),
                            createBorrowRecordsTab(),
                            createReportsTab()
                    );
                    tabPane.setDisable(false);
                });
            }
        });

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("St Mary's Digital Library Management System");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> dbManager.closeConnection());
        primaryStage.show();
    }

    /**
     * Create top bar with title
     */
    private VBox createTopBar() {
        VBox topBar = new VBox();
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");
        Label title = new Label("St Mary's Digital Library Management System");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
        topBar.getChildren().add(title);
        return topBar;
    }

    /**
     * Create Books Management Tab
     */
    private Tab createBooksTab() {
        Tab tab = new Tab("Manage Books", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Search and Filter Section
        HBox searchBox = createBookSearchBox();
        content.getChildren().add(new Separator());
        content.getChildren().add(searchBox);

        // Books Table
        TableView<Book> booksTable = new TableView<>();
        setupBooksTable(booksTable);
        refreshBooksTable(booksTable);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));

        Button addBtn = new Button("Add Book");
        addBtn.setPrefWidth(100);
        addBtn.setOnAction(e -> showAddBookDialog());

        Button editBtn = new Button("Update Book");
        editBtn.setPrefWidth(100);
        editBtn.setOnAction(e -> showUpdateBookDialog(booksTable.getSelectionModel().getSelectedItem()));

        Button deleteBtn = new Button("Delete Book");
        deleteBtn.setPrefWidth(100);
        deleteBtn.setOnAction(e -> deleteBook(booksTable.getSelectionModel().getSelectedItem()));

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefWidth(100);
        refreshBtn.setOnAction(e -> refreshBooksTable(booksTable));

        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        content.getChildren().addAll(new Label("Books in Library:"), booksTable, buttonBox);

        ((VBox) tab.getContent()).getChildren().add(content);
        return tab;
    }

    /**
     * Create search box for books
     */
    private HBox createBookSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));

        Label searchLabel = new Label("Search/Filter:");
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("By Title", "By Author", "By Category");
        searchType.setValue("By Title");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term...");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> {
            // Search implementation would go here
            showInfo("Search Feature", "Search functionality implemented in console version");
        });

        searchBox.getChildren().addAll(searchLabel, searchType, searchField, searchBtn);
        return searchBox;
    }

    /**
     * Setup books table columns
     */
    private void setupBooksTable(TableView<Book> table) {
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getBookId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        titleCol.setPrefWidth(250);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAuthor()));
        authorCol.setPrefWidth(150);

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        categoryCol.setPrefWidth(150);

        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAvailabilityStatus()));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, titleCol, authorCol, categoryCol, statusCol);
        table.setPrefHeight(400);
    }

    /**
     * Refresh books table with latest data
     */
    private void refreshBooksTable(TableView<Book> table) {
        table.getItems().clear();
        List<Book> books = dbManager.getAllBooks();
        table.getItems().addAll(books);
    }

    /**
     * Show add book dialog
     */
    private void showAddBookDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = createBookFormGrid();
        TextField titleField = (TextField) grid.lookup("#titleField");
        TextField authorField = (TextField) grid.lookup("#authorField");
        TextField categoryField = (TextField) grid.lookup("#categoryField");
        ComboBox<String> statusBox = (ComboBox<String>) grid.lookup("#statusBox");

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (validateBookInput(titleField.getText(), authorField.getText(),
                        categoryField.getText(), statusBox.getValue())) {
                    Book book = new Book(0, titleField.getText(), authorField.getText(),
                            categoryField.getText(), statusBox.getValue());
                    if (dbManager.addBook(book)) {
                        showInfo("Success", "Book added successfully!");
                        return book;
                    } else {
                        showError("Error", "Failed to add book");
                    }
                } else {
                    showError("Validation Error", "Please fill all fields correctly");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Create book form grid
     */
    private GridPane createBookFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setId("titleField");
        titleField.setPromptText("Book Title");

        TextField authorField = new TextField();
        authorField.setId("authorField");
        authorField.setPromptText("Author Name");

        TextField categoryField = new TextField();
        categoryField.setId("categoryField");
        categoryField.setPromptText("Category");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.setId("statusBox");
        statusBox.getItems().addAll("Available", "Borrowed");
        statusBox.setValue("Available");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusBox, 1, 3);

        return grid;
    }

    /**
     * Validate book input
     */
    private boolean validateBookInput(String title, String author, String category, String status) {
        return ValidationUtils.isValidBookTitle(title) &&
                ValidationUtils.isValidAuthor(author) &&
                ValidationUtils.isValidCategory(category) &&
                ValidationUtils.isValidAvailabilityStatus(status);
    }

    /**
     * Show update book dialog
     */
    private void showUpdateBookDialog(Book book) {
        if (book == null) {
            showError("Error", "Please select a book to update");
            return;
        }

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Update Book");
        dialog.setHeaderText("Edit book details:");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField categoryField = new TextField(book.getCategory());
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Available", "Borrowed");
        statusBox.setValue(book.getAvailabilityStatus());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                if (validateBookInput(titleField.getText(), authorField.getText(),
                        categoryField.getText(), statusBox.getValue())) {
                    book.setTitle(titleField.getText());
                    book.setAuthor(authorField.getText());
                    book.setCategory(categoryField.getText());
                    book.setAvailabilityStatus(statusBox.getValue());

                    if (dbManager.updateBook(book)) {
                        showInfo("Success", "Book updated successfully!");
                        return book;
                    } else {
                        showError("Error", "Failed to update book");
                    }
                } else {
                    showError("Validation Error", "Please fill all fields correctly");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Delete book with confirmation
     */
    private void deleteBook(Book book) {
        if (book == null) {
            showError("Error", "Please select a book to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Book");
        alert.setContentText("Are you sure you want to delete \"" + book.getTitle() + "\"?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (dbManager.deleteBook(book.getBookId())) {
                showInfo("Success", "Book deleted successfully!");
            } else {
                showError("Error", "Failed to delete book");
            }
        }
    }

    /**
     * Create Members Management Tab
     */
    private Tab createMembersTab() {
        Tab tab = new Tab("Manage Members", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Members Table
        TableView<Member> membersTable = new TableView<>();
        setupMembersTable(membersTable);
        refreshMembersTable(membersTable);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));

        Button addBtn = new Button("Add Member");
        addBtn.setPrefWidth(100);
        addBtn.setOnAction(e -> showAddMemberDialog());

        Button editBtn = new Button("Update Member");
        editBtn.setPrefWidth(100);
        editBtn.setOnAction(e -> showUpdateMemberDialog(membersTable.getSelectionModel().getSelectedItem()));

        Button deleteBtn = new Button("Delete Member");
        deleteBtn.setPrefWidth(100);
        deleteBtn.setOnAction(e -> deleteMember(membersTable.getSelectionModel().getSelectedItem()));

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefWidth(100);
        refreshBtn.setOnAction(e -> refreshMembersTable(membersTable));

        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        content.getChildren().addAll(new Label("Library Members:"), membersTable, buttonBox);

        ((VBox) tab.getContent()).getChildren().add(content);
        return tab;
    }

    /**
     * Setup members table columns
     */
    private void setupMembersTable(TableView<Member> table) {
        TableColumn<Member, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getMemberId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMemberName()));
        nameCol.setPrefWidth(200);

        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        emailCol.setPrefWidth(250);

        TableColumn<Member, String> typeCol = new TableColumn<>("Membership Type");
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMembershipType()));
        typeCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, nameCol, emailCol, typeCol);
        table.setPrefHeight(400);
    }

    /**
     * Refresh members table
     */
    private void refreshMembersTable(TableView<Member> table) {
        table.getItems().clear();
        List<Member> members = dbManager.getAllMembers();
        table.getItems().addAll(members);
    }

    /**
     * Show add member dialog
     */
    private void showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter member details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Member Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Student", "Staff");
        typeBox.setValue("Student");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (ValidationUtils.isValidMemberName(nameField.getText()) &&
                        ValidationUtils.isValidEmail(emailField.getText()) &&
                        ValidationUtils.isValidMembershipType(typeBox.getValue())) {
                    Member member = new Member(0, nameField.getText(), emailField.getText(), typeBox.getValue());
                    if (dbManager.addMember(member)) {
                        showInfo("Success", "Member added successfully!");
                        return member;
                    } else {
                        showError("Error", "Failed to add member");
                    }
                } else {
                    showError("Validation Error", "Please enter valid information");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Show update member dialog
     */
    private void showUpdateMemberDialog(Member member) {
        if (member == null) {
            showError("Error", "Please select a member to update");
            return;
        }

        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Update Member");
        dialog.setHeaderText("Edit member details:");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(member.getMemberName());
        TextField emailField = new TextField(member.getEmail());
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Student", "Staff");
        typeBox.setValue(member.getMembershipType());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                if (ValidationUtils.isValidMemberName(nameField.getText()) &&
                        ValidationUtils.isValidEmail(emailField.getText()) &&
                        ValidationUtils.isValidMembershipType(typeBox.getValue())) {
                    member.setMemberName(nameField.getText());
                    member.setEmail(emailField.getText());
                    member.setMembershipType(typeBox.getValue());

                    if (dbManager.updateMember(member)) {
                        showInfo("Success", "Member updated successfully!");
                        return member;
                    } else {
                        showError("Error", "Failed to update member");
                    }
                } else {
                    showError("Validation Error", "Please enter valid information");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Delete member with confirmation
     */
    private void deleteMember(Member member) {
        if (member == null) {
            showError("Error", "Please select a member to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Member");
        alert.setContentText("Are you sure you want to delete " + member.getMemberName() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (dbManager.deleteMember(member.getMemberId())) {
                showInfo("Success", "Member deleted successfully!");
            } else {
                showError("Error", "Failed to delete member");
            }
        }
    }

    /**
     * Create Borrow Records Management Tab
     */
    private Tab createBorrowRecordsTab() {
        Tab tab = new Tab("Manage Borrowing", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Borrow Records Table
        TableView<BorrowRecord> recordsTable = new TableView<>();
        setupBorrowRecordsTable(recordsTable);
        refreshBorrowRecordsTable(recordsTable);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));

        Button addBtn = new Button("Add Borrow Record");
        addBtn.setPrefWidth(150);
        addBtn.setOnAction(e -> showAddBorrowDialog());

        Button updateBtn = new Button("Update Record");
        updateBtn.setPrefWidth(150);
        updateBtn.setOnAction(e -> showUpdateBorrowDialog(recordsTable.getSelectionModel().getSelectedItem()));

        Button deleteBtn = new Button("Delete Record");
        deleteBtn.setPrefWidth(150);
        deleteBtn.setOnAction(e -> deleteBorrowRecord(recordsTable.getSelectionModel().getSelectedItem()));

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefWidth(150);
        refreshBtn.setOnAction(e -> refreshBorrowRecordsTable(recordsTable));

        buttonBox.getChildren().addAll(addBtn, updateBtn, deleteBtn, refreshBtn);
        content.getChildren().addAll(new Label("Borrowing Records:"), recordsTable, buttonBox);

        ((VBox) tab.getContent()).getChildren().add(content);
        return tab;
    }

    /**
     * Setup borrow records table
     */
    private void setupBorrowRecordsTable(TableView<BorrowRecord> table) {
        TableColumn<BorrowRecord, Integer> idCol = new TableColumn<>("Record ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getRecordId()).asObject());
        idCol.setPrefWidth(80);

        TableColumn<BorrowRecord, Integer> bookCol = new TableColumn<>("Book ID");
        bookCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getBookId()).asObject());
        bookCol.setPrefWidth(80);

        TableColumn<BorrowRecord, Integer> memberCol = new TableColumn<>("Member ID");
        memberCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getMemberId()).asObject());
        memberCol.setPrefWidth(80);

        TableColumn<BorrowRecord, String> borrowCol = new TableColumn<>("Borrow Date");
        borrowCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBorrowDate().toString()));
        borrowCol.setPrefWidth(120);

        TableColumn<BorrowRecord, String> dueCol = new TableColumn<>("Due Date");
        dueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDueDate().toString()));
        dueCol.setPrefWidth(120);

        TableColumn<BorrowRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReturnStatus()));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, bookCol, memberCol, borrowCol, dueCol, statusCol);
        table.setPrefHeight(400);
    }

    /**
     * Refresh borrow records table
     */
    private void refreshBorrowRecordsTable(TableView<BorrowRecord> table) {
        table.getItems().clear();
        List<BorrowRecord> records = dbManager.getAllBorrowRecords();
        table.getItems().addAll(records);
    }

    /**
     * Show add borrow record dialog
     */
    private void showAddBorrowDialog() {
        Dialog<BorrowRecord> dialog = new Dialog<>();
        dialog.setTitle("Add Borrow Record");
        dialog.setHeaderText("Create new borrowing transaction:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");

        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");

        TextField borrowDateField = new TextField();
        borrowDateField.setPromptText("YYYY-MM-DD");

        TextField dueDateField = new TextField();
        dueDateField.setPromptText("YYYY-MM-DD");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Borrowed", "Returned", "Overdue");
        statusBox.setValue("Borrowed");

        grid.add(new Label("Book ID:"), 0, 0);
        grid.add(bookIdField, 1, 0);
        grid.add(new Label("Member ID:"), 0, 1);
        grid.add(memberIdField, 1, 1);
        grid.add(new Label("Borrow Date:"), 0, 2);
        grid.add(borrowDateField, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(dueDateField, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(statusBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (ValidationUtils.isNumeric(bookIdField.getText()) &&
                        ValidationUtils.isNumeric(memberIdField.getText()) &&
                        ValidationUtils.isValidDate(borrowDateField.getText()) &&
                        ValidationUtils.isValidDate(dueDateField.getText()) &&
                        ValidationUtils.isValidReturnStatus(statusBox.getValue())) {

                    LocalDate borrowDate = LocalDate.parse(borrowDateField.getText());
                    LocalDate dueDate = LocalDate.parse(dueDateField.getText());

                    if (!ValidationUtils.isDueDateValid(borrowDate, dueDate)) {
                        showError("Error", "Due date must be after borrow date");
                        return null;
                    }

                    BorrowRecord record = new BorrowRecord(0,
                            Integer.parseInt(bookIdField.getText()),
                            Integer.parseInt(memberIdField.getText()),
                            borrowDate, dueDate, statusBox.getValue());

                    if (dbManager.addBorrowRecord(record)) {
                        showInfo("Success", "Borrow record added successfully!");
                        return record;
                    } else {
                        showError("Error", "Failed to add borrow record");
                    }
                } else {
                    showError("Validation Error", "Please enter valid information");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Show update borrow record dialog
     */
    private void showUpdateBorrowDialog(BorrowRecord record) {
        if (record == null) {
            showError("Error", "Please select a record to update");
            return;
        }

        Dialog<BorrowRecord> dialog = new Dialog<>();
        dialog.setTitle("Update Borrow Record");
        dialog.setHeaderText("Edit borrowing transaction:");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField bookIdField = new TextField(String.valueOf(record.getBookId()));
        TextField memberIdField = new TextField(String.valueOf(record.getMemberId()));
        TextField borrowDateField = new TextField(record.getBorrowDate().toString());
        TextField dueDateField = new TextField(record.getDueDate().toString());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Borrowed", "Returned", "Overdue");
        statusBox.setValue(record.getReturnStatus());

        grid.add(new Label("Book ID:"), 0, 0);
        grid.add(bookIdField, 1, 0);
        grid.add(new Label("Member ID:"), 0, 1);
        grid.add(memberIdField, 1, 1);
        grid.add(new Label("Borrow Date:"), 0, 2);
        grid.add(borrowDateField, 1, 2);
        grid.add(new Label("Due Date:"), 0, 3);
        grid.add(dueDateField, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(statusBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                if (ValidationUtils.isNumeric(bookIdField.getText()) &&
                        ValidationUtils.isNumeric(memberIdField.getText()) &&
                        ValidationUtils.isValidDate(borrowDateField.getText()) &&
                        ValidationUtils.isValidDate(dueDateField.getText()) &&
                        ValidationUtils.isValidReturnStatus(statusBox.getValue())) {

                    LocalDate borrowDate = LocalDate.parse(borrowDateField.getText());
                    LocalDate dueDate = LocalDate.parse(dueDateField.getText());

                    if (!ValidationUtils.isDueDateValid(borrowDate, dueDate)) {
                        showError("Error", "Due date must be after borrow date");
                        return null;
                    }

                    record.setBookId(Integer.parseInt(bookIdField.getText()));
                    record.setMemberId(Integer.parseInt(memberIdField.getText()));
                    record.setBorrowDate(borrowDate);
                    record.setDueDate(dueDate);
                    record.setReturnStatus(statusBox.getValue());

                    if (dbManager.updateBorrowRecord(record)) {
                        showInfo("Success", "Borrow record updated successfully!");
                        return record;
                    } else {
                        showError("Error", "Failed to update borrow record");
                    }
                } else {
                    showError("Validation Error", "Please enter valid information");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Delete borrow record
     */
    private void deleteBorrowRecord(BorrowRecord record) {
        if (record == null) {
            showError("Error", "Please select a record to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Borrow Record");
        alert.setContentText("Are you sure you want to delete this borrow record?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (dbManager.deleteBorrowRecord(record.getRecordId())) {
                showInfo("Success", "Record deleted successfully!");
            } else {
                showError("Error", "Failed to delete record");
            }
        }
    }

    /**
     * Create Reports Tab
     */
    private Tab createReportsTab() {
        Tab tab = new Tab("Reports", new VBox());
        tab.setClosable(false);

        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        Label reportTitle = new Label("Library Reports");
        reportTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TextArea reportArea = new TextArea();
        reportArea.setWrapText(true);
        reportArea.setEditable(false);
        reportArea.setPrefHeight(400);

        Button generateBtn = new Button("Generate Full Report");
        generateBtn.setPrefWidth(150);
        generateBtn.setOnAction(e -> generateReport(reportArea));

        Button overdueBtn = new Button("Show Overdue Books");
        overdueBtn.setPrefWidth(150);
        overdueBtn.setOnAction(e -> showOverdueBooks(reportArea));

        Button availabilityBtn = new Button("Show Availability");
        availabilityBtn.setPrefWidth(150);
        availabilityBtn.setOnAction(e -> showBookAvailability(reportArea));

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(generateBtn, overdueBtn, availabilityBtn);

        content.getChildren().addAll(reportTitle, reportArea, buttonBox);
        ((VBox) tab.getContent()).getChildren().add(content);
        return tab;
    }

    /**
     * Generate full report
     */
    private void generateReport(TextArea reportArea) {
        StringBuilder report = new StringBuilder();

        report.append("=== ST MARY'S DIGITAL LIBRARY SYSTEM - FULL REPORT ===\n\n");

        report.append("--- BOOKS SUMMARY ---\n");
        List<Book> books = dbManager.getAllBooks();
        report.append("Total Books: ").append(books.size()).append("\n");
        long available = books.stream().filter(b -> "Available".equals(b.getAvailabilityStatus())).count();
        report.append("Available Books: ").append(available).append("\n");
        report.append("Borrowed Books: ").append(books.size() - available).append("\n\n");

        report.append("--- MEMBERS SUMMARY ---\n");
        List<Member> members = dbManager.getAllMembers();
        report.append("Total Members: ").append(members.size()).append("\n");
        long students = members.stream().filter(m -> "Student".equals(m.getMembershipType())).count();
        report.append("Students: ").append(students).append("\n");
        report.append("Staff: ").append(members.size() - students).append("\n\n");

        report.append("--- OVERDUE BOOKS ---\n");
        List<BorrowRecord> overdueRecords = dbManager.getOverdueRecords();
        if (overdueRecords.isEmpty()) {
            report.append("No overdue books!\n");
        } else {
            for (BorrowRecord record : overdueRecords) {
                long daysOverdue = LibraryService.getDaysOverdue(record.getDueDate());
                report.append("Book ID: ").append(record.getBookId()).append(", Member ID: ")
                        .append(record.getMemberId()).append(", Days Overdue: ").append(daysOverdue).append("\n");
            }
        }

        reportArea.setText(report.toString());
    }

    /**
     * Show overdue books
     */
    private void showOverdueBooks(TextArea reportArea) {
        StringBuilder report = new StringBuilder();
        report.append("=== OVERDUE BOOKS REPORT ===\n\n");

        List<BorrowRecord> overdueRecords = dbManager.getOverdueRecords();
        if (overdueRecords.isEmpty()) {
            report.append("No overdue books in the system!\n");
        } else {
            for (BorrowRecord record : overdueRecords) {
                Member member = dbManager.getMemberById(record.getMemberId());
                Book book = dbManager.getBookById(record.getBookId());
                long daysOverdue = LibraryService.getDaysOverdue(record.getDueDate());

                report.append("Book: ").append(book != null ? book.getTitle() : "Unknown").append("\n");
                report.append("Member: ").append(member != null ? member.getMemberName() : "Unknown").append("\n");
                report.append("Due Date: ").append(record.getDueDate()).append("\n");
                report.append("Days Overdue: ").append(daysOverdue).append("\n");
                report.append("---\n");
            }
        }

        reportArea.setText(report.toString());
    }

    /**
     * Show book availability
     */
    private void showBookAvailability(TextArea reportArea) {
        StringBuilder report = new StringBuilder();
        report.append("=== BOOK AVAILABILITY REPORT ===\n\n");

        List<Book> books = dbManager.getAllBooks();
        for (Book book : books) {
            report.append("ID: ").append(book.getBookId()).append("\n");
            report.append("Title: ").append(book.getTitle()).append("\n");
            report.append("Author: ").append(book.getAuthor()).append("\n");
            report.append("Status: ").append(book.getAvailabilityStatus()).append("\n");
            report.append("---\n");
        }

        reportArea.setText(report.toString());
    }

    /**
     * Initialize sample data
     */
    private void initializeSampleData() {
        List<Book> existingBooks = dbManager.getAllBooks();
        if (existingBooks.isEmpty()) {
            dbManager.addBook(new Book(0, "Introduction to Java", "John Smith", "Programming", "Available"));
            dbManager.addBook(new Book(0, "Database Systems", "Maria Garcia", "Computer Science", "Borrowed"));
            dbManager.addBook(new Book(0, "Software Engineering Principles", "Alan Brown", "Engineering", "Available"));

            dbManager.addMember(new Member(0, "Alice Johnson", "alice.johnson@stmarys.ac.uk", "Student"));
            dbManager.addMember(new Member(0, "Michael Lee", "michael.lee@stmarys.ac.uk", "Staff"));
            dbManager.addMember(new Member(0, "Sara Ahmed", "sara.ahmed@stmarys.ac.uk", "Student"));

            dbManager.addBorrowRecord(new BorrowRecord(0, 2, 1, LocalDate.of(2025, 3, 1),
                    LocalDate.of(2025, 3, 15), "Borrowed"));
            dbManager.addBorrowRecord(new BorrowRecord(0, 1, 2, LocalDate.of(2025, 3, 2),
                    LocalDate.of(2025, 3, 16), "Returned"));
            dbManager.addBorrowRecord(new BorrowRecord(0, 3, 3, LocalDate.of(2025, 3, 5),
                    LocalDate.of(2025, 3, 19), "Borrowed"));
        }
    }

    /**
     * Show info dialog
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
