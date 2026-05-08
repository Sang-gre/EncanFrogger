package persistence;

public class ScoreEntry {
    public String initials;
    public int score;
    public int level;
    public boolean isAlive;

    public ScoreEntry(String initials, int score, int level, boolean isAlive) {
        this.initials = initials.toUpperCase();
        this.score = score;
        this.level = level;
        this.isAlive = isAlive;
    }
}