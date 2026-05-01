# Dependency Setup Guide

This document explains how to resolve the missing JavaFX and JUnit dependencies for the St Mary's Library Management System.

## Issues Summary

| Component    | Status            | Issue                       |
| ------------ | ----------------- | --------------------------- |
| GUI (JavaFX) | ⚠️ Not Compilable | Missing JavaFX dependencies |
| Unit Tests   | ⚠️ Not Runnable   | Missing JUnit libraries     |

## Quick Start

### Option 1: Automatic Setup (Recommended)

#### Windows

```bash
setup-dependencies.bat
```

#### Linux/Mac

```bash
chmod +x setup-dependencies.sh
./setup-dependencies.sh
```

This will automatically download all required JAR files to the `lib/` folder.

### Option 2: Manual Download

If automatic setup doesn't work, download the following JAR files manually and place them in the `lib/` folder:

#### JavaFX 24.0.1 (Required for GUI)

- `javafx-base-24.jar` - https://repo1.maven.org/maven2/org/openjfx/javafx-base/24.0.1/javafx-base-24.0.1.jar
- `javafx-graphics-24.jar` - https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/24.0.1/javafx-graphics-24.0.1.jar
- `javafx-controls-24.jar` - https://repo1.maven.org/maven2/org/openjfx/javafx-controls/24.0.1/javafx-controls-24.0.1.jar
- `javafx-fxml-24.jar` - https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/24.0.1/javafx-fxml-24.0.1.jar

#### JUnit 5 (Required for Tests)

- `junit-jupiter-api-5.10.0.jar` - https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar
- `junit-jupiter-engine-5.10.0.jar` - https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar
- `junit-platform-console-standalone-1.10.0.jar` - https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar

## Building the Project

### After dependencies are installed:

#### Windows

```bash
build.bat
```

#### Linux/Mac

```bash
chmod +x build.sh
./build.sh
```

## Running the Application

### Console Application

```bash
java -cp bin;lib/sqlite-jdbc-3.45.0.0.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar LibraryConsoleApp
```

### GUI Application (JavaFX)

```bash
# Windows
java --module-path lib --add-modules javafx.controls,javafx.fxml -cp bin;lib/* ui.LibraryManagementGUI

# Linux/Mac
java --module-path lib --add-modules javafx.controls,javafx.fxml -cp bin:lib/* ui.LibraryManagementGUI
```

### Unit Tests

```bash
# Windows
java -cp bin;lib/* org.junit.platform.console.ConsoleLauncher --scan-classpath

# Linux/Mac
java -cp bin:lib/* org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## Dependency Details

### JavaFX 24.0.1

JavaFX is a modern Java-based UI framework used for the graphical user interface (`LibraryManagementGUI.java`). It provides:

- Modern UI controls (buttons, tables, tabs, etc.)
- Scene and stage management
- Layout management

**Modules Required:**

- `javafx.base` - Core JavaFX functionality
- `javafx.graphics` - Graphics rendering
- `javafx.controls` - UI controls
- `javafx.fxml` - FXML support for UI markup

### JUnit 5 (Jupiter)

JUnit 5 is a testing framework used for unit tests (`LibrarySystemTests.java`). It provides:

- Test annotations (`@Test`, `@BeforeEach`, etc.)
- Assertions for validating test results
- Test discovery and execution
- Console launcher for running tests

**Components:**

- `junit-jupiter-api` - JUnit 5 API for writing tests
- `junit-jupiter-engine` - Test engine for executing tests
- `junit-platform-console-standalone` - Console launcher

## Troubleshooting

### Compilation Error: "package javafx does not exist"

**Solution:** Ensure all JavaFX JAR files are in the `lib/` folder and the build script is using the correct classpath.

### Compilation Error: "package org.junit does not exist"

**Solution:** Ensure all JUnit JAR files are in the `lib/` folder and the build script is using the correct classpath.

### Runtime Error: "Could not initialize PRISM"

**Solution:** This typically occurs on Linux/Mac systems without a display. Install JavaFX for your OS or use a remote display.

### JAR Files Not Found

**Solution:**

1. Verify all JAR files are in the `lib/` folder
2. Check file names match exactly (case-sensitive on Linux/Mac)
3. Re-run the setup script to ensure complete downloads

## Project Structure After Setup

```
StMarysLibrarySystem/
├── src/
│   ├── LibraryConsoleApp.java
│   ├── LibrarySystemTests.java
│   ├── database/
│   │   └── DatabaseManager.java
│   ├── models/
│   ├── services/
│   ├── ui/
│   │   └── LibraryManagementGUI.java
│   └── utils/
├── lib/
│   ├── sqlite-jdbc-3.45.0.0.jar
│   ├── slf4j-api-2.0.9.jar
│   ├── slf4j-simple-2.0.9.jar
│   ├── javafx-base-24.jar ✓ NEW
│   ├── javafx-graphics-24.jar ✓ NEW
│   ├── javafx-controls-24.jar ✓ NEW
│   ├── javafx-fxml-24.jar ✓ NEW
│   ├── junit-jupiter-api-5.10.0.jar ✓ NEW
│   ├── junit-jupiter-engine-5.10.0.jar ✓ NEW
│   └── junit-platform-console-standalone-1.10.0.jar ✓ NEW
├── bin/ (generated after build)
├── build.bat (updated)
├── build.sh (updated)
├── setup-dependencies.bat ✓ NEW
├── setup-dependencies.sh ✓ NEW
└── DEPENDENCIES.md ✓ NEW
```

## Verifying Installation

After running the build script, you can verify successful compilation by checking:

```bash
# Windows
dir bin\

# Linux/Mac
ls -la bin/
```

You should see `.class` files for all your Java source files.

## Additional Resources

- [OpenJFX (JavaFX) Documentation](https://openjfx.io/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Maven Central Repository](https://repo1.maven.org/maven2/)
