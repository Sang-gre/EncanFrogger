package gameobjects;

import assets.AssetManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import level.Direction;

public abstract class Player extends GameObject {

    private int animationFrame = 0;
    private int animationCounter = 0;
    protected PlayerType type;
    private int lives;
    private int coins;
    private int level;
    private int cooldownTimer;
    private boolean abilityReady;
    private Direction direction;
    private int stepX;
    private int stepY;
    protected Direction lastDirection = Direction.DOWN;
    private int invincibilityFrames = 0;
    private boolean onPlatform = false;
    private int directionResetTimer = 0;
    private static final int DIRECTION_RESET_DELAY = 20;
    private boolean movedThisTick = false;
    private int visualWidth = 80;
    private int visualHeight = 80;

    public Player(int x, int y, PlayerType type) {
        super(x, y, 40, 40, 5);
        this.stepX = 40;
        this.stepY = 40;

        this.lives = 3;
        this.coins = 0;
        this.level = 1;
        this.abilityReady = true;
        this.type = type;
    }

    @Override
    public void move() {
        if (direction == null)
            return;

        lastDirection = direction;

        switch (direction) {
            case UP:
                y -= stepY;
                break;
            case DOWN:
                y += stepY;
                break;
            case LEFT:
                x -= stepX;
                break;
            case RIGHT:
                x += stepX;
                break;
        }

        direction = null;
    }

    public abstract void useAbility();

    public void loseLife() {
        if (invincibilityFrames > 0)
            return;
        lives--;
        invincibilityFrames = 60;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    @Override
    public void update() {
        if (invincibilityFrames > 0)
            invincibilityFrames--;

        boolean isMoving = (direction != null);
        move();

        if (isMoving) {
            animationCounter++;
            if (animationCounter > 10) {
                animationFrame++;
                animationCounter = 0;
            }
        } else {
            animationFrame = 0;
        }

        // Direction reset timer
        if (movedThisTick) {
            directionResetTimer = DIRECTION_RESET_DELAY;
        } else {
            if (directionResetTimer > 0) {
                directionResetTimer--;
                if (directionResetTimer == 0) {
                    lastDirection = Direction.DOWN;
                }
            }
        }
        movedThisTick = false; // auto-reset each update

        if (!abilityReady) {
            cooldownTimer--;
            if (cooldownTimer <= 0) {
                setAbilityReady(true);
            }
        }
    }

    protected void startCooldown(int frames) {
        setAbilityReady(false);
        cooldownTimer = frames;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage[] currentFrames = getCurrentFrames();
        int currentIndex = getCurrentFrameIndex();
        
        int drawX = x - (visualWidth - width) / 2;
        int drawY = isOnPlatform()
                ? (y + height) - visualHeight
                : y - (visualHeight - height) / 2;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (currentFrames != null && currentFrames.length > 0) {
            g2d.drawImage(currentFrames[currentIndex % currentFrames.length],
                    drawX, drawY, visualWidth, visualHeight, null);
        } else {
            g2d.setColor(Color.GREEN);
            g2d.fillRect(drawX, drawY, visualWidth, visualHeight);
        }

        /* g2d.setColor(new Color(255, 0, 0, 60));
        g2d.fillRect(x, y, width, height);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height); */
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

    public void setDirection(Direction direction) {
        this.direction = direction;
        if (direction != null) {
            lastDirection = direction;
        }
    }

    protected BufferedImage[] getCurrentFrames() {
        return AssetManager.getInstance().getPlayerAnimation(type, getLastDirection());
    }

    protected int getCurrentFrameIndex() {
        return animationFrame;
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

    public int getLevel() {
        return level;
    }

    public void setStepSize(int stepX, int stepY) {
        this.stepX = stepX;
        this.stepY = stepY;
    }

    public boolean isAbilityReady() {
        return abilityReady;
    }

    protected void setAbilityReady(boolean ready) {
        this.abilityReady = ready;
    }

    public void setOnPlatform(boolean onPlatform) {
        this.onPlatform = onPlatform;
    }

    public boolean isOnPlatform() {
        return onPlatform;
    }

    public void setLastDirection(Direction direction) {
        this.lastDirection = direction;
    }

    public void setMovedThisTick(boolean moved) {
        this.movedThisTick = moved;
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
}
