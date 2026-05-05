@echo off
REM Travel Reservation System - Run Script

echo ========================================
echo Starting Travel Reservation System
echo ========================================

REM Set classpath
set CLASSPATH=bin;lib/*

REM Run the application (any mysql-connector-j*.jar under lib\)
java -cp "bin;lib\*" ui.LoginFrame

pause