package core.handlers;

import core.level.LevelManager;
import core.logic.GameState;
import core.logic.GameStateManager;
import gameobjects.Player;
import java.awt.Graphics;

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
                       screens.gameplay.GameOverScreen gameOverScreen,
                       screens.menu.LeaderboardScreen leaderboardScreen,
                       screens.gameplay.PauseScreen pauseScreen,
                       screens.gameplay.CongratsScreen congratsScreen) {

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