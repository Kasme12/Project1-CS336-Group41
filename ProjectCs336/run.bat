@echo off
REM Travel Reservation System - Run Script

echo ========================================
echo Starting Travel Reservation System
echo ========================================

REM Set classpath
set CLASSPATH=bin;lib/*

REM Run the application
java -cp "%CLASSPATH%" ui.LoginFrame

pause