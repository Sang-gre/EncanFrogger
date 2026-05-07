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

    private static AssetManager instance;

    // TODO: turn into singleton. Needs to change all call sites so best done when nobody else is working

    /* PLAYER ANIMATIONS */
    private final Map<PlayerType, Map<Direction, BufferedImage[]>> playerAnimations = new HashMap<>();

    /* CHARACTER SELECT UI */
    private final Map<PlayerType, Image> characterCards = new HashMap<>();
    private final Map<PlayerType, Image> infoCards = new HashMap<>();

    /* MAP SELECT UI */
    private final Map<GameMap, Image> mapBackgrounds = new HashMap<>();
    private final Map<GameMap, Image> mapFlags = new HashMap<>();

    /* SCREEN BACKGROUNDS */
    private final Map<String, Image> backgrounds = new HashMap<>();

    /* BUTTONS */
    private final Map<String, Image> buttons = new HashMap<>();

    /* OBSTACLES */
    private final Map<String, Image> obstacles = new HashMap<>();

    /* PLATFORMS */
    private final Map<String, Image> platforms = new HashMap<>();

    /* CURSOR */
    private Image customCursor;

    /* HUD */
    private final Map<String, Image> hud = new HashMap<>();

    /* FONTS */
    private Font enchantedLandFont;

    /* COINS */
    private final Map<String, Image> coins = new HashMap<>();

    /* GAME OVER SCREEN */
    private final Map<String, Image> gameover = new HashMap<>();

    private AssetManager() {
        loadBackgrounds();
        loadButtons();
        loadCursor();
        loadCharacterCards();
        loadInfoCards();
        loadMapBackgrounds();
        loadMapFlags();
        loadObstacles();
        loadPlatforms();
        loadHUD();
        loadCoins();
        loadAllSpritesheets();
        loadFonts();
        loadGameOver();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    private void loadBackgrounds() {
        backgrounds.put("title", loadImage("ASSETS/Backgrounds/background.png"));
        backgrounds.put("titleFont", loadImage("ASSETS/Backgrounds/titleFont.png"));
        backgrounds.put("menu", loadImage("ASSETS/Backgrounds/buttonDashboardBackground.png"));
        backgrounds.put("characterSelect", loadImage("assets/Backgrounds/chooseCharacterBackground.png"));
        backgrounds.put("mapSelect", loadImage("assets/Backgrounds/mapSelectBackground.png"));
        backgrounds.put("leaderboard", loadImage("assets/Backgrounds/leaderboardPanel.png"));
    }

    private void loadButtons() {
        // Main menu
        buttons.put("start", loadImage("ASSETS/Buttons/startButton.png"));
        buttons.put("menu", loadImage("ASSETS/Buttons/menuButton.png"));
        buttons.put("exit", loadImage("ASSETS/Buttons/exitButton.png"));

        // Navigation
        buttons.put("back", loadImage("assets/Buttons/backButton.png"));
        buttons.put("next", loadImage("assets/Buttons/nextButton.png"));
        buttons.put("select", loadImage("assets/Buttons/selectButton.png"));
    
        // Play again
        buttons.put("playAgain", loadImage("assets/Buttons/playAgainButton.png"));
    }

    private void loadObstacles() {

        obstacles.put("adamyaRock",
                loadImage("assets/obstacles/adamyaObstacles/adamyaRock.png"));

        obstacles.put("ball",
                loadImage("assets/obstacles/adamyaObstacles/adamyaBallLeaves.png"));

        obstacles.put("lava",
                loadImage("assets/obstacles/hathoriaObstacles/hathoriaLava.png"));

        obstacles.put("hathoriaRock",
                loadImage("assets/obstacles/hathoriaObstacles/hathoriaRock.png"));

        obstacles.put("storm",
                loadImage("assets/obstacles/lireoObstacles/lireoStormCloud.png"));

        obstacles.put("wind",
                loadImage("assets/obstacles/lireoObstacles/lireoWind.png"));

        obstacles.put("snowball",
                loadImage("assets/obstacles/mineaveObstacles/mineaveSnowball.png"));

        obstacles.put("mineaveSpike",
                loadImage("assets/obstacles/mineaveObstacles/mineaveSpikes.png"));

        obstacles.put("sapiroRock",
                loadImage("assets/obstacles/sapiroObstacles/sapiroRock.png"));

        obstacles.put("tumbleweed",
                loadImage("assets/obstacles/sapiroObstacles/sapiroTumbleweed.png"));
    }

    private void loadPlatforms() {

        platforms.put("log",
                loadImage("assets/obstacles/adamyaObstacles/adamyaLog.png"));

        platforms.put("lily",
                loadImage("assets/obstacles/adamyaObstacles/adamyaLily.png"));

        platforms.put("lily2",
                loadImage("assets/obstacles/adamyaObstacles/adamyaLily2.png"));

        platforms.put("hathoriaPlatform",
                loadImage("assets/obstacles/hathoriaObstacles/hathoriaPlatform.png"));

        platforms.put("hathoriaPlatform2",
                loadImage("assets/obstacles/hathoriaObstacles/hathoriaPlatform2.png"));

        platforms.put("cloud",
                loadImage("assets/obstacles/lireoObstacles/lireoCloud.png"));

        platforms.put("lireoPlatform",
                loadImage("assets/obstacles/lireoObstacles/lireoDisappearingPlatform.png"));

        platforms.put("glacier",
                loadImage("assets/obstacles/mineaveObstacles/mineaveGlacier.png"));

        platforms.put("mineavePlatform",
                loadImage("assets/obstacles/mineaveObstacles/mineaveIcePlatform.png"));

        platforms.put("sand",
                loadImage("assets/obstacles/sapiroObstacles/sapiroSand.png"));

        platforms.put("sapiroPlatform",
                loadImage("assets/obstacles/sapiroObstacles/sapiroPlatform.png"));

    }

    public Image getObstacleImage(String type) {
        return obstacles.get(type);
    }

    private void loadHUD() {

        hud.put("heart",
                loadImage("assets/heartIcon.png"));

        hud.put("score",
                loadImage("assets/scoreLabel.png"));

        /*
         * hud.put("menu",
         * loadImage("assets/HUD/menuButton.png"));
         */
    }

    private void loadCoins() {
        coins.put("coin", loadImage("assets/coins/coin.png"));
    }

    private void loadCursor() {
        customCursor = loadImage("ASSETS/customCursor.png");
    }

    private void loadCharacterCards() {
        characterCards.put(PlayerType.PAOPAO, loadImage("assets/characterCards/paopaoCard.png"));
        characterCards.put(PlayerType.DEIA, loadImage("assets/characterCards/deiaCard.png"));
        characterCards.put(PlayerType.FLAMARA, loadImage("assets/characterCards/flammaraCard.png"));
        characterCards.put(PlayerType.TERRA, loadImage("assets/characterCards/terraCard.png"));
        characterCards.put(PlayerType.ADAMUS, loadImage("assets/characterCards/adamusCard.png"));
    }

    private void loadInfoCards() {
        infoCards.put(PlayerType.PAOPAO, loadImage("assets/characterInfoCard/paopaoInfoCard.png"));
        infoCards.put(PlayerType.DEIA, loadImage("assets/characterInfoCard/deiaInfoCard.png"));
        infoCards.put(PlayerType.FLAMARA, loadImage("assets/characterInfoCard/flammaraInfoCard.png"));
        infoCards.put(PlayerType.TERRA, loadImage("assets/characterInfoCard/terraInfoCard.png"));
        infoCards.put(PlayerType.ADAMUS, loadImage("assets/characterInfoCard/adamusInfoCard.png"));
    }

    private void loadMapBackgrounds() {
        mapBackgrounds.put(GameMap.LIREO, loadImage("assets/maps/lireoMap.png"));
        mapBackgrounds.put(GameMap.HATHORIA, loadImage("assets/maps/hathoriaMap.png"));
        mapBackgrounds.put(GameMap.ADAMYA, loadImage("assets/maps/adamyaMap.png"));
        mapBackgrounds.put(GameMap.SAPIRO, loadImage("assets/maps/sapiroMap.png"));
        mapBackgrounds.put(GameMap.MINEAVE, loadImage("assets/maps/mineaveMap.png"));
    }

    private void loadMapFlags() {
        mapFlags.put(GameMap.LIREO, loadImage("assets/flags/lireoFlagMap.png"));
        mapFlags.put(GameMap.HATHORIA, loadImage("assets/flags/hathoriaFlagMap.png"));
        mapFlags.put(GameMap.ADAMYA, loadImage("assets/flags/adamyaFlagMap.png"));
        mapFlags.put(GameMap.SAPIRO, loadImage("assets/flags/sapiroFlagMap.png"));
        mapFlags.put(GameMap.MINEAVE, loadImage("assets/flags/mineaveFlagMap.png"));
    }

    private void loadAllSpritesheets() {
        try {
            // columns = frames per row, rows = 4 directions (DOWN, LEFT, RIGHT, UP)
            loadSpritesheet(PlayerType.PAOPAO, "assets/spritesheets/paopaoSpritesheet.png", 9, 4);
            loadSpritesheet(PlayerType.DEIA, "assets/spritesheets/deiaSpritesheet.png", 9, 4);
            loadSpritesheet(PlayerType.FLAMARA, "assets/spritesheets/flammaraSpritesheet.png", 9, 4);
            loadSpritesheet(PlayerType.TERRA, "assets/spritesheets/terraSpritesheet.png", 9, 4);
            loadSpritesheet(PlayerType.ADAMUS, "assets/spritesheets/adamusSpritesheet.png", 9, 4);
        } catch (IOException e) {
            System.err.println("[AssetManager] Failed to load one or more spritesheets:");
            e.printStackTrace();
        }
    }

    /*
     * Slices a spritesheet into per-direction frame arrays.
     * Row order assumed: DOWN=0, LEFT=1, RIGHT=2, UP=3
     */
    private void loadSpritesheet(PlayerType type, String path, int columns, int rows)
            throws IOException {

        BufferedImage sheet = ImageIO.read(new File(path));
        int frameW = sheet.getWidth() / columns;
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
    private Image loadImage(String path) {
        File f = new File(path);
        if (!f.exists()) {
            System.err.printf("[AssetManager] WARNING: asset not found: %s%n", path);
            return null;
        }
        return new ImageIcon(path).getImage();
    }

    private void loadFonts() {
        try {
            enchantedLandFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("assets/Enchanted Land.otf")).deriveFont(20f);
        } catch (Exception e) {
            System.err.println("[AssetManager] WARNING: failed to load font Enchanted Land.ttf");
            enchantedLandFont = new Font("Segoe UI", Font.BOLD, 20); // fallback
        }
    }

    // Returns the animation frames for the given player and direction.
    public BufferedImage[] getPlayerAnimation(PlayerType type, Direction dir) {
        Map<Direction, BufferedImage[]> map = playerAnimations.get(type);
        if (map == null)
            return null;
        BufferedImage[] frames = map.get(dir);
        return (frames != null) ? frames : map.get(Direction.DOWN);
    }

    private void loadGameOver() {
        gameover.put("background", loadImage("assets/Backgrounds/gameoverBackground.png"));
        gameover.put("enterInitials", loadImage("assets/enterInitials.png"));
        gameover.put("enterInitialsBlank", loadImage("assets/enterInitialsBlank.png"));
        gameover.put("okButton", loadImage("assets/Buttons/okButton.png"));
    }

    public Image getCharacterCard(PlayerType type) {
        return characterCards.get(type);
    }

    public Image getInfoCard(PlayerType type) {
        return infoCards.get(type);
    }

    public Image getMapBackground(GameMap map) {
        return mapBackgrounds.get(map);
    }

    public Image getMapFlag(GameMap map) {
        return mapFlags.get(map);
    }

    public Image getBackground(String key) {
        return backgrounds.get(key);
    }

    public Image getButton(String key) {
        return buttons.get(key);
    }

    public Image getObstacle(String key) {
        return obstacles.get(key);
    }

    public Image getPlatform(String key) {
        return platforms.get(key);
    }

    public Image getHUD(String key) {
        return hud.get(key);
    }

    public Image getCoin(String key) {
        return coins.get(key);
    }

    public Image getCustomCursor() {
        return customCursor;
    }

    public Image getPlatformImage(String type) {
        return platforms.get(type);
    }

    public Font getFont(String key) {
        if (key.equals("enchantedLand"))
            return enchantedLandFont;
        return null;
    }

    public Image getGameOver(String key) {
        return gameover.get(key);
    }
}