package characters;

import gameobjects.Player;
import gameobjects.PlayerType;

public class Flamara extends Player {

    public Flamara(int x, int y) {
        super(x, y, PlayerType.FLAMARA);
        maxLevels = 4;
    }
}