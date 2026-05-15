package gameobjects;

import assets.AssetManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class Coin extends GameObject {
    private boolean isCollected;
    private Platform attachedPlatform;
    private int offsetX;
    private int offsetY;
    private int scaledSize = 30;

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
        if (attachedPlatform != null && attachedPlatform.isActive()) {
            this.x = attachedPlatform.getX() + offsetX;
            this.y = attachedPlatform.getY() + offsetY;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!isActive())
            return;

        Image coinImage = AssetManager.getInstance().getCoin("coin");

        int scaledHeight = scaledSize;
        int scaledWidth = scaledHeight; // fallback square

        if (coinImage != null) {
            int origW = coinImage.getWidth(null);
            int origH = coinImage.getHeight(null);
            if (origW > 0 && origH > 0) {
                float aspectRatio = (float) origW / origH;
                scaledWidth = (int) (scaledHeight * aspectRatio);
            }
        }

        int drawY = (attachedPlatform != null)
                ? y - scaledHeight
                : y;

        if (coinImage != null) {
            g.drawImage(coinImage, x, drawY, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(x, drawY, scaledWidth, scaledHeight);
        }
    }

    @Override
    public void onCollide(GameObject other) {
        if (!isCollected) {
            isCollected = true;
            setActive(false);
        }
    }

    public void attachToPlatform(Platform p) {
        this.attachedPlatform = p;
        this.offsetX = this.x - p.getX(); // get distance from platform
        this.offsetY = this.y - p.getY();
    }

    public Platform getAttachedPlatform() {
        return attachedPlatform;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setScaledSize(int size) {
        this.scaledSize = size;
    }

}
