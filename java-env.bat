@echo off
rem Prefer a real JDK over the Windows Oracle javapath launcher shim.
set "JAVA_CMD=java"
set "JAVAC_CMD=javac"
set "JAR_CMD=jar"
set "PROJECT_JDK="
if exist "%ProgramFiles%\Java" for /d %%D in ("%ProgramFiles%\Java\jdk*") do if exist "%%~D\bin\javac.exe" set "PROJECT_JDK=%%~D"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "PROJECT_JDK=%JAVA_HOME%"
if defined PROJECT_JDK (
  set "JAVA_CMD=%PROJECT_JDK%\bin\java.exe"
  set "JAVAC_CMD=%PROJECT_JDK%\bin\javac.exe"
  set "JAR_CMD=%PROJECT_JDK%\bin\jar.exe"
)
