# EncanFrogger

A Frogger-inspired arcade game set in the fantasy world of **Encantadia**.
Play as one of five Sang'gre characters and navigate through five elemental
kingdoms — crossing lava rivers, icy waters, desert sands, open skies, and
tropical coasts — while dodging obstacles, collecting coins, and reaching
the goal before losing your three lives.

Features a character selection screen, kingdom map selection, local
leaderboard, and progressively harder levels. A Help section and Credits
are accessible from the main menu.

---

**Group Sang'gre | CMSC 12**

Denise Jade Leguarda · Lorenz Ed Ocampo · Jessa Jasmine Parcero · Jezrel Marie Saño · Jubiemyr Silvio

---

## Running the Game

### Option A — From Source

**Requirements:** Java JDK 21+

1. Navigate to the `Sang'gre/EncanFrogger/` folder.

2. Compile:

**On Windows:**
```bash
javac -d bin -sourcepath src src\main\Main.java
```

**On Mac/Linux:**
```bash
javac -d bin -sourcepath src src/main/Main.java
```
3. Run:

```bash
java -cp bin main.Main
```

> `Main` is the driver class located at the root of `src/`.

### Option B — From JAR

**Requirements:** Java JDK 21+

1. Navigate to the `Sang'gre/Installer/Jar/` folder.

2. Run:

```bash
java -jar EncanFrogger.jar
```

### Option C — Windows Executable

**Requirements:** Java JDK 21+

Navigate to `Installer/Windows/` and double-click **`EncanFrogger.exe`**.

### Option D — Setup Installer (Windows only)

Navigate to `Installer/Windows/` and double-click **`EncanFroggerSetup.exe`**.

Or download it directly from the [latest release](https://github.com/Sang-gre/Sanggre/releases/download/v1.0/EncanFroggerSetup.exe).

This will install the game to `C:\Program Files\EncanFrogger\` and create
shortcuts on your Desktop and Start Menu. You can launch the game from either shortcut after installation.

