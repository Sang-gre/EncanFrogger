package characters;

import gameobjects.Player;
import gameobjects.GameObject;
import java.awt.*;

public class Adamus extends Player {

    private int bridgeLane;
    private int bridgeDuration;
    private int bridgeTimer;
    private static final int COOLDOWN = 200;

    public Adamus(int x, int y) {
        super(x, y);
        this.bridgeLane = -1;
        this.bridgeDuration = 120;
        this.bridgeTimer = 0;
    }

    @Override
    public void useAbility() {
        if (isAbilityReady()) {
            createBridge();
            startCooldown(COOLDOWN);
        }
    }

    public void createBridge() {
        //TODO: make it so that the bridge is created in the lane Adamus is currently in. 
        // I guess we can just check his y position and determine the lane from that
        bridgeLane = y / 80; // Assuming lane height is 80 pixels
        bridgeTimer = bridgeDuration;
    }

    public boolean isBridgeActive() {
        return bridgeLane >= 0;
    }

    @Override
    public void update() {
        super.update();
        
        if (bridgeTimer > 0) {
            bridgeTimer--;
            if (bridgeTimer == 0) {
                bridgeLane = -1;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(80, 80, 180));
        g.fillRect(x, y, width, height);
        if (isBridgeActive()) {
            g.setColor(new Color(80, 80, 180, 120));
            g.fillRect(0, bridgeLane * 80 + 36, 800, 8);
        }
        // TODO: replace with AssetManager sprite "adamus"
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }

    public int getBridgeLane()     { return bridgeLane; }
    public int getBridgeDuration() { return bridgeDuration; }
}