@echo off
setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVAC="%JAVA_HOME%\bin\javac.exe"
set JAR_TOOL="%JAVA_HOME%\bin\jar.exe"
set JAVA="%JAVA_HOME%\bin\java.exe"
set OUT=target\classes
set JAR_FILE=target\WeddingPlanningVendorBookingSystem.jar

echo Cleaning output directory...
if exist target\classes rmdir /s /q target\classes
mkdir target\classes
if exist target\sources.txt del target\sources.txt

echo Finding source files...
for /r "src\main\java\com\wedding\local" %%f in (*.java) do (
    set "FP=%%f"
    set "FP=!FP:\=/!"
    echo "!FP!" >> target\sources.txt
)
for /r "src\main\java\com\wedding\model" %%f in (*.java) do (
    set "FP=%%f"
    set "FP=!FP:\=/!"
    echo "!FP!" >> target\sources.txt
)

echo Compiling sources...
%JAVAC% -d "%OUT%" @target\sources.txt
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)

echo Creating JAR...
(echo Main-Class: com.wedding.local.LocalWeddingServer) > target\MANIFEST.MF
%JAR_TOOL% cfm "%JAR_FILE%" target\MANIFEST.MF -C "%OUT%" .
if errorlevel 1 (
    echo JAR creation failed.
    pause
    exit /b 1
)

echo Starting app on port 8081...
start "WeddingApp" %JAVA% -jar "%JAR_FILE%" 8081

timeout /t 2 /nobreak >nul

pause
