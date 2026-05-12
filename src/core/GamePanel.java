package core;

import assets.SoundManager;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import level.LevelManager;
import main.GameLauncher;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;
import threads.GameLogicThread;
import threads.RenderThread;

public class GamePanel extends JPanel {

    private final GameLauncher launcher;

    // --- Managers ---
    private final GameStateManager stateManager = new GameStateManager();
    private final ScoreManager scoreManager = new ScoreManager();
    private final SoundManager sound = new SoundManager();
    private final GameRenderer renderer = new GameRenderer(stateManager);

    private final InputHandler inputHandler;
    private GameLogicController logicController;
    private final LevelSetup levelSetup;
    private final ScreenNavigator screenNavigator;

    // --- Game objects ---
    private Player player;
    private LevelManager levelManager;
    private CollisionSystem collisionSystem;

    // --- UI overlays ---
    private ui.HUDpane hud;
    private ui.PauseScreen pauseScreen;
    private ui.GameOverScreen gameOverScreen;
    private ui.LeaderboardScreen leaderboardScreen;
    private ui.CongratsScreen congratsScreen;

    // --- Threads ---
    private GameLogicThread logicThread;
    private RenderThread renderThread;

    // --- Misc state ---
    private String playerInitials;

    public GamePanel(GameLauncher launcher) {
        this.launcher = launcher;

        inputHandler = new InputHandler(this, sound);
        screenNavigator = new ScreenNavigator(this, stateManager, sound, launcher);
        levelSetup = new LevelSetup(this, stateManager, scoreManager, sound, launcher);

        setFocusable(true);
        addKeyListener(inputHandler);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Screen navigation (ScreenNavigator)
    // -------------------------------------------------------------------------

    public void showMapSelect(Player selectedPlayer) {
        screenNavigator.showMapSelect(selectedPlayer);
    }

    public void showMapSelect(Player selectedPlayer, GameMap map) {
        screenNavigator.showMapSelect(selectedPlayer, map);
    }

    public void showCharacterSelect() {
        scoreManager.reset();
        screenNavigator.showCharacterSelect();
    }

    public void showCharacterSelectNextLevel() {
        screenNavigator.showCharacterSelectNextLevel();
    }

    public void showLevelSelect(Player player, GameMap map) {
        screenNavigator.showLevelSelect(player, map);
    }

    // -------------------------------------------------------------------------
    // Level start (LevelSetup)
    // -------------------------------------------------------------------------

    public void startLevel(Player selectedPlayer, GameMap map, int level) {
        playerInitials = launcher.getPlayerInitials();

        levelSetup.startLevel(selectedPlayer, map, level);

        logicController = new GameLogicController(
                this, stateManager, scoreManager, collisionSystem, inputHandler, sound);
    }

    public void enterGameplay(Player player, GameMap map, int level) {
        startLevel(player, map, level);
    }

    // -------------------------------------------------------------------------
    // Game loop (GameLogicThread)
    // -------------------------------------------------------------------------

    public void updateGame() {
        if (logicController != null)
            logicController.updateGame();
    }

    // -------------------------------------------------------------------------
    // Win / lose
    // -------------------------------------------------------------------------

    public void showGameOver() {
        stopThreads();
        sound.play("gameover");
        stateManager.setState(GameState.GAME_OVER);
        stateManager.setPlayerAlive(false);

        int runScore = scoreManager.getScore();
        int runCoins = collisionSystem != null ? collisionSystem.getCoinsCollected() : 0;

        new Thread(() -> LeaderboardManager.upsertEntry(
                new ScoreEntry(playerInitials, runScore, stateManager.getCurrentLevel(), false, runCoins))).start();

        gameOverScreen = new ui.GameOverScreen();
        if (hud != null)
            hud.setVisible(false);
        requestFocusInWindow();
        repaint();
    }

    public void showFinalVictory(int runScore) {
        stopThreads();
        stateManager.setState(GameState.WIN);

        int runCoins = collisionSystem != null ? collisionSystem.getCoinsCollected() : 0;

        new Thread(() -> LeaderboardManager.upsertEntry(
                new ScoreEntry(playerInitials, runScore, stateManager.getCurrentLevel(), true, runCoins))).start();

        int prev = scoreManager.getPreviousScore();
        int total = scoreManager.getTotalScore();

        stateManager.setShowingLeaderboard(false);
        leaderboardScreen = null;

        if (hud != null)
            hud.setVisible(false);

        congratsScreen = new ui.CongratsScreen(prev, total, runCoins);
        repaint();
    }

    public void resetGameOverState() {
        stateManager.resetGameOverState();
        leaderboardScreen = null;
        gameOverScreen = null;
        levelManager = null;
    }

    // -------------------------------------------------------------------------
    // Key events (InputHandler)
    // -------------------------------------------------------------------------

    public void onKeyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        GameState state = stateManager.getState();

        if (state == GameState.PLAYING && key == KeyEvent.VK_ESCAPE) {
            stateManager.setState(GameState.PAUSED);
            pauseScreen.setVisible(true);
            pauseScreen.revalidate();
            return;
        }

        if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
            stateManager.setState(GameState.PLAYING);
            pauseScreen.setVisible(false);
            pauseScreen.revalidate();
            return;
        }

