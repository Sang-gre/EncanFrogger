package persistence;

public class ScoreEntry {
    public String initials;
    public int score;
    public int level;
    public boolean isAlive;
    public int coins;

    public ScoreEntry(String initials, int score, int level, boolean isAlive, int coins) {
        this.initials = initials.toUpperCase();
        this.score = score;
        this.level = level;
        this.isAlive = isAlive;
        this.coins = coins;
    }
}