package core.handlers;

import assets.SoundManager;
import gameobjects.Coin;
import gameobjects.GameObject;
import gameobjects.Obstacle;
import gameobjects.Platform;
import gameobjects.Player;
import java.awt.Rectangle;
import java.util.List;

/* Handles all collision detection and response between the player and game objects */
public class CollisionSystem {

    private static int coinsCollected = 0;
    private final SoundManager sound = SoundManager.getInstance();

    // -------------------------------------------------------------------------
    // Core AABB detection
    // -------------------------------------------------------------------------

    // Returns true if two game objects' bounding boxes overlap.
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

    // -------------------------------------------------------------------------
    // Collision response
    // -------------------------------------------------------------------------
    public void handleCollision(GameObject a, GameObject b) {
        if (a == null || b == null)
            return;
        if (!a.isActive() || !b.isActive())
            return;

        // Player + Coin: collect the coin, play sound, increment counter
        if (a instanceof Player p && b instanceof Coin coin) {
            if (!coin.isCollected()) {
                p.addCoins(1);
                coin.onCollide(p);
                sound.play("coin");
                coinsCollected++;
            }
            return;
        }

        // Player + Obstacle: trigger death, play sound
        if (a instanceof Player && b instanceof Obstacle) {
            a.onCollide(b);
            sound.play("death");
            return;
        }

        // Player + Platform: handled in levelmanager
        if (a instanceof Player && b instanceof Platform)
            return;

        // Default: both objects react to each other
        a.onCollide(b);
        b.onCollide(a);
    }

    // -------------------------------------------------------------------------
    // Batch collision checks
    // -------------------------------------------------------------------------
    public void checkAll(Player player, List<Obstacle> obstacles,
            List<Platform> platforms, List<Coin> coins) {
        if (player == null)
            return;

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

    // Checks for coin collisions along the player's path (used on platforms)
    public void checkCoinsAlongPath(Player player, List<Coin> coins) {
        if (player == null)
            return;

        // Rectangle covering the player's movement
        int minX = Math.min(player.getPreviousX(), player.getX());
        int maxX = Math.max(player.getPreviousX(), player.getX()) + player.getWidth();
        Rectangle swept = new Rectangle(minX, player.getY(), maxX - minX, player.getHeight());

        for (Coin c : coins) {
            if (!c.isActive() || c.isCollected())
                continue;

            if (swept.intersects(c.getBounds()))
                handleCollision(player, c);
        }
    }

    // -------------------------------------------------------------------------
    // Others
    // -------------------------------------------------------------------------
    public static int getCoinsCollected() {
        return coinsCollected;
    }

    public static void resetCoins() {
        coinsCollected = 0;
    }
}