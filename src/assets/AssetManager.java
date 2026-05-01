package assets;

import core.GameMap;
import gameobjects.PlayerType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import level.Direction;

public class AssetManager {

   /* PLAYER ANIMATIONS */
    private static final Map<PlayerType, Map<Direction, BufferedImage[]>> playerAnimations = new HashMap<>();

    /* CHARACTER SELECT UI */
    private static final Map<PlayerType, Image> characterCards = new HashMap<>();
    private static final Map<PlayerType, Image> infoCards      = new HashMap<>();

    /* MAP SELECT UI */
    private static final Map<GameMap, Image> mapBackgrounds = new HashMap<>();
    private static final Map<GameMap, Image> mapFlags = new HashMap<>();

    /* SCREEN BACKGROUNDS */
    private static final Map<String, Image> backgrounds = new HashMap<>();

    /* BUTTONS */
    private static final Map<String, Image> buttons = new HashMap<>();

    /* OBSTACLES */
    private static final Map<String, Image> obstacles = new HashMap<>();

    /* CURSOR */
    private static Image customCursor;

    static {
        loadBackgrounds();
        loadButtons();
        loadCursor();
        loadCharacterCards();
        loadInfoCards();
        loadMapBackgrounds();
        loadMapFlags();
        loadObstacles();
        loadAllSpritesheets();
    }

    private static void loadBackgrounds() {
        backgrounds.put("title",        loadImage("ASSETS/Backgrounds/background.png"));
        backgrounds.put("titleFont",    loadImage("ASSETS/Backgrounds/titleFont.png"));
        backgrounds.put("menu",         loadImage("ASSETS/Backgrounds/buttonDashboardBackground.png"));
        backgrounds.put("characterSelect", loadImage("assets/Backgrounds/chooseCharacterBackground.png"));
        backgrounds.put("mapSelect",    loadImage("assets/Backgrounds/mapSelectBackground.png"));
    }

    private static void loadButtons() {
        // Main menu
        buttons.put("start",    loadImage("ASSETS/Buttons/startButton.png"));
        buttons.put("menu",     loadImage("ASSETS/Buttons/menuButton.png"));
        buttons.put("exit",     loadImage("ASSETS/Buttons/exitButton.png"));
        
        // Navigation
        buttons.put("back",     loadImage("assets/Buttons/backButton.png"));
        buttons.put("next",     loadImage("assets/Buttons/nextButton.png"));
        buttons.put("select",   loadImage("assets/Buttons/selectButton.png"));
    }

    private static void loadObstacles() {
        obstacles.put("rock", loadImage("assets/obstacles/adamyaObstacles/adamyaRock.png"));
        obstacles.put("ball",  loadImage("assets/obstacles/adamyaObstacles/adamyaBallLeaves.png"));
        obstacles.put("log",  loadImage("assets/obstacles/adamyaObstacles/adamyaLog.png"));
    }

    private static void loadCursor() {
        customCursor = loadImage("ASSETS/customCursor.png");
    }

    private static void loadCharacterCards() {
        characterCards.put(PlayerType.PAOPAO,  loadImage("assets/characterCards/paopaoCard.png"));
        characterCards.put(PlayerType.DEIA,    loadImage("assets/characterCards/deiaCard.png"));
        characterCards.put(PlayerType.FLAMARA, loadImage("assets/characterCards/flammaraCard.png"));
        characterCards.put(PlayerType.TERRA,   loadImage("assets/characterCards/terraCard.png"));
        characterCards.put(PlayerType.ADAMUS,  loadImage("assets/characterCards/adamusCard.png"));
    }

    private static void loadInfoCards() {
        infoCards.put(PlayerType.PAOPAO,  loadImage("assets/characterInfoCard/paopaoInfoCard.png"));
        infoCards.put(PlayerType.DEIA,    loadImage("assets/characterInfoCard/deiaInfoCard.png"));
        infoCards.put(PlayerType.FLAMARA, loadImage("assets/characterInfoCard/flammaraInfoCard.png"));
        infoCards.put(PlayerType.TERRA,   loadImage("assets/characterInfoCard/terraInfoCard.png"));
        infoCards.put(PlayerType.ADAMUS,  loadImage("assets/characterInfoCard/adamusInfoCard.png"));
    }

