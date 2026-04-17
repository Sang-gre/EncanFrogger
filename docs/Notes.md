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

### [GamePanel.java](../src/core/GamePanel.java)