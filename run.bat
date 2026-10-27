@echo off
echo ====================================================================
echo   LAUNCHING EXPENSE TRACKER SYSTEM (Java OOP Web Application)
echo ====================================================================

REM If target/classes does not exist, build first
if not exist "target\classes\com\expensetracker\server\StandaloneServer.class" (
    echo Compiling project first...
    call build.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed. Exiting.
        pause
        exit /b %ERRORLEVEL%
    )
)

echo Starting server on http://localhost:8080 ...
echo (Keep this command window open while using the application)
echo.

REM Automatically open default browser after a brief delay
start "" "http://localhost:8080"

REM Launch Java Standalone Server
java -cp "target\classes;lib\mysql-connector-j.jar;src\main\resources;." com.expensetracker.server.StandaloneServer 8080

pause
