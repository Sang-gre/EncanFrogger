package assets;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import gameobjects.PlayerType;
import level.Direction;

public class AssetManager {

    private static final Map<PlayerType, Map<Direction, Image>> playerSprites = new HashMap<>();

    static {
        loadPlayerSprites(PlayerType.PAOPAO, "assets/paopao/");
        loadPlayerSprites(PlayerType.DEIA, "assets/deia/");
        loadPlayerSprites(PlayerType.FLAMARA, "assets/flamara/");
        loadPlayerSprites(PlayerType.TERRA, "assets/terra/");
        loadPlayerSprites(PlayerType.ADAMUS, "assets/adamus/");
    }

    private static void loadPlayerSprites(PlayerType type, String path) {
        Map<Direction, Image> sprites = new HashMap<>();

        sprites.put(Direction.UP, new ImageIcon(path + "back.png").getImage());
        sprites.put(Direction.DOWN, new ImageIcon(path + "front.png").getImage());
        sprites.put(Direction.LEFT, new ImageIcon(path + "left.png").getImage());
        sprites.put(Direction.RIGHT, new ImageIcon(path + "right.png").getImage());

        playerSprites.put(type, sprites);
    }

    
    public static Image getPlayerSprite(PlayerType type, Direction direction) {
        Map<Direction, Image> sprites = playerSprites.get(type);

        if (sprites == null) return null;

        return sprites.getOrDefault(direction, sprites.get(Direction.DOWN));
    }
}