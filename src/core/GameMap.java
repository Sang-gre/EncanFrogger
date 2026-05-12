package core;

public enum GameMap {
    LIREO(3),
    HATHORIA(3),
    ADAMYA(4),
    SAPIRO(4),
    MINEAVE(5);

    private final int maxLevels;

    GameMap(int maxLevels) {
        this.maxLevels = maxLevels;
    }

    public int getMaxLevels() {
        return maxLevels;
    }
}