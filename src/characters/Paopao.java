package characters;

import gameobjects.GameObject;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.*;

public class Paopao extends Player {

    public Paopao(int x, int y) {
        super(x, y, PlayerType.PAOPAO);
    }

    @Override
    public void useAbility() {}

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(100, 200, 220));
        g.fillRect(x, y, width, height);
        // TODO: replace with AssetManager sprite "paopao"
    }

    @Override
    public void onCollide(GameObject other) {
        super.onCollide(other);
    }
}