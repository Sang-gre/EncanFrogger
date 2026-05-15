package core.level;

public enum GameMap {
    LIREO(1, 3),
    HATHORIA(4, 6),
    ADAMYA(7, 10),
    SAPIRO(11, 15),
    MINEAVE(16,20);

    private final int startLevel;
    private final int endLevel;

    GameMap(int startLevel, int endLevel) {
        this.startLevel = startLevel;
        this.endLevel = endLevel;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public int getEndLevel() {
        return endLevel;
    }

    public int getMaxLevels() {
        return endLevel - startLevel + 1;
    }

}