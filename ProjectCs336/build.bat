@echo off
REM Travel Reservation System - Build Script

echo ========================================
echo Building Travel Reservation System
echo ========================================

REM Set Java path (update if different)
set JAVA_HOME=C:\Program Files\Java\jre1.8.0_491
set PATH=%JAVA_HOME%\bin;%PATH%

REM Create lib directory
if not exist lib mkdir lib

REM Download MySQL connector if not exists
if not exist lib\mysql-connector-java-8.0.28.jar (
    echo Downloading MySQL Connector...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.28/mysql-connector-j-8.0.28.jar' -OutFile 'lib\mysql-connector-java-8.0.28.jar'" 2>nul
    if not exist lib\mysql-connector-java-8.0.28.jar (
        echo WARNING: Could not download MySQL connector automatically.
        echo Please manually place mysql-connector-java-8.0.28.jar in the lib folder.
    )
)

REM Create bin directory
if not exist bin mkdir bin

REM Compile source files
echo Compiling source files...
"%JAVA_HOME%\bin\javac" -d bin -cp "lib/*" src\model\*.java src\dao\*.java src\ui\*.java

if errorlevel 1 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo ========================================
echo Build successful!
echo ========================================
echo.
echo To run the application:
echo   run.bat
echo.
pause