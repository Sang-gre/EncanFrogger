package characters;

import gameobjects.GameObject;
import gameobjects.Obstacle;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.*;

public class Deia extends Player {

    private int phaseCount;
    private boolean windShield;
    private static final int COOLDOWN = 60; //can be changed

    public Deia(int x, int y) {
        super(x, y, PlayerType.DEIA);
        this.phaseCount = 3;
        this.windShield = false;
    }

    @Override
    public void useAbility() {
        if (isAbilityReady() && phaseCount > 0) {
            phaseThrough();
            startCooldown(COOLDOWN);
        }
    }

    public void phaseThrough() {
        windShield = true;
        phaseCount--;
    }

    public boolean isPhasing() {
        return windShield;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Composite original = g2d.getComposite();
        if (windShield) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }
        g2d.setColor(new Color(160, 80, 200));
        g2d.fillRect(x, y, width, height);
        g2d.setComposite(original);
        // TODO: replace with AssetManager sprite "deia"
    }

    @Override
    public void onCollide(GameObject other) {
        if (other instanceof Obstacle && windShield) {
            windShield = false;
        } else {
            super.onCollide(other);
        }
    }

    public int getPhaseCount() {
        return phaseCount;
    }

    public boolean isWindShield() {
        return windShield;
    }
}