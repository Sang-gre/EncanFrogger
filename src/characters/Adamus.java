package characters;

import assets.AssetManager;
import gameobjects.GameObject;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.*;
import java.awt.image.BufferedImage;
import level.Direction;

/**
 * Adamus player with bridge ability and sprite animation support.
 *
 * Requirements:
 * - AssetManager must have loaded the Adamus spritesheet at assets/spritesheets/adamusSpritesheet.png
 *   with 9 columns and 4 rows (rows = DOWN, LEFT, RIGHT, UP by default).
 * - Attach a KeyListener that calls startMoveLeft/stopMoveLeft and startMoveRight/stopMoveRight.
 * - Ensure the game loop calls update() and repaints the panel regularly.
 */
public class Adamus extends Player {

    // Bridge ability
    private int bridgeLane;
    private int bridgeDuration;
    private int bridgeTimer;
    private static final int COOLDOWN = 60; // ticks

    // Animation state
    private BufferedImage[] frames;      // current direction frames
    private int frameIndex = 0;          // current frame index
    private int frameTimer = 0;          // ticks since last frame advance
    private final int frameDelay = 20;    // ticks per frame (tweak for speed)

    // Movement flags and physics
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean facingBack = false;  // when true, use UP/back animation
    private final int moveSpeed = 4;     // pixels per tick (adjust to your physics)
    protected int vx = 0;                // horizontal velocity
    protected int vy = 0;                // vertical velocity

    public Adamus(int x, int y) {
        super(x, y, PlayerType.ADAMUS);
        this.bridgeLane = -1;
        this.bridgeDuration = 120;
        this.bridgeTimer = 0;

        // Load default (standing/down) frames from AssetManager
        frames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, Direction.DOWN);
        if (frames == null) {
            System.err.println("[Adamus] WARNING: animation frames for ADAMUS not found.");
        }
    }

    // -------------------------
    // Ability
    // -------------------------
    @Override
    public void useAbility() {
        if (isAbilityReady()) {
            createBridge();
            startCooldown(COOLDOWN);
        }
    }

    public void createBridge() {
        // Determine lane from y position (assumes lane height 80)
        bridgeLane = y / 80;
        bridgeTimer = bridgeDuration;
    }

    public boolean isBridgeActive() {
        return bridgeLane >= 0;
    }

    // -------------------------
    // Movement control methods (call from input handler)
    // -------------------------
    public void startMoveLeft() {
        movingLeft = true;
        movingRight = false;
        facingBack = false;
        vx = -moveSpeed;
        BufferedImage[] leftFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, Direction.LEFT);
        if (leftFrames != null) {
            frames = leftFrames;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    public void stopMoveLeft() {
        movingLeft = false;
        vx = 0;
        BufferedImage[] downFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, Direction.DOWN);
        if (downFrames != null) {
            frames = downFrames;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    public void startMoveRight() {
        movingRight = true;
        movingLeft = false;
        facingBack = false;
        vx = moveSpeed;
        BufferedImage[] rightFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, Direction.RIGHT);
        if (rightFrames != null) {
            frames = rightFrames;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    public void stopMoveRight() {
        movingRight = false;
        vx = 0;
        BufferedImage[] downFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, Direction.DOWN);
        if (downFrames != null) {
            frames = downFrames;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    public void setFacingBack(boolean b) {
        facingBack = b;
        if (facingBack) {
            BufferedImage[] backFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, Direction.UP);
            if (backFrames != null) {
                frames = backFrames;
                frameIndex = 0;
                frameTimer = 0;
            }
        } else {
            // revert to standing if no movement
            if (!movingLeft && !movingRight) {
                BufferedImage[] downFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, Direction.DOWN);
                if (downFrames != null) {
                    frames = downFrames;
                    frameIndex = 0;
                    frameTimer = 0;
                }
            }
        }
    }

    // -------------------------
    // Direction selection helper
    // -------------------------
    private Direction chooseDirection() {
        if (facingBack) return Direction.UP;
        if (movingLeft) return Direction.LEFT;
        if (movingRight) return Direction.RIGHT;
        return Direction.DOWN;
    }

    // -------------------------
    // Update and draw
    // -------------------------
    @Override
    public void update() {
        super.update();

        // Update bridge timer
        if (bridgeTimer > 0) {
            bridgeTimer--;
            if (bridgeTimer == 0) {
                bridgeLane = -1;
            }
        }

        // Determine desired direction and load frames for it if needed
        Direction dir = chooseDirection();
        BufferedImage[] desiredFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.ADAMUS, dir);

        if (desiredFrames != null && desiredFrames != frames) {
            frames = desiredFrames;
            frameIndex = 0;
            frameTimer = 0;
        }

        // Advance animation frame
        if (frames != null && frames.length > 0) {
            frameTimer++;
            if (frameTimer >= frameDelay) {
                frameTimer = 0;
                frameIndex = (frameIndex + 1) % frames.length;
            }
        }

        // Optional: clamp or update position using vx/vy if your Player base doesn't already
        x += vx;
        y += vy;
    }

    @Override
    public void draw(Graphics g) {
        // Draw sprite if available
        if (frames != null && frames.length > 0) {
            BufferedImage img = frames[frameIndex];
            g.drawImage(img, x, y, width, height, null);
        } else {
            // fallback placeholder
            g.setColor(new Color(80, 80, 180));
            g.fillRect(x, y, width, height);
        }

        // Draw bridge overlay if active
        if (isBridgeActive()) {
            g.setColor(new Color(80, 80, 180, 120));
            g.fillRect(0, bridgeLane * 80 + 36, 800, 8);
        }
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }

    // -------------------------
    // Getters
    // -------------------------
    public int getBridgeLane()     { return bridgeLane; }
    public int getBridgeDuration() { return bridgeDuration; }
}
