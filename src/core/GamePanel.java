package core;

import assets.AssetManager;
import gameobjects.Coin;
import gameobjects.Obstacle;
import gameobjects.Platform;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
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

    private GameMap currentMap;

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
        this.player = selectedPlayer;
        this.state = GameState.PLAYING;
        this.currentMap = map;

        this.levelManager = new LevelManager(getWidth(), getHeight());

        levelManager.loadLevel(currentLevel, currentMap);

        // TODO: replace with proper spawn point from MapSelect once maps are
        // implemented
        player.setPosition(getWidth() / 2, (int) (getHeight() * 0.85f));

        removeAll();
        revalidate();
        repaint();

        logicThread = new GameLogicThread(this);
        renderThread = new RenderThread(this);
        logicThread.start();
        renderThread.start();
    }

    public void updateGame() {
        if (state != GameState.PLAYING)
            return;

        if (player != null)
            player.update();
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

        // water lane death
        int playerLane = levelManager.getLaneIndex(player.getY());
        if (levelManager.isPlatformLane(playerLane)
                && !levelManager.isPlayerOnPlatform(player)) {
            player.loseLife();
            resetPlayerPosition();
        }

        // player reaches top
        if (!levelTransitioning && player.getBounds().y <= (int) (getHeight() * 0.10f)) {
            levelTransitioning = true;
            currentLevel = levelManager.getCurrentLevel() + 1;
            stopThreads();
            showCharacterSelect();
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

        switch (key) {
            case KeyEvent.VK_UP:
                player.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                player.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                player.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                player.setDirection(Direction.RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                player.useAbility();
                break;
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN
                || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            player.move();
            player.setDirection(null);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
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