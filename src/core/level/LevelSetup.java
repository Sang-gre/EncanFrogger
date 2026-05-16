package core.level;

import core.GamePanel;
import core.handlers.CollisionSystem;
import core.logic.GameState;
import core.logic.GameStateManager;
import core.logic.ScoreManager;
import gameobjects.Player;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingUtilities;
import main.GameLauncher;

/* Responsible for everything on start level */
public class LevelSetup {

    // --- Dependencies ---
    private final GamePanel gamePanel;
    private final GameStateManager stateManager;
    private final ScoreManager scoreManager;
    private final assets.SoundManager sound;
    private final GameLauncher launcher;

    // --- Misc state ---
    private ComponentAdapter resizeListener;

    public LevelSetup(GamePanel gamePanel,
            GameStateManager stateManager,
            ScoreManager scoreManager,
            assets.SoundManager sound,
            GameLauncher launcher) {
        this.gamePanel = gamePanel;
        this.stateManager = stateManager;
        this.scoreManager = scoreManager;
        this.sound = sound;
        this.launcher = launcher;
    }

    // -------------------------------------------------------------------------
    // Level start
    // -------------------------------------------------------------------------
    public void startLevel(Player selectedPlayer, GameMap map, int level) {
        if (stateManager.isFreshStart()) {
            stateManager.setCurrentLevel(launcher.getStartingLevel());
            scoreManager.setTotalScore(launcher.getStartingScore());
            stateManager.setFreshStart(false);
        }

        stateManager.setPlayerAlive(true);
        stateManager.setLevelTransitioning(false);
        stateManager.setState(GameState.PLAYING);

        gamePanel.setPlayer(selectedPlayer);
        selectedPlayer.resetLives();
        scoreManager.resetCrossing();

        sound.stopBGM();
        sound.playBGM("game");

        initLevelManager(map);
        spawnPlayer(selectedPlayer, gamePanel.getLevelManager());
        buildUI(selectedPlayer);

        gamePanel.startThreads();
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    // -------------------------------------------------------------------------
    // Setup helpers
    // -------------------------------------------------------------------------
    private void initLevelManager(GameMap map) {
        LevelManager old = gamePanel.getLevelManager();
        if (old != null)
            old.clear();

        LevelManager levelManager = new LevelManager(gamePanel.getWidth(), gamePanel.getHeight());
        levelManager.loadLevel(stateManager.getCurrentLevel(), map);

        gamePanel.setLevelManager(levelManager);
        gamePanel.setCollisionSystem(new CollisionSystem());
    }

    private void spawnPlayer(Player player, LevelManager lm) {
        player.setActive(true);
        int spawnCol = lm.getColumnCount() / 2;
        int spawnLane = lm.getLaneCount() - 1;

        int spawnX = lm.getColumnX()[spawnCol] + (lm.getColumnWidth() - player.getWidth()) / 2;
        int spawnY = lm.getLaneY()[spawnLane] + (lm.getLaneHeight() - player.getHeight()) / 2;

        player.setPosition(spawnX, spawnY);
        player.resize(lm.getLaneHeight(), lm.getColumnWidth());
    }

    private void buildUI(Player selectedPlayer) {
        gamePanel.setHud(null);
        gamePanel.setPauseScreen(null);
        gamePanel.removeAll();
        gamePanel.revalidate();
        gamePanel.repaint();
        gamePanel.setLayout(null);

        buildHUD(selectedPlayer);
        buildPauseScreen();
        attachResizeListener(selectedPlayer);
    }

    private void buildHUD(Player player) {
        ui.HUDpane hud = new ui.HUDpane(() -> {
            if (stateManager.getState() == GameState.PLAYING) {
                stateManager.setState(GameState.PAUSED);
                gamePanel.getPauseScreen().setVisible(true);
                gamePanel.getPauseScreen().revalidate();
            } else if (stateManager.getState() == GameState.PAUSED) {
                gamePanel.startLevel(player, stateManager.getCurrentMap(), stateManager.getSelectedLevel());
                gamePanel.getPauseScreen().setVisible(false);
                gamePanel.getPauseScreen().revalidate();
            }
            gamePanel.requestFocusInWindow();
        });

        int hudHeight = gamePanel.getHeight() / 9;
        hud.setBounds(0, 0, gamePanel.getWidth(), hudHeight);
        hud.revalidate();
        hud.updateScore(0);
        hud.updateLives(player.getLives());

        gamePanel.add(hud);
        gamePanel.setComponentZOrder(hud, 0);
        gamePanel.setHud(hud);
    }

    private void buildPauseScreen() {
        screens.gameplay.PauseScreen pauseScreen = new screens.gameplay.PauseScreen(
                () -> {
                    stateManager.setState(GameState.PLAYING);
                    gamePanel.getPauseScreen().setVisible(false);
                    gamePanel.getPauseScreen().revalidate();
                    gamePanel.requestFocusInWindow();
                },
                () -> {
                    gamePanel.stopThreads();
                    sound.stopBGM();
                    launcher.showInstructions(true);
                },
                () -> {
                    gamePanel.stopThreads();
                    sound.stopBGM();
                    launcher.showMainMenu();
                });

        pauseScreen.setBounds(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
        pauseScreen.setVisible(false);
        pauseScreen.revalidate();

        gamePanel.add(pauseScreen);
        gamePanel.setComponentZOrder(pauseScreen, 0);
        gamePanel.setPauseScreen(pauseScreen);
    }

    // -------------------------------------------------------------------------
    // Resize listener
    // -------------------------------------------------------------------------
    private void attachResizeListener(Player player) {
        if (resizeListener != null)
            gamePanel.removeComponentListener(resizeListener);

        resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LevelManager lm = gamePanel.getLevelManager();

                // Pause immediately on resize
                if (stateManager.getState() == GameState.PLAYING) {
                    stateManager.setState(GameState.PAUSED);
                    gamePanel.getPauseScreen().setVisible(true);
                    gamePanel.getPauseScreen().revalidate();
                }

                if (lm == null || player == null)
                    return;

                // Remember where the player was before resize
                int lane = lm.getLaneIndex(player.getY());
                int col = Math.round((float) player.getX() / lm.getColumnWidth());

                lm.resize(gamePanel.getWidth(), gamePanel.getHeight());

                // Clamp to valid range after resize
                lane = Math.max(0, Math.min(lane, lm.getLaneCount() - 1));
                col = Math.max(0, Math.min(col, lm.getColumnCount() - 1));

                // Reposition player in the same lane/column
                int centeredX = lm.getColumnX()[col] + (lm.getColumnWidth() - player.getWidth()) / 2;
                int centeredY = lm.getLaneY()[lane] + (lm.getLaneHeight() - player.getHeight()) / 2;
                player.setPosition(centeredX, centeredY);
                player.setSize(
                        (int) (lm.getColumnWidth() * 0.5),
                        (int) (lm.getLaneHeight() * 0.5));
                player.setVisualSize(lm.getColumnWidth(), (int) (lm.getLaneHeight() * 1.8));
                player.resize(lm.getLaneHeight(), lm.getColumnWidth());

                // Resize UI overlays
                gamePanel.getHud().setBounds(0, 0, gamePanel.getWidth(), gamePanel.getHeight() / 10);
                gamePanel.getHud().revalidate();
                gamePanel.getPauseScreen().setBounds(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
            }
        };

        gamePanel.addComponentListener(resizeListener);
    }
}