        if (state == GameState.GAME_OVER && stateManager.isShowingLeaderboard() && leaderboardScreen != null) {
            if (key == KeyEvent.VK_UP) {
                leaderboardScreen.scroll(-1);
                repaint();
                return;
            }
            if (key == KeyEvent.VK_DOWN) {
                leaderboardScreen.scroll(1);
                repaint();
                return;
            }
        }

        if (state == GameState.GAME_OVER && gameOverScreen != null && !stateManager.isShowingLeaderboard()) {
            if (!gameOverScreen.isShowingPlayAgain()) {
                boolean handled = gameOverScreen.handleKey(e.getKeyCode(), e.getKeyChar());
                if (!handled) {
                    int coins = collisionSystem != null ? collisionSystem.getCoinsCollected() : 0;
                    new Thread(() -> LeaderboardManager.upsertEntry(
                            new ScoreEntry(gameOverScreen.getInitials(), scoreManager.getScore(),
                                    stateManager.getCurrentLevel(), false, coins)))
                            .start();
                    leaderboardScreen = new ui.LeaderboardScreen();
                    stateManager.setShowingLeaderboard(true);
                    requestFocusInWindow();
                }
                repaint();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Mouse routing
    // -------------------------------------------------------------------------

    private void handleMouseClick(MouseEvent e) {
        if (congratsScreen != null) {
            leaderboardScreen = new ui.LeaderboardScreen();
            stateManager.setShowingLeaderboard(true);
            congratsScreen = null;
            repaint();
            return;
        }

        if (stateManager.getState() == GameState.GAME_OVER
                && gameOverScreen != null
                && !stateManager.isShowingLeaderboard()) {
            if (gameOverScreen.isYesClicked(e.getPoint())) {
                resetGameOverState();
                launcher.showInitialsPanel();
                return;
            }
            if (gameOverScreen.isNoClicked(e.getPoint())) {
                leaderboardScreen = new ui.LeaderboardScreen();
                stateManager.setShowingLeaderboard(true);
                requestFocusInWindow();
                repaint();
                return;
            }
        }

        if (leaderboardScreen != null) {
            if (leaderboardScreen.isPlayAgainClicked(e.getPoint())) {
                stateManager.setShowingLeaderboard(false);
                leaderboardScreen = null;
                resetGameOverState();
                if (!stateManager.isPlayerAlive()) {
                    launcher.showInitialsPanel();
                } else {
                    if (player != null)
                        showMapSelect(player);
                }
                return;
            }
            if (leaderboardScreen.isBackClicked(e.getPoint())) {
                resetGameOverState();
                launcher.showMainMenu();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g, getWidth(), getHeight(),
                levelManager, player,
                gameOverScreen, leaderboardScreen,
                pauseScreen, congratsScreen);
    }

    // -------------------------------------------------------------------------
    // Thread management
    // -------------------------------------------------------------------------

    public void startThreads() {
        logicThread = new GameLogicThread(this);
        renderThread = new RenderThread(this);
        logicThread.start();
        renderThread.start();
    }

    public void stopThreads() {
        if (logicThread != null) {
            logicThread.stopThread();
            logicThread.interrupt();
            logicThread = null;
        }
        if (renderThread != null) {
            renderThread.stopThread();
            renderThread.interrupt();
            renderThread = null;
        }
    }

    public void resumeFromInstructions() {
        sound.playBGM("game");
        startThreads();
        pauseScreen.setVisible(true);
        stateManager.setState(GameState.PAUSED);
        requestFocusInWindow();
    }

    public void setSelectedLevel(int level) {
        stateManager.setSelectedLevel(level);
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public GameState getState() {
        return stateManager.getState();
    }

    public void setState(GameState state) {
        stateManager.setState(state);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    public void setCollisionSystem(CollisionSystem collisionSystem) {
        this.collisionSystem = collisionSystem;
    }

    public ui.HUDpane getHud() {
        return hud;
    }

    public void setHud(ui.HUDpane hud) {
        this.hud = hud;
    }

    public ui.PauseScreen getPauseScreen() {
        return pauseScreen;
    }

    public void setPauseScreen(ui.PauseScreen pauseScreen) {
        this.pauseScreen = pauseScreen;
    }

    public void setLeaderboardScreen(ui.LeaderboardScreen leaderboardScreen) {
        this.leaderboardScreen = leaderboardScreen;
    }

    public String getPlayerInitials() {
        return playerInitials;
    }

    public void setPlayerInitials(String initials) {
        this.playerInitials = initials;
    }
}