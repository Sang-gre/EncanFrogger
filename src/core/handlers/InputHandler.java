package core.handlers;

import assets.SoundManager;
import core.GamePanel;
import core.level.LevelManager;
import core.logic.ScoreManager;
import gameobjects.Direction;
import gameobjects.Player;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import ui.HUDpane;

/*  Handles raw keyboard input
    Tracks held keys and processes player movement each game tick */
public class InputHandler implements KeyListener {

    private static final long MOVE_DELAY = 140;
    private final Set<Integer> heldKeys = new HashSet<>();
    private long lastMoveTime = 0;
    
    private final GamePanel gamePanel;
    private final SoundManager sound;

    public InputHandler(GamePanel gamePanel, SoundManager sound) {
        this.gamePanel = gamePanel;
        this.sound = sound;
    }

    // -------------------------------------------------------------------------
    // Movement (called each game tick)
    // -------------------------------------------------------------------------
    public void handleHeldKeys(Player player, LevelManager levelManager, ScoreManager scoreManager, HUDpane hud) {
        // Guard against uninitialized state
        if (player == null || levelManager == null)
            return;

        player.setMovedThisTick(false);

        long now = System.currentTimeMillis();
        if (now - lastMoveTime < MOVE_DELAY)
            return;

        boolean moved = false;

        if (heldKeys.contains(KeyEvent.VK_LEFT) || heldKeys.contains(KeyEvent.VK_A))
            moved = moveHorizontal(player, levelManager, Direction.LEFT);
        else if (heldKeys.contains(KeyEvent.VK_RIGHT) || heldKeys.contains(KeyEvent.VK_D))
            moved = moveHorizontal(player, levelManager, Direction.RIGHT);
        else if (heldKeys.contains(KeyEvent.VK_UP) || heldKeys.contains(KeyEvent.VK_W))
            moved = moveVertical(player, levelManager, scoreManager, Direction.UP);
        else if (heldKeys.contains(KeyEvent.VK_DOWN) || heldKeys.contains(KeyEvent.VK_S))
            moved = moveVertical(player, levelManager, scoreManager, Direction.DOWN);

        if (moved) {
            player.setMovedThisTick(true);
            hud.updateScore(scoreManager.getScore());
            lastMoveTime = now;
        }
    }

    private boolean moveHorizontal(Player player, LevelManager levelManager, Direction dir) {
        player.setLastDirection(dir);

        int currentCol = Math.round((float) player.getX() / levelManager.getColumnWidth());
        int targetCol = currentCol + (dir == Direction.LEFT ? -1 : 1);

        // Check column bounds
        if (targetCol < 0 || targetCol >= levelManager.getColumnCount())
            return false;

        int centeredX = levelManager.getColumnX()[targetCol]
                + (levelManager.getColumnWidth() - player.getWidth()) / 2;
        player.setPosition(centeredX, player.getY());
        sound.play("move");
        return true;
    }

    private boolean moveVertical(Player player, LevelManager levelManager, ScoreManager scoreManager, Direction dir) {
        player.setLastDirection(dir);

        int lane = levelManager.getLaneIndex(player.getY());
        int targetLane = lane + (dir == Direction.UP ? -1 : 1);

        // Check lane bounds
        if (targetLane < 0 || targetLane >= levelManager.getLaneCount())
            return false;

        int centeredY = levelManager.getLaneY()[targetLane]
                + (levelManager.getLaneHeight() - player.getHeight()) / 2;
        player.setPosition(player.getX(), centeredY);

        // Only upward movement triggers lane scoring
        if (dir == Direction.UP)
            scoreManager.onPlayerMovedToLane(targetLane);

        sound.play("move");
        return true;
    }

    // -------------------------------------------------------------------------
    // KeyListener
    // -------------------------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        heldKeys.add(e.getKeyCode());
        gamePanel.onKeyPressed(e); // Forward state-level key events to GamePanel
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) { /* unused */ }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    public Set<Integer> getHeldKeys() {
        return heldKeys;
    }
}