package gameobjects;

import assets.AssetManager;
import java.awt.*;
import level.Direction;

public class Platform extends GameObject {

    private int lane;
    private Direction direction;
    private float exactX;
    private int previousX;
    private String platformType;

    public Platform(
            int x,
            int y,
            int width,
            int height,
            int lane,
            float speed,
            Direction direction,
            String platformType
    ) {

        super(x, y, width, height, speed);

        this.exactX = x;
        this.lane = lane;
        this.direction = direction;
        this.platformType = platformType;
    }

    @Override
    public void move() {

        previousX = x;

        switch (direction) {

            case LEFT:
                exactX -= speed;
                break;

            case RIGHT:
                exactX += speed;
                break;

            default:
                break;
        }

        x = (int) exactX;
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void draw(Graphics g) {

        Image sprite = AssetManager.getPlatform(platformType);

        if (sprite != null) {

            g.drawImage(
                    sprite,
                    x,
                    y,
                    width,
                    height,
                    null
            );

        } else {

            g.setColor(Color.GRAY);

            g.fillRect(
                    x,
                    y,
                    width,
                    height
            );
        }
    }

    @Override
    public void onCollide(GameObject other) {
        // player safe on platform
    }

    public void reset(
            int x,
            int y,
            float speed,
            Direction dir
    ) {

        this.x = x;
        this.exactX = x;
        this.y = y;
        this.speed = speed;
        this.direction = dir;

        setActive(true);
    }

    public boolean isOffScreen(int screenWidth) {

        return x > screenWidth + width
                || x + width < 0;
    }

    public boolean isPlayerOn(GameObject obj) {
        return getBounds().intersects(obj.getBounds());
    }

    public int getLane() {
        return lane;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDeltaX() {
        return x - previousX;
    }
}