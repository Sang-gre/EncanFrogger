package gameobjects;

import java.awt.*;
import java.awt.Graphics;
import java.awt.Rectangle;


public class Coin extends GameObject {
    private boolean isCollected;
    private Platform attachedPlatform;
    private int offsetX;

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
        if (attachedPlatform != null){
            this.x = attachedPlatform.getX() + offsetX;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!isActive()) return;
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }

    @Override
    public void onCollide(GameObject other) {
        if (!isCollected) {
            isCollected = true;
            setActive(false);
        }
    }

    public void attachToPlatform(Platform p){
        this.attachedPlatform = p;
        this.offsetX= x - p.getX(); //get distance from platform
    }
    public boolean isCollected() { return isCollected;}
}
