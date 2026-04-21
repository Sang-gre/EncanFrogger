# EncanFrogger: Full System Documentation

EncanFrogger Code EXPLAINED! An stuff la anay nga I that might be confusing

<details>
<summary><b> 🏗 Relationships Overview </b></summary>

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
</details>

---

<h2> 🛠 System Architecture </h2>

<details>
<summary><b> 📂 Package: characters </b></summary>

Defines different playable characters and their abilities

### [Adamus.java](../src/core/GameLogicThread.java)
- **Role:**
- **Methods:**
- **Relationships:** 

</details>





<details>
<summary><b> 📂 Package: core </b></summary>

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
    - More methods pa for rendering or idkk depende kay Denise
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
    - `computeLanes()` divides the screem area evenly into lanes
    - `resize()` called from GamePanel when window is resized
    - `repositionEntities` repositions objects upon resize
    - `loadLevel()` starts the level. Main method for the class
    - `initLogLanes()` commputes which lanes are platform lanes and stores result into dedicated array
    - `spawnObstacles()` spawns obastcales across all lanes
    - `spawnCoins()` spawns coins at random positions across random lanes
    - `respawn()` when obstacle goes off screen instead of creating a new object, it reuses by calling reset()
    - `update()` called every game tick. Updates all active obstacles and checks if any went off screen, puts them into toRespawn.
    - `centeredY()` returns the Y coordinate that vertically centers an entity within the lane.
- **Relationships:** 

</details>





<details>
<summary><b> 📂 Package: gameobjects </b></summary>

Defines all objects that exist and interact in the game world.

### [Coin.java](../src/core/RenderThread.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [Direction.java](../src/core/Direction.java)
- **Role:**
- **Methods:**
- **Relationships:** 

</details>





<details>
<summary><b> 📂 Package: level </b></summary>

level handling

### [LevelManager.java](../src/core/LevelManager.java)
- **Role:**
- **Methods:**
- **Relationships:** 

</details>




<details>
<summary><b> 📂 Package: main </b></summary>

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

</details>





<details>
<summary><b> 📂 Package: persistence </b></summary>
Saving / loading data

### [DataManager.java](../src/core/DataManager.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [LeaderboardManager.java](../src/core/LeaderboardManager.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [PlayerAccountjava](../src/core/PlayerAccount.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [SaveState.java](../src/core/SaveState.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [Score.java](../src/core/Score.java)
- **Role:**
- **Methods:**
- **Relationships:** 

</details>





<details>
<summary><b> 📂 Package: threads </b></summary>

Runs game processes simultaneously

### [GameLogicThread.java](../src/core/GameLogicThread.java)
- **Role:**
- **Methods:**
- **Relationships:** 

### [RenderThread.java](../src/core/RenderThread.java)
- **Role:**
- **Methods:**
- **Relationships:** 

</details>





<details>
<summary><b> 📂 Package: ui </b></summary>

Runs game processes simultaneously

### [CursorGlassPane.java](../src/core/CursorGlassPane.java)
- **Role:** Custom cursor overlay
- **Methods:** 
    - `CursorGlassPane(Image, JPanel)` sets custom cursor image and attaches mouse listeners
    - `forwardEvent(e)` sends mouse events to the actual UI components underneath
    - `paintComponent(g)` draws the custom cursor at the current mouse position
- **Relationships:** 
    - Receives **mainPanel** so it knows where to forward mouse events

</details>