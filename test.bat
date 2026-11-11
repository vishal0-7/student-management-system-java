@echo off
setlocal
cd /d "%~dp0"
call java-env.bat
call build.bat
if errorlevel 1 exit /b 1
"%JAVA_CMD%" -cp build StudentSystemTest
if errorlevel 1 exit /b 1
pause
