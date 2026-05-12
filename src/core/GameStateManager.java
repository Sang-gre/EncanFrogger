package core;

public class GameStateManager {

    private GameState state;
    private int currentLevel = 1;
    private int selectedLevel = 1;
    private boolean levelTransitioning = false;
    private boolean freshStart = true;
    private boolean playerIsAlive = true;
    private boolean showingLeaderboard = false;
    private GameMap currentMap;

    public GameStateManager() {
        this.state = GameState.CHARACTER_SELECT;
    }

    public void resetForNewRun() {
        currentLevel = 1;
        playerIsAlive = true;
        freshStart = true;
        levelTransitioning = false;
        showingLeaderboard = false;
        state = GameState.CHARACTER_SELECT;
    }

    public void resetGameOverState() {
        showingLeaderboard = false;
        state = GameState.CHARACTER_SELECT;
    }
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
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

    public void setLevelTransitioning(boolean levelTransitioning) {
        this.levelTransitioning = levelTransitioning;
    }

    public boolean isFreshStart() {
        return freshStart;
    }

    public void setFreshStart(boolean freshStart) {
        this.freshStart = freshStart;
    }

    public boolean isPlayerAlive() {
        return playerIsAlive;
    }

    public void setPlayerAlive(boolean playerIsAlive) {
        this.playerIsAlive = playerIsAlive;
    }

    public boolean isShowingLeaderboard() {
        return showingLeaderboard;
    }

    public void setShowingLeaderboard(boolean showingLeaderboard) {
        this.showingLeaderboard = showingLeaderboard;
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }
}