package level;

import assets.AssetManager;
import core.GameMap;
import gameobjects.Coin;
import gameobjects.Obstacle;
import gameobjects.Platform;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LevelManager {

    private static final int LANE_COUNT = 10;
    private static final int OBSTACLE_WIDTH = 60;
    private static final int OBSTACLE_HEIGHT = 30;
    private static final int PLATFORM_WIDTH = 120;
    private static final int COIN_SIZE = 20;
    private static final float BASE_SPEED = 2.5f;
    private static final float SPEED_INCREMENT = 0.4f;
    private static final int BASE_OBSTACLE_COUNT = 3;
    private static final int BASE_COIN_COUNT = 6;
    private static final int PLATFORM_LANE_COUNT = 4;
    private static final int PLATFORM_COUNT = 2;
    private static final int COLUMN_COUNT = 10;

    private int screenWidth;
    private int screenHeight;
    private int[] laneY;
    private int laneHeight;
    private int[] columnX;
    private int columnWidth;

    private int currentLevel;
    private float obstacleSpeed;
    private boolean[] isPlatformLane;
    private boolean[] isObstacleLane;
    private boolean[] isSafeLane;
    private static final int SAFE_LANE = 5;

    private final Map<Obstacle, Integer> obstacleLanes = new HashMap<>();
    private final Map<Platform, Integer> platformLanes = new HashMap<>();
    private final List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private final List<Platform> platforms = new CopyOnWriteArrayList<>();
    private final List<Coin> coins = new CopyOnWriteArrayList<>();
    private final Random rng = new Random(); // random coin position
    private final Map<GameMap, String[]> mapObstacleTypes = new HashMap<>();
    private final Map<GameMap, String[]> mapPlatformTypes = new HashMap<>();

    private Image background;
    private GameMap currentMap;

    public LevelManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        computeLanes();

        initObstaclePools();
        computeLanes();
    }

    public void resize(int newWidth, int newHeight) {
        this.screenWidth = newWidth;
        this.screenHeight = newHeight;
        computeLanes();
        repositionEntities();
    }

    private void computeLanes() {
        int lane0Height = screenHeight / 6;
        int remainingHeight = screenHeight - lane0Height;
        int normalLaneHeight = remainingHeight / (LANE_COUNT - 1); // split rest evenly among lanes 1-9

        laneHeight = normalLaneHeight;
        laneY = new int[LANE_COUNT];
        laneY[0] = 0;
        for (int i = 1; i < LANE_COUNT; i++) {
            laneY[i] = lane0Height + (i - 1) * normalLaneHeight;
        }

        columnWidth = screenWidth / COLUMN_COUNT;
        columnX = new int[COLUMN_COUNT];
        for (int i = 0; i < COLUMN_COUNT; i++) {
            columnX[i] = i * columnWidth;
        }
    }

    private void repositionEntities() { // for the objects position when window resizes
        for (Obstacle o : obstacles) {
            Integer lane = obstacleLanes.get(o);
            if (lane != null) {
                o.setPosition(o.getX(), centeredY(lane, laneHeight));
                o.setSize(columnWidth, laneHeight);
            }
        }
        for (Platform p : platforms) {
            Integer lane = platformLanes.get(p);
            if (lane != null) {
                p.setPosition(p.getX(), centeredY(lane, laneHeight));
                p.setSize(columnWidth * 2, 40);
            }
        }
        coins.clear();
        spawnCoins();
    }

    public void loadLevel(int n, GameMap map) {

        currentMap = map;

        currentLevel = n;
        obstacleSpeed = BASE_SPEED + (n - 1) * SPEED_INCREMENT;

        obstacles.clear();
        obstacleLanes.clear();
        platforms.clear();
        platformLanes.clear();
        coins.clear();

        initPlatformLanes();
        spawnObstacles();
        spawnPlatforms();
        spawnCoins();

        background = AssetManager.getMapBackground(map);
    }

    private void initPlatformLanes() {
        isPlatformLane = new boolean[LANE_COUNT];
        isObstacleLane = new boolean[LANE_COUNT];
        isSafeLane = new boolean[LANE_COUNT];

        for (int i = 0; i < LANE_COUNT; i++) {
            isPlatformLane[i] = i >= 1 && i <= 3;
            isSafeLane[i] = i == 4 || i == 5;
            isObstacleLane[i] = i >= 6 && i <= 8; 
        }
    }

    private void initObstaclePools() {

        mapObstacleTypes.put(GameMap.ADAMYA,
                new String[]{"adamyaRock", "ball"});

        mapObstacleTypes.put(GameMap.HATHORIA,
                new String[]{"lava", "hathoriaRock"});

        mapObstacleTypes.put(GameMap.LIREO,
                new String[]{"storm", "wind"});

        mapObstacleTypes.put(GameMap.SAPIRO,
                new String[]{"sapiroRock", "tumbleweed"});

        mapObstacleTypes.put(GameMap.MINEAVE,
                new String[]{"snowball", "mineaveSpike"});

        mapPlatformTypes.put(GameMap.ADAMYA,
                new String[]{"log", "lily", "lily2"});

        mapPlatformTypes.put(GameMap.HATHORIA,
                new String[]{"hathoriaPlatform", "hathoriaPlatform2"});

        mapPlatformTypes.put(GameMap.LIREO,
                new String[]{"lireoPlatform", "island", "cloud"});

        mapPlatformTypes.put(GameMap.SAPIRO,
                new String[]{"hole", "sapiroSpike"});

        mapPlatformTypes.put(GameMap.MINEAVE,
                new String[]{"mineavePlatform", "glacier"});
    }



    private void spawnObstacles() {
        int countPerLane = BASE_OBSTACLE_COUNT + (currentLevel - 1);

        for (int lane = 0; lane < LANE_COUNT; lane++) {
            if (isPlatformLane[lane])
                continue;
            if (lane == 0)
                continue;
            if (lane == 4)
                continue;
            if (lane == 9)
                continue;

            Direction dir = (lane % 2 == 0) ? Direction.RIGHT : Direction.LEFT;
            int y = centeredY(lane, OBSTACLE_HEIGHT);
            int spread = screenWidth / countPerLane;

            for (int i = 0; i < countPerLane; i++) {
                int startX = (dir == Direction.RIGHT)
                        ? -OBSTACLE_WIDTH - i * spread
                        : screenWidth + i * spread;

                String[] obstacleTypes = mapObstacleTypes.get(currentMap);
                String type = obstacleTypes[rng.nextInt(obstacleTypes.length)];

                Obstacle o = new Obstacle(
                        startX,
                        y,
                        columnWidth,
                        laneHeight,
                        lane,
                        obstacleSpeed,
                        dir,
                        type
                );

                obstacles.add(o);
                obstacleLanes.put(o, lane);
            }
        }
    }

    public void spawnPlatforms() {
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            if (!isPlatformLane[lane])
                continue;

            Direction dir = (lane % 2 == 0) ? Direction.RIGHT : Direction.LEFT;
            int y = centeredY(lane, OBSTACLE_HEIGHT);
            int spread = screenWidth / PLATFORM_COUNT;

            for (int i = 0; i < PLATFORM_COUNT; i++) {
                int startX = (dir == Direction.RIGHT)
                        ? -PLATFORM_WIDTH - i * spread
                        : screenWidth + i * spread;

                String[] platformTypes = mapPlatformTypes.get(currentMap);
                String type = platformTypes[rng.nextInt(platformTypes.length)];

                Platform p = new Platform(
                        startX, y,
                        columnWidth * 2, 40,
                        lane, obstacleSpeed * 0.7f, dir,
                        type);
                platforms.add(p);
                platformLanes.put(p, lane);
            }
        }
    }

    public void spawnCoins() {
        int count = BASE_COIN_COUNT + (currentLevel - 1);

        for (int i = 0; i < count; i++) {

            boolean validPosition = false;
            int attempts = 0;

            while (!validPosition && attempts < 10) { // 10 attempt is just estimation

                int lane = rng.nextInt(LANE_COUNT); // which horizontaal lane will the coin be on?

                // PLATFORM LANE
                if (isPlatformLane(lane)) {

                    // get platforms in this lane
                    List<Platform> candidates = new ArrayList<>();
                    for (Platform p : platforms) {
                        if (platformLanes.get(p) == lane) {
                            candidates.add(p);
                        }
                    }

                    if (candidates.isEmpty()) {
                        attempts++;
                        continue;
                    }

                    // pick a random platform
                    Platform pf = candidates.get(rng.nextInt(candidates.size()));

                    int x = pf.getX() + rng.nextInt(Math.max(1, pf.getWidth() - COIN_SIZE));
                    int y = pf.getY() + (pf.getHeight() - COIN_SIZE) / 2;

                    Coin coin = new Coin(x, y, COIN_SIZE, COIN_SIZE);
                    coin.attachToPlatform(pf);

                    coins.add(coin);
                    validPosition = true;

                } else { // LAND/OBSTACLE LANE
                    int x = rng.nextInt(Math.max(1, screenWidth - COIN_SIZE)); // random horiz position such that it
                                                                               // wont go off screen
                    int y = centeredY(lane, COIN_SIZE); // to place the coin inside the lane, cnetered vertically

                    Coin coin = new Coin(x, y, COIN_SIZE, COIN_SIZE);

                    boolean collides = false;

                    // must NOT collide with any obstacle
                    for (Obstacle obs : obstacles) {
                        if (obs.getBounds().intersects(coin.getBounds())) {
                            collides = true;
                            break;
                        }
                    }
                    if (!collides) {
                        coins.add(coin);
                        validPosition = true;
                    }
                }

                attempts++;
            }
        }
    }

    public void update() {
        List<Obstacle> toRespawn = new CopyOnWriteArrayList<>();
        for (Obstacle o : obstacles) {
            if (!o.isActive())
                continue;
            o.update();
            if (o.isOffScreen(screenWidth))
                toRespawn.add(o);
        }
        for (Obstacle o : toRespawn)
            respawnObstacle(o);

        List<Platform> toRespawnPlatforms = new CopyOnWriteArrayList<>();
        for (Platform p : platforms) {
            if (!p.isActive())
                continue;
            p.update();

            if (p.isOffScreen(screenWidth))
                toRespawnPlatforms.add(p);
        }
        for (Platform p : toRespawnPlatforms)
            respawnPlatform(p);

        for (Coin c : coins) {
            if (c.isActive())
                c.update();
        }
    }

    public void draw(Graphics g, int width, int height) {
        if (background != null) {
            g.drawImage(background, 0, 0, width, height, null);
        }

        for (Obstacle o : obstacles) {
            if (o.isActive())
                o.draw(g);
        }
        for (Platform p : platforms) {
            if (p.isActive())
                p.draw(g);
        }
        for (Coin c : coins) {
            if (c.isActive())
                c.draw(g);
        }
    }

    private void respawnObstacle(Obstacle o) {
        Direction dir = o.getDirection();
        int x = (dir == Direction.RIGHT) ? -OBSTACLE_WIDTH : screenWidth;
        Integer lane = obstacleLanes.get(o);
        int y = centeredY(lane, OBSTACLE_HEIGHT);
        o.reset(x, y, obstacleSpeed, dir);
    }

    private void respawnPlatform(Platform p) {
        Direction dir = p.getDirection();
        int x = (dir == Direction.RIGHT) ? -PLATFORM_WIDTH : screenWidth;
        Integer lane = platformLanes.get(p);
        int y = centeredY(lane, OBSTACLE_HEIGHT);
        p.reset(x, y, obstacleSpeed * 0.7f, dir);
    }

    private int centeredY(int lane, int entityHeight) {
        return laneY[lane] + laneHeight - entityHeight;
    }

    public boolean isPlayerOnPlatform(gameobjects.Player player) {
        for (Platform p : platforms) {
            if (p.isActive() && p.isPlayerOn(player))
                return true;
        }
        return false;
    }

    public int getLaneIndex(int y) {
        int lane0Height = screenHeight / 6;
        if (y < lane0Height)
            return 0;
        for (int i = 1; i < LANE_COUNT; i++) {
            if (y >= laneY[i] && y < laneY[i] + laneHeight) {
                return i;
            }
        }
        // if it falls below last lane, clamp to lane 9
        return LANE_COUNT - 1;
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    public List<Platform> getPlatforms() {
        return Collections.unmodifiableList(platforms);
    }

    public List<Coin> getCoins() {
        return Collections.unmodifiableList(coins);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public float getObstacleSpeed() {
        return obstacleSpeed;
    }

    public int getLaneHeight() {
        return laneHeight;
    }

    public int getLaneCount() {
        return LANE_COUNT;
    }

    public boolean isPlatformLane(int lane) {
        return lane >= 0 && lane < LANE_COUNT && isPlatformLane[lane];
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public int[] getColumnX() {
        return columnX;
    }

    public int[] getLaneY() {
        return laneY;
    }
}