package characters;

import gameobjects.Player;
import gameobjects.PlayerType;

public class Terra extends Player {

    public Terra(int x, int y) {
        super(x, y, PlayerType.TERRA);
        maxLevels = 3;
    }
}