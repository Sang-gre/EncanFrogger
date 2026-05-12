package core;

import assets.SoundManager;
import gameobjects.Platform;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import level.Direction;
import level.LevelManager;
import main.GameLauncher;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;
import threads.GameLogicThread;
import threads.RenderThread;

public class GamePanel extends JPanel implements KeyListener {

    private final GameLauncher launcher;

    private GameState state;
    private Player player;
    private LevelManager levelManager;
    private CollisionSystem collisionSystem;
    private ScoreManager scoreManager = new ScoreManager();
    private SoundManager sound = new SoundManager();
    private ui.PauseScreen pauseScreen;

    private GameLogicThread logicThread;
    private RenderThread renderThread;

    private boolean levelTransitioning = false;
    private int currentLevel = 1;
    private int selectedLevel = 1;

    private GameMap currentMap;
    private final Set<Integer> heldKeys = new HashSet<>();

    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 140;
    private ui.HUDpane hud;
    private ui.GameOverScreen gameOverScreen;
    private ui.LeaderboardScreen leaderboardScreen;
    private ui.CongratsScreen congratsScreen;

    private boolean showingLeaderboard = false;
    private int platformDeltaX = 0;

    private String playerInitials;
    private boolean playerIsAlive = true;
    private boolean freshStart = true;
    private int coins = new CollisionSystem().getCoinsCollected();

    private int previousScore = 0;
    private int totalScore = 0;


    private final Thread leaderboardWorker = new Thread();

