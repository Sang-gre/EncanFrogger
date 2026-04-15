package gameobjects;

import java.awt.*;

public abstract class Player extends GameObject {

    private int lives;
    private int coins;
    private int level;
    private boolean abilityReady;
    private Direction direction;

    public Player(int x, int y) {
        super(x, y, 40, 40, 5); // default size & speed

        this.lives = 3;
        this.coins = 0;
        this.level = 1;
        this.abilityReady = true;
    }

    public void setDirection(Direction direction){
        this.direction = direction;
    }
    
    // movement using Direction enum
    @Override
public void move() {
    if (direction == null) return;

    switch (direction) {
        case UP: 
            y -= speed; 
            break;
        case DOWN: 
            y += speed; 
            break;
        case LEFT: 
            x -= speed; 
            break;
        case RIGHT: 
            x += speed; 
            break;
    }
}

    public void useAbility() {
        if (abilityReady) {
            System.out.println("Ability used!");
            abilityReady = false;
        }
    }

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
    move();
}

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }

    @Override
    public void onCollide(GameObject other) {
        // basic example
        System.out.println("Player collided with: " + other.getClass().getSimpleName());
        loseLife();
    }
}
