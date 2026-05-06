@echo off
REM Setup script to download required dependencies for St Mary's Library System
REM This script downloads JavaFX, JUnit 5, and other required libraries

echo ========================================
echo St Mary's Library System - Dependency Setup
echo ========================================
echo.

REM Check if lib directory exists
if not exist lib mkdir lib
cd lib

echo Downloading dependencies...
echo.

REM Check if wget or curl is available
where curl >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set DOWNLOAD_CMD=curl -L -o
    goto DOWNLOAD_WITH_CURL
)

where wget >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set DOWNLOAD_CMD=wget -O
    goto DOWNLOAD_WITH_WGET
)

echo Error: Neither curl nor wget found in PATH
echo Please download the following JAR files manually to the lib\ folder:
echo.
echo JavaFX 24.0.1 modules:
echo   - https://gluonhq.com/products/javafx/
echo   - Download: javafx-sdk-24.0.1-windows.zip
echo   - Extract the lib folder contents to lib\
echo.
echo JUnit 5:
echo   - https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar
echo   - https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar
echo   - https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar
echo.
echo After downloading, run: build.bat
pause
exit /b 1

:DOWNLOAD_WITH_CURL
echo Using curl for downloads...
echo.

REM JavaFX libraries
echo Downloading JavaFX libraries...
%DOWNLOAD_CMD% javafx-base-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-base/24.0.1/javafx-base-24.0.1.jar"
%DOWNLOAD_CMD% javafx-graphics-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/24.0.1/javafx-graphics-24.0.1.jar"
%DOWNLOAD_CMD% javafx-controls-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/24.0.1/javafx-controls-24.0.1.jar"
%DOWNLOAD_CMD% javafx-fxml-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/24.0.1/javafx-fxml-24.0.1.jar"

REM JUnit 5 libraries
echo Downloading JUnit 5 libraries...
%DOWNLOAD_CMD% junit-jupiter-api-5.10.0.jar "https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar"
%DOWNLOAD_CMD% junit-jupiter-engine-5.10.0.jar "https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar"
%DOWNLOAD_CMD% junit-platform-console-standalone-1.10.0.jar "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar"

echo.
echo Dependencies downloaded successfully!
pause
goto END

:DOWNLOAD_WITH_WGET
echo Using wget for downloads...
echo.

REM JavaFX libraries
echo Downloading JavaFX libraries...
%DOWNLOAD_CMD% javafx-base-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-base/24.0.1/javafx-base-24.0.1.jar"
%DOWNLOAD_CMD% javafx-graphics-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/24.0.1/javafx-graphics-24.0.1.jar"
%DOWNLOAD_CMD% javafx-controls-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/24.0.1/javafx-controls-24.0.1.jar"
%DOWNLOAD_CMD% javafx-fxml-24.jar "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/24.0.1/javafx-fxml-24.0.1.jar"

REM JUnit 5 libraries
echo Downloading JUnit 5 libraries...
%DOWNLOAD_CMD% junit-jupiter-api-5.10.0.jar "https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar"
%DOWNLOAD_CMD% junit-jupiter-engine-5.10.0.jar "https://repo1.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar"
%DOWNLOAD_CMD% junit-platform-console-standalone-1.10.0.jar "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar"

echo.
echo Dependencies downloaded successfully!
pause
goto END

:END
cd ..
