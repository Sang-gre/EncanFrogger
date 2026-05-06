package core;

import assets.AssetManager;
import gameobjects.Platform;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import level.LevelManager;
import main.GameLauncher;
import threads.GameLogicThread;
import threads.RenderThread;

public class GamePanel extends JPanel implements KeyListener {

    private final GameLauncher launcher;
    private final AssetManager assetManager;

    private GameState state;
    private Player player;
    private LevelManager levelManager;
    private CollisionSystem collisionSystem;
    private ScoreManager scoreManager = new ScoreManager();

    private GameLogicThread logicThread;
    private RenderThread renderThread;

    private boolean levelTransitioning = false;
    private int currentLevel = 1;

    private GameMap currentMap;
    private final Set<Integer> heldKeys = new HashSet<>();

    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 140;
    private ui.HUDpane hud;
    private ui.GameOverScreen gameOverScreen;

    private int platformDeltaX = 0;

    // for delta time calculation fed into the timer
    private long lastUpdateTime = System.currentTimeMillis();

    public GamePanel(GameLauncher launcher) {
        this.launcher = launcher;
        this.assetManager = new AssetManager();
        this.state = GameState.CHARACTER_SELECT;

        setFocusable(true);
        addKeyListener(this);

        showCharacterSelect();
    }

    // Has back button on first character select
    public void showCharacterSelect() {
        stopThreads();
        for (ComponentListener cl : getComponentListeners()) {
            removeComponentListener(cl);
        }
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, () -> launcher.menuGame()), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showMapSelect(Player selectedPlayer) {
        this.state = GameState.MAP_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new MapSelect(this, () -> showCharacterSelect(), selectedPlayer), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void startLevel(Player selectedPlayer, GameMap map) {
        this.levelTransitioning = false;
        this.player = selectedPlayer;
        this.state = GameState.PLAYING;
        this.currentMap = map;

        this.levelManager = new LevelManager(getWidth(), getHeight());
        this.collisionSystem = new CollisionSystem();
        scoreManager.resetCrossing();

        levelManager.loadLevel(currentLevel, currentMap);

        // resize player to grid cell
        player.setSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());
        player.setStepSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());

        // spawn player at bottom center cell
        int spawnCol = levelManager.getColumnCount() / 2;
        int spawnLane = levelManager.getLaneCount() - 1;
        // int[] colX = levelManager.getColumnX();
        // int[] laneY = levelManager.getLaneY();

        // int x = colX[spawnCol];
        // int y = laneY[spawnLane];
        player.setPosition(
                levelManager.getColumnX()[spawnCol],
                levelManager.getLaneY()[spawnLane]);

        removeAll();
        revalidate();
        repaint();

        setLayout(null);

        hud = new ui.HUDpane();
        hud.setBounds(0, 0, 800, 60);
        add(hud);
        setComponentZOrder(hud, 0);

        // sync HUD with starting state
        hud.updateScore(scoreManager.getScore());
        hud.updateLives(player.getLives());
        lastUpdateTime = System.currentTimeMillis();

        // handle window resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (levelManager == null || player == null)
                    return;

                // Remember which lane and column the player is on BEFORE resize
                int currentLane = levelManager.getLaneIndex(player.getY());
                int currentCol = player.getX() / levelManager.getColumnWidth();

