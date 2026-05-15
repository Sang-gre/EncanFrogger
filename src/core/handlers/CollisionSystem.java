package core.handlers;

import assets.SoundManager;
import gameobjects.Coin;
import gameobjects.GameObject;
import gameobjects.Obstacle;
import gameobjects.Platform;
import gameobjects.Player;
import java.awt.Rectangle;
import java.util.List;

public class CollisionSystem {

    private static int coinsCollected = 0;
    private SoundManager sound = SoundManager.getInstance();

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
                sound.play("coin");
                coinsCollected++;
            }
            return;
        }

        // player + obstacle
        if (a instanceof Player && b instanceof Obstacle) {
            a.onCollide(b);
            sound.play("death");
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

    public void checkCoinsAlongPath(Player player, List<Coin> coins) {
        int minX = Math.min(player.getPreviousX(), player.getX());
        int maxX = Math.max(player.getPreviousX(), player.getX()) + player.getWidth();

        for (Coin c : coins) {
            if (!c.isActive() || c.isCollected())
                continue;

            Rectangle swept = new Rectangle(minX, player.getY(), maxX - minX, player.getHeight());

            if (swept.intersects(c.getBounds())) {
                handleCollision(player, c);
            }
        }
    }

    public static int getCoinsCollected() {
        return coinsCollected;
    }
}