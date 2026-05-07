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
    private static final int LANE_TOP_PADDING = 5;
    private final String[] laneObstacleType = new String[LANE_COUNT];
    private final String[] lanePlatformType = new String[LANE_COUNT];

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
    private static final int MIN_GAP = 120;

    private final Map<Obstacle, Integer> obstacleLanes = new HashMap<>();
    private final Map<Platform, Integer> platformLanes = new HashMap<>();
    private final List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private final List<Platform> platforms = new CopyOnWriteArrayList<>();
    private final List<Coin> coins = new CopyOnWriteArrayList<>();
    private final Random rng = new Random();

    private final Map<GameMap, String[]> mapObstacleTypes = new HashMap<>();
    private final Map<GameMap, String[]> mapPlatformTypes = new HashMap<>();

    private final Map<Integer, List<List<Obstacle>>> obstacleGroups = new HashMap<>();
    private final Map<Integer, List<List<Platform>>> platformGroups = new HashMap<>();
    
    private Image background;
    private GameMap currentMap;

    public LevelManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        computeLanes();
        initObstaclePools();
    }

    private static final int GROUP_GAP = 4;

    private int[] computeGroupOffsets(int itemWidth, int groupSize, int gap, Direction dir) {
        int[] offsets = new int[groupSize];
        for (int i = 0; i < groupSize; i++) {
            offsets[i] = i * (itemWidth + gap);
        }
        return offsets;
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

        int normalLaneHeight = remainingHeight / (LANE_COUNT - 1);

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

    private Dimension scaleToFitLane(Image img, int maxWidth, int maxHeight) {
        if (img == null) {
            return new Dimension(Math.max(1, maxWidth), Math.max(1, maxHeight));
        }

        int origW = img.getWidth(null);
        int origH = img.getHeight(null);
        if (origW <= 0 || origH <= 0) {
            return new Dimension(Math.max(1, maxWidth), Math.max(1, maxHeight));
        }

        float scale = (float) maxHeight / origH;
        int newW = Math.round(origW * scale);
        int newH = Math.round(origH * scale);

        if (newW > maxWidth) {
            scale = (float) maxWidth / origW;
            newW = Math.round(origW * scale);
            newH = Math.round(origH * scale);
        }

        return new Dimension(newW, newH);
    }

    private void repositionEntities() {

        for (Obstacle o : obstacles) {
            Integer lane = obstacleLanes.get(o);
            if (lane != null) {
                Image img = AssetManager.getInstance().getObstacleImage(o.getType());
                Dimension d = scaleToFitLane(img, columnWidth, laneHeight);
                int y = centeredY(lane, laneHeight) + (laneHeight - d.height) / 2;
                o.setPosition(o.getX(), y);
                o.setSize(d.width, d.height);
                o.setImage(img);
            }
        }

        for (Platform p : platforms) {
            Integer lane = platformLanes.get(p);
            if (lane != null) {
                Image img = AssetManager.getInstance().getPlatformImage(p.getType());
                int availableHeight = Math.max(1, laneHeight - LANE_TOP_PADDING);
                Dimension d = scaleToFitLane(img, columnWidth * 2, availableHeight);
                int laneTop = centeredY(lane, laneHeight);
                int y = laneTop + LANE_TOP_PADDING + (availableHeight - d.height) / 2;
                p.setPosition(p.getX(), y);
                p.setSize(d.width, d.height);
                p.setImage(img);
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

        assignLaneTypesForMap(currentMap);

        spawnObstacles();
        spawnPlatforms();
        spawnCoins();

        background = AssetManager.getInstance().getMapBackground(map);
    }

    private void assignLaneTypesForMap(GameMap map) {
        Arrays.fill(laneObstacleType, null);
        Arrays.fill(lanePlatformType, null);

        for (int lane = 0; lane < LANE_COUNT; lane++) {
            if (!isPlatformLane[lane] && lane != 0 && lane != 9 && !isSafeLane[lane]) {
                String[] obstacleTypes = mapObstacleTypes.get(map);
                if (obstacleTypes != null && obstacleTypes.length > 0) {
                    laneObstacleType[lane] = obstacleTypes[rng.nextInt(obstacleTypes.length)];
                }
            }

            if (isPlatformLane[lane]) {
                String[] platformTypes = mapPlatformTypes.get(map);
                if (platformTypes != null && platformTypes.length > 0) {
                    lanePlatformType[lane] = platformTypes[rng.nextInt(platformTypes.length)];
                }
            }
        }

    }

    private void initPlatformLanes() {

        isPlatformLane = new boolean[LANE_COUNT];
        isObstacleLane = new boolean[LANE_COUNT];
        isSafeLane = new boolean[LANE_COUNT];

        for (int i = 0; i < LANE_COUNT; i++) {

            isPlatformLane[i] = i >= 1 && i <= 3;

            isSafeLane[i] = i == 4;

            isObstacleLane[i] = i >= 5 && i <= 8;
        }
    }

    private void initObstaclePools() {

        mapObstacleTypes.put(
                GameMap.ADAMYA,
                new String[]{"adamyaRock", "ball"}
        );

        mapObstacleTypes.put(
                GameMap.HATHORIA,
                new String[]{"lava", "hathoriaRock"}
        );

        mapObstacleTypes.put(
                GameMap.LIREO,
                new String[]{"storm", "wind"}
        );

        mapObstacleTypes.put(
                GameMap.SAPIRO,
                new String[]{"sapiroRock", "tumbleweed"}
        );

        mapObstacleTypes.put(
                GameMap.MINEAVE,
                new String[]{"snowball", "mineaveSpike"}
        );

        mapPlatformTypes.put(
                GameMap.ADAMYA,
                new String[]{"log", "lily", "lily2"}
        );

        mapPlatformTypes.put(
                GameMap.HATHORIA,
                new String[]{"hathoriaPlatform", "hathoriaPlatform2"}
        );

        mapPlatformTypes.put(
                GameMap.LIREO,
                new String[]{"lireoPlatform", "cloud"}
        );

        mapPlatformTypes.put(
                GameMap.SAPIRO,
                new String[]{"sand", "sapiroPlatform"}
        );

        mapPlatformTypes.put(
                GameMap.MINEAVE,
                new String[]{"mineavePlatform", "glacier"}
        );
    }

    private void spawnObstacles() {
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            if (isPlatformLane[lane]) continue;
            if (lane == 0 || lane == 9) continue;
            if (isSafeLane[lane]) continue;

            Direction dir = (lane % 2 == 0) ? Direction.RIGHT : Direction.LEFT;

            int groupsPerLane = 2;   // fixed number of groups
            int groupSize = 3;       // items per group

            List<List<Obstacle>> laneGroups = new ArrayList<>();

            for (int g = 0; g < groupsPerLane; g++) {
                List<Obstacle> group = new ArrayList<>();

                String assignedType = laneObstacleType[lane];
                Image img = AssetManager.getInstance().getObstacleImage(assignedType);
                Dimension d = scaleToFitLane(img, columnWidth, laneHeight);

                int itemW = d.width;
                int itemH = d.height;

                // Align groups to fixed columns (every 3rd column for spacing)
                int baseX = columnX[(g * 3) % COLUMN_COUNT];

                for (int j = 0; j < groupSize; j++) {
                    int x = baseX + j * (itemW + GROUP_GAP);
                    int y = centeredY(lane, itemH);

                    Obstacle o = new Obstacle(
                            x, y,
                            itemW, itemH,
                            lane,
                            obstacleSpeed,
                            dir,
                            assignedType
                    );
                    o.setImage(img);

                    obstacles.add(o);
                    obstacleLanes.put(o, lane);
                    group.add(o);
                }

                laneGroups.add(group);
            }
            obstacleGroups.put(lane, laneGroups);
        }
    }

    public void spawnPlatforms() {
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            if (!isPlatformLane[lane]) continue;

            Direction dir = (lane % 2 == 0) ? Direction.RIGHT : Direction.LEFT;

            int groupsPerLane = 2;   // fixed number of groups
            int groupSize = 2;       // items per group

            List<List<Platform>> laneGroups = new ArrayList<>();

            for (int g = 0; g < groupsPerLane; g++) {
                List<Platform> group = new ArrayList<>();

                String assignedType = lanePlatformType[lane];
                Image img = AssetManager.getInstance().getPlatformImage(assignedType);

                int availableHeight = Math.max(1, laneHeight - LANE_TOP_PADDING);
                Dimension d = scaleToFitLane(img, columnWidth * 2, availableHeight);

                int itemW = d.width;
                int itemH = d.height;

                // Align groups to fixed columns (every 3rd column for spacing)
                int baseX = columnX[(g * 3) % COLUMN_COUNT];

                for (int j = 0; j < groupSize; j++) {
                    int x = baseX + j * (itemW + GROUP_GAP);
                    int y = centeredY(lane, laneHeight)
                            + LANE_TOP_PADDING
                            + (availableHeight - itemH) / 2;

                    Platform p = new Platform(
                            x, y,
                            itemW, itemH,
                            lane,
                            obstacleSpeed * 0.7f,
                            dir,
                            assignedType
                    );
                    p.setImage(img);

                    platforms.add(p);
                    platformLanes.put(p, lane);
                    group.add(p);
                }

                laneGroups.add(group);
            }
            platformGroups.put(lane, laneGroups);
        }
    }




    public void spawnCoins() {

    coins.clear();

    int count = BASE_COIN_COUNT + Math.max(0, (currentLevel - 1));

    for (int i = 0; i < count; i++) {

        boolean placed = false;
        int attempts = 0;

        while (!placed && attempts < 15) {

            int lane = rng.nextInt(LANE_COUNT);

            // ❌ NO COINS ON TOP 2 LANES
            if (isTopTwoLanes(lane)) {
                attempts++;
                continue;
            }

            int col = rng.nextInt(COLUMN_COUNT);
            int x = getColumnCenteredX(col);

            int y = centeredY(lane, COIN_SIZE);

            Coin coin = new Coin(x, y, COIN_SIZE, COIN_SIZE);

            // ============================
            // PLATFORM COINS (FIXED)
            // ============================
            if (isPlatformLane(lane)) {

                List<Platform> candidates = new ArrayList<>();

                for (Platform p : platforms) {
                    if (platformLanes.get(p) == lane) {
                        candidates.add(p);
                    }
                }

                if (!candidates.isEmpty()) {

                    Platform pf = candidates.get(rng.nextInt(candidates.size()));

                    int platformCenterX =
                            pf.getX() + (pf.getWidth() / 2) - (COIN_SIZE / 2);

                    int platformCenterY =
                            pf.getY() + (pf.getHeight() / 2) - (COIN_SIZE / 2);

                    coin.setPosition(platformCenterX, platformCenterY);
                    coin.attachToPlatform(pf);

                    coins.add(coin);
                    placed = true;
                }

            } else {

                // ============================
                // NORMAL GRID COINS (10-COLUMN FIXED)
                // ============================

                boolean collides = false;

                for (Obstacle obs : obstacles) {
                    if (obs.getBounds().intersects(coin.getBounds())) {
                        collides = true;
                        break;
                    }
                }

                if (!collides) {
                    coins.add(coin);
                    placed = true;
                }
            }

            attempts++;
        }
    }
}

    private void respawnObstacleGroup(List<Obstacle> group) {

        if (group.isEmpty()) return;

        Obstacle first = group.get(0);
        Direction dir = first.getDirection();
        int lane = obstacleLanes.get(first);

        int jitter = rng.nextInt(200);

        Image img = AssetManager.getInstance().getObstacleImage(first.getType());
        Dimension d = scaleToFitLane(img, columnWidth, laneHeight);

        int itemW = d.width;
        int itemH = d.height;

        int baseX = (dir == Direction.RIGHT)
                ? -itemW - jitter
                : screenWidth + jitter;

        for (int i = 0; i < group.size(); i++) {

            int x = (dir == Direction.RIGHT)
                    ? baseX - i * (itemW + GROUP_GAP)
                    : baseX + i * (itemW + GROUP_GAP);

            int y = centeredY(lane, itemH);

            Obstacle o = group.get(i);
            o.reset(x, y, obstacleSpeed, dir);
            o.setSize(itemW, itemH);
            o.setImage(img);
        }
    }

    private void respawnPlatformGroup(List<Platform> group) {

        if (group.isEmpty()) return;

        Platform first = group.get(0);
        Direction dir = first.getDirection();
        int lane = platformLanes.get(first);

        int jitter = rng.nextInt(200);

        Image img = AssetManager.getInstance().getPlatformImage(first.getType());
        int availableHeight = Math.max(1, laneHeight - LANE_TOP_PADDING);
        Dimension d = scaleToFitLane(img, columnWidth * 2, availableHeight);

        int itemW = d.width;
        int itemH = d.height;

        int baseX = (dir == Direction.RIGHT)
                ? -itemW - jitter
                : screenWidth + jitter;

        for (int i = 0; i < group.size(); i++) {

            int x = (dir == Direction.RIGHT)
                    ? baseX - i * (itemW + GROUP_GAP)
                    : baseX + i * (itemW + GROUP_GAP);

            int y = centeredY(lane, laneHeight)
                    + LANE_TOP_PADDING
                    + (availableHeight - itemH) / 2;

            Platform p = group.get(i);
            p.reset(x, y, obstacleSpeed * 0.7f, dir);
            p.setSize(itemW, itemH);
            p.setImage(img);
        }
    }

    public void update() {

        List<Obstacle> toRespawn = new CopyOnWriteArrayList<>();

        for (Obstacle o : obstacles) {

            if (!o.isActive()) continue;

            o.update();

            if (o.isOffScreen(screenWidth)) {
                toRespawn.add(o);
            }
        }

        for (Map.Entry<Integer, List<List<Obstacle>>> entry : obstacleGroups.entrySet()) {
            for (List<Obstacle> group : entry.getValue()) {

                boolean allOffScreen = true;

                for (Obstacle o : group) {
                    if (!o.isOffScreen(screenWidth)) {
                        allOffScreen = false;
                        break;
                    }
                }

                if (allOffScreen) {
                    respawnObstacleGroup(group);
                }
            }
        }

        List<Platform> toRespawnPlatforms = new CopyOnWriteArrayList<>();

        for (Platform p : platforms) {

            if (!p.isActive()) continue;

            p.update();

            if (p.isOffScreen(screenWidth)) {
                toRespawnPlatforms.add(p);
            }
        }

        for (Map.Entry<Integer, List<List<Platform>>> entry : platformGroups.entrySet()) {
            for (List<Platform> group : entry.getValue()) {

                boolean allOffScreen = true;

                for (Platform p : group) {
                    if (!p.isOffScreen(screenWidth)) {
                        allOffScreen = false;
                        break;
                    }
                }

                if (allOffScreen) {
                    respawnPlatformGroup(group);
                }
            }
        }

        for (Coin c : coins) {

            if (c.isActive()) {
                c.update();
            }
        }
    }

    public void draw(Graphics g, int width, int height) {

        if (background != null) {
            g.drawImage(background, 0, 0, width, height, null);
        }

        for (Obstacle o : obstacles) {
            if (o.isActive()) o.draw(g);
        }

        for (Platform p : platforms) {
            if (p.isActive()) p.draw(g);
        }

        for (Coin c : coins) {
            if (c.isActive()) c.draw(g);
        }
    }

    private void respawnObstacle(Obstacle o) {
        Direction dir = o.getDirection();
        int jitter = rng.nextInt(200);
        int x = (dir == Direction.RIGHT)
                ? -OBSTACLE_WIDTH - jitter
                : screenWidth + jitter;

        Integer lane = obstacleLanes.get(o);
        Image img = AssetManager.getInstance().getObstacleImage(o.getType());
        Dimension d = scaleToFitLane(img, columnWidth, laneHeight);
        int y = centeredY(lane, d.height);

        o.reset(x, y, obstacleSpeed, dir);
        o.setSize(d.width, d.height);
        o.setImage(img);
    }

    private void respawnPlatform(Platform p) {

        Direction dir = p.getDirection();

        int jitter = rng.nextInt(200);
        int x = (dir == Direction.RIGHT)
                ? -PLATFORM_WIDTH - jitter
                : screenWidth + jitter;

        Integer lane = platformLanes.get(p);
        Image img = AssetManager.getInstance().getPlatformImage(p.getType());
        int availableHeight = Math.max(1, laneHeight - LANE_TOP_PADDING);
        Dimension d = scaleToFitLane(img, columnWidth * 2, availableHeight);
        int y = centeredY(lane, laneHeight) + LANE_TOP_PADDING + (availableHeight - d.height) / 2;

        p.reset(x, y, obstacleSpeed * 0.7f, dir);
        p.setSize(d.width, d.height);
        p.setImage(img);
    }

    private int centeredY(int lane, int entityHeight) {
    return laneY[lane] + (laneHeight - entityHeight) / 2;
}

    public boolean isPlayerOnPlatform(gameobjects.Player player) {

        for (Platform p : platforms) {

            if (p.isActive() && p.isPlayerOn(player)) {
                return true;
            }
        }

        return false;
    }

    public int getLaneIndex(int y) {

        int lane0Height = screenHeight / 6;

        if (y < lane0Height) return 0;

        for (int i = 1; i < LANE_COUNT; i++) {

            if (y >= laneY[i]
                    && y < laneY[i] + laneHeight) {

                return i;
            }
        }

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
        return lane >= 0
                && lane < LANE_COUNT
                && isPlatformLane[lane];
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

    public int centerPlayerY(int lane, int height) {
    return laneY[lane] + (laneHeight - height) / 2;
}

    private int getColumnCenteredX(int col) {
    return columnX[col] + (columnWidth - COIN_SIZE) / 2;
}

    private boolean isTopTwoLanes(int lane) {
    return lane == 0 || lane == 1;
}
}
