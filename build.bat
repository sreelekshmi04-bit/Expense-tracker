@echo off
echo ====================================================================
echo   COMPILING EXPENSE TRACKER SYSTEM (Java OOP Application)
echo ====================================================================

if not exist "target\classes" mkdir "target\classes"

echo [1/2] Compiling Java classes with javac...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$src = Get-ChildItem -Recurse -Filter '*.java' src\main\java | ForEach-Object { '\"' + ($_.FullName.Replace('\', '/')) + '\"' }; $src | Set-Content 'target\sources.txt' -Encoding Ascii; & javac -d 'target\classes' -cp 'lib/mysql-connector-j.jar;src/main/resources' '@target\sources.txt'"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please check the errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Copying database configuration and resources...
if exist "db.properties" copy "db.properties" "target\classes\db.properties" >nul
if exist "src\main\resources\*" copy "src\main\resources\*" "target\classes\" >nul

echo ====================================================================
echo   BUILD SUCCESSFUL!
echo   Compiled classes are in: target\classes
echo   To launch the server, run: run.bat
echo ====================================================================
