@echo off
REM Travel Reservation System - Run Script

echo ========================================
echo Starting Travel Reservation System
echo ========================================

REM Set classpath
set CLASSPATH=bin;lib/*

REM Run the application
java -cp "bin;lib/mysql-connector-j-9.7.0.jar" ui.LoginFrame

pause