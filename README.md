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

**Requirements:** Java JDK 25+

1. Navigate to the `Sang'gre/` folder.

2. Compile:

**On Windows:**
```bash
javac -d EncanFrogger\bin -sourcepath EncanFrogger\src EncanFrogger\src\main\Main.java
```

**On Mac/Linux:**
```bash
javac -d EncanFrogger/bin -sourcepath EncanFrogger/src EncanFrogger/src/main/Main.java
```
3. Run:

```bash
java -cp EncanFrogger/bin main.Main
```

> `Main` is the driver class located at the root of `src/`.

### Option B — From JAROption B — From JAR
Requirements: Java JDK 17+

Navigate to the Sang'gre/ folder.

Run:

java -jar Installer/Jar/EncanFrogger.jar

**Requirements:** Java JDK 17+

1. Navigate to the `Sang'gre/` folder.

2. Run:

```bash
java -jar Installer/Jar/EncanFrogger.jar
```

### Option C — Installer (Windows only)

Navigate to `Installer/Windows/EncanFrogger/` and double-click **`EncanFrogger.exe`**.

> On first launch, a splash screen will appear while assets load. This is expected.

### Option D — Setup Installer (Windows only)

Navigate to `Installer/Release/` and double-click **`EncanFroggerSetup.exe`**.

This will install the game to `C:\Program Files\EncanFrogger\` and create
shortcuts on your Desktop and Start Menu. You can launch the game from
either shortcut after installation.