package data;

import gameobjects.PlayerType;

public class SaveState {
    private int playerX;
    private int playerY;
    private int score;
    private int level;
    private int lives;
    private PlayerType playerType;

    public SaveState(int x, int y, int score, int level, int lives, PlayerType playerType) {
        this.playerX = x;
        this.playerY = y;
        this.score = score;
        this.level = level;
        this.lives = lives;
        this.playerType = playerType;
    }

    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLives() { return lives; }
    public PlayerType getPlayerType() { return playerType; }
}
