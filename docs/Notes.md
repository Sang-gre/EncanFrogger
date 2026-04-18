# EncanFrogger: Full System Documentation

EncanFrogger Code EXPLAINED! An stuff la anay nga I that might be confusing

---

## 🏗 Relationships Overview
Refer to the UML diagram
- **Association:** ----------
    - has-a or talks-to
    - Class A works with Class B
    - `private OtherClass obj;`
- **Dependency** ..........>
    - uses-a
    - Class A uses Class B (Temporary)
    - `public void Method(OtherClass obj){}`
- **Inheritance** ---------->
    - is-a
    - Class A is a Class B
    - `extends`
- **Aggregation** <>---------
    - has-a or "Part-of" but independent
    - Class A has a Class B (Independent)
    - `public Constructor(OtherClass obj){}`

---

## 📂 Package: `main`
The starting point of the application.

### [GameLauncher.java](../src/main/GameLauncher.java)
- **Role:** Entry point
- **Methods:**
    - `GameLauncher()`: insert text 
- **Relationships:** 
    - Creates **MainPanel** and **TitlePanel** and gives itself (this) so those panels can call **GameLauncher** methods
    - Creates and shows **GamePanel**
    - Puts **CursorGlassPane** on top of everything (overlay layer). **CursorGlassPane** captures mouse clicks, then **GameLauncher** forwards those clicks to the actual UI components

### [MainPanel.java](../src/main/MainPanel.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [TitlePanel.java](../src/main/TitlePanel.java)
- **Role:**
- **Methods:**
- **Relationships:** 

---

## 📂 Package: `core`
Main gameplay

### [CharacterSelect.java](../src/core/CharacterSelect.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [CollisionSystem.java](../src/core/CollisionSystem.java)
- **Role:** Handles object collision
- **Methods:**
- **Relationships:**
    - Uses **GameObject** as parameters for collision checking
    - Used by **GameLogicThread** to detect and handle collisions

### [GamePanel.java](../src/core/GamePanel.java)
- **Role:**
- **Methods:**
    - `showCharacterSelect()` shows character selection screeen with back button
    - `showCharacterSelectMidGame()` shows character select without back button
    - `showMap()`
    - `startLevel(Player)` starts the game: sets player, initializes systems, starts threads
    - `updateGame()` updates player, objects, and checks collisions
    - `checkGameConditions()` checks win/lose conditions and updates state
    - `keyPressed(KeyEvent e)` handles controls (pause, move, ability, menu navigation)
    - `stopThreads()`stops game threads before switching screens
    - More methods pa for rendering
- **Relationships:** 
    - Receives **GameLauncher** so it can call its methods
    - Creates and controls **CharacterSelect** (passes itself so selection affects GamePanel)
    - Receives selected **Player** from **CharacterSelect** and uses it for gameplay
    - Creates and starts **GameLogicThread** and **RenderThread** to run the game loop
    - Uses **AssetManager** to load game assets (sprites, sounds)
    - Updates **Player** during the game (updateGame())
    - Uses **GameState** to control behavior

### [GameState.java](../src/core/GameState.java)
- **Role:**
- **Methods:**
- **Relationships:** 