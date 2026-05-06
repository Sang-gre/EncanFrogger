package gameobjects;

import assets.AssetManager;
import java.awt.*;


public class Coin extends GameObject {
    private boolean isCollected;
    private Platform attachedPlatform;
    private int offsetX;
    private int offsetY;

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
        if (attachedPlatform != null && attachedPlatform.isActive()){
            this.x = attachedPlatform.getX() + offsetX;
            this.y = attachedPlatform.getY() + offsetY;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!isActive()) return;

        Image coinImage = AssetManager.getCoin("coin");
        int scaledWidth = 30; 
        int scaledHeight = 30;

        if (coinImage != null) {
            g.drawImage(coinImage, x, y, scaledWidth, scaledHeight, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, scaledWidth, scaledHeight);
        }
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
        this.offsetX= this.x - p.getX(); //get distance from platform
        this.offsetY = this.y - p.getY();
    }

    public Platform getAttachedPlatform(){
        return attachedPlatform;
    }
    public boolean isCollected() { return isCollected;}

}
