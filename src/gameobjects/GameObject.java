package gameobjects;
import javax.swing.*;
import java.awt.*;

public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected int speed;
    protected boolean isActive;

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

    // basic movement (can be overridden)
    public void move() {
        // default: no movement
}

    // update logic
    public void update() {
        move();
    }

    public boolean isActive() {
    return isActive;
}

    // render method
    public abstract void draw(Graphics g);

    // collision handling
    public abstract void onCollide(GameObject other);
}
