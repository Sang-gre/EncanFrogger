package assets;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import gameobjects.PlayerType;
import level.Direction;

public class AssetManager {

    private static final Map<PlayerType, Map<Direction, Image>> playerSprites = new HashMap<>();
    private static final Map<PlayerType, Image> characterCards = new HashMap<>();
    private static final Map<PlayerType, Image> infoCards = new HashMap<>();

    static {
        loadPlayerSprites(PlayerType.PAOPAO, "assets/paopao/");
        loadPlayerSprites(PlayerType.DEIA, "assets/deia/");
        loadPlayerSprites(PlayerType.FLAMARA, "assets/flamara/");
        loadPlayerSprites(PlayerType.TERRA, "assets/terra/");
        loadPlayerSprites(PlayerType.ADAMUS, "assets/adamus/");

        loadCardAssets(PlayerType.PAOPAO,   "assets/characterCards/paopaoCard.png",   "assets/characterInfoCard/paopaoInfoCard.png");
        loadCardAssets(PlayerType.TERRA,    "assets/characterCards/terraCard.png",    "assets/characterInfoCard/terraInfoCard.png");
        loadCardAssets(PlayerType.FLAMARA,  "assets/characterCards/flammaraCard.png", "assets/characterInfoCard/flammaraInfoCard.png");
        loadCardAssets(PlayerType.ADAMUS,   "assets/characterCards/adamusCard.png",   "assets/characterInfoCard/adamusInfoCard.png");
        loadCardAssets(PlayerType.DEIA,     "assets/characterCards/deiaCard.png",     "assets/characterInfoCard/deiaInfoCard.png");
    }

    private static void loadPlayerSprites(PlayerType type, String path) {
        Map<Direction, Image> sprites = new HashMap<>();
        sprites.put(Direction.UP,    new ImageIcon(path + "back.png").getImage());
        sprites.put(Direction.DOWN,  new ImageIcon(path + "front.png").getImage());
        sprites.put(Direction.LEFT,  new ImageIcon(path + "left.png").getImage());
        sprites.put(Direction.RIGHT, new ImageIcon(path + "right.png").getImage());
        playerSprites.put(type, sprites);
    }

    private static void loadCardAssets(PlayerType type, String cardPath, String infoPath) {
        characterCards.put(type, new ImageIcon(cardPath).getImage());
        infoCards.put(type,      new ImageIcon(infoPath).getImage());
    }

    public static Image getPlayerSprite(PlayerType type, Direction direction) {
        Map<Direction, Image> sprites = playerSprites.get(type);
        if (sprites == null) return null;
        return sprites.getOrDefault(direction, sprites.get(Direction.DOWN));
    }

    public static Image getCharacterCard(PlayerType type) {
        return characterCards.get(type);
    }

    public static Image getInfoCard(PlayerType type) {
        return infoCards.get(type);
    }
}