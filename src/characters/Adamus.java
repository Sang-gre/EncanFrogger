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
 * - AssetManager must have loaded the Adamus spritesheet at
 * assets/spritesheets/adamusSpritesheet.png
 * with 9 columns and 4 rows (rows = DOWN, LEFT, RIGHT, UP by default).
 * - Attach a KeyListener that calls startMoveLeft/stopMoveLeft and
 * startMoveRight/stopMoveRight.
 * - Ensure the game loop calls update() and repaints the panel regularly.
 */
public class Adamus extends Player {

    // Bridge ability
    private int bridgeLane;
    private int bridgeDuration;
    private int bridgeTimer;
    private static final int COOLDOWN = 60; // ticks

    // Animation state
    private BufferedImage[] frames; // current direction frames
    private int frameIndex = 0; // current frame index
    private int frameTimer = 0; // ticks since last frame advance
    private final int frameDelay = 10; // ticks per frame (tweak for speed)

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
    // Update and draw
    // -------------------------
    @Override
    public void update() {
        super.update();

        Direction dir = getLastDirection();
        BufferedImage[] desiredFrames = AssetManager.getInstance()
                .getPlayerAnimation(PlayerType.ADAMUS, dir);

        if (desiredFrames != null && desiredFrames != frames) {
            frames = desiredFrames;
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
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }

    // -------------------------
    // Getters
    // -------------------------
    public int getBridgeLane() {
        return bridgeLane;
    }

    public int getBridgeDuration() {
        return bridgeDuration;
    }

    @Override
    protected BufferedImage[] getCurrentFrames() {
        return frames;
    }

    @Override
    protected int getCurrentFrameIndex() {
        return frameIndex;
    }
}
