@echo off
setlocal
cd /d "%~dp0"
call java-env.bat
"%JAVAC_CMD%" -version >nul 2>nul
if errorlevel 1 (
  echo Java JDK 17 or later is required. Add its bin folder to PATH.
  exit /b 1
)
if not exist build mkdir build
"%JAVAC_CMD%" --release 17 -encoding UTF-8 -d build src\*.java tests\*.java
if errorlevel 1 exit /b 1
"%JAR_CMD%" --create --file StudentManagementSystem.jar --main-class StudentApp -C build .
if errorlevel 1 exit /b 1
echo Build complete.
