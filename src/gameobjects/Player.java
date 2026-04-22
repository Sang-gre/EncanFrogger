package gameobjects;

import java.awt.image.BufferedImage;
import java.awt.*;

import assets.AssetManager;
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

    public Player(int x, int y, PlayerType type) {
        super(x, y, 40, 40, 5);

        this.lives = 3;
        this.coins = 0;
        this.level = 1;
        this.abilityReady = true;
        this.type = type;

        this.stepX = 40;
        this.stepY = 40;
    }

    @Override
public void move() {
    if (direction == null) return;

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

    // boundaries
    x = Math.max(0, Math.min(x, 800 - width));
    y = Math.max(0, Math.min(y, 500 - height));

    direction = null;
}

    public abstract void useAbility();

    public void loseLife() {
        lives--;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    @Override
    public void update() {

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

    BufferedImage[] frames =
            AssetManager.getPlayerAnimation(type, getLastDirection());

    if (frames != null) {
        BufferedImage currentFrame = frames[animationFrame % frames.length];
        g.drawImage(currentFrame, x, y, width, height, null);
    } else {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }
}

    @Override
    public void onCollide(GameObject other) {
        // TODO: handle different collision types (obstacles, coins)
        System.out.println("Player collided with: " + other.getClass().getSimpleName());
        loseLife();
    }

    public void setDirection(Direction direction) {
    this.direction = direction;
    if (direction != null) {
        lastDirection = direction;
    }
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
}
