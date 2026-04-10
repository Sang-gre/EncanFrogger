@echo off
echo Compiling EncanFrogger...
if not exist bin mkdir bin

:: This finds all .java files and compiles them
javac -d bin -sourcepath src src/main/GameLauncher.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Check your code for errors.
    pause
    exit /b %errorlevel%
)

echo Compilation successful! Starting game...
java -cp bin main.GameLauncher