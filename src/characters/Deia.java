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
    private int frameDelay = 10;

    public Deia(int x, int y) {
        super(x, y, PlayerType.DEIA);
        this.phaseCount = 3;
        this.windShield = false;

        // Load default frames
        frames = AssetManager.getInstance().getPlayerAnimation(PlayerType.DEIA, Direction.DOWN);
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

    @Override
    public void update() {
        super.update();

        Direction dir = getLastDirection();
        BufferedImage[] desiredFrames = AssetManager.getInstance()
                .getPlayerAnimation(PlayerType.DEIA, dir);

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
        Graphics2D g2d = (Graphics2D) g;
        Composite original = g2d.getComposite();

        if (windShield) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }

        super.draw(g); // handles all sizing, anchoring, debug hitbox

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

    public int getPhaseCount() {
        return phaseCount;
    }

    public boolean isWindShield() {
        return windShield;
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
