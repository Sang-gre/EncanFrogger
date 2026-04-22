package assets;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import gameobjects.PlayerType;
import level.Direction;
// ichachange pa kasi dapat hiwalay-hiwalay ang png for fron, back, etc.
public class AssetManager {
/*
    private static final Map<PlayerType, Map<Direction, Image>> playerSprites = new HashMap<>();

    static {
        loadPlayerSprites(PlayerType.PAOPAO, "assets/spritesheets/paopao/");
        loadPlayerSprites(PlayerType.DEIA, "assets/spritesheets/deia/");
        loadPlayerSprites(PlayerType.FLAMARA, "assets/spritesheets/flammara/");
        loadPlayerSprites(PlayerType.TERRA, "assets/spritesheets/terra/");
        loadPlayerSprites(PlayerType.ADAMUS, "assets/spritesheets/adamus/");
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
    */
}