    private static void loadMapBackgrounds() {
        mapBackgrounds.put(GameMap.LIREO,    loadImage("assets/maps/lireoMap.png"));
        mapBackgrounds.put(GameMap.HATHORIA, loadImage("assets/maps/hathoriaMap.png"));
        mapBackgrounds.put(GameMap.ADAMYA,   loadImage("assets/maps/adamyaMap.png"));
        mapBackgrounds.put(GameMap.SAPIRO,   loadImage("assets/maps/sapiroMap.png"));
        mapBackgrounds.put(GameMap.MINEAVE,  loadImage("assets/maps/mineaveMap.png"));
    }

    private static void loadMapFlags() {
        mapFlags.put(GameMap.LIREO,    loadImage("assets/flags/lireoFlagMap.png"));
        mapFlags.put(GameMap.HATHORIA, loadImage("assets/flags/hathoriaFlagMap.png"));
        mapFlags.put(GameMap.ADAMYA,   loadImage("assets/flags/adamyaFlagMap.png"));
        mapFlags.put(GameMap.SAPIRO,   loadImage("assets/flags/sapiroFlagMap.png"));
        mapFlags.put(GameMap.MINEAVE,  loadImage("assets/flags/mineaveFlagMap.png"));
    }

    private static void loadAllSpritesheets() {
        try {
            // columns = frames per row, rows = 4 directions (DOWN, LEFT, RIGHT, UP)
            loadSpritesheet(PlayerType.PAOPAO,  "assets/spritesheets/paopaoSpritesheet.png",   7, 4);
            loadSpritesheet(PlayerType.DEIA,    "assets/spritesheets/deiaSpritesheet.png",     6, 4);
            loadSpritesheet(PlayerType.FLAMARA, "assets/spritesheets/flammaraSpritesheet.png", 5, 4);
            loadSpritesheet(PlayerType.TERRA,   "assets/spritesheets/terraSpritesheet.png",    7, 4);
            loadSpritesheet(PlayerType.ADAMUS,  "assets/spritesheets/adamusSpritesheet.png",   6, 4);
        } catch (IOException e) {
            System.err.println("[AssetManager] Failed to load one or more spritesheets:");
            e.printStackTrace();
        }
    }

    /*  Slices a spritesheet into per-direction frame arrays.
        Row order assumed: DOWN=0, LEFT=1, RIGHT=2, UP=3 */
    private static void loadSpritesheet(PlayerType type, String path, int columns, int rows)
            throws IOException {

        BufferedImage sheet = ImageIO.read(new File(path));
        int frameW = sheet.getWidth()  / columns;
        int frameH = sheet.getHeight() / rows;

        Direction[] rowOrder = { Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP };

        Map<Direction, BufferedImage[]> dirMap = new HashMap<>();

        for (int row = 0; row < rows; row++) {
            BufferedImage[] frames = new BufferedImage[columns];
            for (int col = 0; col < columns; col++) {
                frames[col] = sheet.getSubimage(col * frameW, row * frameH, frameW, frameH);
            }
            dirMap.put(rowOrder[row], frames);
        }

        playerAnimations.put(type, dirMap);
    }

    // Convenience loader: returns null and prints a warning instead of throwing.
    private static Image loadImage(String path) {
        File f = new File(path);
        if (!f.exists()) {
            System.err.printf("[AssetManager] WARNING: asset not found: %s%n", path);
            return null;
        }
        return new ImageIcon(path).getImage();
    }

    // Returns the animation frames for the given player and direction.
    public static BufferedImage[] getPlayerAnimation(PlayerType type, Direction dir) {
        Map<Direction, BufferedImage[]> map = playerAnimations.get(type);
        if (map == null) return null;
        BufferedImage[] frames = map.get(dir);
        return (frames != null) ? frames : map.get(Direction.DOWN);
    }

    public static Image getCharacterCard(PlayerType type) {
        return characterCards.get(type);
    }

    public static Image getInfoCard(PlayerType type) {
        return infoCards.get(type);
    }

    public static Image getMapBackground(GameMap map) {
        return mapBackgrounds.get(map);
    }

    public static Image getMapFlag(GameMap map) {
        return mapFlags.get(map);
    }

    public static Image getBackground(String key) {
        return backgrounds.get(key);
    }
    
    public static Image getButton(String key) {
        return buttons.get(key);
    }

    public static Image getObstacle(String key) {
        return obstacles.get(key);
    }   

    public static Image getCustomCursor() {
        return customCursor;
    }
}