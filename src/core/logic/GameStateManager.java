package core.logic;

import core.level.GameMap;

public class GameStateManager {

    // --- State ---
    private GameState state;
    private GameMap currentMap;
    private int currentLevel = 1;
    private int selectedLevel = 1;
    private boolean levelTransitioning = false;
    private boolean freshStart = true;
    private boolean playerIsAlive = true;
    private boolean showingLeaderboard = false;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public GameStateManager() {
        this.state = GameState.CHARACTER_SELECT;
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------
    public void resetForNewRun() {
        state = GameState.CHARACTER_SELECT;
        currentLevel = 1;
        playerIsAlive = true;
        freshStart = true;
        levelTransitioning = false;
        showingLeaderboard = false;
    }

    public void resetGameOverState() {
        state = GameState.CHARACTER_SELECT;
        showingLeaderboard = false;
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------
    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void incrementLevel() {
        this.currentLevel++;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public boolean isLevelTransitioning() {
        return levelTransitioning;
    }

    public void setLevelTransitioning(boolean v) {
        this.levelTransitioning = v;
    }

    public boolean isFreshStart() {
        return freshStart;
    }

    public void setFreshStart(boolean v) {
        this.freshStart = v;
    }

    public boolean isPlayerAlive() {
        return playerIsAlive;
    }

    public void setPlayerAlive(boolean v) {
        this.playerIsAlive = v;
    }

    public boolean isShowingLeaderboard() {
        return showingLeaderboard;
    }

    public void setShowingLeaderboard(boolean v) {
        this.showingLeaderboard = v;
    }
}