@echo off
title EncanFrogger Developer Tool
cls

echo [1/3] Cleaning old builds...
if exist bin\main rmdir /s /q bin\main

echo [2/3] Compiling EncanFrogger...
:: This one line compiles GameLauncher AND all its dependencies (core, ui, etc.)
javac -d bin -sourcepath src src/main/Main.java

if %errorlevel% neq 0 (
    echo.
    echo [!] COMPILATION ERROR detected.
    echo Please fix your Java code and try again.
    echo.
    pause
    exit /b %errorlevel%
)

echo [3/3] Launching Game...
echo.
java -cp bin main.Main

:: This keeps the window open if the game crashes so you can see why
if %errorlevel% neq 0 (
    echo.
    echo [!] Game crashed or closed unexpectedly.
    pause
)