package assets;

import core.level.GameMap;
import gameobjects.Direction;
import gameobjects.PlayerType;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class AssetManager {

    private static AssetManager instance;

    private static String getBasePath() {
        try {
            File codeSource = new File(
                    AssetManager.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());
            if (codeSource.getName().endsWith(".jar")) {
                return codeSource.getParentFile()
                        .getParentFile()
                        .getParentFile()
                        .getAbsolutePath() + File.separator;
            }
            return codeSource.getParentFile()
                    .getParentFile()
                    .getAbsolutePath() + File.separator;
        } catch (Exception e) {
            return "";
        }
    }

    private static final String BASE = getBasePath();

    /* GAME LOGO/ICON */
    private final Map<String, ImageIcon> logo = new HashMap<>();

    private final Map<String, Image> congrats = new HashMap<>();

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
    private Font proffaliceHandwriteFont;

    /* COINS */
    private final Map<String, Image> coins = new HashMap<>();

    /* GAME OVER SCREEN */
    private final Map<String, Image> gameover = new HashMap<>();

    /* POPUPS */
    private final Map<String, Image> popups = new HashMap<>();

    /* INSTRUCTIONS */
    private final Map<String, Image> instructions = new HashMap<>();

    /* TRACKER */
    private final Map<String, Image> tracker = new HashMap<>();

    private AssetManager() {
        loadLogo();
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
        loadPopups();
        loadInstructions();
        loadCongrats();
        loadTracker();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    private void loadCongrats() {

        congrats.put("levelClearedBackground",
                loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/levelClearedBackground.png"));

        congrats.put("levelCleared",
                loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/levelCleared.png"));
    }

    private void loadLogo() {
        logo.put("logo", new ImageIcon(BASE + "EncanFrogger/assets/icons/gameLogo.png"));
    }

    private void loadTracker() {
        tracker.put("coinTrack", loadImage(BASE + "EncanFrogger/assets/images/Buttons/coinTrackLabel.png"));
        tracker.put("levelTrack", loadImage(BASE + "EncanFrogger/assets/images/Buttons/levelTrackLabel.png"));
    }

    private void loadBackgrounds() {
        backgrounds.put("title", loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/background.png"));
        backgrounds.put("titleFont", loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/titleFont.png"));
        backgrounds.put("menu",
                loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/buttonDashboardBackground.png"));
        backgrounds.put("characterSelect",
                loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/chooseCharacterBackground.png"));
        backgrounds.put("mapSelect",
                loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/mapSelectBackground.png"));
        backgrounds.put("leaderboard", loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/leaderboardPanel.png"));
        backgrounds.put("initials", loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/initialsBackground.png"));
        backgrounds.put("pausePanel", loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/pausePanel.png"));
    }

    private void loadButtons() {
        // Main menu
        buttons.put("start", loadImage(BASE + "EncanFrogger/assets/images/Buttons/startButton.png"));
        buttons.put("menu", loadImage(BASE + "EncanFrogger/assets/images/Buttons/menuButton.png"));
        buttons.put("exit", loadImage(BASE + "EncanFrogger/assets/images/Buttons/exitButton.png"));

        // Navigation
        buttons.put("back", loadImage(BASE + "EncanFrogger/assets/images/Buttons/backButton.png"));
        buttons.put("next", loadImage(BASE + "EncanFrogger/assets/images/Buttons/nextButton.png"));
        buttons.put("select", loadImage(BASE + "EncanFrogger/assets/images/Buttons/selectButton.png"));

        // Play again
        buttons.put("playAgain", loadImage(BASE + "EncanFrogger/assets/images/Buttons/playAgainButton.png"));
        buttons.put("ok", loadImage(BASE + "EncanFrogger/assets/images/Buttons/okButton.png"));
        buttons.put("yes", loadImage(BASE + "EncanFrogger/assets/images/Buttons/yesButton.png"));
        buttons.put("no", loadImage(BASE + "EncanFrogger/assets/images/Buttons/noButton.png"));

        // Popup Dialog
        buttons.put("ok2", loadImage(BASE + "EncanFrogger/assets/images/Buttons/okButton2.png"));

        // Instructions Button
        buttons.put("leftArrow", loadImage(BASE + "EncanFrogger/assets/images/Buttons/leftArrowButton.png"));
        buttons.put("rightArrow", loadImage(BASE + "EncanFrogger/assets/images/Buttons/rightArrowButton.png"));
        buttons.put("xButton", loadImage(BASE + "EncanFrogger/assets/images/Buttons/exButton.png"));

        // Pause Panel Buttons
        buttons.put("exit2", loadImage(BASE + "EncanFrogger/assets/images/Buttons/exitButton2.png"));
        buttons.put("menu2", loadImage(BASE + "EncanFrogger/assets/images/Buttons/menuButton2.png"));
        buttons.put("resume", loadImage(BASE + "EncanFrogger/assets/images/Buttons/resumeButton.png"));
        buttons.put("pause", loadImage(BASE + "EncanFrogger/assets/images/Buttons/pauseButton.png"));

        // leaderboard Buttons
        buttons.put("leaderboardBtn", loadImage(BASE + "EncanFrogger/assets/images/Buttons/leaderboardButton.png"));

    }

    private void loadObstacles() {

        obstacles.put("adamyaRock",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/adamyaObstacles/adamyaRock.png"));

        obstacles.put("ball",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/adamyaObstacles/adamyaBallLeaves.png"));

        obstacles.put("lava",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/hathoriaObstacles/hathoriaLava.png"));

        obstacles.put("hathoriaRock",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/hathoriaObstacles/hathoriaRock.png"));

        obstacles.put("storm",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/lireoObstacles/lireoStormCloud.png"));

        obstacles.put("wind",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/lireoObstacles/lireoWind.png"));

        obstacles.put("snowball",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/mineaveObstacles/mineaveSnowball.png"));

        obstacles.put("mineaveSpike",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/mineaveObstacles/mineaveSpikes.png"));

        obstacles.put("sapiroRock",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/sapiroObstacles/sapiroRock.png"));

        obstacles.put("tumbleweed",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/sapiroObstacles/sapiroTumbleweed.png"));
    }

    private void loadPlatforms() {

        platforms.put("log",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/adamyaObstacles/adamyaLog.png"));

        platforms.put("lily",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/adamyaObstacles/adamyaLily.png"));

        platforms.put("lily2",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/adamyaObstacles/adamyaLily2.png"));

        platforms.put("hathoriaPlatform",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/hathoriaObstacles/hathoriaPlatform.png"));

        platforms.put("hathoriaPlatform2",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/hathoriaObstacles/hathoriaPlatform2.png"));

        platforms.put("cloud",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/lireoObstacles/lireoCloud.png"));

        platforms.put("lireoPlatform",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/lireoObstacles/lireoDisappearingPlatform.png"));

        platforms.put("glacier",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/mineaveObstacles/mineaveGlacier.png"));

        platforms.put("mineavePlatform",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/mineaveObstacles/mineaveIcePlatform.png"));

        platforms.put("sand",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/sapiroObstacles/sapiroSand.png"));

        platforms.put("sapiroPlatform",
                loadImage(BASE + "EncanFrogger/assets/images/obstacles/sapiroObstacles/sapiroPlatform.png"));

    }

    public Image getObstacleImage(String type) {
        return obstacles.get(type);
    }

    private void loadHUD() {

        hud.put("heart",
                loadImage(BASE + "EncanFrogger/assets/images/hud/heartIcon.png"));

        hud.put("score",
                loadImage(BASE + "EncanFrogger/assets/images/hud/scoreLabel.png"));
    }

    private void loadCoins() {
        coins.put("coin", loadImage(BASE + "EncanFrogger/assets/images/coins/coin.png"));
    }

    private void loadCursor() {
        customCursor = loadImage(BASE + "EncanFrogger/assets/images/customCursor.png");
    }

    private void loadCharacterCards() {
        characterCards.put(PlayerType.PAOPAO,
                loadImage(BASE + "EncanFrogger/assets/images/characterCards/paopaoCard.png"));
        characterCards.put(PlayerType.DEIA, loadImage(BASE + "EncanFrogger/assets/images/characterCards/deiaCard.png"));
        characterCards.put(PlayerType.FLAMARA,
                loadImage(BASE + "EncanFrogger/assets/images/characterCards/flammaraCard.png"));
        characterCards.put(PlayerType.TERRA,
                loadImage(BASE + "EncanFrogger/assets/images/characterCards/terraCard.png"));
        characterCards.put(PlayerType.ADAMUS,
                loadImage(BASE + "EncanFrogger/assets/images/characterCards/adamusCard.png"));
    }

    private void loadInfoCards() {
        infoCards.put(PlayerType.PAOPAO,
                loadImage(BASE + "EncanFrogger/assets/images/characterInfoCard/paopaoInfoCard.png"));
        infoCards.put(PlayerType.DEIA,
                loadImage(BASE + "EncanFrogger/assets/images/characterInfoCard/deiaInfoCard.png"));
        infoCards.put(PlayerType.FLAMARA,
                loadImage(BASE + "EncanFrogger/assets/images/characterInfoCard/flammaraInfoCard.png"));
        infoCards.put(PlayerType.TERRA,
                loadImage(BASE + "EncanFrogger/assets/images/characterInfoCard/terraInfoCard.png"));
        infoCards.put(PlayerType.ADAMUS,
                loadImage(BASE + "EncanFrogger/assets/images/characterInfoCard/adamusInfoCard.png"));
    }

    private void loadMapBackgrounds() {
        mapBackgrounds.put(GameMap.LIREO, loadImage(BASE + "EncanFrogger/assets/images/maps/lireoMap.png"));
        mapBackgrounds.put(GameMap.HATHORIA, loadImage(BASE + "EncanFrogger/assets/images/maps/hathoriaMap.png"));
        mapBackgrounds.put(GameMap.ADAMYA, loadImage(BASE + "EncanFrogger/assets/images/maps/adamyaMap.png"));
        mapBackgrounds.put(GameMap.SAPIRO, loadImage(BASE + "EncanFrogger/assets/images/maps/sapiroMap.png"));
        mapBackgrounds.put(GameMap.MINEAVE, loadImage(BASE + "EncanFrogger/assets/images/maps/mineaveMap.png"));
    }

    private void loadMapFlags() {
        mapFlags.put(GameMap.LIREO, loadImage(BASE + "EncanFrogger/assets/images/flags/lireoFlagMap.png"));
        mapFlags.put(GameMap.HATHORIA, loadImage(BASE + "EncanFrogger/assets/images/flags/hathoriaFlagMap.png"));
        mapFlags.put(GameMap.ADAMYA, loadImage(BASE + "EncanFrogger/assets/images/flags/adamyaFlagMap.png"));
        mapFlags.put(GameMap.SAPIRO, loadImage(BASE + "EncanFrogger/assets/images/flags/sapiroFlagMap.png"));
        mapFlags.put(GameMap.MINEAVE, loadImage(BASE + "EncanFrogger/assets/images/flags/mineaveFlagMap.png"));
    }

    private void loadAllSpritesheets() {
        try {
            // columns = frames per row, rows = 4 directions (DOWN, LEFT, RIGHT, UP)
            loadSpritesheet(PlayerType.PAOPAO, BASE + "EncanFrogger/assets/images/spritesheets/paopaoSpritesheet.png",
                    9, 4);
            loadSpritesheet(PlayerType.DEIA, BASE + "EncanFrogger/assets/images/spritesheets/deiaSpritesheet.png", 9,
                    4);
            loadSpritesheet(PlayerType.FLAMARA,
                    BASE + "EncanFrogger/assets/images/spritesheets/flammaraSpritesheet.png", 9, 4);
            loadSpritesheet(PlayerType.TERRA, BASE + "EncanFrogger/assets/images/spritesheets/terraSpritesheet.png", 9,
                    4);
            loadSpritesheet(PlayerType.ADAMUS, BASE + "EncanFrogger/assets/images/spritesheets/adamusSpritesheet.png",
                    9, 4);
        } catch (IOException e) {
            System.err.println("[AssetManager] Failed to load one or more spritesheets:");
        }
    }

    // Slices a spritesheet into per-direction frame arrays.
    // DOWN=0, LEFT=1, RIGHT=2, UP=3
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
                BufferedImage sub = sheet.getSubimage(col * frameW, row * frameH, frameW, frameH);

                // copy into an independent image so it doesn't rely on sheet's pixel data
                BufferedImage copy = new BufferedImage(frameW, frameH, sheet.getType());
                Graphics2D g = copy.createGraphics();
                g.drawImage(sub, 0, 0, null);
                g.dispose();

                frames[col] = copy;
            }
            dirMap.put(rowOrder[row], frames);
        }

        playerAnimations.put(type, dirMap);
        sheet.flush(); // safe to flush now — all frames are independent copies
    }

    private void loadFonts() {
        try {
            proffaliceHandwriteFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File(BASE + "EncanFrogger/assets/fonts/Proffalice Handwrite Regular.ttf")).deriveFont(20f);
        } catch (IOException | FontFormatException e) {
            System.err.println("[AssetManager] WARNING: failed to load Font");
            proffaliceHandwriteFont = new Font("Segoe UI", Font.BOLD, 20); // fallback
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
        gameover.put("background", loadImage(BASE + "EncanFrogger/assets/images/Backgrounds/gameoverBackground.png"));
        gameover.put("okButton", loadImage(BASE + "EncanFrogger/assets/images/Buttons/okButton.png"));
    }

    private void loadPopups() {
        popups.put("characterSelect", loadImage(BASE + "EncanFrogger/assets/images/Popups/characterSelectPopup.png"));
        popups.put("mapSelect", loadImage(BASE + "EncanFrogger/assets/images/Popups/mapSelectPopup.png"));
        popups.put("initialsInput", loadImage(BASE + "EncanFrogger/assets/images/Popups/initialsInputPopup.png"));
        popups.put("initialsTaken", loadImage(BASE + "EncanFrogger/assets/images/Popups/initialsTakenPopup.png"));
    }

    private void loadInstructions() {
        instructions.put("instruction1", loadImage(BASE + "EncanFrogger/assets/images/instructions/page1.png"));
        instructions.put("instruction2", loadImage(BASE + "EncanFrogger/assets/images/instructions/page2.png"));
        instructions.put("instruction3", loadImage(BASE + "EncanFrogger/assets/images/instructions/page3.png"));
        instructions.put("instruction4", loadImage(BASE + "EncanFrogger/assets/images/instructions/page4.png"));
        instructions.put("instruction5", loadImage(BASE + "EncanFrogger/assets/images/instructions/page5.png"));
        instructions.put("instruction6", loadImage(BASE + "EncanFrogger/assets/images/instructions/page6.png"));
        instructions.put("instruction7", loadImage(BASE + "EncanFrogger/assets/images/instructions/page7.png"));
    }

    // Convenience loader: returns null and prints a warning instead of throwing.
    private Image loadImage(String path) {
        try {
            BufferedImage raw = ImageIO.read(new File(path));

            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

            BufferedImage optimized = gc.createCompatibleImage(
                    raw.getWidth(), raw.getHeight(), Transparency.TRANSLUCENT);

            Graphics2D g = optimized.createGraphics();
            g.drawImage(raw, 0, 0, null);
            g.dispose();

            raw.flush();
            return optimized;

        } catch (IOException e) {
            System.err.println("[AssetManager] Failed to load: " + path);
            return null;
        }
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

    public Image getCongrats(String key) {
        return congrats.get(key);
    }

    public Image getLogoImage(String key) {
        ImageIcon icon = logo.get(key);
        return (icon != null) ? icon.getImage() : null;
    }

    public Image getObstacle(String key) {
        return obstacles.get(key);
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
        if (key.equals("proffaliceHandwrite")) {
            return proffaliceHandwriteFont;
        }
        return null;
    }

    public Image getGameOver(String key) {
        return gameover.get(key);
    }

    public Image getPopup(String key) {
        return popups.get(key);
    }

    public Image getInstructions(String key) {
        return instructions.get(key);
    }

    public Image getTracker(String key) {
        return tracker.get(key);
    }

    public Image getLevelButtonImage(int level) {
        return buttons.get("level" + level);
    }
}