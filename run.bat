@echo off
echo =================================================================
echo                    Chess Server Startup Script
echo =================================================================
echo.
echo Compiling Java Chess Backend...
javac backend/src/ChessServe.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please make sure JDK is installed and on your PATH.
    pause
    exit /b %errorlevel%
)
echo [SUCCESS] Compiled successfully.
echo.
echo Starting Chess Server on http://localhost:9090 ...
echo Press Ctrl+C to stop the server.
echo.
java -cp backend/src ChessServe
pause
