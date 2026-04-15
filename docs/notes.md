# EncanFrogger Project Documentation

notes basically

---

## 📂 assets
*Classes responsible for resource handling.*

### [AssetManager.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\assets\AssetManager.java)
- **Role:** Centralized Resource Controller.
- **Description:** Manages the loading, caching, and retrieval of external media (Sprites, Backgrounds, Sound Effects, and Fonts).
- **Why it exists:** To prevent the game from loading the same image multiple times into memory, ensuring performance stability and easy access to assets from any class.

---

## 📂 core
*The engine and state management logic.*

### [GamePanel.java](../src/core/GamePanel.java)
- **Role:** The "Heart" of the Game.
- **Description:** A custom `JPanel` that houses the main Game Loop. It coordinates the timing for updating game logic and repainting the screen.
- **Key Task:** It calls the `update()` and `draw()` methods of all active game objects.

### [GameState.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\core\GameState.java)
- **Role:** State Controller (likely an Enum).
- **Description:** Defines the different phases of the application (e.g., `MENU`, `PLAYING`, `PAUSED`, `GAMEOVER`).
- **Key Task:** Used by the `GamePanel` and `MainPanel` to determine which logic to execute and which screens to display.

---

## 📂 gameobjects
*Entities that exist within the game world.*

### [GameObject.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\gameobjects\GameObject.java)
- **Role:** Abstract Base Class.
- **Description:** The blueprint for anything that moves or exists on the game grid. Contains shared attributes like `x`, `y` coordinates, `width`, `height`, and velocity.
- **Key Task:** Forces all subclasses to implement `update()` and `draw(Graphics g)` methods.

### [Player.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\gameobjects\Player.java)
- **Role:** User-controlled Entity.
- **Description:** Inherits from `GameObject`. Contains specific logic for the frog, including keyboard input handling, jumping animations, and life-count management.
- **Key Task:** Handles collision checks with obstacles and goals.

---

## 📂 main
*Application entry point.*

### [GameLauncher.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\main\GameLauncher.java)
- **Role:** The Bootstrapper.
- **Description:** Contains the `public static void main` method. It initializes the `JFrame` (the window), sets the window size, and adds the primary panels.
- **Key Task:** Sets up the application environment and makes the window visible.

---

## 📂 ui
*User interface components and screen layouts.*

### [Cursor.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\ui\components\Cursor.java)
- **Role:** Custom Pointer Logic.
- **Description:** Defines the behavior and appearance of a custom in-game cursor.
- **Key Task:** Calculates the cursor's position relative to the game grid or UI elements.

### [CursorGlassPane.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\ui\overlays\CursorGlassPane.java)
- **Role:** Global Rendering Layer.
- **Description:** A "Glass Pane" layer that sits on top of all other UI components.
- **Key Task:** Used to draw the custom cursor so that it never gets hidden behind buttons or background elements.

### [MainPanel.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\ui\screens\MainPanel.java)
- **Role:** Primary Container.
- **Description:** Acts as the parent container for the actual gameplay area. It may switch between different game views.
- **Key Task:** Manages the transition between the game world and the UI.

### [TitlePanel.java](C:\Users\Merajim\Documents\VSCode\EncanFrogger\src\ui\screens\TitlePanel.java)
- **Role:** Menu Screen.
- **Description:** The visual layout for the start screen/main menu.
- **Key Task:** Handles "Start Game," "Settings," or "Exit" button interactions.

---

## 🛠 Collaboration Summary
- **Adding a new enemy?** Extend `GameObject` in the `gameobjects` package.
- **Adding a new sound?** Register it in `AssetManager`.
- **Changing window size?** Look in `GameLauncher`.
- **UI looks wrong?** Check `MainPanel` or `TitlePanel`.