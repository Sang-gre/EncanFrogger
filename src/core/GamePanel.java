package core;

import assets.SoundManager;
import core.handlers.CollisionSystem;
import core.handlers.GameRenderer;
import core.handlers.InputHandler;
import core.handlers.ScreenNavigator;
import core.level.GameMap;
import core.level.LevelManager;
import core.level.LevelSetup;
import core.logic.GameLogicController;
import core.logic.GameState;
import core.logic.GameStateManager;
import core.logic.ScoreManager;
import gameobjects.Player;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
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
    private final SoundManager sound = SoundManager.getInstance();
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
    private screens.gameplay.PauseScreen pauseScreen;
    private screens.gameplay.GameOverScreen gameOverScreen;
    private screens.menu.LeaderboardScreen leaderboardScreen;
    private screens.gameplay.CongratsScreen congratsScreen;

    // --- Threads ---
    private GameLogicThread logicThread;
    private RenderThread renderThread;

    // --- Misc state ---
    private String playerInitials;

    private final MouseAdapter mouseAdapter;

    public GamePanel(GameLauncher launcher) {
        this.launcher = launcher;

        inputHandler = new InputHandler(this, sound);
        screenNavigator = new ScreenNavigator(this, stateManager, sound, launcher);
        levelSetup = new LevelSetup(this, stateManager, scoreManager, sound, launcher);

        setFocusable(true);
        addKeyListener(inputHandler);

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        };
        addMouseListener(mouseAdapter);
    }

    public void dispose() {
        stopThreads();
        removeKeyListener(inputHandler);
        removeMouseListener(mouseAdapter);

        if (hud != null) {
            hud.setVisible(false);
            hud = null;
        }
        if (pauseScreen != null) {
            pauseScreen.setVisible(false);
            pauseScreen = null;
        }

        if (levelManager != null) {
            levelManager.clear();
            levelManager = null;
        }

        logicController = null;
        player = null;
        collisionSystem = null;
        gameOverScreen = null;
        leaderboardScreen = null;
        congratsScreen = null;
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

    // -------------------------------------------------------------------------
    // Level start (LevelSetup)
    // -------------------------------------------------------------------------
    public void startLevel(Player selectedPlayer, GameMap map, int level) {
        playerInitials = launcher.getPlayerInitials();

        levelSetup.startLevel(selectedPlayer, map, level);
        logicController = new GameLogicController(
                this, stateManager, scoreManager, collisionSystem, inputHandler, sound);
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
        int runCoins = getCoinsCollected();

        saveScoreAsync(new ScoreEntry(playerInitials, runScore, stateManager.getCurrentLevel(), false, runCoins));

        gameOverScreen = new screens.gameplay.GameOverScreen();
        if (hud != null)
            hud.setVisible(false);
        requestFocusInWindow();
        repaint();
    }

    public void showFinalVictory(int runScore) {
        stopThreads();
        stateManager.setState(GameState.WIN);

        int runCoins = getCoinsCollected();

        saveScoreAsync(new ScoreEntry(playerInitials, runScore, stateManager.getCurrentLevel(), true, runCoins));

        int prev = scoreManager.getPreviousScore();
        int total = scoreManager.getTotalScore();

        stateManager.setShowingLeaderboard(false);
        leaderboardScreen = null;

        if (hud != null)
            hud.setVisible(false);

        congratsScreen = new screens.gameplay.CongratsScreen(prev, total, runCoins);
        repaint();
    }

    public void resetGameOverState() {
        stateManager.resetGameOverState();
        leaderboardScreen = null;
        gameOverScreen = null;
        if (levelManager != null) {
            levelManager.clear();
            levelManager = null;
        }
    }

    private void saveScoreAsync(ScoreEntry entry) {
        new Thread(() -> LeaderboardManager.upsertEntry(entry)).start();
    }

    // -------------------------------------------------------------------------
    // Key events (InputHandler)
    // -------------------------------------------------------------------------
    public void onKeyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        GameState state = stateManager.getState();

        if (stateManager.isShowingLeaderboard()) {
            handleLeaderboardKeyPress(key);
            return;
        }

        switch (state) {
            case PLAYING -> handlePlayingKeyPress(key);
            case PAUSED -> handlePausedKeyPress(key);
            case GAME_OVER -> handleGameOverKeyPress(e);
            default -> {}
        }
    }

    private void handleLeaderboardKeyPress(int key) {
        if (leaderboardScreen == null)
            return;
        if (key == KeyEvent.VK_UP)
            leaderboardScreen.scroll(-1);
        if (key == KeyEvent.VK_DOWN)
            leaderboardScreen.scroll(1);
        repaint();
    }

    private void handlePlayingKeyPress(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            stateManager.setState(GameState.PAUSED);
            pauseScreen.setVisible(true);
            pauseScreen.revalidate();
        }
    }

    private void handlePausedKeyPress(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            stateManager.setState(GameState.PLAYING);
            pauseScreen.setVisible(false);
            pauseScreen.revalidate();
        }
    }

    private void handleGameOverKeyPress(KeyEvent e) {
        if (gameOverScreen == null || gameOverScreen.isShowingPlayAgain())
            return;

        boolean handled = gameOverScreen.handleKey(e.getKeyCode(), e.getKeyChar());
        if (!handled) {
            saveScoreAsync(new ScoreEntry(
                    gameOverScreen.getInitials(),
                    scoreManager.getScore(),
                    stateManager.getCurrentLevel(),
                    false,
                    getCoinsCollected()));
            leaderboardScreen = new screens.menu.LeaderboardScreen();
            stateManager.setShowingLeaderboard(true);
            requestFocusInWindow();
        }
        repaint();
    }

    // -------------------------------------------------------------------------
    // Mouse routing
    // -------------------------------------------------------------------------
    private void handleMouseClick(MouseEvent e) {
        if (congratsScreen != null) {
            handleCongratsClick();
            return;
        }

        if (stateManager.isShowingLeaderboard()) {
            handleLeaderboardClick(e);
            return;
        }

        if (stateManager.getState() == GameState.GAME_OVER && gameOverScreen != null) {
            handleGameOverClick(e);
        }
    }

    private void handleCongratsClick() {
        leaderboardScreen = new screens.menu.LeaderboardScreen();
        stateManager.setShowingLeaderboard(true);
        congratsScreen = null;
        repaint();
    }

    private void handleLeaderboardClick(MouseEvent e) {
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

    private void handleGameOverClick(MouseEvent e) {
        if (gameOverScreen.isYesClicked(e.getPoint())) {
            resetGameOverState();
            launcher.showInitialsPanel();
            return;
        }
        if (gameOverScreen.isNoClicked(e.getPoint())) {
            leaderboardScreen = new screens.menu.LeaderboardScreen();
            stateManager.setShowingLeaderboard(true);
            requestFocusInWindow();
            repaint();
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

    public screens.gameplay.PauseScreen getPauseScreen() {
        return pauseScreen;
    }

    public void setPauseScreen(screens.gameplay.PauseScreen pauseScreen) {
        this.pauseScreen = pauseScreen;
    }

    public void setLeaderboardScreen(screens.menu.LeaderboardScreen leaderboardScreen) {
        this.leaderboardScreen = leaderboardScreen;
    }

    public String getPlayerInitials() {
        return playerInitials;
    }

    public void setPlayerInitials(String initials) {
        this.playerInitials = initials;
    }

    private int getCoinsCollected() {
        return CollisionSystem.getCoinsCollected();
    }
}