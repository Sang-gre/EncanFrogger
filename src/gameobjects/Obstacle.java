package gameobjects;

import java.awt.*;
import level.Direction;

public class Obstacle extends GameObject {

    private int lane;
    private Direction direction;
    private String obstacleType;
    private Image image;

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
    public void onCollide(GameObject other) {}

    public void reset(int x, int y, float speed, Direction dir) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.direction = dir;
        setActive(true);
    }

    private static final int OFFSCREEN_MARGIN = 60;

    public boolean isOffScreen(int screenWidth) {
        int margin = Math.max(width, OFFSCREEN_MARGIN);
        return x > screenWidth + margin || x + margin < 0;
    }



    public int getLane() {
        return lane;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getType() {
        return obstacleType;
    }

    public void setImage(Image img) {
        this.image = img;
    }

    @Override
    public void draw(Graphics g) {
        if (!isActive()) return;
        if (image != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}
