package core;

import gameobjects.Player;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingUtilities;
import level.LevelManager;
import main.GameLauncher;

/* Responsible for everything on start level */
public class LevelSetup {

    private final GamePanel gamePanel;
    private final GameStateManager stateManager;
    private final ScoreManager scoreManager;
    private final assets.SoundManager sound;
    private final GameLauncher launcher;

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

    public void startLevel(Player selectedPlayer, GameMap map, int level) {
        if (stateManager.isFreshStart()) {
            stateManager.setCurrentLevel(launcher.getStartingLevel());
            scoreManager.setScore(launcher.getStartingScore());
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

        LevelManager levelManager = new LevelManager(gamePanel.getWidth(), gamePanel.getHeight());
        levelManager.loadLevel(stateManager.getCurrentLevel(), map);
        gamePanel.setLevelManager(levelManager);
        gamePanel.setCollisionSystem(new CollisionSystem());

        spawnPlayer(selectedPlayer, levelManager);

        gamePanel.removeAll();
        gamePanel.revalidate();
        gamePanel.repaint();
        gamePanel.setLayout(null);

        buildHUD(selectedPlayer);
        buildPauseScreen(selectedPlayer, map);
        attachResizeListener(selectedPlayer);

        gamePanel.startThreads();
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    private void spawnPlayer(Player player, LevelManager levelManager) {
        int spawnCol = levelManager.getColumnCount() / 2;
        int spawnLane = levelManager.getLaneCount() - 1;
        int spawnY = levelManager.getLaneY()[spawnLane]
                + (levelManager.getLaneHeight() - player.getHeight()) / 2;
        int spawnX = levelManager.getColumnX()[spawnCol]
                + (levelManager.getColumnWidth() - player.getWidth()) / 2;
        player.setPosition(spawnX, spawnY);
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
        hud.updateScore(scoreManager.getScore());
        hud.updateLives(player.getLives());

        gamePanel.add(hud);
        gamePanel.setComponentZOrder(hud, 0);
        gamePanel.setHud(hud);
    }

    private void buildPauseScreen(Player selectedPlayer, GameMap map) {
        ui.PauseScreen pauseScreen = new ui.PauseScreen(
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

    private void attachResizeListener(Player player) {
        gamePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                LevelManager levelManager = gamePanel.getLevelManager();

                if (stateManager.getState() == GameState.PLAYING) {
                    stateManager.setState(GameState.PAUSED);
                    gamePanel.getPauseScreen().setVisible(true);
                    gamePanel.getPauseScreen().revalidate();
                }

                if (levelManager == null || player == null)
                    return;

                int currentLane = levelManager.getLaneIndex(player.getY());
                int currentCol = Math.round((float) player.getX() / levelManager.getColumnWidth());

                levelManager.resize(gamePanel.getWidth(), gamePanel.getHeight());
                player.setSize(
                        (int) (levelManager.getColumnWidth() * 0.5),
                        (int) (levelManager.getLaneHeight() * 0.5));
                player.setVisualSize(levelManager.getColumnWidth(), (int) (levelManager.getLaneHeight() * 1.8));

                currentLane = Math.max(0, Math.min(currentLane, levelManager.getLaneCount() - 1));
                currentCol = Math.max(0, Math.min(currentCol, levelManager.getColumnCount() - 1));

                int centeredX = levelManager.getColumnX()[currentCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;
                int centeredY = levelManager.getLaneY()[currentLane]
                        + (levelManager.getLaneHeight() - player.getHeight()) / 2;
                player.setPosition(centeredX, centeredY);

                int hudHeight = gamePanel.getHeight() / 10;
                gamePanel.getHud().setBounds(0, 0, gamePanel.getWidth(), hudHeight);
                gamePanel.getHud().revalidate();
                gamePanel.getPauseScreen().setBounds(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
            }
        });
    }
}