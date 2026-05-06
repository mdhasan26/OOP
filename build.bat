@echo off
REM Build script for St Mary's Library Management System
REM This script compiles all Java files and prepares the application for execution

echo Compiling St Mary's Digital Library Management System...

REM Set paths
set JAVA_SRC=src
set CLASS_OUTPUT=bin
set LIB_PATH=lib

REM Create bin directory if it doesn't exist
if not exist %CLASS_OUTPUT% mkdir %CLASS_OUTPUT%

REM Build classpath with all libraries
set CLASSPATH=%LIB_PATH%\sqlite-jdbc-3.45.0.0.jar;%LIB_PATH%\javafx-controls-24.jar;%LIB_PATH%\javafx-fxml-24.jar;%LIB_PATH%\javafx-graphics-24.jar;%LIB_PATH%\javafx-base-24.jar;%LIB_PATH%\junit-jupiter-api-5.10.0.jar;%LIB_PATH%\junit-jupiter-engine-5.10.0.jar;%LIB_PATH%\junit-platform-console-standalone-1.10.0.jar

REM Compile non-GUI Java files first (class path)
echo Compiling non-GUI source files...
javac -d %CLASS_OUTPUT% -cp %CLASSPATH% ^
  %JAVA_SRC%\*.java ^
  %JAVA_SRC%\database\*.java ^
  %JAVA_SRC%\models\*.java ^
  %JAVA_SRC%\services\*.java ^
  %JAVA_SRC%\utils\*.java

REM Compile GUI sources using classpath
echo Compiling GUI sources (JavaFX)...
javac -d %CLASS_OUTPUT% -cp %CLASSPATH%;%CLASS_OUTPUT% ^
  %JAVA_SRC%\ui\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    echo.
    echo Make sure the following JAR files exist in the lib\ folder:
    echo   - sqlite-jdbc-3.45.0.0.jar
    echo   - javafx-controls-24.jar
    echo   - javafx-fxml-24.jar
    echo   - javafx-graphics-24.jar
    echo   - javafx-base-24.jar
    echo   - junit-jupiter-api-5.10.0.jar
    echo   - junit-jupiter-engine-5.10.0.jar
    echo   - junit-platform-console-standalone-1.10.0.jar
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo To run the console application:
echo   java -cp bin;%CLASSPATH% LibraryConsoleApp
echo.
echo To run the GUI application:
echo   java --module-path %LIB_PATH% --add-modules javafx.controls,javafx.fxml -cp bin;%CLASSPATH% ui.LibraryManagementGUI
echo.
echo To run unit tests:
echo   java -cp bin;%CLASSPATH% org.junit.platform.console.ConsoleLauncher --scan-classpath
echo.
pause
