package core;

import assets.SoundManager;
import gameobjects.Player;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import level.Direction;
import level.LevelManager;

/* Handles key inputs */
public class InputHandler implements KeyListener {
    private final Set<Integer> heldKeys = new HashSet<>();
    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 140;

    private final GamePanel gamePanel;
    private final SoundManager sound;

    public InputHandler(GamePanel gamePanel, SoundManager sound) {
        this.gamePanel = gamePanel;
        this.sound = sound;
    }

    public void handleHeldKeys(Player player, LevelManager levelManager, ScoreManager scoreManager, ui.HUDpane hud) {
        player.setMovedThisTick(false);

        if (player == null || levelManager == null)
            return;

        long now = System.currentTimeMillis();

        if (now - lastMoveTime < MOVE_DELAY)
            return;

        boolean moved = false;

        if (heldKeys.contains(KeyEvent.VK_LEFT) || heldKeys.contains(KeyEvent.VK_A)) {
            player.setLastDirection(Direction.LEFT);
            int currentCol = Math.round((float) player.getX() / levelManager.getColumnWidth());
            int targetCol = currentCol - 1;

            if (targetCol >= 0) {
                int centeredX = levelManager.getColumnX()[targetCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;
                player.setPosition(centeredX, player.getY());
                moved = true;
                sound.play("move");
            }

        } else if (heldKeys.contains(KeyEvent.VK_RIGHT) || heldKeys.contains(KeyEvent.VK_D)) {
            player.setLastDirection(Direction.RIGHT);
            int currentCol = Math.round((float) player.getX() / levelManager.getColumnWidth());
            int targetCol = currentCol + 1;

            if (targetCol < levelManager.getColumnCount()) {
                int centeredX = levelManager.getColumnX()[targetCol]
                        + (levelManager.getColumnWidth() - player.getWidth()) / 2;
                player.setPosition(centeredX, player.getY());
                moved = true;
                sound.play("move");
            }

        } else if (heldKeys.contains(KeyEvent.VK_UP) || heldKeys.contains(KeyEvent.VK_W)) {
            player.setLastDirection(Direction.UP);
            int lane = levelManager.getLaneIndex(player.getY());

            if (lane > 0) {
                int targetLane = lane - 1;
                int centeredY = levelManager.getLaneY()[targetLane]
                        + (levelManager.getLaneHeight() - player.getHeight()) / 2;
                player.setPosition(player.getX(), centeredY);
                scoreManager.onPlayerMovedToLane(targetLane);
                moved = true;
                sound.play("move");
            }

        } else if (heldKeys.contains(KeyEvent.VK_DOWN) || heldKeys.contains(KeyEvent.VK_S)) {
            player.setLastDirection(Direction.DOWN);
            int lane = levelManager.getLaneIndex(player.getY());

            if (lane < levelManager.getLaneCount() - 1) {
                int targetLane = lane + 1;
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

    @Override
    public void keyPressed(KeyEvent e) {
        heldKeys.add(e.getKeyCode());
        gamePanel.onKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public Set<Integer> getHeldKeys() {
        return heldKeys;
    }
}