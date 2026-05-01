package gameobjects;

import assets.AssetManager;
import java.awt.*;
import level.Direction;

public class Obstacle extends GameObject {

    private int lane;
    private Direction direction;
    private String obstacleType;

    public Obstacle(int x, int y, int width, int height, int lane, float speed, Direction direction, String obstacleType) {
        super(x, y, width, height, speed);

        this.lane = lane;
        this.direction = direction;
        this.obstacleType = obstacleType;
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

        Image sprite = AssetManager.getObstacle(obstacleType);

        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    @Override
    public void onCollide(GameObject other) {}

    public void reset(int x, int y, float speed, Direction dir) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.direction = dir;
        setActive(true);
    }

    public boolean isOffScreen(int screenWidth) {
        return x > screenWidth + width || x + width < 0;
    }

    public int getLane() {
        return lane;
    }

    public Direction getDirection() {
        return direction;
    }
}