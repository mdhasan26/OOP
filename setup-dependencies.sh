#!/bin/bash
# Setup script to download required dependencies for St Mary's Library System
# This script downloads JavaFX, JUnit 5, and other required libraries

echo "========================================"
echo "St Mary's Library System - Dependency Setup"
echo "========================================"
echo ""

# Check if lib directory exists
mkdir -p lib
cd lib

echo "Downloading dependencies..."
echo ""

# Function to download files
download_file() {
    local url=$1
    local filename=$2
    
    if command -v curl &> /dev/null; then
        echo "Downloading $filename..."
        curl -L -o "$filename" "$url"
    elif command -v wget &> /dev/null; then
        echo "Downloading $filename..."
        wget -O "$filename" "$url"
    else
        echo "Error: Neither curl nor wget found"
        return 1
    fi
}

# Download JavaFX libraries
echo "Downloading JavaFX libraries..."
download_file "https://repo1.maven.org/maven2/org/openjfx/javafx-base/24.0.1/javafx-base-24.0.1.jar" "javafx-base-24.jar"
download_file "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/24.0.1/javafx-graphics-24.0.1.jar" "javafx-graphics-24.jar"
download_file "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/24.0.1/javafx-controls-24.0.1.jar" "javafx-controls-24.jar"
download_file "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/24.0.1/javafx-fxml-24.0.1.jar" "javafx-fxml-24.jar"

# Download JUnit 5 libraries
echo ""
echo "Downloading JUnit 5 libraries..."
download_file "https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar" "junit-jupiter-api-5.10.0.jar"
download_file "https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar" "junit-jupiter-engine-5.10.0.jar"
download_file "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar" "junit-platform-console-standalone-1.10.0.jar"

echo ""
echo "Dependencies downloaded successfully!"
echo ""
echo "Next steps:"
echo "  1. Navigate back to the project root: cd .."
echo "  2. Run the build script: ./build.sh"
echo ""

cd ..
