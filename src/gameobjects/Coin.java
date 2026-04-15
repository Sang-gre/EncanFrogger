package gameobjects;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Coin extends GameObject {
    private boolean isCollected;

    public Coin(int x, int y, int width, int height) {
        super(x, y, width, height, 0);
        this.isCollected = false;
    }

    @Override
    public void move() {
        // Coins do not move
    }

    @Override
    public void update() {
        if (!isActive()) return;
        if (x + width < 0) {
            setActive(false);
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!isActive()) return;
        // drawing logic
    }

    @Override
    public void onCollide(GameObject other) {
        if (!isCollected) {
            isCollected = true;
            setActive(false);
        }
    }

    public boolean isCollected() { return isCollected;}
}
