package level;

import core.GameMap;
import gameobjects.Coin;
import gameobjects.Obstacle;
import gameobjects.Platform;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;

public class LevelManager {

    private static final int LANE_COUNT = 10;
    private static final float LANE_TOP_MARGIN = 0.05f;
    private static final float LANE_BOTTOM_MARGIN = 0.05f;
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

    private final Map<Obstacle, Integer> obstacleLanes = new HashMap<>();
    private final Map<Platform, Integer> platformLanes = new HashMap<>();
    private final List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private final List<Platform> platforms = new CopyOnWriteArrayList<>();
    private final List<Coin> coins = new CopyOnWriteArrayList<>();
    private final Random rng = new Random();

    private Image background;

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

        columnWidth = screenWidth / COLUMN_COUNT;
        columnX = new int[COLUMN_COUNT];
        for (int i = 0; i < COLUMN_COUNT; i++) {
            columnX[i] = i * columnWidth;
        }
    }

    private void repositionEntities() {
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
                p.setSize(columnWidth * 2, laneHeight);
            }
        }
        coins.clear();
        spawnCoins();
    }

    public void loadLevel(int n, GameMap map) {
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
            isPlatformLane[i] = i >= 1 && i <= PLATFORM_LANE_COUNT;
        }
    }

    private void spawnObstacles() {
        int countPerLane = BASE_OBSTACLE_COUNT + (currentLevel - 1);

        for (int lane = 0; lane < LANE_COUNT; lane++) {
            if (isPlatformLane[lane])
                continue;
            if (lane == 0 || lane == LANE_COUNT - 1)
                continue;

            Direction dir = (lane % 2 == 0) ? Direction.RIGHT : Direction.LEFT;
            int y = centeredY(lane, OBSTACLE_HEIGHT);
            int spread = screenWidth / countPerLane;

            for (int i = 0; i < countPerLane; i++) {
                int startX = (dir == Direction.RIGHT)
                        ? -OBSTACLE_WIDTH - i * spread
                        : screenWidth + i * spread;

                Obstacle o = new Obstacle(
                        startX, y,
                        columnWidth, laneHeight,
                        lane, obstacleSpeed, dir);
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

                Platform p = new Platform(
                        startX, y,
                        columnWidth * 2, laneHeight,
                        lane, obstacleSpeed * 0.7f, dir);
                platforms.add(p);
                platformLanes.put(p, lane);
            }
        }
    }

    public void spawnCoins() {
        int count = BASE_COIN_COUNT + (currentLevel - 1);

        for (int i = 0; i < count; i++) {
            int lane = rng.nextInt(LANE_COUNT);
            int x = rng.nextInt(Math.max(1, screenWidth - COIN_SIZE));
            int y = centeredY(lane, COIN_SIZE);

            // TODO: if isPlatformLane[lane], attach coin to a platform
            // TODO: avoid spawning coins directly on top of obstacles
            coins.add(new Coin(x, y, COIN_SIZE, COIN_SIZE));
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
            for (Coin c : coins) {
                if (c.isActive() && p.isPlayerOn(c)) {
                    c.setPosition(c.getX() + (int) p.getSpeed(), c.getY());
                }
            }
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
        return laneY[lane] + (laneHeight - entityHeight) / 2;
    }

    public boolean isPlayerOnPlatform(gameobjects.Player player) {
        for (Platform p : platforms) {
            if (p.isActive() && p.isPlayerOn(player))
                return true;
        }
        return false;
    }

    public int getLaneIndex(int y) {
        for (int i = 0; i < LANE_COUNT; i++) {
            if (y >= laneY[i] && y < laneY[i] + laneHeight) {
                return i;
            }
        }
        return -1;
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