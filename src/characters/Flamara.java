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
    private int frameDelay = 10;

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

    @Override
    public void update() {
        super.update();

        Direction dir = getLastDirection();
        BufferedImage[] desiredFrames = AssetManager.getInstance()
                .getPlayerAnimation(PlayerType.TERRA, dir);

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

    public float getDashSpeed() {
        return dashSpeed;
    }

    public int getDashDuration() {
        return dashDuration;
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
