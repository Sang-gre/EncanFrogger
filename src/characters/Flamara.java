package characters;

import gameobjects.Player;
import gameobjects.GameObject;
import java.awt.*;

public class Flamara extends Player {

    private float dashSpeed;
    private int dashDuration;
    private int dashTimer;
    private float normalSpeed;
    private static final int COOLDOWN = 60; // can be changed

    public Flamara(int x, int y) {
        super(x, y);
        this.dashSpeed = 20.0f;
        this.dashDuration = 10;
        this.normalSpeed = speed;
        this.dashTimer = 0;
    }

    @Override
    public void useAbility() {
        if (isAbilityReady()) {
            dashForward();
            startCooldown(COOLDOWN);
        }
    }

    public void dashForward() {
        speed = dashSpeed;
        dashTimer = dashDuration;
        // Made her dash forward twice. Like idk how the speed thing would work kasi
        if (getDirection() != null) {
            move();
            move();
        }
    }

    @Override
    public void update() {
        super.update();

        if (dashTimer > 0) {
            dashTimer--;
            if (dashTimer == 0) {
                speed = normalSpeed;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(220, 80, 20));
        g.fillRect(x, y, width, height);
        // TODO: replace with AssetManager sprite "flamara"
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }

    public float getDashSpeed() {
        return dashSpeed;
    }

    public float getDashDuration() {
        return dashDuration;
    }
}