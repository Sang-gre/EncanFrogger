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

    private final GamePanel gamePanel;
    private final GameStateManager stateManager;
    private final ScoreManager scoreManager;
    private final CollisionSystem collisionSystem;
    private final InputHandler inputHandler;
    private final assets.SoundManager sound;

    private int platformDeltaX = 0;

    public GameLogicController(GamePanel gamePanel,
                               GameStateManager stateManager,
                               ScoreManager scoreManager,
                               CollisionSystem collisionSystem,
                               InputHandler inputHandler,
                               assets.SoundManager sound) {
        this.gamePanel = gamePanel;
        this.stateManager = stateManager;
        this.scoreManager = scoreManager;
        this.collisionSystem = collisionSystem;
        this.inputHandler = inputHandler;
        this.sound = sound;
    }

    public void updateGame() {
        if (stateManager.getState() != GameState.PLAYING)
            return;

        Player player = gamePanel.getPlayer();
        LevelManager levelManager = gamePanel.getLevelManager();
        ui.HUDpane hud = gamePanel.getHud();

        inputHandler.handleHeldKeys(player, levelManager, scoreManager, hud);

        if (player != null)
            player.update();
        if (levelManager != null)
            levelManager.update();

        checkGameConditions(player, levelManager, hud);
    }

    private void checkGameConditions(Player player, LevelManager levelManager, ui.HUDpane hud) {
        if (player == null || levelManager == null)
            return;

        int livesBefore = player.getLives();
        int coinsBefore = collisionSystem.getCoinsCollected();

        collisionSystem.checkAll(player,
                levelManager.getObstacles(),
                levelManager.getPlatforms(),
                levelManager.getCoins());
        collisionSystem.checkCoinsAlongPath(player, levelManager.getCoins());

        // Coin collection
        int coinsAfter = collisionSystem.getCoinsCollected();
        if (coinsAfter > coinsBefore) {
            for (int i = 0; i < (coinsAfter - coinsBefore); i++) {
                scoreManager.onCoinCollected();
            }
            hud.updateScore(scoreManager.getScore());
        }

        // Player hit
        if (player.getLives() < livesBefore) {
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition(player, levelManager);
        }

        // Platform riding
        boolean onPlatform = false;
        platformDeltaX = 0;

        for (Platform p : levelManager.getPlatforms()) {
            if (p.isActive() && p.isPlayerOn(player)) {
                onPlatform = true;
                platformDeltaX = p.getDeltaX();
                break;
            }
        }

        if (onPlatform) {
            player.setOnPlatform(true);
            player.setX(player.getX() + platformDeltaX);
        } else {
            player.setOnPlatform(false);
        }

        // Water lane death
        int playerLane = levelManager.getLaneIndex(player.getY());

        if (levelManager.isPlatformLane(playerLane) && !onPlatform) {
            player.loseLife();
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition(player, levelManager);
        }

        // Reached top — win
        if (!stateManager.isLevelTransitioning() && playerLane == 0) {
            stateManager.setLevelTransitioning(true);
            scoreManager.onReachedTop(stateManager.getCurrentLevel());
            hud.updateScore(scoreManager.getScore());
            stateManager.incrementLevel();

            int runScore = scoreManager.getTotalScore();
            gamePanel.stopThreads();
            SwingUtilities.invokeLater(() -> gamePanel.showFinalVictory(runScore));
            return;
        }

        // Fell off screen while on log
        if (player.getX() + player.getWidth() < 0 || player.getX() > gamePanel.getWidth()) {
            player.loseLife();
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition(player, levelManager);
        }

        // Game over
        if (!player.isAlive()) {
            SwingUtilities.invokeLater(gamePanel::showGameOver);
        }
    }

    private void resetPlayerPosition(Player player, LevelManager levelManager) {
        int col = levelManager.getColumnCount() / 2;
        int lane = levelManager.getLaneCount() - 1;

        int centeredX = levelManager.getColumnX()[col]
                + (levelManager.getColumnWidth() - player.getWidth()) / 2;
        int centeredY = levelManager.getLaneY()[lane]
                + (levelManager.getLaneHeight() - player.getHeight()) / 2;

        player.setPosition(centeredX, centeredY);
        stateManager.setLevelTransitioning(false);
    }

    public int getPlatformDeltaX() {
        return platformDeltaX;
    }
}