    public GamePanel(GameLauncher launcher) {
        this.launcher = launcher;
        this.state = GameState.CHARACTER_SELECT;

        setFocusable(true);
        addKeyListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (congratsScreen != null) {
                    leaderboardScreen = new ui.LeaderboardScreen();
                    showingLeaderboard = true;
                    congratsScreen = null;
                    repaint();
                    return;
                }

                if (state == GameState.GAME_OVER && gameOverScreen != null && !showingLeaderboard) {
                    if (gameOverScreen.isYesClicked(e.getPoint())) {
                        resetGameOverState();
                        launcher.showInitialsPanel();
                        return;
                    }
                    if (gameOverScreen.isNoClicked(e.getPoint())) {
                        leaderboardScreen = new ui.LeaderboardScreen();
                        showingLeaderboard = true;
                        requestFocusInWindow();
                        repaint();
                        return;
                    }
                }

                if (leaderboardScreen != null) {
                    if (leaderboardScreen.isPlayAgainClicked(e.getPoint())) {
                        showingLeaderboard = false;
                        leaderboardScreen = null;
                        resetGameOverState();
                        if (player != null) {
                            showMapSelect(player); // now valid
                        }
                        return;
                    }
                    if (leaderboardScreen.isBackClicked(e.getPoint())) {
                        resetGameOverState();
                        launcher.showMainMenu();
                    }
                }
            }
        });
    }

   
    public void showMapSelect(Player selectedPlayer) {
        showMapSelect(selectedPlayer, currentMap != null ? currentMap : GameMap.LIREO);
    }

    // map selection screen
    public void showMapSelect(Player selectedPlayer, GameMap map) {
        this.state = GameState.MAP_SELECT;
        this.currentMap = map;

        removeAll();
        setLayout(new BorderLayout());
        add(new MapSelect(this, () -> showLevelSelect(selectedPlayer, map), selectedPlayer), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

        // Show character select at the start of a new run
    public void showCharacterSelect() {
        stopThreads();

        // remove resize listeners
        for (ComponentListener cl : getComponentListeners()) {
            removeComponentListener(cl);
        }

        scoreManager = new ScoreManager();
        currentLevel = 1;
        playerIsAlive = true;
        this.state = GameState.CHARACTER_SELECT;

        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, () -> launcher.showMainMenu()), BorderLayout.CENTER);

        revalidate();
        repaint();
        freshStart = true;
    }

    // Show character select after completing a level
    public void showCharacterSelectNextLevel() {
        stopThreads();
        sound.stopBGM();
        sound.playBGM("menu");

        for (ComponentListener cl : getComponentListeners()) {
            removeComponentListener(cl);
        }

        this.state = GameState.CHARACTER_SELECT;

        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, () -> launcher.showMainMenu()), BorderLayout.CENTER);

        revalidate();
        repaint();
        freshStart = false;
    }


    // starts the actual gameplay
    public void startLevel(Player selectedPlayer, GameMap map, int level) {
        currentLevel = freshStart ? launcher.getStartingLevel() : level;
        if (freshStart) {
            scoreManager.setScore(launcher.getStartingScore());
        }

        playerIsAlive = true;
        playerInitials = launcher.getPlayerInitials();
        scoreManager.resetCrossing();

        new Thread(() -> LeaderboardManager.upsertEntry(
            new ScoreEntry(playerInitials, scoreManager.getScore(), currentLevel, true, coins)
        )).start();

        this.levelTransitioning = false;
        this.player = selectedPlayer;
        this.state = GameState.PLAYING;
        this.currentMap = map;

        sound.stopBGM();
        sound.playBGM("game");

        this.levelManager = new LevelManager(getWidth(), getHeight());
        this.collisionSystem = new CollisionSystem();
        levelManager.loadLevel(currentLevel, currentMap);

        // spawn player at bottom center cell
        int spawnCol = levelManager.getColumnCount() / 2;
        int spawnLane = levelManager.getLaneCount() - 1;
        int spawnY = levelManager.getLaneY()[spawnLane] + (levelManager.getLaneHeight() - selectedPlayer.getHeight()) / 2;
        int spawnX = levelManager.getColumnX()[spawnCol] + (levelManager.getColumnWidth() - selectedPlayer.getWidth()) / 2;
        player.setPosition(spawnX, spawnY);

        removeAll();
        revalidate();
        repaint();
        setLayout(null);

        hud = new ui.HUDpane(() -> {
            if (state == GameState.PLAYING) {
                state = GameState.PAUSED;
                pauseScreen.setVisible(true);
                pauseScreen.revalidate();
            } else if (state == GameState.PAUSED) {
                enterGameplay(player, currentMap, selectedLevel);
                pauseScreen.setVisible(false);
                pauseScreen.revalidate();
            }
            requestFocusInWindow();
        });
        int hudHeight = getHeight() / 9;
        hud.setBounds(0, 0, getWidth(), hudHeight);
        hud.revalidate();
        add(hud);
        setComponentZOrder(hud, 0);

        hud.updateScore(scoreManager.getScore());
        hud.updateLives(player.getLives());

        pauseScreen = new ui.PauseScreen(
            () -> { setState(GameState.PLAYING); pauseScreen.setVisible(false); pauseScreen.revalidate(); requestFocusInWindow(); },
            () -> { stopThreads(); sound.stopBGM(); launcher.showInstructions(true); },
            () -> { stopThreads(); sound.stopBGM(); launcher.showMainMenu(); }
        );
        pauseScreen.setBounds(0, 0, getWidth(), getHeight());
        pauseScreen.setVisible(false);
        pauseScreen.revalidate();
        add(pauseScreen);
        setComponentZOrder(pauseScreen, 0);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (state == GameState.PLAYING) {
                    state = GameState.PAUSED;
                    pauseScreen.setVisible(true);
                    pauseScreen.revalidate();
                }
                if (levelManager == null || player == null) return;
                int currentLane = levelManager.getLaneIndex(player.getY());
                int currentCol = Math.round((float) player.getX() / levelManager.getColumnWidth());
                levelManager.resize(getWidth(), getHeight());
                player.setSize((int)(levelManager.getColumnWidth() * 0.5), (int)(levelManager.getLaneHeight() * 0.5));
                player.setVisualSize(levelManager.getColumnWidth(), (int)(levelManager.getLaneHeight() * 1.8));
                currentLane = Math.max(0, Math.min(currentLane, levelManager.getLaneCount() - 1));
                currentCol = Math.max(0, Math.min(currentCol, levelManager.getColumnCount() - 1));
                int centeredX = levelManager.getColumnX()[currentCol] + (levelManager.getColumnWidth() - player.getWidth()) / 2;
                int centeredY = levelManager.getLaneY()[currentLane] + (levelManager.getLaneHeight() - player.getHeight()) / 2;
                player.setPosition(centeredX, centeredY);
                int hudHeight = getHeight() / 10;
                hud.setBounds(0, 0, getWidth(), hudHeight);
                hud.revalidate();
                pauseScreen.setBounds(0, 0, getWidth(), getHeight());
            }
        });

        logicThread = new GameLogicThread(this);
        renderThread = new RenderThread(this);
        logicThread.start();
        renderThread.start();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public void updateGame() {
        if (state != GameState.PLAYING)
            return;

        // compute delta time for the countdown timer
        long now = System.currentTimeMillis();

        handleHeldKeys();

        if (player != null)
            player.update();
        if (levelManager != null)
            levelManager.update();

        checkGameConditions();
    }

    private void handleHeldKeys() {
        player.setMovedThisTick(false);

        if (player == null || levelManager == null)
            return;

        long now = System.currentTimeMillis();

        if (now - lastMoveTime < MOVE_DELAY)
            return;

        boolean moved = false;

        if (heldKeys.contains(KeyEvent.VK_LEFT) || heldKeys.contains(KeyEvent.VK_A)) {
            player.setLastDirection(Direction.LEFT);
            int currentCol = Math.round(
                    (float) player.getX()
                            / levelManager.getColumnWidth());

            int targetCol = currentCol - 1;

            if (targetCol >= 0) {

                // center player inside target column
                int centeredX = levelManager.getColumnX()[targetCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;

                player.setPosition(centeredX, player.getY());

                moved = true;
                sound.play("move");
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_RIGHT) || heldKeys.contains(KeyEvent.VK_D)) {
            player.setLastDirection(Direction.RIGHT);
            int currentCol = Math.round(
                    (float) player.getX()
                            / levelManager.getColumnWidth());

            int targetCol = currentCol + 1;

            if (targetCol < levelManager.getColumnCount()) {

                // center player inside target column
                int centeredX = levelManager.getColumnX()[targetCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;

                player.setPosition(centeredX, player.getY());

                moved = true;
                sound.play("move");
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_UP) || heldKeys.contains(KeyEvent.VK_W)) {
            player.setLastDirection(Direction.UP);
            int lane = levelManager.getLaneIndex(player.getY());

            if (lane > 0) {

                int targetLane = lane - 1;

                // center player vertically
                int centeredY = levelManager.getLaneY()[targetLane]
                        + (levelManager.getLaneHeight() - player.getHeight()) / 2;

                player.setPosition(player.getX(), centeredY);

                // award crossing points
                scoreManager.onPlayerMovedToLane(targetLane);

                moved = true;
                sound.play("move");
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_DOWN) || heldKeys.contains(KeyEvent.VK_S)) {
            player.setLastDirection(Direction.DOWN);
            int lane = levelManager.getLaneIndex(player.getY());

            if (lane < levelManager.getLaneCount() - 1) {

                int targetLane = lane + 1;

                // center player vertically
                int centeredY = levelManager.getLaneY()[targetLane]
                        + (levelManager.getLaneHeight() - player.getHeight()) / 2;

                player.setPosition(player.getX(), centeredY);

                moved = true;
                sound.play("move");
            }
        }

        if (moved) {
            player.setMovedThisTick(true);
            hud.updateScore(scoreManager.getScore());
            lastMoveTime = now;
        }
    }


    private void checkGameConditions() {
        if (player == null || levelManager == null)
            return;

        int livesBefore = player.getLives();
        int coinsBefore = collisionSystem.getCoinsCollected();
        System.out.println(coinsBefore);

        collisionSystem.checkAll(player,
                levelManager.getObstacles(),
                levelManager.getPlatforms(),
                levelManager.getCoins());

        // update score if a new coin was collected
        int coinsAfter = collisionSystem.getCoinsCollected();
        System.out.println(coinsAfter);

        if (coinsAfter > coinsBefore) {
            for (int i = 0; i < (coinsAfter - coinsBefore); i++) {
                scoreManager.onCoinCollected();
            }
            hud.updateScore(scoreManager.getScore());
        }

        // update lives HUD if player was hit
        if (player.getLives() < livesBefore) {
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition();
        }

        // move player along with platform
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

        // water lane death — only if in platform zone but not on a log
        int playerLane = levelManager.getLaneIndex(player.getY());

        if (levelManager.isPlatformLane(playerLane) && !onPlatform) {
            player.loseLife();
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition();
        }

        // player reaches top lane — award completion bonus then advance
        if (!levelTransitioning && playerLane == 0) {
            levelTransitioning = true;

            scoreManager.onReachedTop(currentLevel);
            hud.updateScore(scoreManager.getScore());

            // --- Track scores ---
            previousScore = scoreManager.getScore();
            totalScore += previousScore;

            stopThreads();

            SwingUtilities.invokeLater(() -> {
                showFinalVictory();
            });

            return;
        }


        // player falls off side of screen while on log
        if (player.getX() + player.getWidth() < 0 || player.getX() > getWidth()) {
            player.loseLife();
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition();
        }

        // game over
        if (!player.isAlive()) {
            SwingUtilities.invokeLater(this::showGameOver);
        }
    }

    public void showGameOver() {
        stopThreads();
        sound.play("gameover");
        state = GameState.GAME_OVER;
        playerIsAlive = false;
        new Thread(() -> LeaderboardManager
                .upsertEntry(new ScoreEntry(playerInitials, scoreManager.getScore(), currentLevel, false, coins))).start();
        gameOverScreen = new ui.GameOverScreen();
        if (hud != null)
            hud.setVisible(false);
        requestFocusInWindow();
        repaint();
    }

    private void resetPlayerPosition() {
        int col = levelManager.getColumnCount() / 2;
        int lane = levelManager.getLaneCount() - 1;

        // respawn player at centered bottom lane

        int centeredX = levelManager.getColumnX()[col]
                + (levelManager.getColumnWidth() - player.getWidth()) / 2;
        int centeredY = levelManager.getLaneY()[lane]
                + (levelManager.getLaneHeight() - player.getHeight()) / 2;

        player.setPosition(centeredX, centeredY);

        levelTransitioning = false;
    }

    public void resetGameOverState() {
        showingLeaderboard = false;
        leaderboardScreen = null;
        gameOverScreen = null;
        levelManager = null;
        
        state = GameState.CHARACTER_SELECT;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        heldKeys.add(e.getKeyCode());

        int key = e.getKeyCode();

        if (state == GameState.PLAYING && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PAUSED;
            pauseScreen.setVisible(true);
            pauseScreen.revalidate();
            return;
        }

        if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
            setState(GameState.PLAYING);
            pauseScreen.setVisible(false);
            pauseScreen.revalidate();
            return;
        }

        if (state == GameState.GAME_OVER && showingLeaderboard && leaderboardScreen != null) {
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

        if (state == GameState.GAME_OVER && gameOverScreen != null && !showingLeaderboard) {
            if (!gameOverScreen.isShowingPlayAgain()) {
                boolean handled = gameOverScreen.handleKey(e.getKeyCode(), e.getKeyChar());
                if (!handled) {
                    new Thread(() -> LeaderboardManager
                            .upsertEntry(new ScoreEntry(gameOverScreen.getInitials(), scoreManager.getScore(),
                                    currentLevel, false, coins)))
                            .start();
                    leaderboardScreen = new ui.LeaderboardScreen();
                    showingLeaderboard = true;
                    requestFocusInWindow();
                }
                repaint();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
    }

    public void showLevelSelect(Player player, GameMap map) {

    stopThreads();
    state = GameState.LEVEL_SELECT;

    removeAll();
    setLayout(new BorderLayout());

    add(new LevelSelect(
            this,
            () -> showMapSelect(player),
            player,
            map
    ), BorderLayout.CENTER);

    revalidate();
    repaint();
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

    

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
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

    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (levelManager != null)
            levelManager.draw(g, getWidth(), getHeight());

        // draw player normally since movement is already grid-aligned
        if (player != null) {
            player.draw(g);
        }

        // Game over overlay
        if (state == GameState.GAME_OVER && gameOverScreen != null && !showingLeaderboard) {
            gameOverScreen.draw(g, getWidth(), getHeight());
        }

        // Leaderboard overlay
        if (showingLeaderboard && leaderboardScreen != null) {
            leaderboardScreen.draw(g, getWidth(), getHeight());
        }

        // Pause overlay
        if (state == GameState.PAUSED && pauseScreen != null) {
            pauseScreen.draw(g, getWidth(), getHeight());
        }

        if (congratsScreen != null) {
            congratsScreen.draw(g, getWidth(), getHeight());
}
    }

    private void showFinalVictory() {
        stopThreads();
        state = GameState.WIN;

        showingLeaderboard = false;
        leaderboardScreen = null;

        congratsScreen = new ui.CongratsScreen(true);

        repaint();
    }


    public void setSelectedLevel(int level) {
        this.selectedLevel = level;
}
    public void resumeFromInstructions() {
        sound.playBGM("game");
        logicThread = new GameLogicThread(this);
        renderThread = new RenderThread(this);
        logicThread.start();
        renderThread.start();
        pauseScreen.setVisible(true);
        state = GameState.PAUSED;
        requestFocusInWindow();
    }
    public void enterGameplay(Player player, GameMap map, int level) {
    startLevel(player, map, level);
}
}