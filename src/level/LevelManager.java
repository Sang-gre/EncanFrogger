package level;

import core.GameMap;
import gameobjects.Coin;
import gameobjects.Obstacle;
import java.awt.*;
import java.util.*;
import javax.swing.ImageIcon;

public class LevelManager {

    private static final int LANE_COUNT = 6;
    private static final float LANE_TOP_MARGIN = 0.10f;
    private static final float LANE_BOTTOM_MARGIN = 0.15f;
    private static final int OBSTACLE_WIDTH = 60;
    private static final int OBSTACLE_HEIGHT = 40;
    private static final int COIN_SIZE = 20;
    private static final float BASE_SPEED = 2.5f;
    private static final float SPEED_INCREMENT = 0.4f;
    private static final int BASE_OBSTACLE_COUNT = 3;
    private static final int BASE_COIN_COUNT = 6;
    private static final int PLATFORM_LANE_COUNT = 2;

    private int screenWidth;
    private int screenHeight;

    private int[] laneY;
    private int laneHeight;

    private int currentLevel;
    private float obstacleSpeed;

    private boolean[] isPlatformLane;

    private final Map<Obstacle, Integer> obstacleLanes = new HashMap<>();
    private final java.util.List<Obstacle> obstacles = new ArrayList<>();
    private final java.util.List<Coin> coins = new ArrayList<>();
    private final Random rng = new Random();

    private Image background;

    public LevelManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        computeLanes();
    }

    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        computeLanes();
    }

    private void computeLanes() {
        int topY = (int) (screenHeight * LANE_TOP_MARGIN);
        int bottomY = (int) (screenHeight * (1f - LANE_BOTTOM_MARGIN));
        int usable = bottomY - topY;

        laneHeight = usable / LANE_COUNT;
        laneY = new int[LANE_COUNT];

        for (int i = 0; i < LANE_COUNT; i++) {
            laneY[i] = topY + i * laneHeight;
        }
    }

    public void loadLevel(int n, GameMap map) {
        currentLevel = n;
        obstacleSpeed = BASE_SPEED + (n - 1) * SPEED_INCREMENT;

        obstacles.clear();
        obstacleLanes.clear();
        coins.clear();

        spawnObstacles();
        spawnCoins();
        initPlatformLanes();

        switch (map) {
            case LIREO:
                background = new ImageIcon("assets/maps/lireoMap.png").getImage();
                break;
            case HATHORIA:
                background = new ImageIcon("assets/maps/hathoriaMap.png").getImage();
                break;
            case ADAMYA:
                background = new ImageIcon("assets/maps/adamyaMap.png").getImage();
                break;
            case SAPIRO:
                background = new ImageIcon("assets/maps/sapiroMap.png").getImage();
                break;
            case MINEAVE:
                background = new ImageIcon("assets/maps/mineaveMap.png").getImage();
                break;
        }
    }

    private void initPlatformLanes() {
        isPlatformLane = new boolean[LANE_COUNT];
        for (int i = 0; i < LANE_COUNT; i++) {
            isPlatformLane[i] = i >= (LANE_COUNT - PLATFORM_LANE_COUNT);
        }
    }

    public void update() {
        for (Obstacle o : obstacles) o.update();
        for (Coin c : coins) c.update();
    }

    public void draw(Graphics g, int width, int height) {

        if (background != null) {
            g.drawImage(background, 0, 0, width, height, null);
        }

        for (Obstacle o : obstacles) o.draw(g);
        for (Coin c : coins) c.draw(g);
    }

    private void spawnObstacles() {
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            for (int i = 0; i < BASE_OBSTACLE_COUNT; i++) {
                Obstacle o = new Obstacle(0, 0, OBSTACLE_WIDTH, OBSTACLE_HEIGHT, lane, obstacleSpeed, Direction.RIGHT);
                obstacles.add(o);
                obstacleLanes.put(o, lane);
            }
        }
    }

    private void spawnCoins() {
        for (int i = 0; i < BASE_COIN_COUNT; i++) {
            coins.add(new Coin(100, 100, COIN_SIZE, COIN_SIZE));
        }
    }

    public java.util.List<Obstacle> getObstacles() { return obstacles; }
    public java.util.List<Coin> getCoins() { return coins; }
    public int getCurrentLevel() { return currentLevel; }
}