package gameobjects;

import java.awt.*;
import level.Direction;

public class Platform extends GameObject {

    private int lane;
    private Direction direction;
    private float exactX;
    private int previousX;
    private String platformType;
    private Image image;

    private static final int OFFSCREEN_MARGIN = 120;

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
        if (!isActive()) return;
        if (image != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
        }
    }

    @Override
    public void onCollide(GameObject other) {
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
        int margin = Math.max(width, OFFSCREEN_MARGIN);
        return x > screenWidth + margin || x + margin < 0;
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

    public String getType() {
        return platformType;
    }

    public void setImage(Image img) {
        this.image = img;
    }
}
