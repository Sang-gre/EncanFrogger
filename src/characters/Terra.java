package characters;

import assets.AssetManager;
import gameobjects.GameObject;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.*;
import java.awt.image.BufferedImage;
import level.Direction;

public class Terra extends Player {

    // Ability state
    private int slowRadius;
    private int slowDuration;
    private static final int COOLDOWN = 60;

    // Animation state
    private BufferedImage[] frames;
    private int frameIndex = 0;
    private int frameTimer = 0;
    private int frameDelay = 10;

    public Terra(int x, int y) {
        super(x, y, PlayerType.TERRA);
        this.slowRadius = 100;
        this.slowDuration = 180;

        // Load default frames
        frames = AssetManager.getInstance().getPlayerAnimation(PlayerType.TERRA, Direction.DOWN);
        if (frames == null) {
            System.err.println("[Terra] WARNING: animation frames not found.");
        }
    }

    @Override
    public void useAbility() {
        if (isAbilityReady()) {
            slowNearby();
            startCooldown(COOLDOWN);
        }
    }

    public void slowNearby() {
        // TODO: accept List<Obstacle> and reduce speed of obstacles within slowRadius
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

    public int getSlowRadius() {
        return slowRadius;
    }

    public int getSlowDuration() {
        return slowDuration;
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
