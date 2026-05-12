package characters;

import gameobjects.Player;
import gameobjects.PlayerType;

public class Adamus extends Player {

    public Adamus(int x, int y) {
        super(x, y, PlayerType.ADAMUS);
        maxLevels = 4;
    }
}