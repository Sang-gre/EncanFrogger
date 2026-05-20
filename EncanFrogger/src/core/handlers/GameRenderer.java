package core.handlers;

import core.level.LevelManager;
import core.logic.GameState;
import core.logic.GameStateManager;
import gameobjects.Player;
import java.awt.Graphics;
import screens.gameplay.CongratsScreen;
import screens.gameplay.GameOverScreen;
import screens.gameplay.PauseScreen;
import screens.menu.LeaderboardScreen;

/*  Renders the game world and UI overlays each frame */
public class GameRenderer {

    private final GameStateManager stateManager;

    public GameRenderer(GameStateManager stateManager) {
        this.stateManager = stateManager;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    // Overlay is only drawn when its game state is active
    public void render(Graphics g, int width, int height,
            LevelManager levelManager, Player player,
            GameOverScreen gameOverScreen, LeaderboardScreen leaderboardScreen,
            PauseScreen pauseScreen, CongratsScreen congratsScreen) {

        // Draw game world
        if (levelManager != null)
            levelManager.draw(g, width, height);

        // Draw player
        if (player != null)
            player.draw(g);

        // Draw pause overlay
        if (stateManager.getState() == GameState.PAUSED && pauseScreen != null)
            pauseScreen.draw(g, width, height);

        // Draw congrats screen on win
        if (congratsScreen != null)
            congratsScreen.draw(g, width, height);

        // Draw game over screen (only when no leaderboard)
        if (stateManager.getState() == GameState.GAME_OVER
                && gameOverScreen != null
                && !stateManager.isShowingLeaderboard()) {
            gameOverScreen.draw(g, width, height);
        }

        // Draw leaderboard (after game over or win)
        if (stateManager.isShowingLeaderboard() && leaderboardScreen != null)
            leaderboardScreen.draw(g, width, height);
    }
}