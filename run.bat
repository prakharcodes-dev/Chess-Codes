@echo off
setlocal enabledelayedexpansion

echo =================================================================
echo                    Chess Server Startup Script
echo =================================================================
echo.

where javac >nul 2>&1
if %errorlevel% neq 0 (
    echo Searching for Java JDK installation...
    
    if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set "PATH=%JAVA_HOME%\bin;%PATH%"
            goto :FOUND_JDK
        )
    )
    
    for /d %%D in ("C:\Program Files\Java\jdk*") do (
        if exist "%%D\bin\javac.exe" (
            set "PATH=%%D\bin;%PATH%"
            goto :FOUND_JDK
        )
    )
    
    for /d %%D in ("C:\Program Files (x86)\Java\jdk*") do (
        if exist "%%D\bin\javac.exe" (
            set "PATH=%%D\bin;%PATH%"
            goto :FOUND_JDK
        )
    )

    echo.
    echo [ERROR] Compilation failed! Please make sure JDK is installed and on your PATH.
    pause
    exit /b 1
)

:FOUND_JDK
echo Compiling Java Chess Backend...
javac backend/src/ChessServe.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check your Java code.
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
