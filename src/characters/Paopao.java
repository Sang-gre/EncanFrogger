package characters;

import gameobjects.Player;
import gameobjects.PlayerType;

public class Paopao extends Player {

    public Paopao(int x, int y) {
        super(x, y, PlayerType.PAOPAO);
        maxLevels = 3;
    }
}