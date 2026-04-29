package core;

import assets.AssetManager;
import gameobjects.Platform;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
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

    private GameLogicThread logicThread;
    private RenderThread renderThread;

    private boolean levelTransitioning = false;
    private int currentLevel = 1;

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

        int x = colX[spawnCol];
        int y = laneY[spawnLane];
        player.setPosition(x, y);

        removeAll();
        revalidate();
        repaint();

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
        int livesBefore = player.getLives();

        collisionSystem.checkAll(player, levelManager.getObstacles(),
                levelManager.getPlatforms(),
                levelManager.getCoins());
        
        if (player.getLives() < livesBefore) {
            resetPlayerPosition();
        }

        // move player along with platform
        boolean onPlatform = false;
        for (Platform p : levelManager.getPlatforms()) {
            if (p.isActive() && p.isPlayerOn(player)) {
                onPlatform = true;
                player.setX(player.getX() + p.getDeltaX());
                break;
            }
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
        int spawnCol = levelManager.getColumnCount() / 2;
        int spawnLane = levelManager.getLaneCount() - 1;
        int x = levelManager.getColumnX()[spawnCol];
        int y = levelManager.getLaneY()[spawnLane];
        player.setPosition(x, y);
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

        switch (key) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                int currentLaneUp = levelManager.getLaneIndex(player.getY());
                if (currentLaneUp > 0) {
                    player.setPosition(player.getX(), levelManager.getLaneY()[currentLaneUp - 1]);
                }
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                int currentLaneDown = levelManager.getLaneIndex(player.getY());
                if (currentLaneDown < levelManager.getLaneCount() - 1) {
                    player.setPosition(player.getX(), levelManager.getLaneY()[currentLaneDown + 1]);
                }
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (player.getX() - levelManager.getColumnWidth() >= 0) {
                    player.setPosition(player.getX() - levelManager.getColumnWidth(), player.getY());
                }
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (player.getX() + levelManager.getColumnWidth() + player.getWidth() <= getWidth()) {
                    player.setPosition(player.getX() + levelManager.getColumnWidth(), player.getY());
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
            levelManager.draw(g, getWidth(), getHeight());

            // To be removed
            g.setColor(Color.YELLOW);
            int[] laneY = levelManager.getLaneY();
            int laneH = levelManager.getLaneHeight();
            for (int i = 0; i < laneY.length; i++) {
                g.drawLine(0, laneY[i], getWidth(), laneY[i]);
                g.drawString("Lane " + i, 5, laneY[i] + 15);
            }
            g.drawLine(0, laneY[laneY.length - 1] + laneH,
                    getWidth(), laneY[laneY.length - 1] + laneH);
        }

        if (player != null) {
            player.draw(g);
        }
    }
}