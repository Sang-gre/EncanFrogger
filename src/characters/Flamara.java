package characters;

import assets.AssetManager;
import gameobjects.GameObject;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.*;
import java.awt.image.BufferedImage;
import level.Direction;

public class Flamara extends Player {

    // Ability state
    private float dashSpeed;
    private int dashDuration;
    private int dashTimer;
    private float normalSpeed;
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

    public Flamara(int x, int y) {
        super(x, y, PlayerType.FLAMARA);
        this.dashSpeed = 20.0f;
        this.dashDuration = 10;
        this.normalSpeed = speed;
        this.dashTimer = 0;

        // Load default frames
        frames = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, Direction.DOWN);
        if (frames == null) {
            System.err.println("[Flammara] WARNING: animation frames not found.");
        }
    }

    @Override
    public void useAbility() {
        if (isAbilityReady()) {
            dashForward();
            startCooldown(COOLDOWN);
        }
    }

    public void dashForward() {
        speed = dashSpeed;
        dashTimer = dashDuration;
        // Dash forward twice in current direction
        if (getDirection() != null) {
            move();
            move();
        }
    }

    // Movement control
    public void startMoveLeft() {
        movingLeft = true; movingRight = false; facingBack = false;
        vx = -moveSpeed;
        BufferedImage[] left = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, Direction.LEFT);
        if (left != null) { frames = left; frameIndex = 0; frameTimer = 0; }
    }

    public void stopMoveLeft() {
        movingLeft = false; vx = 0;
        BufferedImage[] down = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, Direction.DOWN);
        if (down != null) { frames = down; frameIndex = 0; frameTimer = 0; }
    }

    public void startMoveRight() {
        movingRight = true; movingLeft = false; facingBack = false;
        vx = moveSpeed;
        BufferedImage[] right = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, Direction.RIGHT);
        if (right != null) { frames = right; frameIndex = 0; frameTimer = 0; }
    }

    public void stopMoveRight() {
        movingRight = false; vx = 0;
        BufferedImage[] down = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, Direction.DOWN);
        if (down != null) { frames = down; frameIndex = 0; frameTimer = 0; }
    }

    public void setFacingBack(boolean b) {
        facingBack = b;
        if (facingBack) {
            BufferedImage[] back = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, Direction.UP);
            if (back != null) { frames = back; frameIndex = 0; frameTimer = 0; }
        } else if (!movingLeft && !movingRight) {
            BufferedImage[] down = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, Direction.DOWN);
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

        // Dash timer
        if (dashTimer > 0) {
            dashTimer--;
            if (dashTimer == 0) {
                speed = normalSpeed;
            }
        }

        // Animation update
        Direction dir = chooseDirection();
        BufferedImage[] desired = AssetManager.getInstance().getPlayerAnimation(PlayerType.FLAMARA, dir);
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
        if (frames != null && frames.length > 0) {
            BufferedImage img = frames[frameIndex];
            g.drawImage(img, x, y, width, height, null);
        } else {
            g.setColor(new Color(220, 80, 20));
            g.fillRect(x, y, width, height);
        }
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }

    public float getDashSpeed() { return dashSpeed; }
    public int getDashDuration() { return dashDuration; }
}
