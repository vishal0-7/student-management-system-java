@echo off
setlocal
cd /d "%~dp0"
call java-env.bat
if not exist StudentManagementSystem.jar call build.bat
if errorlevel 1 goto fail
"%JAVA_CMD%" -jar StudentManagementSystem.jar
if errorlevel 1 goto fail
exit /b 0
:fail
echo Could not launch. Check that Java 17 or later is installed and available on PATH.
pause
exit /b 1
