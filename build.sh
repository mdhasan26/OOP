#!/bin/bash
# Build script for St Mary's Library Management System
# This script compiles all Java files and prepares the application for execution

echo "Compiling St Mary's Digital Library Management System..."

# Set paths
JAVA_SRC="src"
CLASS_OUTPUT="bin"
LIB_PATH="lib"

# Create bin directory if it doesn't exist
mkdir -p $CLASS_OUTPUT

# Build classpath with all libraries
CLASSPATH="$LIB_PATH/sqlite-jdbc-3.45.0.0.jar:$LIB_PATH/slf4j-api-2.0.9.jar:$LIB_PATH/slf4j-simple-2.0.9.jar:$LIB_PATH/javafx-controls-24.jar:$LIB_PATH/javafx-fxml-24.jar:$LIB_PATH/javafx-graphics-24.jar:$LIB_PATH/javafx-base-24.jar:$LIB_PATH/junit-jupiter-api-5.10.0.jar:$LIB_PATH/junit-jupiter-engine-5.10.0.jar:$LIB_PATH/junit-platform-console-standalone-1.10.0.jar"

# Compile all Java files
echo "Compiling source files..."
javac -d $CLASS_OUTPUT -cp $CLASSPATH \
    $JAVA_SRC/*.java \
    $JAVA_SRC/database/*.java \
    $JAVA_SRC/models/*.java \
    $JAVA_SRC/services/*.java \
    $JAVA_SRC/ui/*.java \
    $JAVA_SRC/utils/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    echo ""
    echo "Make sure the following JAR files exist in the lib/ folder:"
    echo "  - sqlite-jdbc-3.45.0.0.jar"
    echo "  - slf4j-api-2.0.9.jar"
    echo "  - slf4j-simple-2.0.9.jar"
    echo "  - javafx-controls-24.jar"
    echo "  - javafx-fxml-24.jar"
    echo "  - javafx-graphics-24.jar"
    echo "  - javafx-base-24.jar"
    echo "  - junit-jupiter-api-5.10.0.jar"
    echo "  - junit-jupiter-engine-5.10.0.jar"
    echo "  - junit-platform-console-standalone-1.10.0.jar"
    exit 1
fi

echo "Compilation successful!"
echo ""
echo "To run the console application:"
echo "  java -cp bin:$CLASSPATH LibraryConsoleApp"
echo ""
echo "To run the GUI application:"
echo "  java --module-path $LIB_PATH --add-modules javafx.controls,javafx.fxml -cp bin:$CLASSPATH ui.LibraryManagementGUI"
echo ""
echo "To run unit tests:"
echo "  java -cp bin:$CLASSPATH org.junit.platform.console.ConsoleLauncher --scan-classpath"
echo ""
