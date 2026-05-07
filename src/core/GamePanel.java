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
import persistence.LeaderboardManager;
import persistence.ScoreEntry;
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
    private ui.LeaderboardScreen leaderboardScreen;

    private boolean showingLeaderboard = false;
    private int platformDeltaX = 0;

    private long lastUpdateTime = System.currentTimeMillis();

    public GamePanel(GameLauncher launcher) {
        this.launcher = launcher;
        this.assetManager = new AssetManager();

        // game starts at character select
        this.state = GameState.CHARACTER_SELECT;

        setFocusable(true);
        addKeyListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                 // handle game over screen buttons
                if (state == GameState.GAME_OVER && gameOverScreen != null && !showingLeaderboard) {
                    gameOverScreen.isBannerClicked(e.getPoint());

                    // save score when OK is clicked
                    if (gameOverScreen.isOkClicked(e.getPoint())) {
                        String initials = gameOverScreen.getInitials();
                        LeaderboardManager.saveEntry(new ScoreEntry(initials, scoreManager.getScore()));
                        leaderboardScreen = new ui.LeaderboardScreen();
                        showingLeaderboard = true;
                        requestFocusInWindow();
                        repaint();
                    }
                    repaint();
                }

                // handle leaderboard play again button
                if (showingLeaderboard && leaderboardScreen != null) {
                    if (leaderboardScreen.isPlayAgainClicked(e.getPoint())) {
                        showingLeaderboard = false;
                        leaderboardScreen = null;
                        showCharacterSelect();
                    }
                }
            }
        });
        showCharacterSelect();
    }

    // Has back button on first character select
    public void showCharacterSelect() {
        stopThreads();

         // remove resize listeners
        for (ComponentListener cl : getComponentListeners()) {
            removeComponentListener(cl);
        }
        scoreManager = new ScoreManager();
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, () -> launcher.menuGame()), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // character select shown after level completion
    public void showCharacterSelectNextLevel() {
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

     // map selection screen
    public void showMapSelect(Player selectedPlayer) {
        this.state = GameState.MAP_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new MapSelect(this, () -> showCharacterSelect(), selectedPlayer), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // starts the actual gameplay
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
        // resize player slightly smaller than lane for proper visual centering
player.setSize(
        (int) (levelManager.getColumnWidth() * 1.0),
        (int) (levelManager.getLaneHeight() * 1.0)
);

// movement still snaps to full grid size
player.setStepSize(
        levelManager.getColumnWidth(),
        levelManager.getLaneHeight()
);

        // spawn player at bottom center cell
        int spawnCol = levelManager.getColumnCount() / 2;
        int spawnLane = levelManager.getLaneCount() - 1;

        // center player vertically inside lane
        int spawnY =
                levelManager.getLaneY()[spawnLane]
                + (levelManager.getLaneHeight() - player.getHeight()) / 2;

        // center player horizontally inside column
        int spawnX =
                levelManager.getColumnX()[spawnCol]
                + (levelManager.getColumnWidth() - player.getWidth()) / 2;
        
        player.setPosition(spawnX, spawnY);

        removeAll();
        revalidate();
        repaint();

        setLayout(null);

        hud = new ui.HUDpane();
        hud.setBounds(0, 0, 800, 60);
        add(hud);

        // ensure HUD stays on top
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
                int currentCol =
                        Math.round(
                                (float) player.getX()
                                / levelManager.getColumnWidth()
                        );

                // Recompute the grid
                levelManager.resize(getWidth(), getHeight());
                // keep player scaled properly after resize
player.setSize(
        (int) (levelManager.getColumnWidth() * 1.0),
        (int) (levelManager.getLaneHeight() * 1.0)
);

player.setStepSize(
        levelManager.getColumnWidth(),
        levelManager.getLaneHeight()
);

                // Clamp in case grid shrunk
                currentLane = Math.max(0, Math.min(currentLane, levelManager.getLaneCount() - 1));
                currentCol = Math.max(0, Math.min(currentCol, levelManager.getColumnCount() - 1));

                // Snap player back to the same logical cell in the new grid
                // preserve centered vertical alignment after resize
                
                int centeredX =
                        levelManager.getColumnX()[currentCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;

                int centeredY =
                        levelManager.getLaneY()[currentLane]
                        + (levelManager.getLaneHeight() - player.getHeight()) / 2;

                player.setPosition(centeredX, centeredY);
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

        boolean moved = false;

         if (heldKeys.contains(KeyEvent.VK_LEFT)
                || heldKeys.contains(KeyEvent.VK_A)) {

            int currentCol =
                    Math.round(
                            (float) player.getX()
                            / levelManager.getColumnWidth()
                    );

            int targetCol = currentCol - 1;

            if (targetCol >= 0) {

                // center player inside target column
                int centeredX =
                        levelManager.getColumnX()[targetCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;

                player.setPosition(centeredX, player.getY());

                moved = true;
            }
        }
        

        else if (heldKeys.contains(KeyEvent.VK_RIGHT)
                || heldKeys.contains(KeyEvent.VK_D)) {

            int currentCol =
                    Math.round(
                            (float) player.getX()
                            / levelManager.getColumnWidth()
                    );

            int targetCol = currentCol + 1;

            if (targetCol < levelManager.getColumnCount()) {

                // center player inside target column
                int centeredX =
                        levelManager.getColumnX()[targetCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;

                player.setPosition(centeredX, player.getY());

                moved = true;
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_UP)
                || heldKeys.contains(KeyEvent.VK_W)) {

            int lane =
                    levelManager.getLaneIndex(player.getY());

            if (lane > 0) {

                int targetLane = lane - 1;

                // center player vertically
                int centeredY =
                        levelManager.getLaneY()[targetLane]
                        + (levelManager.getLaneHeight() - player.getHeight()) / 2;

                player.setPosition(player.getX(), centeredY);

                // award crossing points
                scoreManager.onPlayerMovedToLane(targetLane);

                moved = true;
            }
        }

        else if (heldKeys.contains(KeyEvent.VK_DOWN)
                || heldKeys.contains(KeyEvent.VK_S)) {

            int lane =
                    levelManager.getLaneIndex(player.getY());

            if (lane < levelManager.getLaneCount() - 1) {

                int targetLane = lane + 1;

                // center player vertically
                int centeredY =
                        levelManager.getLaneY()[targetLane]
                        + (levelManager.getLaneHeight() - player.getHeight()) / 2;

                player.setPosition(player.getX(), centeredY);

                moved = true;
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

            player.setX(player.getX() + platformDeltaX);

            // keep inside screen
            if (player.getX() < 0) {
                player.setX(0);
            }

            if (player.getX() + player.getWidth() > getWidth()) {

                player.setX(
                        getWidth() - player.getWidth()
                );
            }
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
            SwingUtilities.invokeLater(() -> showCharacterSelectNextLevel());
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
        if (hud != null)
            hud.setVisible(false);
        requestFocusInWindow();
        repaint();
    }

    private void resetPlayerPosition() {
        int col = levelManager.getColumnCount() / 2;
        int lane = levelManager.getLaneCount() - 1;

        // respawn player at centered bottom lane

        int centeredX =
                levelManager.getColumnX()[col]
                + (levelManager.getColumnWidth() - player.getWidth()) / 2;
        int centeredY =
                levelManager.getLaneY()[lane]
                + (levelManager.getLaneHeight() - player.getHeight()) / 2;

        player.setPosition(centeredX, centeredY);

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
            boolean handled = gameOverScreen.handleKey(e.getKeyCode(), e.getKeyChar());
            if (!handled) {
                String initials = gameOverScreen.getInitials();
                LeaderboardManager.saveEntry(new ScoreEntry(initials, scoreManager.getScore())); // ← remove this line
                leaderboardScreen = new ui.LeaderboardScreen();
                showingLeaderboard = true;
                requestFocusInWindow();
            }
            repaint();
            
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
    }
}