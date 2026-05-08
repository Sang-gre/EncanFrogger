package characters;

import assets.AssetManager;
import gameobjects.GameObject;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.*;
import java.awt.image.BufferedImage;
import level.Direction;

public class Paopao extends Player {

    // Animation state
    private BufferedImage[] frames;
    private int frameIndex = 0;
    private int frameTimer = 0;
    private final int frameDelay = 5;

    public Paopao(int x, int y) {
        super(x, y, PlayerType.PAOPAO);

        // Load default (standing/down) frames
        frames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, Direction.DOWN);
        if (frames == null) {
            System.err.println("[Paopao] WARNING: animation frames not found.");
        }
    }

    @Override
    public void useAbility() {
        // Paopao’s special ability can be implemented here later
    }

    @Override
    public void update() {
        super.update();

         Direction dir = getLastDirection();
        // Always sync to last direction
        BufferedImage[] desiredFrames = AssetManager.getInstance()
                .getPlayerAnimation(PlayerType.PAOPAO, dir);

        if (desiredFrames != null) {
            // Only reset animation if direction actually changed
            if (desiredFrames != frames) {
                frames = desiredFrames;
                frameIndex = 0;
                frameTimer = 0;
            }

            // Always advance animation
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

    @Override
    protected BufferedImage[] getCurrentFrames() {
        return frames;
    }

    @Override
    protected int getCurrentFrameIndex() {
        return frameIndex;
    }
}
