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
    private final int frameDelay = 20; // ticks per frame (adjust for speed)

    // Movement flags
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean facingBack = false;
    private final int moveSpeed = 4;
    protected int vx = 0;
    protected int vy = 0;

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

    // Movement control methods
    public void startMoveLeft() {
        movingLeft = true; movingRight = false; facingBack = false;
        vx = -moveSpeed;
        BufferedImage[] leftFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, Direction.LEFT);
        if (leftFrames != null) { frames = leftFrames; frameIndex = 0; frameTimer = 0; }
    }

    public void stopMoveLeft() {
        movingLeft = false; vx = 0;
        BufferedImage[] downFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, Direction.DOWN);
        if (downFrames != null) { frames = downFrames; frameIndex = 0; frameTimer = 0; }
    }

    public void startMoveRight() {
        movingRight = true; movingLeft = false; facingBack = false;
        vx = moveSpeed;
        BufferedImage[] rightFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, Direction.RIGHT);
        if (rightFrames != null) { frames = rightFrames; frameIndex = 0; frameTimer = 0; }
    }

    public void stopMoveRight() {
        movingRight = false; vx = 0;
        BufferedImage[] downFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, Direction.DOWN);
        if (downFrames != null) { frames = downFrames; frameIndex = 0; frameTimer = 0; }
    }

    public void setFacingBack(boolean b) {
        facingBack = b;
        if (facingBack) {
            BufferedImage[] backFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, Direction.UP);
            if (backFrames != null) { frames = backFrames; frameIndex = 0; frameTimer = 0; }
        } else {
            if (!movingLeft && !movingRight) {
                BufferedImage[] downFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, Direction.DOWN);
                if (downFrames != null) { frames = downFrames; frameIndex = 0; frameTimer = 0; }
            }
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

        Direction dir = chooseDirection();
        BufferedImage[] desiredFrames = AssetManager.getInstance().getPlayerAnimation(PlayerType.PAOPAO, dir);
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

        x += vx;
        y += vy;
    }

    @Override
    public void draw(Graphics g) {
        if (frames != null && frames.length > 0) {
            BufferedImage img = frames[frameIndex];
            g.drawImage(img, x, y, width, height, null);
        } else {
            g.setColor(new Color(100, 200, 220));
            g.fillRect(x, y, width, height);
        }
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }
}
