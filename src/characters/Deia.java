package characters;

import assets.AssetManager;
import gameobjects.GameObject;
import gameobjects.Obstacle;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.*;
import java.awt.image.BufferedImage;
import level.Direction;

public class Deia extends Player {

    // Ability state
    private int phaseCount;
    private boolean windShield;
    private static final int COOLDOWN = 60;

    // Animation state
    private BufferedImage[] frames;
    private int frameIndex = 0;
    private int frameTimer = 0;
    private int frameDelay = 20; // ticks per frame

    // Movement flags
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean facingBack = false;
    private final int moveSpeed = 4;
    protected int vx = 0;
    protected int vy = 0;

    public Deia(int x, int y) {
        super(x, y, PlayerType.DEIA);
        this.phaseCount = 3;
        this.windShield = false;

        // Load default frames
        frames = AssetManager.getPlayerAnimation(PlayerType.DEIA, Direction.DOWN);
        if (frames == null) {
            System.err.println("[Deia] WARNING: animation frames not found.");
        }
    }

    @Override
    public void useAbility() {
        if (isAbilityReady() && phaseCount > 0) {
            phaseThrough();
            startCooldown(COOLDOWN);
        }
    }

    public void phaseThrough() {
        windShield = true;
        phaseCount--;
    }

    public boolean isPhasing() {
        return windShield;
    }

    // Movement control
    public void startMoveLeft() {
        movingLeft = true; movingRight = false; facingBack = false;
        vx = -moveSpeed;
        BufferedImage[] left = AssetManager.getPlayerAnimation(PlayerType.DEIA, Direction.LEFT);
        if (left != null) { frames = left; frameIndex = 0; frameTimer = 0; }
    }

    public void stopMoveLeft() {
        movingLeft = false; vx = 0;
        BufferedImage[] down = AssetManager.getPlayerAnimation(PlayerType.DEIA, Direction.DOWN);
        if (down != null) { frames = down; frameIndex = 0; frameTimer = 0; }
    }

    public void startMoveRight() {
        movingRight = true; movingLeft = false; facingBack = false;
        vx = moveSpeed;
        BufferedImage[] right = AssetManager.getPlayerAnimation(PlayerType.DEIA, Direction.RIGHT);
        if (right != null) { frames = right; frameIndex = 0; frameTimer = 0; }
    }

    public void stopMoveRight() {
        movingRight = false; vx = 0;
        BufferedImage[] down = AssetManager.getPlayerAnimation(PlayerType.DEIA, Direction.DOWN);
        if (down != null) { frames = down; frameIndex = 0; frameTimer = 0; }
    }

    public void setFacingBack(boolean b) {
        facingBack = b;
        if (facingBack) {
            BufferedImage[] back = AssetManager.getPlayerAnimation(PlayerType.DEIA, Direction.UP);
            if (back != null) { frames = back; frameIndex = 0; frameTimer = 0; }
        } else if (!movingLeft && !movingRight) {
            BufferedImage[] down = AssetManager.getPlayerAnimation(PlayerType.DEIA, Direction.DOWN);
            if (down != null) { frames = down; frameIndex = 0; frameTimer = 0; }
        }
    }

    private Direction chooseDirection() {
        if (facingBack) return Direction.UP;
        if (movingLeft) return Direction.LEFT;
        if (movingRight) return Direction.RIGHT;
        return Direction.DOWN;
    }

    @Override
    public void update() {
        super.update();

        // Animation update
        Direction dir = chooseDirection();
        BufferedImage[] desired = AssetManager.getPlayerAnimation(PlayerType.DEIA, dir);
        if (desired != null && desired != frames) {
            frames = desired;
            frameIndex = 0;
            frameTimer = 0;
        }

        if (frames != null && frames.length > 0) {
            frameTimer++;
            if (frameTimer >= frameDelay) {
                frameTimer = 0;
                frameIndex = (frameIndex + 1) % frames.length;
            }
        }

        x += vx;
        y += vy;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Composite original = g2d.getComposite();
        if (windShield) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }

        if (frames != null && frames.length > 0) {
            BufferedImage img = frames[frameIndex];
            g2d.drawImage(img, x, y, width, height, null);
        } else {
            g2d.setColor(new Color(160, 80, 200));
            g2d.fillRect(x, y, width, height);
        }

        g2d.setComposite(original);
    }

    @Override
    public void onCollide(GameObject other) {
        if (other instanceof Obstacle && windShield) {
            windShield = false;
        } else {
            super.onCollide(other);
        }
    }

    public int getPhaseCount() { return phaseCount; }
    public boolean isWindShield() { return windShield; }
}
