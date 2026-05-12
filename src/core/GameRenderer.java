package core;

import gameobjects.Player;
import java.awt.Graphics;
import level.LevelManager;

/* Handles rendering */
public class GameRenderer {

    private final GameStateManager stateManager;

    public GameRenderer(GameStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void render(Graphics g,
                       int width,
                       int height,
                       LevelManager levelManager,
                       Player player,
                       ui.GameOverScreen gameOverScreen,
                       ui.LeaderboardScreen leaderboardScreen,
                       ui.PauseScreen pauseScreen,
                       ui.CongratsScreen congratsScreen) {

        if (levelManager != null)
            levelManager.draw(g, width, height);

        if (player != null)
            player.draw(g);

        if (stateManager.getState() == GameState.GAME_OVER
                && gameOverScreen != null
                && !stateManager.isShowingLeaderboard()) {
            gameOverScreen.draw(g, width, height);
        }

        if (stateManager.isShowingLeaderboard() && leaderboardScreen != null) {
            leaderboardScreen.draw(g, width, height);
        }

        if (stateManager.getState() == GameState.PAUSED && pauseScreen != null) {
            pauseScreen.draw(g, width, height);
        }

        if (congratsScreen != null) {
            congratsScreen.draw(g, width, height);
        }
    }
}