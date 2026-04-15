package core;

import gameobjects.GameObject;

import java.awt.Rectangle;

public class CollisionSystem {

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

        a.onCollide(b);
        b.onCollide(a);
    }
}