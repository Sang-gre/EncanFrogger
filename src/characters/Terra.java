package characters;

import gameobjects.Player;
import gameobjects.GameObject;
import java.awt.*;

public class Terra extends Player {

    private int slowRadius;
    private int slowDuration;
    private static final int COOLDOWN = 60; // can be changed, might be too big

    public Terra(int x, int y) {
        super(x, y);
        this.slowRadius = 100;
        this.slowDuration = 180;
    }

    @Override
    public void useAbility() {
        if (isAbilityReady()) {
            slowNearby();
            startCooldown(COOLDOWN);
        }
    }

    public void slowNearby() {
        // TODO: accept List<Obstacle> and reduce speed of obstacles within slowRadius
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(34, 139, 34));
        g.fillRect(x, y, width, height);
        // TODO: replace with AssetManager sprite "terra"
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }

    public int getSlowRadius()   { return slowRadius; }
    public int getSlowDuration() { return slowDuration; }
}