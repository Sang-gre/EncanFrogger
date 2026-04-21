package gameobjects;

import java.awt.*;
import level.Direction;

public abstract class Player extends GameObject {

    private int lives;
    private int coins;
    private int level;
    private int cooldownTimer;
    private boolean abilityReady;
    private Direction direction;

    public Player(int x, int y) {
        super(x, y, 40, 40, 5);

        this.lives = 3;
        this.coins = 0;
        this.level = 1;
        this.abilityReady = true;
    }

    @Override
    public void move() {
        if (direction == null)
            return;
        switch (direction) {
            case UP:
                y -= (int) speed;
                break;
            case DOWN:
                y += (int) speed;
                break;
            case LEFT:
                x -= (int) speed;
                break;
            case RIGHT:
                x += (int) speed;
                break;
        }
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
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }

    @Override
    public void onCollide(GameObject other) {
        // TODO: handle different collision types (obstacles, coins)
        System.out.println("Player collided with: " + other.getClass().getSimpleName());
        loseLife();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
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

    public boolean isAbilityReady() {
        return abilityReady;
    }

    protected void setAbilityReady(boolean ready) {
        this.abilityReady = ready;
    }
}