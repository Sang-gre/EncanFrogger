package gameobjects;

import java.awt.*;

import level.Direction;

public class Obstacle extends GameObject {

    private int lane;
    private float speed; // diff from int speed
    private Direction direction;

    public Obstacle(int x, int y, int width, int height, int lane, float speed, Direction direction) {
        super(x, y, width, height, 0);

        this.lane = lane;
        this.speed = speed;
        this.direction = direction;
    }

    @Override
    public void move() {
        switch (direction) {
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    @Override
    public void onCollide(GameObject other) {
        // deactivate on collision
        if (other instanceof Player) {
            setActive(false);
            System.out.println("Obstacle hit the player!");
        }
    }

    public void reset(int x, int y, float speed, Direction dir) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.direction = dir;
        setActive(true);
    }

    public boolean isOffScreen(int screenWidth) {
        return x > screenWidth || x + width < 0;
    }
}
