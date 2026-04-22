package core;

import assets.AssetManager;
import gameobjects.Coin;
import gameobjects.Obstacle;
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
import threads.GameLogicThread;
import threads.RenderThread;

public class GamePanel extends JPanel implements KeyListener {

    private final GameLauncher launcher;
    private final AssetManager assetManager;

    private GameState state;
    private Player player;
    private LevelManager levelManager;
    private CollisionSystem collisionSystem;

    private GameLogicThread logicThread;
    private RenderThread renderThread;

    private boolean levelTransitioning = false;
    private int currentLevel = 1;
    private float platformOffsetAccumulator = 0f;

    private GameMap currentMap;
    private final Set<Integer> heldKeys = new HashSet<>();

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
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, () -> launcher.menuGame()), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Mid-game character selection
    public void showCharacterSelectMidGame() {
        stopThreads();
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, null), BorderLayout.CENTER);
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

        levelManager.loadLevel(currentLevel, currentMap);

        // resize player to grid cell
        player.setSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());
        player.setStepSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());

        // spawn player at bottom center cell
        int spawnCol = levelManager.getColumnCount() / 2;
        int spawnLane = levelManager.getLaneCount() - 1;
        int[] colX = levelManager.getColumnX();
        int[] laneY = levelManager.getLaneY();

        int x = colX[spawnCol] + (levelManager.getColumnWidth() - player.getWidth()) / 2;
        int y = laneY[spawnLane] + (levelManager.getLaneHeight() - player.getHeight()) / 2;

        y += 25;
        player.setPosition(x, y);

        removeAll();
        revalidate();
        repaint();

        // handle window resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (levelManager != null && player != null) {
                    levelManager.resize(getWidth(), getHeight());
                    player.setSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());
                    player.setStepSize(levelManager.getColumnWidth(), levelManager.getLaneHeight());
                }
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

        if (player != null) {
            player.update();
        }
        if (levelManager != null)
            levelManager.update();

        checkGameConditions();
    }

    private void checkGameConditions() {
        if (player == null || levelManager == null)
            return;

        collisionSystem.checkAll(player, levelManager.getObstacles(),
                levelManager.getPlatforms(),
                levelManager.getCoins());

        // move player along with platform if standing on one
        boolean onPlatform = false;
        for (Platform p : levelManager.getPlatforms()) {
            if (p.isActive() && p.isPlayerOn(player)) {
                onPlatform = true;
                player.setX(player.getX() + p.getDeltaX());
                break;
            }
        }

        if (!onPlatform) {
            platformOffsetAccumulator = 0f;
        }

        // water lane death — only if in platform zone but not on a log
        int playerLane = levelManager.getLaneIndex(player.getY());
        if (levelManager.isPlatformLane(playerLane) && !onPlatform) {
            player.loseLife();
            resetPlayerPosition();
        }

        // player reaches top lane
        if (!levelTransitioning && playerLane == 0) {
            levelTransitioning = true;
            currentLevel = levelManager.getCurrentLevel() + 1;
            stopThreads();
            showCharacterSelect();
        }

        // player falls off side of screen while on log
        if (player.getX() + player.getWidth() < 0 || player.getX() > getWidth()) {
            player.loseLife();
            resetPlayerPosition();
        }

        // game over
        if (!player.isAlive()) {
            state = GameState.GAME_OVER;
        }
    }

    private void resetPlayerPosition() {
        player.setPosition(getWidth() / 2, (int) (getHeight() * 0.85f));
        levelTransitioning = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (state == GameState.PLAYING && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PAUSED;
            return;
        }

        if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PLAYING;
            return;
        }

        if (state != GameState.PLAYING || player == null)
            return;

        int px = player.getX();
        int py = player.getY();
        int stepX = levelManager.getColumnWidth();
        int stepY = levelManager.getLaneHeight();

        switch (key) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (py - stepY >= 0) {
                    player.setDirection(Direction.UP);
                    player.move();
                    player.setDirection(null);
                }
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (py + stepY + player.getHeight() <= getHeight()) {
                    player.setDirection(Direction.DOWN);
                    player.move();
                    player.setDirection(null);
                }
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (px - stepX >= 0) {
                    player.setDirection(Direction.LEFT);
                    player.move();
                    player.setDirection(null);
                }
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (px + stepX + player.getWidth() <= getWidth()) {
                    player.setDirection(Direction.RIGHT);
                    player.move();
                    player.setDirection(null);
                }
                break;
            case KeyEvent.VK_SPACE:
                player.useAbility();
                break;
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

        if (levelManager != null) {
            levelManager.resize(getWidth(), getHeight());
            levelManager.draw(g, getWidth(), getHeight());
        }

        if (player != null) {
            player.draw(g);
        }
    }
}