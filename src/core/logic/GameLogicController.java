package core.logic;

import core.GamePanel;
import core.handlers.CollisionSystem;
import core.handlers.InputHandler;
import core.level.LevelManager;
import gameobjects.Platform;
import gameobjects.Player;
import javax.swing.SwingUtilities;

/* Owns the per-tick game update logic */
public class GameLogicController {

    // --- Dependencies ---
    private final GamePanel gamePanel;
    private final GameStateManager stateManager;
    private final ScoreManager scoreManager;
    private final CollisionSystem collisionSystem;
    private final InputHandler inputHandler;

    // --- Misc state ---
    private int platformDeltaX = 0;

    public GameLogicController(GamePanel gamePanel,
            GameStateManager stateManager,
            ScoreManager scoreManager,
            CollisionSystem collisionSystem,
            InputHandler inputHandler) {
        this.gamePanel = gamePanel;
        this.stateManager = stateManager;
        this.scoreManager = scoreManager;
        this.collisionSystem = collisionSystem;
        this.inputHandler = inputHandler;
    }

    // -------------------------------------------------------------------------
    // Game update
    // -------------------------------------------------------------------------
    public void updateGame() {
        if (stateManager.getState() != GameState.PLAYING)
            return;

        Player player = gamePanel.getPlayer();
        LevelManager levelManager = gamePanel.getLevelManager();
        ui.HUDpane hud = gamePanel.getHud();

        int prevX = player.getX();
        inputHandler.handleHeldKeys(player, levelManager, scoreManager, hud);
        boolean playerMoved = player.getX() != prevX;

        player.update();
        if (levelManager != null)
            levelManager.update();

        checkGameConditions(player, levelManager, hud, playerMoved);
    }

    // -------------------------------------------------------------------------
    // Game conditions
    // -------------------------------------------------------------------------
    private void checkGameConditions(Player player, LevelManager lm, ui.HUDpane hud, boolean playerMoved) {
        if (player == null || lm == null)
            return;

        int livesBefore = player.getLives();
        int coinsBefore = CollisionSystem.getCoinsCollected();

        collisionSystem.checkAll(player, lm.getObstacles(), lm.getPlatforms(), lm.getCoins());

        if (playerMoved)
            collisionSystem.checkCoinsAlongPath(player, lm.getCoins());

        // Coin collection
        int coinsAfter = CollisionSystem.getCoinsCollected();
        if (coinsAfter > coinsBefore) {
            for (int i = 0; i < (coinsAfter - coinsBefore); i++)
                scoreManager.onCoinCollected();
            hud.updateScore(scoreManager.getScore());
        }

        // Player hit by obstacle
        if (player.getLives() < livesBefore) {
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition(player, lm);
        }

        // Platform riding
        checkPlatformRiding(player, lm);

        // Fell into water without a platform
        int playerLane = lm.getLaneIndex(player.getY());
        if (lm.isPlatformLane(playerLane) && !player.isOnPlatform()) {
            player.loseLife();
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition(player, lm);
        }

        // Reached top — level complete
        if (!stateManager.isLevelTransitioning() && playerLane == 0) {
            handleLevelComplete(player, hud);
            return;
        }

        // Fell off screen while riding a platform
        if (player.getX() + player.getWidth() < 0 || player.getX() > gamePanel.getWidth()) {
            player.loseLife();
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition(player, lm);
        }

        // Game over
        if (!player.isAlive())
            SwingUtilities.invokeLater(gamePanel::showGameOver);
    }

    // -------------------------------------------------------------------------
    // Condition helpers
    // -------------------------------------------------------------------------
    private void checkPlatformRiding(Player player, LevelManager lm) {
        platformDeltaX = 0;

        for (Platform p : lm.getPlatforms()) {
            if (p.isActive() && p.isPlayerOn(player)) {
                player.setOnPlatform(true);
                platformDeltaX = p.getDeltaX();
                player.setX(player.getX() + platformDeltaX);
                return;
            }
        }

        player.setOnPlatform(false);
    }

    private void handleLevelComplete(Player player, ui.HUDpane hud) {
        stateManager.setLevelTransitioning(true);
        player.setActive(false);
        scoreManager.onReachedTop(stateManager.getCurrentLevel());
        hud.updateScore(scoreManager.getScore());
        stateManager.incrementLevel();

        int runScore = scoreManager.getTotalScore();
        gamePanel.stopThreads();
        SwingUtilities.invokeLater(() -> gamePanel.showFinalVictory(runScore));
    }

    private void resetPlayerPosition(Player player, LevelManager lm) {
        player.setActive(true);

        int col = lm.getColumnCount() / 2;
        int lane = lm.getLaneCount() - 1;

        int centeredX = lm.getColumnX()[col] + (lm.getColumnWidth() - player.getWidth()) / 2;
        int centeredY = lm.getLaneY()[lane] + (lm.getLaneHeight() - player.getHeight()) / 2;

        player.setPosition(centeredX, centeredY);
        stateManager.setLevelTransitioning(false);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    public int getPlatformDeltaX() {
        return platformDeltaX;
    }
}