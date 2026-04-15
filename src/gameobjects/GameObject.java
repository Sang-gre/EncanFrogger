package gameobjects;

import java.awt.*;

public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected int speed;
    private boolean isActive;

    public GameObject(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.isActive = true;
    }

    // AABB collision boundary
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // basic movement
    public abstract void move();
    // update logic
    public abstract void update();
    // render method
    public abstract void draw(Graphics g);
    // collision handling
    public abstract void onCollide(GameObject other);

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getSpeed() { return speed; }
    public boolean isActive() { return isActive; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setActive(boolean active) { this.isActive = active; }
}
