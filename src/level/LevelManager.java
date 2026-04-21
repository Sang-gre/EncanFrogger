package level;

import gameobjects.Coin;
import gameobjects.GameObject;
import gameobjects.Obstacle;
import gameobjects.Player;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LevelManager {

    // All of these can be changed, like I just set those values for now
    private static final int LANE_COUNT = 6;
    private static final float LANE_TOP_MARGIN = 0.10f; // Goal zone
    private static final float LANE_BOTTOM_MARGIN = 0.15f; // Start zone
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
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Coin> coins = new ArrayList<>();
    private final Random rng = new Random();

    public LevelManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        computeLanes();
    }

    public void resize(int newWidth, int newHeight) {
        this.screenWidth = newWidth;
        this.screenHeight = newHeight;
        computeLanes();
        repositionEntities();
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

    private void repositionEntities() {
        for (Obstacle o : obstacles) {
            Integer lane = obstacleLanes.get(o);
            if (lane != null) {
                o.setPosition(o.getBounds().x, centeredY(lane, OBSTACLE_HEIGHT));
            }
        }
        coins.clear(); // I'm not sure about this pa, basically it would despawn a coin when screen is
                       // resized
        spawnCoins();
    }

    public void loadLevel(int n) {
        currentLevel = n;
        obstacleSpeed = BASE_SPEED + (n - 1) * SPEED_INCREMENT;

        obstacles.clear();
        obstacleLanes.clear();
        coins.clear();

        spawnObstacles();
        spawnCoins();
        initPlatformLanes();

        // TODO: load map-specific background/tileset based on selected map
        // TODO: adjust LANE_COUNT or lane layout depending on the map
        // TODO: spawn log obstacles

        // Debug
        System.out.println("Level " + n + " loaded | speed=" + obstacleSpeed);
    }

    private void initPlatformLanes() {
        isPlatformLane = new boolean[LANE_COUNT];
        for (int i = 0; i < LANE_COUNT; i++) {
            isPlatformLane[i] = i >= (LANE_COUNT - PLATFORM_LANE_COUNT);
        }
    }

    public void update() {
        List<Obstacle> toRespawn = new ArrayList<>();

        for (Obstacle o : obstacles) {
            if (!o.isActive())
                continue;
            o.update();
            if (o.isOffScreen(screenWidth)) {
                toRespawn.add(o);
            }
        }

        for (Obstacle o : toRespawn) {
            respawn(o);
        }

        for (Coin c : coins) {
            if (c.isActive())
                c.update();
        }
    }

    public void draw(Graphics g) {
        for (Obstacle o : obstacles) {
            if (o.isActive())
                o.draw(g);
        }
        for (Coin c : coins) {
            if (c.isActive())
                c.draw(g);
        }
    }

    public void spawnObstacles() {
        int countPerLane = BASE_OBSTACLE_COUNT + (currentLevel - 1);

        for (int lane = 0; lane < LANE_COUNT; lane++) {
            Direction dir = (lane % 2 == 0) ? Direction.RIGHT : Direction.LEFT;
            int y = centeredY(lane, OBSTACLE_HEIGHT);
            int spread = screenWidth / countPerLane;

            for (int i = 0; i < countPerLane; i++) {
                int startX = (dir == Direction.RIGHT)
                        ? -OBSTACLE_WIDTH - i * spread
                        : screenWidth + i * spread;

                // TODO: replace with Log instead of Obstacle pag nasa water
                // TODO: vary obstacle width n stuff
                Obstacle o = new Obstacle(
                        startX, y,
                        OBSTACLE_WIDTH, OBSTACLE_HEIGHT,
                        lane, obstacleSpeed, dir);
                obstacles.add(o);
                obstacleLanes.put(o, lane);
            }
        }
    }

    public void spawnCoins() {
        int count = BASE_COIN_COUNT + (currentLevel - 1);

        for (int i = 0; i < count; i++) {
            int lane = rng.nextInt(LANE_COUNT);
            int x = rng.nextInt(Math.max(1, screenWidth - COIN_SIZE));
            int y = centeredY(lane, COIN_SIZE);

            // TODO: if isLogLane[lane], lagay yung coin sa log
            // TODO: avoid spawning coins directly on top of obstacles
            coins.add(new Coin(x, y, COIN_SIZE, COIN_SIZE));
        }
    }

    public void checkUnlocks() {
        // TODO: increase obstacleSpeed per level
        // TODO: add vertical obstacles??
        // TODO: basta iba iba na lanes
    }

    private void respawn(Obstacle o) {
        boolean fromLeft = rng.nextBoolean();
        Direction dir = fromLeft ? Direction.RIGHT : Direction.LEFT;
        int x = fromLeft ? -OBSTACLE_WIDTH : screenWidth;
        Integer lane = obstacleLanes.get(o);
        int y = centeredY(lane, OBSTACLE_HEIGHT);

        o.reset(x, y, obstacleSpeed, dir);
    }

    private int centeredY(int lane, int entityHeight) {
        return laneY[lane] + (laneHeight - entityHeight) / 2;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public float getObstacleSpeed() {
        return obstacleSpeed;
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    public List<Coin> getCoins() {
        return Collections.unmodifiableList(coins);
    }

    public boolean isPlatformLane(int lane) {
        return lane >= 0 && lane < LANE_COUNT && isPlatformLane[lane];
    }
}