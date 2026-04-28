@echo off
title EncanFrogger Tester
cls

echo [1/3] Cleaning old builds...
if exist bin\main rmdir /s /q bin\main

echo [2/3] Compiling EncanFrogger...
javac -d bin -sourcepath src src/main/GameLauncher.java

if %errorlevel% neq 0 (
    echo.
    echo [!] COMPILATION ERROR detected.
    echo Please fix your Java code and try again.
    echo.
    pause
    exit /b %errorlevel%
)

echo [3/3] Launching Tester...
echo.
java -cp bin main.GameLauncher --test

if %errorlevel% neq 0 (
    echo.
    echo [!] Tester crashed or closed unexpectedly.
    pause
)