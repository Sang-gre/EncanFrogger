# ENCANFROGGER - README

**Group Sang'gre**
* **Denise Jade Leguarda** – Graphics & Rendering Developer
* **Lorenz Ed Ocampo** – Asset & Integration Manager
* **Jessa Jasmine Parcero** – Gameplay & Physics Developer
* **Jezrel Marie Saño** – Lead Architect
* **Jubiemyr Silvio** – Systems Engineer

**Course:** CMSC 12

---

## IMPORTANT NOTE

This project can be run in **TWO** ways as required for the laboratory activity:

1. [**Running from SOURCE CODE (Java CLI)**](#running-from-source-code)
2. [**Running from INSTALLER (One-click app)**](#running-from-installer)

Both methods are fully functional and included in this package.

---

## REQUIREMENTS (FOR SOURCE RUN)
* **Java JDK 17** or higher installed on the system.
* **Command Prompt / Terminal** access.

---

## RUNNING FROM SOURCE CODE

### Step 1: Open Terminal

Navigate to the root `EncanFrogger` folder where the `src` directory is located.

### Step 2: Compile the Project

Execute the following commands to generate the class files into an `out` folder:

```bash
dir /s /b src\*.java > sources.txt
mkdir out
javac -d out @sources.txt
```

### Step 3: Run the Game

Execute the compiled project using the following command:

```bash
java -cp out main.GameLauncher
```

**OR** (if the JAR file is preferred):

```bash
java -jar release\EncanFrogger.jar
```

---

## RUNNING FROM INSTALLER

### Step 1: Open Folder

Navigate to the following directory:

```
EncanFrogger-Installer
```

### Step 2: Launch the App

Double-click the executable file:

**`EncanFrogger.exe`**