package core;

import assets.AssetManager;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.GameLauncher;
import threads.GameLogicThread;
import threads.RenderThread;

public class GamePanel extends JPanel implements KeyListener {

    private final GameLauncher launcher;
    private final AssetManager assetManager;

    private GameState state;
    private Player player;

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

    public void showCharacterSelect() {
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, () -> launcher.menuGame()), BorderLayout.CENTER); // add BorderLayout.CENTER
        revalidate();
        repaint();
    }

    public void showCharacterSelectMidGame() {
        this.state = GameState.CHARACTER_SELECT;
        removeAll();
        setLayout(new BorderLayout());
        add(new CharacterSelect(this, null), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void startLevel(Player selectedPlayer) {
        this.player = selectedPlayer;
        this.state  = GameState.PLAYING;

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


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (state == GameState.PLAYING && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PLAYING;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}


    public GameState getState()           { return state; }
    public void setState(GameState state) { this.state = state; }
    public Player getPlayer()             { return player; }
    public void setPlayer(Player player)  { this.player = player; }
    public AssetManager getAssetManager() { return assetManager; }
}