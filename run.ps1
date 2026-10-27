# Launch script for PowerShell
Write-Host "====================================================================" -ForegroundColor Green
Write-Host "  LAUNCHING EXPENSE TRACKER SYSTEM (Java OOP Web Application)" -ForegroundColor Green
Write-Host "====================================================================" -ForegroundColor Green

if (-not (Test-Path "target\classes\com\expensetracker\server\StandaloneServer.class")) {
    Write-Host "Compiling Java files..." -ForegroundColor Yellow
    if (-not (Test-Path "target\classes")) { New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null }
    $files = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
    & javac -encoding UTF-8 -cp "lib\mysql-connector-j.jar;src\main\resources" -d "target\classes" $files
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed." -ForegroundColor Red
        Exit
    }
    Copy-Item "db.properties" -Destination "target\classes\db.properties" -Force -ErrorAction SilentlyContinue
}

Write-Host "Starting server on http://localhost:8080 ..." -ForegroundColor Cyan
Write-Host "(Keep this window open while using the application)" -ForegroundColor Gray
Start-Process "http://localhost:8080"
& java -cp "target\classes;lib\mysql-connector-j.jar;src\main\resources;." com.expensetracker.server.StandaloneServer 8080
