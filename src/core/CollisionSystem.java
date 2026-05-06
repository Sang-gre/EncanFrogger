package core;

import gameobjects.Coin;
import gameobjects.GameObject;
import gameobjects.Obstacle;
import gameobjects.Platform;
import gameobjects.Player;
import java.awt.Rectangle;
import java.util.List;

public class CollisionSystem {

    private int coinsCollected = 0; // running total, read by GamePanel to detect new pickups

    public boolean checkAABB(GameObject a, GameObject b) {
        if (a == null || b == null)
            return false;
        if (!a.isActive() || !b.isActive())
            return false;

        Rectangle boundsA = a.getBounds();
        Rectangle boundsB = b.getBounds();

        if (boundsA == null || boundsB == null)
            return false;

        return boundsA.intersects(boundsB);
    }

    public void handleCollision(GameObject a, GameObject b) {
        if (a == null || b == null)
            return;
        if (!a.isActive() || !b.isActive())
            return;

        // player + coin
        if (a instanceof Player && b instanceof Coin) {
            Coin coin = (Coin) b;
            if (!coin.isCollected()) {
                ((Player) a).addCoins(1);
                coin.onCollide(a);
                coinsCollected++; // increment so GamePanel can detect the pickup
            }
            return;
        }

        // player + obstacle
        if (a instanceof Player && b instanceof Obstacle) {
            a.onCollide(b);
            return;
        }

        // player + platform
        if (a instanceof Player && b instanceof Platform) {
            return;
        }

        a.onCollide(b);
        b.onCollide(a);
    }

    public void checkAll(Player player, List<Obstacle> obstacles,
            List<Platform> platforms, List<Coin> coins) {
        for (Obstacle o : obstacles) {
            if (checkAABB(player, o))
                handleCollision(player, o);
        }
        for (Platform p : platforms) {
            if (checkAABB(player, p))
                handleCollision(player, p);
        }
        for (Coin c : coins) {
            if (checkAABB(player, c))
                handleCollision(player, c);
        }
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }
}