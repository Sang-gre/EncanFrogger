package core;

import assets.AssetManager;
import gameobjects.Coin;
import gameobjects.Direction;
import gameobjects.Obstacle;
import gameobjects.Player;
import level.LevelManager;
import main.GameLauncher;
import threads.GameLogicThread;
import threads.RenderThread;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements KeyListener {

    private final GameLauncher launcher;
    private final AssetManager assetManager;

    private GameState state;
    private Player player;
    private LevelManager    levelManager;
    private CollisionSystem collisionSystem;

    private GameLogicThread logicThread;
    private RenderThread renderThread;

    public GamePanel(GameLauncher launcher) {
        this.launcher     = launcher;
        this.assetManager = new AssetManager();
        this.state        = GameState.CHARACTER_SELECT;

        setFocusable(true);
        addKeyListener(this);

        showCharacterSelect();
    }

    // Has back button pagfirst character select palang
    public void showCharacterSelect() {
        stopThreads();
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, () -> launcher.menuGame()), BorderLayout.CENTER); // add BorderLayout.CENTER
        revalidate();
        repaint();
    }

    // Idkk, for mid game na character selection kasi where would the back button go??
    // Or idk what if save state?? will figure it out later
    public void showCharacterSelectMidGame() {
        stopThreads();
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, null), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showMap() {
        // implement
    }

    public void startLevel(Player selectedPlayer) {
        this.player = selectedPlayer;
        this.state  = GameState.PLAYING;

        this.levelManager    = new LevelManager();
        this.collisionSystem = new CollisionSystem();

        // Level Manager loads level here
        
        removeAll();
        revalidate();
        repaint();

        logicThread = new GameLogicThread(this);
        renderThread = new RenderThread(this);
        logicThread.start();
        renderThread.start();
    }

    public void updateGame() {
        if (player != null) {
            player.update();
        }
    }

    private void checkGameConditions() {
        // implement
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (state == GameState.PLAYING && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PAUSED;
        }
        
        if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PLAYING;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public void stopThreads() {
        if (logicThread != null) {
            logicThread.interrupt();
            logicThread = null;
        }
        if (renderThread != null) {
            renderThread.interrupt();
            renderThread = null;
        }
    }

    public GameState getState()           { return state; }
    public void setState(GameState state) { this.state = state; }
    public Player getPlayer()             { return player; }
    public void setPlayer(Player player)  { this.player = player; }
    public AssetManager getAssetManager() { return assetManager; }
    public LevelManager   getLevelManager()        { return levelManager; }
    public CollisionSystem getCollisionSystem()    { return collisionSystem; }
}