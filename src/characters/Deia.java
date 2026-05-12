package characters;

import gameobjects.Player;
import gameobjects.PlayerType;

public class Deia extends Player {

    public Deia(int x, int y) {
        super(x, y, PlayerType.DEIA);
        maxLevels = 5;
    }
}