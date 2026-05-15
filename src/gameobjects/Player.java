package gameobjects;

import assets.AssetManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public abstract class Player extends GameObject {

    // --- Animation ---
    private BufferedImage[] currentFrames;
    private int frameIndex = 0;
    private int frameTimer = 0;
    private final int frameDelay = 7;
    private int idleTimer = 0;
    private static final int IDLE_DELAY = 15;

    // --- Identity ---
    protected PlayerType type;
    protected int maxLevels;

    // --- Stats ---
    private int lives;
    private int coins;

    // --- Movement ---
    private Direction direction;
    private int stepX;
    private int stepY;
    protected Direction lastDirection = Direction.DOWN;
    private boolean movedThisTick = false;
    private int previousX;

    // --- Visual ---
    private int visualWidth = 80;
    private int visualHeight = 80;

    // --- Damage ---
    private int invincibilityFrames = 0;
    private boolean onPlatform = false;

    public Player(int x, int y, PlayerType type) {
        super(x, y, 40, 40, 5);
        this.type = type;
        this.stepX = 40;
        this.stepY = 40;
        this.lives = 3;
        this.coins = 0;

        // Default standing frames
        currentFrames = AssetManager.getInstance()
                .getPlayerAnimation(type, Direction.DOWN);
    }

    // -------------------------------------------------------------------------
    // Core lifecycle
    // -------------------------------------------------------------------------
    @Override
    public void update() {
        // Invincibility countdown
        if (invincibilityFrames > 0)
            invincibilityFrames--;

        // Move, then sync animation direction
        move();
        syncAnimation();

        // Reset per-tick flag
        movedThisTick = false;
    }

    @Override
    public void draw(Graphics g) {
        if (currentFrames == null || currentFrames.length == 0) {
            // Fallback: solid rectangle so broken assets are obvious
            g.setColor(Color.GREEN);
            g.fillRect(x, y, visualWidth, visualHeight);
            return;
        }

        int drawX = x - (visualWidth - width) / 2;
        int drawY = isOnPlatform()
                ? (y + height) - visualHeight
                : y - (visualHeight - height) / 2;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(currentFrames[frameIndex], drawX, drawY,
                visualWidth, visualHeight, null);
    }

    @Override
    public void onCollide(GameObject other) {
        if (other instanceof Obstacle) {
            loseLife();
        } else if (other instanceof Coin) {
            addCoins(1);
            other.setActive(false);
        }
    }

    // -------------------------------------------------------------------------
    // Movement
    // -------------------------------------------------------------------------
    @Override
    public void move() {
        if (direction == null)
            return;

        lastDirection = direction;
        movedThisTick = true;

        switch (direction) {
            case UP -> y -= stepY;
            case DOWN -> y += stepY;
            case LEFT -> x -= stepX;
            case RIGHT -> x += stepX;
        }

        direction = null;
    }

    // -------------------------------------------------------------------------
    // Animation
    // -------------------------------------------------------------------------
    private void syncAnimation() {
        if (movedThisTick) {
            idleTimer = IDLE_DELAY;
        } else if (idleTimer > 0) {
            idleTimer--;
        }

        Direction displayDirection = (idleTimer > 0) ? lastDirection : Direction.DOWN;

        BufferedImage[] desired = AssetManager.getInstance()
                .getPlayerAnimation(type, displayDirection);

        if (desired != currentFrames) {
            currentFrames = desired;
            frameIndex = 0;
            frameTimer = 0;
        }

        if (currentFrames != null && currentFrames.length > 0) {
            if (++frameTimer >= frameDelay) {
                frameTimer = 0;
                frameIndex = (frameIndex + 1) % currentFrames.length;
            }
        }
    }

    public void resize(int laneHeight, int columnWidth) {
        int visualHeight = (int) (laneHeight * 1.4f);

        // get the image to calculate aspect ratio
        BufferedImage[] frames = AssetManager.getInstance().getPlayerAnimation(type, Direction.DOWN);
        if (frames != null && frames.length > 0) {
            BufferedImage img = frames[0];
            float aspectRatio = (float) img.getWidth() / img.getHeight();
            int visualWidth = (int) (visualHeight * aspectRatio);
            setVisualSize(visualWidth, visualHeight);
        } else {
            setVisualSize(visualHeight, visualHeight); // fallback square
        }

        width = (int) (laneHeight * 0.5f);
        height = (int) (laneHeight * 0.5f);
        stepX = columnWidth;
        stepY = laneHeight;
    }

    // -------------------------------------------------------------------------
    // Damage
    // -------------------------------------------------------------------------
    public void loseLife() {
        if (invincibilityFrames > 0)
            return;
        lives--;
        invincibilityFrames = 60;
    }

    public void resetLives() {
        this.lives = 3;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
    public void setDirection(Direction direction) {
        this.direction = direction;
        if (direction != null)
            lastDirection = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public int getLives() {
        return lives;
    }

    public int getCoins() {
        return coins;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void setStepSize(int stepX, int stepY) {
        this.stepX = stepX;
        this.stepY = stepY;
    }

    public void setOnPlatform(boolean onPlatform) {
        this.onPlatform = onPlatform;
    }

    public boolean isOnPlatform() {
        return onPlatform;
    }

    public void setLastDirection(Direction d) {
        this.lastDirection = d;
    }

    public void setMovedThisTick(boolean b) {
        this.movedThisTick = b;
    }

    public void setVisualSize(int w, int h) {
        this.visualWidth = w;
        this.visualHeight = h;
    }

    public int getVisualWidth() {
        return visualWidth;
    }

    public int getVisualHeight() {
        return visualHeight;
    }

    @Override
    public void setPosition(int x, int y) {
        this.previousX = this.x;
        this.x = x;
        this.y = y;
    }

    public int getPreviousX() {
        return previousX;
    }
}