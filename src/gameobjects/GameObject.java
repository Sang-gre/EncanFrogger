package gameobjects;

import java.awt.*;

public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected float speed;
    private boolean isActive;

    public GameObject(int x, int y, int width, int height, float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.isActive = true;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public abstract void move();

    public abstract void update();

    public abstract void draw(Graphics g);

    public abstract void onCollide(GameObject other);

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}