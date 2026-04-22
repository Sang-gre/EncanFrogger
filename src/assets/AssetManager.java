package assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gameobjects.PlayerType;
import level.Direction;

public class AssetManager {

    private static final Map<PlayerType, Map<Direction, BufferedImage[]>> playerAnimations = new HashMap<>();

    static {
        try {
          
            loadCharacter(PlayerType.PAOPAO, "assets/spritesheets/paopaoSpritesheet.png", 7, 4);
            loadCharacter(PlayerType.DEIA, "assets/spritesheets/deiaSpritesheet.png", 6, 4);
            loadCharacter(PlayerType.FLAMARA, "assets/spritesheets/flammaraSpritesheet.png", 5, 4);
            loadCharacter(PlayerType.TERRA, "assets/spritesheets/terraSpritesheet.png", 7, 4);
            loadCharacter(PlayerType.ADAMUS, "assets/spritesheets/adamusSpritesheet.png", 6, 4);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private static void loadCharacter(PlayerType type, String path, int columns, int rows) throws IOException {

        BufferedImage sheet = ImageIO.read(new File(path));

        int frameWidth = sheet.getWidth() / columns;
        int frameHeight = sheet.getHeight() / rows;

        Map<Direction, BufferedImage[]> directionMap = new HashMap<>();

        Direction[] directions = {
                Direction.DOWN,
                Direction.LEFT,
                Direction.RIGHT,
                Direction.UP
        };

        for (int row = 0; row < rows; row++) {

            BufferedImage[] frames = new BufferedImage[columns];

            for (int col = 0; col < columns; col++) {
                frames[col] = sheet.getSubimage(
                        col * frameWidth,
                        row * frameHeight,
                        frameWidth,
                        frameHeight
                );
            }

            directionMap.put(directions[row], frames);
        }

        playerAnimations.put(type, directionMap);
    }

   
    public static BufferedImage[] getPlayerAnimation(PlayerType type, Direction dir) {

        Map<Direction, BufferedImage[]> map = playerAnimations.get(type);

        if (map == null) return null;

        BufferedImage[] frames = map.get(dir);

        if (frames == null) {
            return map.get(Direction.DOWN); 
        }

        return frames;
    }
}