                // Recompute the grid
                levelManager.resize(getWidth(), getHeight());
                player.setSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());
                player.setStepSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());

                // Clamp in case grid shrunk
                currentLane = Math.max(0, Math.min(currentLane, levelManager.getLaneCount() - 1));
                currentCol = Math.max(0, Math.min(currentCol, levelManager.getColumnCount() - 1));

                // Snap player back to the same logical cell in the new grid
                player.setPosition(
                        levelManager.getColumnX()[currentCol],
                        levelManager.getLaneY()[currentLane]);
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
        float delta = (now - lastUpdateTime) / 1000f;
        lastUpdateTime = now;

        // time's up — treat like a death
        if (scoreManager.isTimeUp()) {
            player.loseLife();
            hud.updateLives(player.getLives());
            scoreManager.onPlayerDied();
            resetPlayerPosition();

            if (!player.isAlive()) {
                SwingUtilities.invokeLater(this::showGameOver);
                return;
            }
        }

        handleHeldKeys();

        if (player != null)
            player.update();
        if (levelManager != null)
            levelManager.update();

        checkGameConditions();
    }

    private void handleHeldKeys() {
        if (player == null || levelManager == null)
            return;

        long now = System.currentTimeMillis();

        if (now - lastMoveTime < MOVE_DELAY)
            return;

        int stepX = levelManager.getColumnWidth();

        boolean moved = false;

        if (heldKeys.contains(KeyEvent.VK_LEFT) || heldKeys.contains(KeyEvent.VK_A)) {
            if (player.getX() - stepX >= 0) {
                player.setPosition(player.getX() - stepX, player.getY());
                moved = true;
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_RIGHT) || heldKeys.contains(KeyEvent.VK_D)) {
            if (player.getX() + stepX + player.getWidth() <= getWidth()) {
                player.setPosition(player.getX() + stepX, player.getY());
                moved = true;
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_UP) || heldKeys.contains(KeyEvent.VK_W)) {
            int lane = levelManager.getLaneIndex(player.getY());
            if (lane > 0) {
                player.setPosition(player.getX(), levelManager.getLaneY()[lane - 1]);
                scoreManager.onPlayerMovedToLane(lane - 1); // award hop points if new furthest lane
                moved = true;
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_DOWN) || heldKeys.contains(KeyEvent.VK_S)) {
            int lane = levelManager.getLaneIndex(player.getY());
            if (lane < levelManager.getLaneCount() - 1) {
                player.setPosition(player.getX(), levelManager.getLaneY()[lane + 1]);
                moved = true; // no points for moving backward
            }
        }

        if (moved) {
            hud.updateScore(scoreManager.getScore());
            lastMoveTime = now;
        }
    }

    private void checkGameConditions() {
        if (player == null || levelManager == null)
            return;

        int livesBefore = player.getLives();
        int coinsBefore = collisionSystem.getCoinsCollected();

        collisionSystem.checkAll(player,
                levelManager.getObstacles(),
                levelManager.getPlatforms(),
                levelManager.getCoins());

        // update score if a new coin was collected
        int coinsAfter = collisionSystem.getCoinsCollected();
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
            player.setPosition(player.getX() + platformDeltaX, player.getY());
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
            currentLevel++;
            stopThreads();
            SwingUtilities.invokeLater(() -> showCharacterSelect());
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
        state = GameState.GAME_OVER;
        gameOverScreen = new ui.GameOverScreen();

        // Enable mouse clicks for OK button
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (state == GameState.GAME_OVER && gameOverScreen != null) {
                    gameOverScreen.isBannerClicked(e.getPoint()); // ← ADD: clicking banner activates typing
                    if (gameOverScreen.isOkClicked(e.getPoint())) {
                        // TODO: save initials + score
                        launcher.menuGame();
                    }
                    repaint(); // ← ADD: so banner swaps to blank immediately on click
                }
            }
        });

        repaint();
    }

    private void resetPlayerPosition() {
        int col = levelManager.getColumnCount() / 2;
        int lane = levelManager.getLaneCount() - 1;

        player.setPosition(
                levelManager.getColumnX()[col],
                levelManager.getLaneY()[lane]);

        levelTransitioning = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        heldKeys.add(e.getKeyCode());

        int key = e.getKeyCode();

        if (state == GameState.PLAYING && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PAUSED;
            return;
        }

        if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PLAYING;
            return;
        }

        if (key == KeyEvent.VK_SPACE && player != null) {
            player.useAbility();
        }

        if (state == GameState.GAME_OVER && gameOverScreen != null) {
            boolean handled = gameOverScreen.handleKey(e.getKeyCode(), e.getKeyChar());
            if (!handled) {
                // ENTER was pressed — confirm
                launcher.menuGame();
            }
            repaint();
            return;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
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

    public AssetManager getAssetManager() {
        return assetManager;
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

        if (player != null) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform old = g2.getTransform();
            g2.translate(
                    Math.round(player.getX()) - player.getX(),
                    Math.round(player.getY()) - player.getY());
            player.draw(g2);
            g2.setTransform(old);
        }

        // Draw game over overlay on top of everything
        if (state == GameState.GAME_OVER && gameOverScreen != null) {
            gameOverScreen.draw(g, getWidth(), getHeight());
        }
    }
}